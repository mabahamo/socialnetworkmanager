/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BasicStroke;
import java.awt.Stroke;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author manuel
 */
public class SNEdgeStrokeTransformer implements Transformer<SNEdge,Stroke>{
    final static float dash1[] = { 10.0f };
    final static BasicStroke dashed = new BasicStroke(1.0f,
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    final static BasicStroke normal = new BasicStroke();
    public SNEdgeStrokeTransformer(VisualizationViewer<SNNode, SNEdge> vv) {
        
    }

    public Stroke transform(SNEdge arg0) {
       return normal;
        
    }


}
