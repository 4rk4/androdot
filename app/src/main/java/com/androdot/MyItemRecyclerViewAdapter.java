/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package com.androdot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androdot.ItemFragment.OnItemFragmentInteractionListener;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter< MyItemRecyclerViewAdapter
        .ViewHolder > {
    
    private final List< OneArticle >                mValues;
    private final OnItemFragmentInteractionListener mListener;
    
    public MyItemRecyclerViewAdapter( List< OneArticle > items, OnItemFragmentInteractionListener
            listener ) {
        mValues = items;
        mListener = listener;
    }
    
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext( ) ).inflate( R.layout.fragment_item,
                                                                         parent, false );
        return new ViewHolder( view );
    }
    
    @Override
    public void onBindViewHolder( final ViewHolder holder, int position ) {
        StringBuilder sb = new StringBuilder( );
        sb.append( "N° " );
        sb.append( mValues.get( position ).getId( ) );
        sb.append( " - " );
        sb.append( mValues.get( position ).getDateCreated( ) );
        holder.mItem = mValues.get( position );
        holder.mContentView.setText( mValues.get( position ).getTitre( ) );
        holder.mDateCreated.setText( sb.toString( ) );
        holder.mCategorie.setText( mValues.get( position ).getCategorie( ) );
        holder.mView.setOnClickListener( new View.OnClickListener( ) {
            
            @Override
            public void onClick( View v ) {
                if ( null != mListener ) {
                    mListener.clickOnItem( holder.mItem );
                }
            }
        } );
    }
    
    @Override
    public int getItemCount( ) {
        return mValues.size( );
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        public final  View       mView;
        public final  TextView   mContentView;
        public final  TextView   mDateCreated;
        private final TextView   mCategorie;
        public        OneArticle mItem;
        
        public ViewHolder( View view ) {
            super( view );
            mView = view;
            mContentView = view.findViewById( R.id.content );
            mDateCreated = view.findViewById( R.id.dateCreated );
            mCategorie = view.findViewById( R.id.categorie );
        }
        
        @Override
        public String toString( ) {
            return super.toString( ) + " '" + mContentView.getText( ) + "'";
        }
    }
}
