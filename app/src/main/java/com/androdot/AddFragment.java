/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */

package com.androdot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {
    
    private static final String ARG_UTI                 = "mUser";
    private static final String ARG_MDP                 = "mPassword";
    private static final String ARG_URL                 = "url";
    private static final String ARG_NBP                 = "nbRecentPosts";
    private static final String ARG_ONE                 = "mOneArticle";
    private static final String ARG_CAT                 = "categories";
    private static final int    CHOOSE_FILE_REQUESTCODE = 100;
    private final        float  IMG_SIZE                = 1980f;
    View.OnClickListener handlerAddFile = new View.OnClickListener( ) {
        
        @Override
        public void onClick( View view ) {
            Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
            intent.addCategory( Intent.CATEGORY_OPENABLE );
            intent.setType( "image/*" );
            Intent i = Intent.createChooser( intent, "Image" );
            startActivityForResult( i, CHOOSE_FILE_REQUESTCODE );
        }
    };
    private String                        mUser;
    private String                        mPassword;
    private String                        mUrlBlog;
    private OneArticle                    mOneArticle;
    private String                        mPostId = "-1";
    private OnFragmentInteractionListener mListener;
    private EditText                      etEnTete;
    private EditText                      etArticle;
    private EditText                      etTitre;
    private CheckBox                      cbPublier;
    private CheckBox                      cbCommentaires;
    private CheckBox                      cbRetroLiens;
    private String                        messageReponse;
    private long                          numeroPost;
    private String                        mNbRecentPosts;
    private Button                        btEnvoyer;
    private AutoCompleteTextView          actCategorie;
    View.OnClickListener handlerRaz = new View.OnClickListener( ) {
        
        @Override
        public void onClick( View view ) {
            actCategorie.setText( "" );
            etArticle.setText( "" );
            etTitre.setText( "" );
            etEnTete.setText( "" );
            cbCommentaires.setChecked( false );
            cbPublier.setChecked( false );
            cbRetroLiens.setChecked( false );
            etTitre.requestFocus( );
        }
    };
    private Button                    btUpdate;
    private Button                    btRaz;
    private Button                    btAddFile;
    private ArrayAdapter< String >    adapter;
    private View.OnClickListener      handlerSendArticle = new View.OnClickListener( ) {
        
        @Override
        public void onClick( View view ) {
            if ( etTitre.getText( ).toString( ).equals( "" ) || etArticle.getText( ).toString( )
                                                                         .equals( "" ) ) {
                Snackbar.make( getActivity( ).findViewById( R.id.mainLayout ), getResources( )
                        .getString( R.string.warningSend ), Snackbar.LENGTH_LONG ).show( );
                return;
            } else {
                if ( !adapter.equals( actCategorie.getText( ).toString( ) ) ) {
                    addCategorie( );
                } else {
                    addArticle( );
                }
            }
        }
    };
    private byte[]                    fileByteArray;
    private ContentLoadingProgressBar pg;
    private String                    mImageName;
    private View.OnClickListener      handlerUpdate      = new View.OnClickListener( ) {
        
        @Override
        public void onClick( View view ) {
            mListener.update( );
        }
    };
    
    public AddFragment( ) {
    }
    
    public static AddFragment newInstance( String u, String m, String b, String n ) {
        AddFragment fragment = new AddFragment( );
        Bundle      args     = new Bundle( );
        args.putString( ARG_UTI, u );
        args.putString( ARG_MDP, m );
        args.putString( ARG_URL, b );
        args.putString( ARG_NBP, n );
        fragment.setArguments( args );
        return fragment;
    }
    
    public static AddFragment newInstance( String user, String password, String url, String
            nbRecentPosts, OneArticle oneArticle ) {
        AddFragment fragment = new AddFragment( );
        Bundle      args     = new Bundle( );
        args.putString( ARG_UTI, user );
        args.putString( ARG_MDP, password );
        args.putString( ARG_URL, url );
        args.putString( ARG_NBP, nbRecentPosts );
        args.putSerializable( ARG_ONE, oneArticle );
        fragment.setArguments( args );
        return fragment;
    }
    
    public void addCategorie( ) {
        try {
            messageReponse = getActivity( ).getResources( ).getString( R.string.stdError );
            XMLRPCCallback listener = new XMLRPCCallback( ) {
                
                @Override
                public void onResponse( long id, Object result ) {
                    messageReponse = String.format( getResources( ).getString( R.string
                                                                                       .categorieCreated ), String.valueOf( result ) );
                    addArticle( );
                    adapter.notifyDataSetChanged( );
                }
                
                @Override
                public void onError( long id, XMLRPCException error ) {
                    Log.i( "Error", "error xmlrpc: " + error.toString( ) );
                }
                
                @Override
                public void onServerError( long id, XMLRPCServerException error ) {
                    Log.i( "Error", "error server xmlrpc: " + error.toString( ) );
                }
            };
            XMLRPCClient          client = new XMLRPCClient( new URL( mUrlBlog ) );
            Map< String, Object > struct = new HashMap( );
            struct.put( "name", actCategorie.getText( ).toString( ) );
            struct.put( "slug", actCategorie.getText( ).toString( ) );
            struct.put( "category_description", actCategorie.getText( ).toString( ) );
            long categorieCreated = client.callAsync( listener, "wp.newCategory", "1", mUser,
                                                      mPassword, struct );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    public void addArticle( ) {
        try {
            messageReponse = getActivity( ).getResources( ).getString( R.string.stdError );
            XMLRPCCallback listener = new XMLRPCCallback( ) {
                
                @Override
                public void onResponse( long id, Object result ) {
                    messageReponse = String.format( getResources( ).getString( R.string.postOk ),
                                                    String.valueOf( result ) );
                    mListener.onAddItem( messageReponse );
                }
                
                @Override
                public void onError( long id, XMLRPCException error ) {
                    mListener.onAddItem( messageReponse );
                }
                
                @Override
                public void onServerError( long id, XMLRPCServerException error ) {
                    mListener.onAddItem( messageReponse );
                }
            };
            if ( actCategorie.getText( ).toString( ).isEmpty( ) ) {
                actCategorie.setText( getActivity( ).getResources( ).getString( R.string
                                                                                        .default_cat_name ) );
            }
            XMLRPCClient          client = new XMLRPCClient( new URL( mUrlBlog ) );
            Map< String, Object > struct = new HashMap( );
            struct.put( "title", etTitre.getText( ).toString( ) );
            struct.put( "description", etArticle.getText( ).toString( ) );
            struct.put( "mt_allow_comments", cbCommentaires.isChecked( ) );
            struct.put( "mt_allow_pings", cbRetroLiens.isChecked( ) );
            struct.put( "mt_excerpt", etEnTete.getText( ).toString( ) );
            struct.put( "categories", new String[]{ actCategorie.getText( ).toString( ) } );
            if ( mOneArticle != null ) {
                numeroPost = client.callAsync( listener, "metaWeblog.editPost", mPostId, mUser,
                                               mPassword, struct, cbPublier.isChecked( ) );
            } else {
                numeroPost = client.callAsync( listener, "metaWeblog.newPost", "1", mUser,
                                               mPassword, struct, cbPublier.isChecked( ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == CHOOSE_FILE_REQUESTCODE ) {
            if ( resultCode == RESULT_OK ) {
                if ( data != null ) {
                    Uri selectedImageUri = data.getData( );
                    Uri sURI             = Uri.parse( String.valueOf( data.getData( ) ) );
                    try {
                        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images
                                .Media.DISPLAY_NAME };
                        Cursor c = MediaStore.Images.Media.query( getActivity( ).getContentResolver
                                ( ), sURI, projection );
                        int id = c.getColumnIndex( "_display_name" );
                        c.moveToFirst( );
                        mImageName = c.getString( id );
                        ByteArrayOutputStream stream = new ByteArrayOutputStream( );
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap( getActivity( )
                                                                                   .getContentResolver( ), selectedImageUri );
                        float newWidth;
                        float newHeight;
                        float coef;
                        if ( bitmap.getWidth( ) > IMG_SIZE || bitmap.getHeight( ) > IMG_SIZE ) {
                            coef = Math.max( bitmap.getWidth( ), bitmap.getHeight( ) ) / IMG_SIZE;
                            newWidth = ( Float.valueOf( bitmap.getWidth( ) ) / coef );
                            newHeight = ( Float.valueOf( bitmap.getHeight( ) ) / coef );
                            bitmap = Bitmap.createScaledBitmap( bitmap, ( int ) newWidth, ( int )
                                    newHeight, false );
                        }
                        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream );
                        fileByteArray = stream.toByteArray( );
                        beforeUploadFile( );
                    } catch ( IOException e ) {
                        e.printStackTrace( );
                    }
                }
            }
        }
    }
    
    private void beforeUploadFile( ) {
        try {
            pg.show( );
            Map< String, Object > media = new ArrayMap<>( );
            media.put( "name", mImageName );
            media.put( "bits", fileByteArray );
            XMLRPCCallback request = new XMLRPCCallback( ) {
                
                @Override
                public void onResponse( long id, Object result ) {
                    final String lien = ( String ) ( ( HashMap< String, Object > ) result ).get(
                            "url" );
                    Snackbar.make( getActivity( ).findViewById( R.id.mainLayout ), getActivity( )
                            .getResources( ).getString( R.string.msg_send_img ), Snackbar
                                           .LENGTH_LONG ).show( );
                    Handler handler = new Handler( Looper.getMainLooper( ) );
                    handler.post( new Runnable( ) {
                        
                        @Override
                        public void run( ) {
                            afterUploadFile( lien, mImageName );
                        }
                    } );
                }
                
                @Override
                public void onError( long id, XMLRPCException error ) {
                    Log.i( "Error", "error xmlrpc: " + error.toString( ) );
                }
                
                @Override
                public void onServerError( long id, XMLRPCServerException error ) {
                    Log.i( "Error", "error server xmlrpc: " + error.toString( ) );
                }
            };
            XMLRPCClient client = new XMLRPCClient( new URL( mUrlBlog ) );
            client.callAsync( request, "metaWeblog.newMediaObject", "1", mUser, mPassword, media );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    protected void afterUploadFile( String lien, String dispName ) {
        pg.hide( );
        etArticle.append( "<figure style=\"float: left; margin: 0 1em 1em 0;\"><img alt=\"\" " +
                          "class=\"media\" src=\"" + lien + "\" /></figure>" );
    }
    
    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnFragmentInteractionListener ) {
            mListener = ( OnFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString( ) + " must implement " +
                                        "OnFragmentInteractionListener" );
        }
    }
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments( ) != null ) {
            mUser = getArguments( ).getString( ARG_UTI );
            mPassword = getArguments( ).getString( ARG_MDP );
            mUrlBlog = getArguments( ).getString( ARG_URL );
            mNbRecentPosts = getArguments( ).getString( ARG_NBP );
            mOneArticle = ( OneArticle ) getArguments( ).getSerializable( ARG_ONE );
        }
    }
    
    @Override
    public View onCreateView( LayoutInflater inflater, final ViewGroup container, Bundle
            savedInstanceState ) {
        View v = inflater.inflate( R.layout.fragment_add, container, false );
        pg = v.findViewById( R.id.progressbar );
        etEnTete = v.findViewById( R.id.entete );
        etArticle = v.findViewById( R.id.article );
        etTitre = v.findViewById( R.id.titre );
        btEnvoyer = v.findViewById( com.androdot.R.id.envoyer );
        btAddFile = v.findViewById( R.id.btAddFile );
        btRaz = v.findViewById( com.androdot.R.id.btRaz );
        cbPublier = v.findViewById( R.id.publier );
        cbCommentaires = v.findViewById( R.id.commentaires );
        cbRetroLiens = v.findViewById( R.id.retroLiens );
        actCategorie = v.findViewById( com.androdot.R.id.categorie );
        btUpdate = v.findViewById( com.androdot.R.id.btMaj );
        btUpdate.setOnClickListener( handlerUpdate );
        Updater u = new Updater( getActivity( ) );
        adapter = new ArrayAdapter<>( getActivity( ), android.R.layout.simple_dropdown_item_1line,
                                      u.getAdapterCategories( ) );
        adapter.setNotifyOnChange( true );
        adapter.notifyDataSetChanged( );
        actCategorie.setAdapter( adapter );
        if ( mOneArticle != null ) {
            etTitre.setText( mOneArticle.getTitre( ) );
            etEnTete.setText( mOneArticle.getEntete( ) );
            etArticle.setText( mOneArticle.getArticle( ) );
            actCategorie.setText( mOneArticle.getCategorie( ) );
            mPostId = mOneArticle.getId( );
            if ( mOneArticle.getLien( ).equals( "1" ) ) {
                cbRetroLiens.setChecked( true );
            }
            if ( mOneArticle.getCommentaires( ).equals( "1" ) ) {
                cbCommentaires.setChecked( true );
            }
            if ( mOneArticle.getPublier( ).equals( "1" ) ) {
                cbPublier.setChecked( true );
            }
        }
        btRaz.setOnClickListener( handlerRaz );
        btAddFile.setOnClickListener( handlerAddFile );
        btEnvoyer.setOnClickListener( handlerSendArticle );
        return v;
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        ( ( AppCompatActivity ) getActivity( ) ).getSupportActionBar( ).setSubtitle( R.string
                                                                                             .app_subtitle_add );
        ( ( AppCompatActivity ) getActivity( ) ).getSupportActionBar( ).setTitle( R.string
                                                                                          .app_name );
    }
    
    @Override
    public void onDetach( ) {
        super.onDetach( );
        mListener = null;
    }
    
    public interface OnFragmentInteractionListener {
        
        void onAddItem( String msg );
        
        void update( );
    }
}
