package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Layout es el canvas, implementa una funcion para retornar una coordenada para cada vertice en el grafo. 
 * @author manuel
 */
public class SNLayout<V extends SNNode, E extends SNEdge> extends StaticLayout<V, E> {

    public Logger logger = Logger.getLogger(SNLayout.class);
    
    public SNLayout(Graph<V, E> graph) {
        super(graph);
    }
    
    @Override
    /**
     * JUNG no trabaja con Layouts dinámicos, por lo que sobreescribo este 
     * método para que el layout se calcule cada vez que algo ocurre.
     */public Dimension getSize() {
        super.getSize();
        Collection<SNNode> vertices = (Collection<SNNode>) this.getGraph().getVertices();
        Iterator<SNNode> it = vertices.iterator();
        int minWidth = 0;
        int minHeight = 0;
        int width = 500;
        int height = 300;
        while (it.hasNext()) {
            SNNode n = it.next();
            width = Math.max(width, n.getPosition().x);
            height = Math.max(height, n.getPosition().y);
            minWidth = Math.min(minWidth, n.getPosition().x);
            minHeight = Math.min(minHeight, n.getPosition().y);
        }
        return new Dimension(width + 300 - minWidth, height + 300 - minHeight);
    }

}
