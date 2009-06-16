/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.gui.RelationMenu;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

/**
 * Esta clase se encarga de crear y dibujar los arcos
 * @author manuel
 */
class RelationsGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener, MouseMotionListener  {

    private Logger logger = Logger.getLogger(RelationsGraphMousePlugin.class);
    private SNNode startVertex;
    private VisualizationServer.Paintable edgePaintable,arrowPaintable;
    protected CubicCurve2D rawEdge = new CubicCurve2D.Float();
    protected Shape edgeShape,rawArrowShape,arrowShape;
    private VisualizationViewer<SNNode, SNEdge> vv;
    
    public RelationsGraphMousePlugin() {
        super(MouseEvent.BUTTON1_MASK );
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        edgePaintable = new EdgePaintable();
        arrowPaintable = new ArrowPaintable();
    }

        /**
     * overrided to be more flexible, and pass events with
     * key combinations. The default responds to both ButtonOne
     * and ButtonOne+Shift
     */
    @Override
    public boolean checkModifiers(MouseEvent e) {
        logger.debug("Event " + ((e.getModifiers() & modifiers) != 0));
        return (e.getModifiers() & modifiers) != 0;
    }
    
    public void mouseClicked(MouseEvent e) {

    
    }

    /**
     * If the mouse is pressed in an empty area, create a new vertex there.
     * If the mouse is pressed on an existing vertex, prepare to create
     * an edge from that vertex to another
     */

    public void mousePressed(MouseEvent e) {
        if (checkModifiers(e)) {
            vv = (VisualizationViewer<SNNode, SNEdge>) e.getSource();
            final Point2D p = e.getPoint();
            GraphElementAccessor<SNNode, SNEdge> pickSupport = vv.getPickSupport();
            if (pickSupport != null) {
                final SNNode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
                logger.debug("Vertex pressed " + vertex);
                if (vertex != null) { // get ready to make an edge
                    startVertex = vertex;
                    down = e.getPoint();
                    transformEdgeShape(down, down);
                    vv.addPostRenderPaintable(edgePaintable);
                    transformArrowShape(down, e.getPoint());
                    vv.addPostRenderPaintable(arrowPaintable);
                }
            }
            vv.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        vv = (VisualizationViewer<SNNode, SNEdge>) e.getSource();
        if (checkModifiers(e)) {
            logger.debug("check modifiers");
            final Point2D p = e.getPoint();
            Layout<SNNode, SNEdge> layout = vv.getModel().getGraphLayout();
            GraphElementAccessor<SNNode, SNEdge> pickSupport = vv.getPickSupport();
            if (pickSupport != null) {
                final SNNode endVertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                final SNEdge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                /**
                 * Actor a Actor
                 */if (endVertex != null && startVertex != null && endVertex instanceof SNActor && startVertex instanceof SNActor) {
                        logger.debug("Relacion Actor - Actor");
                        RelationMenu rm = new RelationMenu((SNActor)startVertex,(SNActor)endVertex);
                        rm.show(vv, e.getX(), e.getY());
                }
                /**
                 * Actor a una relacion nodificada
                 */else if (endVertex != null && startVertex != null && endVertex instanceof SNRelation && startVertex instanceof SNActor){
                    logger.debug("Relacion actor - relacion como nodo");
                    RelationMenu rm = new RelationMenu((SNActor)startVertex,(SNRelation)endVertex);
                    rm.show(vv,e.getX(),e.getY());
                }
                
                /**
                 * Actor a una relacion dibujada como arco.
                 */else if(edge != null && startVertex != null) {
                     logger.debug("Relacion actor - relacion como arco");
                     if (((SNEdge)edge).getRelation().getParticipants().size()==2){
                         RelationMenu rm = new RelationMenu((SNEdge)edge,(SNActor)startVertex);
                         rm.show(vv,e.getX(),e.getY());
                     }
                }
            }
        }
        startVertex = null;
        down = null;
        vv.removePostRenderPaintable(edgePaintable);
        vv.removePostRenderPaintable(arrowPaintable);
        vv.repaint();
    }
    
    public void reset(){
        if (vv == null)
            return;
        vv.removePostRenderPaintable(edgePaintable);
        vv.removePostRenderPaintable(arrowPaintable);
        vv.repaint();
    }

   /**
     * If startVertex is non-null, stretch an edge shape between
     * startVertex and the mouse pointer to simulate edge creation
     */
    public void mouseDragged(MouseEvent e) {
        if (checkModifiers(e)) {
            vv = (VisualizationViewer<SNNode, SNEdge>) e.getSource();
            if (startVertex != null) {
                transformEdgeShape(down, e.getPoint());
                transformArrowShape(down, e.getPoint());
            }            
            vv.repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {}

        /**
     * code lifted from PluggableRenderer to move an edge shape into an
     * arbitrary position
     */
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
        edgeShape = xform.createTransformedShape(rawEdge);
    }

    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);

        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        arrowShape = xform.createTransformedShape(rawArrowShape);
    }

    /**
     * Used for the edge creation visual effect during mouse drag
     */
    class EdgePaintable implements VisualizationServer.Paintable {

        public void paint(Graphics g) {
            if (edgeShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D) g).draw(edgeShape);
                g.setColor(oldColor);
            }
        }

        public boolean useTransform() {
            return false;
        }

    }

    /**
     * Used for the directed edge creation visual effect during mouse drag
     */
    class ArrowPaintable implements VisualizationServer.Paintable {
        public void paint(Graphics g) {
            if (arrowShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D) g).fill(arrowShape);
                g.setColor(oldColor);
            }
        }
        public boolean useTransform() {
            return false;
        }
    }

   
    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }



}
