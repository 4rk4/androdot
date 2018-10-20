/**
 * @author 4ar4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 * <p>
 * XMLRPC method:
 * metaWeblog.getRecentPosts
 * https://github.com/dotclear/dotclear/blob/master/inc/core/class.dc.xmlrpc.php
 */

package com.androdot;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Updater {
    
    private final Context              context;
    private       String               mUser;
    private       String               mPassword;
    private       String               mUrlBlog;
    private       String               mNbRecentPost;
    private       List< OneArticle >   listArticles      = new ArrayList<>( );
    private       List< OneCategorie > listCategories    = new ArrayList<>( );
    private       String               fileNameArticles  = "list_artcles.dat";
    private       String               fileNameCategorie = "list_categories.dat";
    private       OnUpdateListener     mListener;
    
    public Updater( Context ctx, String user, String password, String urlBlog, String nbPost ) {
        this.context = ctx;
        this.mUser = user;
        this.mPassword = password;
        this.mUrlBlog = urlBlog;
        this.mNbRecentPost = nbPost;
        if ( context instanceof OnUpdateListener ) {
            mListener = ( OnUpdateListener ) context;
        } else {
            throw new RuntimeException( context.toString( ) + " must implement OnUpdateListener" );
        }
    }
    
    public Updater( Context c ) {
        this.context = c;
        this.listArticles.add( new OneArticle( ) );
        this.listCategories.add( new OneCategorie( ) );
    }
    
    public void updateCategories( ) {
        boolean del = context.deleteFile( fileNameCategorie );
        try {
            XMLRPCCallback listener = new XMLRPCCallback( ) {
                
                public void onResponse( long id, Object result ) {
                    Object[]              res = ( Object[] ) result;
                    Map< String, Object > struct;
                    int                   i   = 0;
                    for ( Object r : res ) {
                        struct = ( Map< String, Object > ) res[ i++ ];
                        listCategories.add( new OneCategorie( String.valueOf( struct.get(
                                "categoryName" ) ), String.valueOf( struct.get( "categoryId" ) ),
                                                              false ) );
                    }
                    FileOutputStream fos = null;
                    try {
                        fos = context.openFileOutput( fileNameCategorie, Context.MODE_PRIVATE );
                        ObjectOutputStream os = new ObjectOutputStream( fos );
                        os.writeObject( listCategories );
                        os.close( );
                        fos.close( );
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    }
                    mListener.onUpdateCategorie( );
                }
                
                public void onError( long id, XMLRPCException error ) {
                    mListener.onUpdateCategorieError( );
                }
                
                public void onServerError( long id, XMLRPCServerException error ) {
                    mListener.onUpdateCategorieServerError( );
                }
            };
            XMLRPCClient client = new XMLRPCClient( new URL( mUrlBlog ) );
            long         id     = client.callAsync( listener, "mt.getCategoryList", "1", mUser, mPassword );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    public ArrayList< String > getAdapterCategories( ) {
        FileInputStream     fis               = null;
        ArrayList< String > adapterCategories = new ArrayList<>( );
        try {
            fis = context.openFileInput( fileNameCategorie );
            ObjectInputStream is = new ObjectInputStream( fis );
            this.setListCategories( ( List< OneCategorie > ) is.readObject( ) );
            is.close( );
            fis.close( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
        for ( OneCategorie cat : getListCategories( ) ) {
            adapterCategories.add( cat.getCategoryId( ) );
        }
        return adapterCategories;
    }
    
    public List< OneCategorie > getListCategories( ) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput( fileNameCategorie );
            ObjectInputStream is = new ObjectInputStream( fis );
            this.setListCategories( ( List< OneCategorie > ) is.readObject( ) );
            is.close( );
            fis.close( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
        return listCategories;
    }
    
    public void setListCategories( List< OneCategorie > listCategories ) {
        this.listCategories.clear( );
        this.listCategories = listCategories;
    }
    
    public void updateArticles( ) {
        boolean del = context.deleteFile( fileNameArticles );
        try {
            XMLRPCCallback listener = new XMLRPCCallback( ) {
                
                public void onResponse( long id, Object result ) {
                    // Handling the servers response
                    Object[]              res = ( Object[] ) result;
                    Map< String, Object > struct;
                    int                   i   = 0;
                    for ( Object r : res ) {
                        struct = ( Map< String, Object > ) res[ i++ ];
                        SimpleDateFormat sdf         = new SimpleDateFormat( "dd/MM/yyyy hh:mm" );
                        String           dateFormate = sdf.format( struct.get( "dateCreated" ) );
                        OneArticle       article     = new OneArticle( );
                        article.setDateCreated( String.valueOf( dateFormate ) );
                        article.setId( String.valueOf( struct.get( "postid" ) ) );
                        article.setTitre( String.valueOf( struct.get( "title" ) ) );
                        article.setEntete( String.valueOf( struct.get( "mt_excerpt" ) ) );
                        article.setArticle( String.valueOf( struct.get( "description" ) ) );
                        article.setCommentaires( String.valueOf( struct.get( "mt_allow_comments" )
                                                               ) );
                        article.setPublier( String.valueOf( struct.get( "post_status" ) ) );
                        article.setLien( String.valueOf( struct.get( "mt_allow_pings" ) ) );
                        OneCategorie categorie;
                        if ( struct.get( "categories" ) != null ) {
                            Object[] arrayCat = ( Object[] ) struct.get( "categories" );
                            categorie = new OneCategorie( String.valueOf( arrayCat[ 0 ] ) );
                            Log.i( "arka", arrayCat.toString( ) );
                        } else {
                            categorie = new OneCategorie( "" );
                        }
                        article.setCategorie( categorie );
                        listArticles.add( article );
                    }
                    Log.i( "arka", listArticles.toString( ) );
                    FileOutputStream fos = null;
                    try {
                        fos = context.openFileOutput( fileNameArticles, Context.MODE_PRIVATE );
                        ObjectOutputStream os = new ObjectOutputStream( fos );
                        os.writeObject( listArticles );
                        os.close( );
                        fos.close( );
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    }
                    mListener.onUpdateItem( );
                }
                
                public void onError( long id, XMLRPCException error ) {
                    mListener.onUpdateItemError( );
                }
                
                public void onServerError( long id, XMLRPCServerException error ) {
                    mListener.onUpdateItemServerError( );
                }
            };
            XMLRPCClient client = new XMLRPCClient( new URL( mUrlBlog ) );
            long id = client.callAsync( listener, "metaWeblog.getRecentPosts", "1", mUser,
                                        mPassword, Integer.valueOf( mNbRecentPost ) );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    public List< OneArticle > getListArticles( ) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput( fileNameArticles );
            ObjectInputStream is = new ObjectInputStream( fis );
            this.setListArticles( ( List< OneArticle > ) is.readObject( ) );
            is.close( );
            fis.close( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
        return listArticles;
    }
    
    public void setListArticles( List< OneArticle > listArticles ) {
        this.listArticles.clear( );
        this.listArticles = listArticles;
    }
    
    public interface OnUpdateListener {
        
        void onUpdateItem( );
        
        void onUpdateItemError( );
        
        void onUpdateItemServerError( );
        
        void onUpdateCategorie( );
        
        void onUpdateCategorieError( );
        
        void onUpdateCategorieServerError( );
    }
}
