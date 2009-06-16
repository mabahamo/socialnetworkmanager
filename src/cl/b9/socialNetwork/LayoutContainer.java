/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import edu.uci.ics.jung.algorithms.layout.Layout;

/**
 * Contenedor de un layout, asocia cada layout con un nombre y una descripcion
 * @author manuel
 */
public class LayoutContainer {

    private String name, description;
    private Class layout;
    
    /**
     * contenedor de layout.
     * @param name
     * @param description
     * @param layout clase que extiende de edu.uci.ics.jung.algorithms.layout.Layout
     */public LayoutContainer(String name, String description, Class<? extends Layout> layout){
        this.name = name;
        this.description = description;
        this.layout = layout;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public Class getLayout(){
        return this.layout;
    }
    
    
}
