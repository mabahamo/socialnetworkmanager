/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNEdge;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author manuel
 */
public class SNEdgeLabeler implements Transformer<SNEdge,String>{

    boolean show;
    
    public SNEdgeLabeler(boolean show) {
        this.show = show;
    }

    public String transform(SNEdge edge) {
        if (show)
            return edge.getLabel();
        else
            return "";
    }

}
