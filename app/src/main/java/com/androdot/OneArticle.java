/**
 * @author 4rk4
 * @version 3.15
 * Licensed under the GPL version 3
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package com.androdot;

import java.io.Serializable;

public class OneArticle implements Serializable {
    
    private String       id           = "";
    private String       dateCreated  = "";
    private String       titre        = "";
    private String       article      = "";
    private String       entete       = "";
    private String       lien         = "";
    private String       commentaires = "";
    private String       publier      = "";
    private OneCategorie categorie    = new OneCategorie( );
    
    public OneArticle( String dateCreated, String id, String titre, String entete, String article,
                       String lien, String commentaires, String publier, OneCategorie categorie ) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.titre = titre;
        this.article = article;
        this.entete = entete;
        this.lien = lien;
        this.commentaires = commentaires;
        this.publier = publier;
        this.categorie = categorie;
    }
    
    public OneArticle( ) {
    }
    
    public String getId( ) {
        return id;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    
    public String getDateCreated( ) {
        return dateCreated;
    }
    
    public void setDateCreated( String dateCreated ) {
        this.dateCreated = dateCreated;
    }
    
    public String getTitre( ) {
        return titre;
    }
    
    public void setTitre( String titre ) {
        this.titre = titre;
    }
    
    public String getArticle( ) {
        return article;
    }
    
    public void setArticle( String article ) {
        this.article = article;
    }
    
    public String getEntete( ) {
        return entete;
    }
    
    public void setEntete( String entete ) {
        this.entete = entete;
    }
    
    public String getLien( ) {
        return lien;
    }
    
    public void setLien( String lien ) {
        this.lien = lien;
    }
    
    public String getCommentaires( ) {
        return commentaires;
    }
    
    public void setCommentaires( String commentaires ) {
        this.commentaires = commentaires;
    }
    
    public String getPublier( ) {
        return publier;
    }
    
    public void setPublier( String publier ) {
        this.publier = publier;
    }
    
    public String getCategorie( ) {
        return categorie.getCategoryId( );
    }
    
    public void setCategorie( OneCategorie categorie ) {
        this.categorie = categorie;
    }
    
    @Override
    public String toString( ) {
        return "OneArticle{" + "id='" + id + '\'' + ", dateCreated='" + dateCreated + '\'' + ", "
               + "titre='" + titre + '\'' + ", article='" + article + '\'' + ", entete='" +
               entete + '\'' + ", lien='" + lien + '\'' + ", commentaires='" + commentaires +
               '\'' + ", " + "publier='" + publier + '\'' + ", categorie=" + categorie + '}';
    }
}