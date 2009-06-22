/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.model.SNActorFamily;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo que maneja la lista de familias
 * @author manuel
 */
public class FamiliesTableModel extends AbstractTableModel {

    private static FamiliesTableModel instance;
    private Vector<SNActorFamily> families;
    
    private FamiliesTableModel(){
        families = new Vector<SNActorFamily>();
        
    }
    public static FamiliesTableModel getInstance(){
        if (instance == null){
            instance = new FamiliesTableModel();
        }
        return instance;
    }

    public void add(SNActorFamily family) {
        families.add(family);
        this.fireTableDataChanged();
    }

    public void add(Collection<SNActorFamily> collection){
        families.addAll(collection);
        this.fireTableDataChanged();
    }
    
    public int getRowCount() {
        return families.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 1){
            return families.get(rowIndex);
        }
        else {
            return families.get(rowIndex).getColor();
        }
    }

    public void remove(int familyId) {
        Iterator<SNActorFamily> it = families.iterator();
        while(it.hasNext()){
            SNActorFamily f = it.next();
            if (f.getId() == familyId){
                it.remove();
                return;
            }
        }
        this.fireTableDataChanged();
    }

    public void reset() {
        families.clear();
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column){
        switch(column){
            case 0:
                return "";
            case 1:
                return "Familia";
        }
        return "undefined";
    }

    
}
