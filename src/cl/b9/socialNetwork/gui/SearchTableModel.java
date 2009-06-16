/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import javax.swing.table.DefaultTableModel;

 public class SearchTableModel extends DefaultTableModel {
        public SearchTableModel(){
            super();
            this.addColumn("Id");
            this.addColumn("Familia");
            this.addColumn("Actor");
            
            
        }



    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }    
        
    public void add(Integer id, String actor, String family) {
        Object[] s = new Object[3];
        s[0] = id;
        s[1] = family;
        s[2] = actor;
        this.addRow(s);
    }

    public void reset(){
        while(this.getRowCount()>0){
            this.removeRow(0);
        }
    }
    
}