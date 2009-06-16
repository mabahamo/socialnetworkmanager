/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.gui.*;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNRelation;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import java.awt.Shape;

public class SNShape<V> extends EllipseVertexShapeTransformer<V> {
    
    public static enum Type {
        TRIANGLE, RECTANGLE, STAR, CIRCLE
    }
    
    
    public SNShape(VisualizationViewer vv) {
        setSizeTransformer(new SNVertexSizeFunction<V>(20,vv));
    }

    @Override
    public Shape transform(V v) {
        if (v instanceof Graph) {
            int size = ((Graph) v).getVertexCount();
            if (size < 8) {
                int sides = Math.max(size, 3);
                return factory.getRegularPolygon(v, sides);
            } else {
                return factory.getRegularStar(v, size);
            }
        }
        if (v instanceof SNRelation){
            return factory.getRectangle(v);
        }
        if (v instanceof SNActor){
            
            return factory.getEllipse(v);
            
        }
        return super.transform(v);
    }
}


