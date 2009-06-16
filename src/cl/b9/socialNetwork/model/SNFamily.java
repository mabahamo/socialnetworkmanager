/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.b9.socialNetwork.model;

import java.awt.Color;

/**
 * Familia de actores. Cada familia comparte el mismo color y las mismas relaciones.
 * @author manuel
 */
public class SNFamily  {

    public static final int ACTOR = 1;
    public static final int RELATION = 2;
    private String label;
    private Color color;
    private int familyType = -1;

    public SNFamily(String label, int familyType) {
        this.label = label;
        int r = (int) (Math.random() * 255);
        int g = (int) (Math.random() * 255);
        int b = (int) (Math.random() * 255);
        this.color = new Color(r, g, b);
        this.familyType = familyType;
    }

    public Color getColor() {
        return this.color;
    }
    
    public void setColor(Color c){
        this.color = c;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    public String getLabel(){
        return this.label;
    }

    public void setLabel(String label){
        this.label = label;
    }
    

}
