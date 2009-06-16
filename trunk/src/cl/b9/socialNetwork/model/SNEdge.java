/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.b9.socialNetwork.model;

import java.awt.Color;

/**
 * Representación de un arco en JUNG. Un arco puede representar una relaci&oacute;n en una red social, o una participaci&oacute;n en una relaci&oacute;n
 * @author manuel
 */
public class SNEdge {

    String label;
    private SNRelation relation;


    public SNEdge(String label,SNRelation relation) {
        this.label = label;
        this.relation = relation;
    }
    
    public SNRelation getRelation(){
        return this.relation;
    }

    public void setColor(Color color) {
        relation.setColor(color);
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return "Edge: " + label;
    }

}
