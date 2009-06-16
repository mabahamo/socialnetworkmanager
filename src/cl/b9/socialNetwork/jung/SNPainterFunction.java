/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNRelation;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

/**
 * Esta clase se encarga de identificar con que color se debe pintar cada elemento, nodo o arco. Los colores cambian si es que el nodo o arco esta seleccionado
 * @author manuel
 */
public class SNPainterFunction<V> implements Transformer<V,Paint>{

    private VisualizationViewer vv;

    public SNPainterFunction(VisualizationViewer vv) {
        this.vv = vv;
    }
    
    public Paint transform(V v) {
        if (v instanceof SNRelation){
            SNRelation r = (SNRelation)v;
            return r.getColor();
        }
        if (v instanceof SNActor){
            SNActor actor = (SNActor)v;
            if (vv.getPickedVertexState().isPicked(v)){
                return Color.YELLOW;
            }
            return actor.getColor();
        }
        if (v instanceof SNEdge){
            SNEdge e = (SNEdge)v;
            if (vv.getPickedEdgeState().isPicked(v)){
                return Color.CYAN;
            }
            return e.getRelation().getColor();
        }
        
        return Color.BLACK;
    }


}
