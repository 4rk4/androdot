/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */

package com.androdot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView
                                                                       .OnNavigationItemSelectedListener, ItemFragment.OnItemFragmentInteractionListener,
                                                               AddFragment.OnFragmentInteractionListener, Updater.OnUpdateListener {
    
    private static final int             CODE_PREF  = 12;
    private              FragmentManager fm;
    private              String          mUser;
    private              String          mUrlBlog;
    private              String          mPassword;
    private              String          mNumberRecentPost;
    private              Updater         mUpdater;
    private              boolean         notUpdated = true;
    
    @SuppressLint( "ResourceType" )
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        mUser = preferences.getString( "prefNom", "" );
        mUrlBlog = preferences.getString( "prefUrl", "" );
        mPassword = preferences.getString( "prefPassword", "" );
        mNumberRecentPost = preferences.getString( "prefNbArticles", "10" );
        if ( mUser.equals( "" ) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setMessage( R.string.dialog_message ).setTitle( R.string.dialog_title );
            builder.setPositiveButton( R.string.dialog_ok, new DialogInterface.OnClickListener( ) {
                
                public void onClick( DialogInterface dialog, int id ) {
                    startActivityForResult( new Intent( getApplicationContext( ), SettingsActivity
                            .class ), CODE_PREF );
                }
            } );
            AlertDialog dialog = builder.create( );
            dialog.show( );
        } else {
            mUpdater = new Updater( this );
        }
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string
                .navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState( );
        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
        fm = getSupportFragmentManager( );
        fm.beginTransaction( ).replace( R.id.mainFrag, AddFragment.newInstance( mUser, mPassword,
                                                                                mUrlBlog, mNumberRecentPost ) ).commit( );
    }
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater( ).inflate( R.menu.main, menu );
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId( );
        if ( id == R.id.action_settings ) {
            startActivityForResult( new Intent( getApplicationContext( ), SettingsActivity.class ),
                                    CODE_PREF );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }
    
    @Override
    public boolean onNavigationItemSelected( MenuItem item ) {
        int id = item.getItemId( );
        if ( id == R.id.nav_send ) {
            String   sujet  = getResources( ).getString( R.string.contact_subject );
            String[] email  = { getResources( ).getString( R.string.contact_email_ArkaSoftWare ) };
            String   titre  = getResources( ).getString( R.string.contact_title );
            Intent   intent = new Intent( Intent.ACTION_SEND );
            intent.setType( "text/html" );
            intent.putExtra( Intent.EXTRA_EMAIL, email );
            intent.putExtra( Intent.EXTRA_SUBJECT, sujet );
            startActivity( Intent.createChooser( intent, titre ) );
        } else if ( id == R.id.nav_mod ) {
            fm.beginTransaction( ).replace( R.id.mainFrag, ItemFragment.newInstance( mUpdater
                                                                                             .getListArticles( ) ) ).addToBackStack( null ).commit( );
        } else if ( id == R.id.nav_add ) {
            fm.beginTransaction( ).replace( R.id.mainFrag, AddFragment.newInstance( mUser,
                                                                                    mPassword, mUrlBlog, mNumberRecentPost ) ).addToBackStack( null ).commit( );
        }
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
    
    @Override
    public void clickOnItem( OneArticle item ) {
        fm.beginTransaction( ).replace( R.id.mainFrag, AddFragment.newInstance( mUser, mPassword,
                                                                                mUrlBlog, mNumberRecentPost, item ) ).addToBackStack( null ).commit( );
    }
    
    @SuppressLint( "ResourceType" )
    @Override
    public void onAddItem( String msg ) {
        Snackbar.make( findViewById( R.id.mainFrag ), msg, Snackbar.LENGTH_LONG ).show( );
        update( );
    }
    
    public boolean isNetworkConnected( ) {
        ConnectivityManager cm = ( ConnectivityManager ) getSystemService( Context
                                                                                   .CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo( );
        if ( activeNetwork != null && activeNetwork.isConnectedOrConnecting( ) ) {
            return true;
        } else {
            Snackbar.make( findViewById( R.id.mainFrag ), getResources( ).getString( R.string
                                                                                             .msgConnctedError ), Snackbar.LENGTH_LONG ).show( );
            return false;
        }
    }
    
    @Override
    public void update( ) {
        if ( isNetworkConnected( ) ) {
            mUpdater = new Updater( this, mUser, mPassword, mUrlBlog, mNumberRecentPost );
            mUpdater.updateArticles( );
            mUpdater.updateCategories( );
        }
    }
    
    @Override
    public void onUpdateItem( ) {
        Snackbar.make( findViewById( R.id.mainFrag ), getResources( ).getString( R.string.msgUpdate
                                                                               ), Snackbar
                               .LENGTH_LONG ).show( );
    }
    
    @Override
    public void onUpdateItemError( ) {
        Snackbar.make( findViewById( R.id.mainFrag ), getResources( ).getString( R.string
                                                                                         .msgUpdateError ), Snackbar.LENGTH_LONG ).show( );
    }
    
    @Override
    public void onUpdateItemServerError( ) {
        Snackbar.make( findViewById( R.id.mainFrag ), getResources( ).getString( R.string
                                                                                         .msgUpdateError ), Snackbar.LENGTH_LONG ).show( );
    }
    
    @Override
    public void onUpdateCategorie( ) {
    }
    
    @Override
    public void onUpdateCategorieError( ) {
    }
    
    @Override
    public void onUpdateCategorieServerError( ) {
    }
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == CODE_PREF ) {
            if ( resultCode == RESULT_OK ) {
                if ( isNetworkConnected( ) && isAuth( ) ) {
                    mUpdater = new Updater( this, mUser, mPassword, mUrlBlog, mNumberRecentPost );
                    mUpdater.updateArticles( );
                    mUpdater.updateCategories( );
                }
            }
        }
    }
    
    /**
     * @// TODO: 08/01/18 Verify if user is authenticate
     */
    private boolean isAuth( ) {
        return false;
    }
    
    @Override
    public void onBackPressed( ) {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed( );
        }
    }
}
