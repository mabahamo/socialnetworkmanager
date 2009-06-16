/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import java.awt.Color;
import java.awt.Point;

/**
 * Interfaz para un nodo en JUNG. En una red social un nodo puede representar un actor o una relaci&oacute;n
 * @author manuel
 */
public interface SNNode {
    public Point getPosition();
    public Color getColor();
    public String getLabel();
    public void setColor(Color c);
    public void setLabel(String label);
    public void setPosition(int x, int y);
    
    /**
     * Retorna la id que posee este objeto en la base de datos
     */public int getId();
}
