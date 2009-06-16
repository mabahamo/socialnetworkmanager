package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.model.SNNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.apache.commons.collections15.Transformer;

public class SNVertexSizeFunction<V> implements Transformer<V, Integer> {

    int size;
    VisualizationViewer vv;

    public SNVertexSizeFunction(Integer size, VisualizationViewer vv) {
        this.size = size;
        this.vv = vv;
    }

    public Integer transform(V v) {
        if (v instanceof Graph) {
            return 30;
        }
        if (v instanceof SNNode){
            int input = vv.getGraphLayout().getGraph().getInEdges(v).size();
            int output = vv.getGraphLayout().getGraph().getOutEdges(v).size();
            return Math.min(20 + (input+output-1)*2,50);
        }
        return size;
    }
}


