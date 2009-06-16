/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.Color;
import java.awt.Point;
import java.util.Observable;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
public class SNActor extends Observable implements SNNode {

    private String familyId;

    private String label;
    private int id = -1;
    private ImageIcon imageIcon;
    private Point point = new Point(0,0);
    private Logger logger = Logger.getLogger(SNActor.class);
    private Color color;

    public SNActor(String family, String label){
        this.familyId = family;
        this.label = label;
        this.color = getFamily().getColor();
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

    public void setImageIcon(ImageIcon imageIcon) {
        if (imageIcon != null){
            logger.debug("Attaching image to actor");
        }
        this.imageIcon = imageIcon;
    }
    
    public Icon getIcon(){
        if (imageIcon == null){
            return null;
        }
        return imageIcon;
    }


    public void setPosition(int x, int y) {
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

    public String getActorType() {
        return this.familyId;
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
        this.setChanged();
        this.notifyObservers();
    }

    private SNActorFamily getFamily() {
        SNActorFamily family = ObjectManager.getInstance().getFamily(this.familyId);
        return family;
    }

    
    
  
}
