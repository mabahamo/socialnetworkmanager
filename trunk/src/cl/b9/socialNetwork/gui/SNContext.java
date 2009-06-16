/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.GraphPanel;

/**
 * @deprecated 
 * @author manuel
 */
class SNContext {

    private static SNContext instance;
    private GraphPanel g;
    
    private SNContext(){

    }

    public static SNContext getInstance(){
        if (instance == null){
            instance = new SNContext();
        }
        return instance;
    }
    
    public void setMainGraph(GraphPanel g){
        this.g = g;
    }
    
  
    
}
