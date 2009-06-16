/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.model.Participant;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNRelation;
import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author manuel
 */
public class RelationMenu extends JPopupMenu{
    
    public RelationMenu(SNEdge edge,SNActor actor){
        super("Relation Menu");
        SNRelation relation = edge.getRelation();
        Vector<Participant> p = relation.getParticipants();
        this.add(new NewRelationItem(relation,actor));
        this.add(new JSeparator());
    }
    
    public RelationMenu(SNActor actor, SNRelation relation){
        super("Relation Menu");
        this.add(new JoinRelation(actor,relation));
        
    }
    
    public RelationMenu(SNActor source, SNActor dest) {
        super("Relation Menu");
        this.add(new NewRelationItem(source,dest));
        this.add(new JSeparator());
        Collection<String> relationsFamily = ObjectManager.getInstance().getRelationsFamily(source.getActorType(),dest.getActorType());
        Iterator<String> it = relationsFamily.iterator();
        while(it.hasNext()){
            this.add(new RelationItem(it.next(),source,dest));
        }
    }
    
    private class RelationItem extends JMenuItem {

      
        private RelationItem(final String relationFamily,final SNActor actor1, final SNActor actor2) {
            super(relationFamily);
            this.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    try {
                        SNDirector.getInstance().createRelation(relationFamily, actor1, actor2);
                    } catch (Exception ex) {
                        Popup.showError(null, ex.getLocalizedMessage());
                        Logger.getLogger(RelationMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                }
                
            });
        }
  
        
    }
    
    private class JoinRelation extends JMenuItem{
        private JoinRelation(final SNActor actor, final SNRelation relation){
            super("Participar en relación " + relation.getLabel());
            this.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    String rol = JOptionPane.showInputDialog("Rol de " + actor.getLabel() + " en esta relación?");
                    if (rol != null){
                        if (rol.length() == 0){
                            JOptionPane.showMessageDialog(null, "Error, rol no puede ser vacio", "Error", JOptionPane.ERROR_MESSAGE);
                        }else {
                            try {
                                SNDirector.getInstance().addParticipant(actor, rol, relation);
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Error al agregar participante en la relación", "Error", JOptionPane.ERROR_MESSAGE);
                                Logger.getLogger(RelationMenu.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }
                }
            });
        }
        
    }
    
    private class NewRelationItem extends JMenuItem {
        private NewRelationItem(SNRelation relation,SNActor actor){
            super("Crear nuevo rol");
            this.addActionListener(new RelationItemActionListener2(relation,actor));
        }
        private NewRelationItem(SNActor source,SNActor dest){
            super("Crear nuevo tipo de relación");
            this.addActionListener(new RelationItemActionListener1(source,dest));
        }
        
        
        private class RelationItemActionListener1 implements ActionListener{
            private SNActor source,dest;
            public RelationItemActionListener1(SNActor source, SNActor dest){
                this.source = source;
                this.dest = dest;
            }
            public void actionPerformed(ActionEvent e){
                 RelationDialog nrd = new RelationDialog(null,source,dest);
                 nrd.setVisible(true);
            }
            
        }

        private class RelationItemActionListener2 implements ActionListener{
            private SNRelation relation;
            private SNActor actor;
            public RelationItemActionListener2(SNRelation relation, SNActor actor){
                this.relation = relation;
                this.actor = actor;
            }
            public void actionPerformed(ActionEvent e){
                 NewRoleDialog nrd = new NewRoleDialog(null,relation,actor);
                 nrd.setVisible(true);
            }
            
        }
        
        
        
        
    }


}
