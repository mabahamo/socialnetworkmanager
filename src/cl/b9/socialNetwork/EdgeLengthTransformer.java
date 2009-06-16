/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.model.SNEdge;

/**
 *
 * @author manuel
 */
class EdgeLengthTransformer implements org.apache.commons.collections15.Transformer<SNEdge,Integer>{

    public EdgeLengthTransformer() {
    }

    public Integer transform(SNEdge arg0) {
        int aux = arg0.getLabel().length()*30 - arg0.getRelation().getParticipants().size()*8;
        int min = arg0.getLabel().length()*8;
        if (aux < min){
            return min;
        }
        else {
            return aux;
        }
    }

}
