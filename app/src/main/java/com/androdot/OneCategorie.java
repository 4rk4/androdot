/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package com.androdot;

import java.io.Serializable;

public class OneCategorie implements Serializable {
    
    private String  categoryId   = "";
    private String  categoryName = "";
    private boolean isPrimary    = false;
    
    public OneCategorie( String categoryId, String categoryName, boolean isPrimary ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.isPrimary = isPrimary;
    }
    
    public OneCategorie( String categoryId ) {
        this.categoryId = categoryId;
    }
    
    public OneCategorie( ) {
    }
    
    public String getCategoryId( ) {
        return categoryId;
    }
    
    public void setCategoryId( String categoryId ) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName( ) {
        return categoryName;
    }
    
    public void setCategoryName( String categoryName ) {
        this.categoryName = categoryName;
    }
    
    public boolean isPrimary( ) {
        return isPrimary;
    }
    
    public void setPrimary( boolean primary ) {
        isPrimary = primary;
    }
    
    @Override
    public String toString( ) {
        return categoryId + ":" + categoryName;
    }
}