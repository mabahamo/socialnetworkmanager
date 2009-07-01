/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNNode;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Manuel
 */
public class SNNodeLabeler  implements Transformer<SNNode,String>{

    boolean show;
    
    public SNNodeLabeler(boolean show) {
        this.show = show;
    }

    public String transform(SNNode node) {
        if (show)
            return node.getLabel();
        else
            return "";
    }
}
