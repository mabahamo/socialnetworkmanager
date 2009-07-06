/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.Color;
import java.awt.Point;
import java.util.Observable;

/**
 *
 * @author manuel
 */
public class SNActor extends Observable implements SNNode {

    private int familyId;

    private String label;
    private int id = -1;
    private Point point = new Point(0,0);

    public SNActor(SNActorFamily family, String label){
        this.familyId = family.getId();
        this.label = label;
    }

    public SNActor(int familyId, String label){
        this.familyId = familyId;
        this.label = label;
    }
    
    /**
     * Database id for this object
     * @param id
     */public void setId(int id){
        this.id = id;
    }
     
     /**
      * Retorna la id de este objeto en la base de datos
      * @return unique id 
      */public int getId(){
         return this.id;
     }

    public void setPosition(int x, int y) {
        if (this.point != null && this.point.x == x && this.point.y == y){
            //no hay cambios
            return;
        }
        this.point.x = x;
        this.point.y = y;
        this.setChanged();
        this.notifyObservers();
    }
    
    public Point getPosition(){
        return this.point;
    }


    @Override
    public String toString() {
        return "Node: " + label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
        this.setChanged();
        this.notifyObservers();
    }

    public Color getColor(){
        return getFamily().getColor();
    }

    public SNActorFamily getFamily() {
        SNActorFamily family = ObjectManager.getInstance().getFamily(this.familyId);
        return family;
    }

    public Object getIcon(){
        return null;
    }

    
    
  
}
