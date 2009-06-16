/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.Color;
import java.awt.geom.Point2D;

/**
 *
 * @author manuel
 */
public class SNVertexRenderer extends BasicVertexRenderer<SNNode, SNEdge>{

    @Override
    public void paintVertex(RenderContext<SNNode,SNEdge> rc, Layout<SNNode,SNEdge> layout, SNNode vertex) {
        if (rc.getPickedVertexState().isPicked(vertex)){
            GraphicsDecorator g = rc.getGraphicsContext();
            Point2D p = layout.transform(vertex);
            p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
            int x = (int)p.getX();
            int y = (int)p.getY();
            g.setColor(Color.CYAN);
            g.fillOval(x-15,y-15,30,30);
            super.paintVertex(rc, layout, vertex);
        }else {
            super.paintVertex(rc, layout, vertex);
        }
    }
    
}
