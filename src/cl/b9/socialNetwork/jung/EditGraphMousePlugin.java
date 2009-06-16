/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.gui.ActorDialog;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import org.apache.log4j.Logger;

/**
 * Clase que permite editar los actores y las relaciones.
 * @author manuel
 */
class EditGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener{

   private Logger logger = Logger.getLogger(EditGraphMousePlugin.class);
    
   public EditGraphMousePlugin() {
        super(MouseEvent.BUTTON1_MASK);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2){
            logger.debug("Doble click " + e);
            VisualizationViewer<SNNode, SNEdge> vv = (VisualizationViewer<SNNode, SNEdge>) e.getSource();
            GraphElementAccessor<SNNode, SNEdge> pickSupport = vv.getPickSupport();
            Point2D p = e.getPoint();
            SNNode node = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
            if (node == null)
                return;
            ActorDialog ad = new ActorDialog(node);
            ad.setVisible(true);
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }


}
