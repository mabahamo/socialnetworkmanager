/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.model.SNActorFamily;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Clase para interactuar con la tabla de familias
 * @author manuel
 */
class TableMouseListener extends MouseAdapter {
   // private Logger logger = Logger.getLogger(TableMouseListener.class);
    
    public TableMouseListener() {
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON3){
            JPopupMenu popup = new JPopupMenu();
            final JTable jt = (JTable)e.getComponent();
            if (jt.getSelectedRow()<0){
                return;
            }
            final SNActorFamily family = (SNActorFamily)jt.getValueAt(jt.getSelectedRow(), 1);
            JMenuItem je = new JMenuItem("Editar familia " + family);
            je.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    
                    ActorFamilyDialog af = new ActorFamilyDialog(family.getId());
                    af.setVisible(true);
                }
            });
            
            
            JMenuItem jd = new JMenuItem("Eliminar familia " + family);
            jd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        SNDirector.getInstance().removeActorFamily(family.getId());
                        
                    } catch (SQLException ex) {
                        Popup.showError(jt, "Error al eliminar familia \n " + ex.getLocalizedMessage());
                        java.util.logging.Logger.getLogger(TableMouseListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            popup.add(je);
            popup.add(jd);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }


}
