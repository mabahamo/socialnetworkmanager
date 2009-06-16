/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.SNDirector;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JTable;

/**
 *
 * @author manuel
 */
class SearchActorMouseListener implements MouseListener{

    public SearchActorMouseListener() {
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==2){
            JTable jt = (JTable)e.getComponent();
            if (jt.getSelectedRow()<0){
                return;
            }
            int actorId = (Integer)jt.getValueAt(jt.getSelectedRow(), 0);
            SNDirector.getInstance().centerAtActor(actorId);
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
