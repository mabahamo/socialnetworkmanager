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
        //Dimension generic = super.getSize();
        Collection<SNNode> vertices = (Collection<SNNode>) this.getGraph().getVertices();
        Iterator<SNNode> it = vertices.iterator();
        int minX = 0;
        int minY = 0;
        int maxX = 500;
        int maxY = 300;
        while (it.hasNext()) {
            SNNode n = it.next();
            maxX = Math.max(maxX, n.getPosition().x);
            maxY = Math.max(maxY, n.getPosition().y);
            minX = Math.min(minX, n.getPosition().x);
            minY = Math.min(minY, n.getPosition().y);
        }
        Dimension d = new Dimension(maxX -minX  + 300 , maxY -minY + 300 );
      
       return d;
       
    }

}
