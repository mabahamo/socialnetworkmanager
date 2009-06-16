/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.b9.socialNetwork;

import cl.b9.socialNetwork.jung.SNEdgeStrokeTransformer;
import cl.b9.socialNetwork.gui.SNVertexIconTransformer;
import cl.b9.socialNetwork.jung.SNPainterFunction;
import cl.b9.socialNetwork.jung.SNShape;
import cl.b9.socialNetwork.jung.SNLabeler;
import cl.b9.socialNetwork.jung.SNLayout;
import cl.b9.socialNetwork.jung.SNModalMouse;
import cl.b9.socialNetwork.jung.SNVertexRenderer;
import cl.b9.socialNetwork.model.Participant;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import cl.b9.socialNetwork.util.PolygonUtils;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.log4j.Logger;

/**
 * Encapsula la lógica de JUNG para trabajar con grafos, esta clase provee los métodos necesarios para crear actores y relaciones (y los convierte a nodos y arcos según corresponda)
 * @author manuel
 */
public class GraphPanel {

    private SparseMultigraph<SNNode, SNEdge> graph;
    private SNLayout<SNNode, SNEdge> layout;
    //private StaticLayout<SNNode, SNEdge> layout;
    private VisualizationViewer<SNNode, SNEdge> vv;
    private Frame frame;
    private Logger logger = Logger.getLogger(GraphPanel.class);
    //AbsoluteCrossoverScalingControl satelliteScaler = new AbsoluteCrossoverScalingControl();
    ScalingControl scaler = new CrossoverScalingControl();
    //ScalingControl scaler = new ViewScalingControl();
    private GraphZoomScrollPane pane;

    /**
     * Instancia un nuevo panel para dibujar un grafico
     * @param frame contenedor
     * @param width 
     * @param height 
     */public GraphPanel(Frame frame, int width, int height) {
        this.frame = frame;
        graph = new SparseMultigraph<SNNode, SNEdge>();
        //layout = new StaticLayout<SNNode, SNEdge>(graph, new Dimension(width, height));
        layout = new SNLayout<SNNode, SNEdge>(graph);

        logger.debug("Layout size " + layout.getSize());

        //SNMenuMousePlugin myPlugin = new SNMenuMousePlugin(this, frame, layout);
        //layout.setSize( );


        vv = new VisualizationViewer<SNNode, SNEdge>(layout, layout.getSize());

        logger.debug("Dimension " + vv.getSize());
        logger.debug("PreferredSize " + vv.getPreferredSize());


        SNLabeler snl = new SNLabeler();

        vv.getRenderContext().setVertexLabelTransformer(snl);
        vv.getRenderContext().setEdgeLabelTransformer(snl);
        vv.getRenderContext().setVertexShapeTransformer(new SNShape(vv));
        vv.getRenderContext().setVertexFillPaintTransformer(new SNPainterFunction<SNNode>(vv));
        vv.getRenderContext().setEdgeStrokeTransformer(new SNEdgeStrokeTransformer(vv));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new SNPainterFunction<SNEdge>(vv));
        vv.getRenderContext().setVertexIconTransformer(new SNVertexIconTransformer());

        vv.getRenderer().setVertexRenderer(new SNVertexRenderer());

        SNModalMouse modalMouse = new SNModalMouse();
        vv.setGraphMouse(modalMouse);
        // satellite = buildSatellite();
        pane = new GraphZoomScrollPane(vv);
        //     originalTransform = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
        this.repaint();

        vv.setBackground(Color.WHITE);
    // satelliteScaler.scale(satellite, 0.09f, satellite.getCenter());


    }
    private SatelliteVisualizationViewer<SNNode, SNEdge> sat;

    /**
     * Recupera la vista de satélite
     * @return
     */public SatelliteVisualizationViewer getSatellite() {
        if (sat == null) {
            sat = new SatelliteVisualizationViewer<SNNode, SNEdge>(vv, new Dimension(300, 300));
            sat.getRenderContext().setVertexShapeTransformer(new SNShape(vv));
            sat.getRenderContext().setVertexFillPaintTransformer(new SNPainterFunction(sat));
            sat.getRenderContext().setVertexIconTransformer(new SNVertexIconTransformer());
            //  sat.getRenderContext().getMultiLayerTransformer().setTransformer(Layer.LAYOUT, new MutableAffineTransformer(originalTransform));
            sat.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            sat.scaleToLayout(scaler);

        }
        return sat;
    }

    public void resizeSatellite(Dimension newSize, Dimension oldSize) {
        logger.debug("Resize Satellite:" + newSize);

        int dx = newSize.width - oldSize.width;
        int dy = newSize.height - oldSize.height;

        if (Math.abs(dx) > Math.abs(dy)) {
            float rw = (float) newSize.width / (float) oldSize.width;
            scaler.scale(sat, rw, sat.getRenderContext().getMultiLayerTransformer().inverseTransform(new Point(0, 0)));
        }//else {
        //   float rh = (float)newSize.height/(float)oldSize.height;
        // scaler.scale(sat, rh, sat.getRenderContext().getMultiLayerTransformer().inverseTransform(new Point(0,0)));            
        //}
        this.repaint();
    }

    public GraphZoomScrollPane getScrollPane() {
        return pane;
    }

    public void reset() {
        Collection<SNNode> v = graph.getVertices();
        logger.debug("Eliminado nodos : " + v.size());
        Object[] nodes = (Object[]) v.toArray();
        for (int i = 0; i < nodes.length; i++) {
            graph.removeVertex((SNNode) nodes[i]);
        }
        logger.debug("Eliminado arcos : " + v.size());
        Object[] edges = (Object[]) v.toArray();
        for (int i = 0; i < edges.length; i++) {
            graph.removeEdge((SNEdge) edges[i]);
        }

        layout.reset();

        vv.setGraphLayout(layout);
        this.repaint();
        System.gc();
    }

    public VisualizationViewer getViewer() {
        return this.vv;
    }

    /**
     * Inserta un actor en la posicion especificada
     * @param node
     */
    public void add(SNActor node) {
        layout.getGraph().addVertex(node);
        layout.setLocation(node, node.getPosition());
        this.repaint();
    }

    /**
     * Elimina el nodo desde la base de datos
     * @param node
     */
    public void remove(SNNode node) {
        if (node instanceof SNActor) {
            layout.getGraph().removeVertex(node);
        } else if (node instanceof SNRelation) {
            SNRelation r = (SNRelation) node;
            Collection<SNEdge> col = graph.getIncidentEdges(node);
            if (col == null) {
                // FIXME
                //layout.getGraph().removeEdge(((SNRelation)node).getEdge());
                } else {
                Iterator<SNEdge> it = col.iterator();
                while (it.hasNext()) {
                    layout.getGraph().removeEdge(it.next());
                }
            }
            layout.getGraph().removeVertex(node);
        } else {
            throw new UnsupportedOperationException("Remove " + node);
        }


        this.repaint();
    }

    /**
     * Por eficiencia modificamos el grafico existente para agregar una relacion ternaria. Lo simple sería decirle que se dibujara de nuevo desde la base de datos
     * @param relation
     * @param actor
     * @param role
     */
    public void add(SNRelation relation, SNActor actor, String role) {
        Vector<Participant> p = relation.getParticipants();
        if (p.size() == 2) {
            SNActor source = p.get(0).getActor();
            SNActor dest = p.get(1).getActor();
            Iterator<SNEdge> edges = layout.getGraph().getIncidentEdges(source).iterator();
            while (edges.hasNext()) {
                SNEdge e = edges.next();
                if (e.getLabel().equals(relation.getLabel()) && layout.getGraph().isSource(source, e) && layout.getGraph().isDest(dest, e)) {
                    layout.getGraph().removeEdge(e);
                }
            }
            layout.getGraph().addVertex(relation);
            SNEdge e = new SNEdge(p.get(0).getRole(), relation);
            SNEdge f = new SNEdge(p.get(1).getRole(), relation);
            SNEdge g = new SNEdge(role, relation);
            layout.getGraph().addEdge(e, p.get(0).getActor(), relation, EdgeType.DIRECTED);
            layout.getGraph().addEdge(f, p.get(1).getActor(), relation, EdgeType.DIRECTED);
            layout.getGraph().addEdge(g, actor, relation, EdgeType.DIRECTED);
            Polygon polygon = new Polygon();
            polygon.addPoint((int) layout.getX(p.get(0).getActor()), (int) layout.getY(p.get(0).getActor()));
            polygon.addPoint((int) layout.getX(p.get(1).getActor()), (int) layout.getY(p.get(1).getActor()));
            polygon.addPoint((int) layout.getX(actor), (int) layout.getY(actor));
            java.awt.Point point = PolygonUtils.polygonCenterOfMass(polygon);
            relation.setPosition(point.x, point.y);
            layout.setLocation(relation, point);

        }
        repaint();

    }

    public void add(SNRelation relation, boolean showBinaryRelationsAsNodes) {
        Vector<Participant> p = relation.getParticipants();
        logger.debug("Participantes en relacion " + p.size());
        if (p.size() == 2 && !showBinaryRelationsAsNodes) {
            //relaciones binarias no dibujan el
            SNEdge edge = new SNEdge(relation.getLabel(), relation);
            logger.debug("Participant from " + p.get(0).getActor());
            logger.debug("Participant to " + p.get(1).getActor());
            layout.getGraph().addEdge(edge, p.get(0).getActor(), p.get(1).getActor(), relation.getEdgeType());
        } else {
            layout.getGraph().addVertex(relation);
            layout.setLocation(relation, relation.getPosition());
            Iterator<Participant> it = p.iterator();
            while (it.hasNext()) {
                Participant p1 = it.next();
                SNEdge e = new SNEdge(p1.getRole(), relation);
                layout.getGraph().addEdge(e, p1.getActor(), relation, EdgeType.DIRECTED);
            }
        }

        this.repaint();
    }

    public Dimension getDimension() {
        Collection<SNNode> vertices = graph.getVertices();
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

    public void repaint() {
        //  originalTransform = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
        vv.setSize(this.getDimension());
        vv.getPickedEdgeState().clear();
        vv.getPickedVertexState().clear();
        //truco muy sucio para forzar la actualización de las barras de scroll
        pane.getComponentListeners()[0].componentResized(null);


        this.getViewer().updateUI();
        this.getViewer().repaint();

    //satellite.scaleToLayout(scaler);
    //    this.satellite.updateUI();
    //    this.satellite.repaint();
        
    }

    public SparseMultigraph<SNNode, SNEdge> getGraph() {
        return this.graph;
    }

    public Layout getLayout() {
        return this.vv.getGraphLayout();
    }

    /**
     * Centra la vista en el actor especificado, además lo deja seleccionado
     * @param actorId
     */
    void centerAtActor(SNActor actor) {
        Point2D q = layout.transform(actor);
        Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
        vv.getRenderContext().getPickedEdgeState().clear();
        vv.getRenderContext().getPickedVertexState().clear();
        vv.getRenderContext().getPickedVertexState().pick(actor, true);
        final double dx = (lvc.getX() - q.getX()) / 10;
        final double dy = (lvc.getY() - q.getY()) / 10;
        Runnable animator = new Runnable() {

            public void run() {
                for (int i = 0; i < 10; i++) {
                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        logger.warn(ex.getLocalizedMessage());
                    }
                }
            }
        };
        Thread thread = new Thread(animator);
        thread.start();
    }

    void changeLayout(Class layoutClass) {
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = Graph.class;
        
        
        try {
            Layout newLayout;

            if (layoutClass == SpringLayout.class){
                newLayout = new SpringLayout(graph, new EdgeLengthTransformer());
            }else {
                newLayout = (Layout) layoutClass.getConstructor(parameterTypes).newInstance(graph);            
            }
            newLayout.setGraph(this.getGraph());
            Dimension dv = vv.getGraphLayout().getSize();
            newLayout.setSize(getDimension());
            

            LayoutTransition<SNNode, SNEdge> lt = new LayoutTransition<SNNode, SNEdge>(vv, vv.getGraphLayout(), newLayout);
            Animator animator = new Animator(lt);
            animator.start();

            Collection<SNNode> c = this.getGraph().getVertices();
            Iterator<SNNode> it = c.iterator();
            while (it.hasNext()) {
                SNNode n = it.next();
                Point2D p = (Point2D) newLayout.transform(n);
                n.setPosition((int) p.getX(), (int) p.getY());
            }
            vv.repaint();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Retorna la cordenada inversa de un punto de acuerdo a su layout
     * @param point
     * @return
     */
    Point2D inverseTransform(Point point) {
        return vv.getRenderContext().getMultiLayerTransformer().inverseTransform(point);
    }

    void remove(SNEdge edge) {
        this.graph.removeEdge(edge);
    }

    void renameRelation(SNNode relation, String input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void setScale(float f) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
