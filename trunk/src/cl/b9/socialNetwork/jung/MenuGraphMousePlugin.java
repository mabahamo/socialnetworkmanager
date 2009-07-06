/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.gui.ActorDialog;
import cl.b9.socialNetwork.gui.ActorFamilyDialog;
import cl.b9.socialNetwork.gui.RelationDialog;
import cl.b9.socialNetwork.gui.Popup;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import cl.b9.socialNetwork.persistence.ObjectManager;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Este plugin se encarga de presentar los menus contextuales
 * @author manuel
 */
class MenuGraphMousePlugin extends AbstractPopupGraphMousePlugin {

    private static Logger logger = Logger.getLogger(MenuGraphMousePlugin.class);

    public MenuGraphMousePlugin() {
    }

    @Override
    protected void handlePopup(MouseEvent e) {
        VisualizationViewer<SNNode,SNEdge> vv =(VisualizationViewer<SNNode,SNEdge>)e.getSource();
        Layout<SNNode,SNEdge> layout = vv.getGraphLayout();
        Graph<SNNode,SNEdge> graph = layout.getGraph();
        Point p = e.getPoint();     
        JPopupMenu popup = new JPopupMenu();
        GraphElementAccessor<SNNode,SNEdge> pickSupport = vv.getPickSupport();
        SNNode node = pickSupport.getVertex(layout, p.getX(),p.getY());
        SNEdge edge = pickSupport.getEdge(layout, p.getX(),p.getY());
        if (node == null && edge == null){
            //click en el canvas
            popup.add(new FamilyActorMenuItem());
            Collection<SNActorFamily> actorFamilies = ObjectManager.getInstance().refreshActorFamilies();
            if (actorFamilies.size()>0){
                popup.add(new JSeparator());
            }
            Iterator<SNActorFamily> it = actorFamilies.iterator();
            while(it.hasNext()){
                logger.debug("Click en " + p);
                logger.debug("Inverse transform " + SNDirector.getInstance().inverseTransform(p));
                popup.add(new ActorMenuItem(it.next(),layout,SNDirector.getInstance().inverseTransform(p)));
            }
        }
        if (node != null){
            //click sobre un nodo
            popup.add(new EditNodeItem(node));
            popup.add(new DeleteNodeItem(node));
        }
        else if (edge != null){
            //click sobre un arco
            popup.add(new EditNodeItem(edge.getRelation()));
            popup.add(new DeleteNodeItem(edge));
        }
        
        if(popup.getComponentCount() > 0) {
            popup.show(vv, e.getX(), e.getY());
        }
    }

    private class FamilyActorMenuItem extends JMenuItem {
        public FamilyActorMenuItem(){
            super("Crear nueva familia de actores");
            this.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent arg0) {
                    (new ActorFamilyDialog()).setVisible(true);
                }
            });
    }
    }
    
    private class ActorMenuItem extends JMenuItem {
         public ActorMenuItem(final SNActorFamily family,final Layout layout, final Point2D p){
             super("Crear actor de la familia " + family);
             this.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent e){
                     ActorDialog d = new ActorDialog(family,p);
                     d.setVisible(true);
            }
             });
         }     
     }

    public class DeleteNodeItem extends JMenuItem{
           public DeleteNodeItem(final SNNode node){
               super("Eliminar " + node);
               if (node instanceof SNRelation){
                   this.setText("Eliminar relación : " + node);
               }
               if (node instanceof SNActor){
                   this.setText("Eliminar actor : " + node);
               }
               this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    int r = JOptionPane.showConfirmDialog(null, "¿Estás que deseas eliminar permanentemente " + node + "?","Eliminar nodo", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION){
                        try {
                            SNDirector.getInstance().remove(node);
                        } catch (SQLException ex) {
                            Popup.showError(null, "Error al eliminar nodo \n" + ex.getLocalizedMessage());
                            logger.warn(ex);
                        }
                    }
                }
            });
           }
           
           public DeleteNodeItem(final SNEdge edge){
               super("Eliminar relacion " + edge.getRelation());
               this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    int r = JOptionPane.showConfirmDialog(null, "¿Estás que deseas eliminar permanentemente " + edge + "?","Eliminar nodo", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION){
                        try {
                            SNDirector.getInstance().remove(edge);
                        } catch (SQLException ex) {
                            Popup.showError(null, "Error al eliminar nodo \n" + ex.getLocalizedMessage());
                           logger.warn(ex);
                        }
                    }
                }
            });
           }

       }
        
        
       private class EditNodeItem extends JMenuItem{
           public EditNodeItem(final SNNode node){
               super("Editar");
               if (node instanceof SNRelation){
                   this.setText("Editar relación : " + node);
               }
               if (node instanceof SNActor){
                   this.setText("Editar actor : " + node);
               }
                this.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if (node instanceof SNActor){
                        ActorDialog d = new ActorDialog((SNActor)node);
                        d.setVisible(true);
                    }
                    if (node instanceof SNRelation){
                        RelationDialog d = new RelationDialog((SNRelation)node);
                        d.setVisible(true);
                    }
                }
            });
           }
       }
    
}
