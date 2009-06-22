/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNRelation;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author manuel
 */
public class SNLabeler implements Transformer {

    public String transform(Object arg0) {
        if (arg0 instanceof SNActor) {
            SNActor actor = (SNActor) arg0;
            if (SNDirector.getInstance().isDebugEnabled()){
                return actor.getLabel() + " <" + (int)actor.getPosition().getX() + "," + (int)actor.getPosition().getY() + ">";
            }
            else {
                return actor.getLabel();
            }
                
        }
        if (arg0 instanceof SNEdge){
            SNEdge edge = (SNEdge) arg0;
            return edge.getLabel();
        }
        if (arg0 instanceof SNRelation){
            SNRelation r = (SNRelation)arg0;
            return r.getLabel();
        }
        return "+";
    }
}
