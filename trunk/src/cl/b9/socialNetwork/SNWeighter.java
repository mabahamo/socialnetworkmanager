/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.model.SNEdge;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author manuel
 */
class SNWeighter implements Transformer<SNEdge,Integer>{

    public SNWeighter() {
        super();
    }
    

    public Integer transform(SNEdge arg0) {
        return 1;
    }
    
    
}
