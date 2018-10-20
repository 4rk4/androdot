/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package com.androdot;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

public class ItemFragment extends Fragment {
    
    private static final String                            ARG_COLUMN_COUNT = "column-count";
    private static final String                            ARG_ITEMS        = "items";
    private static       List< OneArticle >                ITEMS;
    private static       String                            utilisateur      = "";
    private static       String                            mdp              = "";
    private static       String                            urlBlog          = "";
    private static       String                            nbPost           = "";
    private              int                               mColumnCount     = 1;
    private              OnItemFragmentInteractionListener mListener;
    private              ProgressDialog                    progress;
    
    public ItemFragment( ) {
    }
    
    @SuppressWarnings( "unused" )
    public static ItemFragment newInstance( List< OneArticle > i ) {
        ItemFragment fragment = new ItemFragment( );
        Bundle       args     = new Bundle( );
        args.putSerializable( ARG_ITEMS, ( Serializable ) i );
        fragment.setArguments( args );
        return fragment;
    }
    
    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnItemFragmentInteractionListener ) {
            mListener = ( OnItemFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString( ) + " must implement " +
                                        "OnItemFragmentInteractionListener" );
        }
    }
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments( ) != null ) {
            ITEMS = ( List< OneArticle > ) getArguments( ).getSerializable( ARG_ITEMS );
        }
    }
    
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_item_list, container, false );
        if ( view instanceof RecyclerView ) {
            Context      context      = view.getContext( );
            RecyclerView recyclerView = ( RecyclerView ) view;
            if ( mColumnCount <= 1 ) {
                LinearLayoutManager llm = new LinearLayoutManager( context );
                recyclerView.setLayoutManager( llm );
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                        recyclerView.getContext( ), llm.getOrientation( ) );
                recyclerView.addItemDecoration( dividerItemDecoration );
            } else {
                recyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            recyclerView.setAdapter( new MyItemRecyclerViewAdapter( ITEMS, mListener ) );
        }
        return view;
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        ( ( AppCompatActivity ) getActivity( ) ).getSupportActionBar( ).setSubtitle( R.string
                                                                                             .app_subtitle_mod );
        ( ( AppCompatActivity ) getActivity( ) ).getSupportActionBar( ).setTitle( R.string
                                                                                          .app_name );
    }
    
    @Override
    public void onDetach( ) {
        super.onDetach( );
        mListener = null;
    }
    
    public interface OnItemFragmentInteractionListener {
        
        void clickOnItem( OneArticle item );
    }
}