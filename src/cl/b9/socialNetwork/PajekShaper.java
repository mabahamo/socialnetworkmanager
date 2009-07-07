/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNNode;
import java.util.HashMap;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Manuel
 */
class PajekShaper implements Transformer<SNNode,String>{


    private String[] availables = {"ellipse", "box", "diamond", "cross"};
    private HashMap<String, String> conversion = new HashMap<String,String>();

    public PajekShaper() {
    }

    public String transform(SNNode arg0) {
        if (arg0 instanceof SNActor){
            SNActor actor = (SNActor)arg0;
            String family = actor.getFamily().getName();
            if (!conversion.containsKey(family)){
                conversion.put(family, availables[conversion.size()%availables.length]);
            }
            return conversion.get(family);
        }
        return "cross";
    }

}
