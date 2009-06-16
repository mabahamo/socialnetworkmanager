/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * Modelo que maneja la lista de familias
 * @author manuel
 */
public class FamiliesListModel implements ComboBoxModel
{
    private FamiliesTableModel model;
    private static FamiliesListModel instance;
    
    private FamiliesListModel(){
        model = FamiliesTableModel.getInstance();
    }
    
    public static FamiliesListModel getInstance(){
        if (instance == null){
            instance = new FamiliesListModel();
            
        }
        return instance;
    }
    
    private Object selected;

    public void setSelectedItem(Object anItem) {
        selected = anItem;
    }

    public Object getSelectedItem() {
        return selected;
    }

    public int getSize() {
        return model.getRowCount();
    }

    public Object getElementAt(int index) {
        return model.getValueAt(index, 1);
    }

    public void addListDataListener(ListDataListener l) {
        
    }

    public void removeListDataListener(ListDataListener l) {
        
    }
    
    
}
