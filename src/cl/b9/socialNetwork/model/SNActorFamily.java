/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

/**
 *
 * @author manuel
 */
public class SNActorFamily extends Observable implements Serializable{

    private String name;
    private Color color;
    private int id;
    
    public SNActorFamily(int id, String name, Color color){
        this.name = name;
        this.color = color;
        this.id = id;
    }

   public SNActorFamily(String name, Color color){
        this.name = name;
        this.color = color;
        this.id = -1;
   }

   public boolean equals(SNActorFamily other){
       return this.getName().equals(other.getName());
   }

    
    public Color getColor(){
        return this.color;
    }
    
    public String getName(){
        return this.name;
    }

  

    public void setColor(Color color) {
        this.color = color;
        this.setChanged();
        this.notifyObservers();
    }

    public void setName(String name) {
        this.name = name;
        this.setChanged();
        this.notifyObservers();
    }
    
    @Override
     public String toString(){
        return this.name;
    }

    /**
     * Identidad de este objeto en la base de datos
     * @param identity
     */
    public void setId(int identity) {
        this.id = identity;
    }

    public int getId(){
        return this.id;
    }


}
