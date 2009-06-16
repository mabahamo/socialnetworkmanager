/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo que maneja la lista de familias
 * @author manuel
 */
public class FamiliesTableModel extends AbstractTableModel{

    private static FamiliesTableModel instance;
    private HashMap<String,Color> families;
    
    private FamiliesTableModel(){
        families = new HashMap<String,Color>();
        
    }
    public static FamiliesTableModel getInstance(){
        if (instance == null){
            instance = new FamiliesTableModel();
        }
        return instance;
    }

    public void add(String family, Color color) {
        families.put(family, color);
        this.fireTableDataChanged();
    }
    
    public int getRowCount() {
        return families.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] llaves = (Object[]) families.keySet().toArray();
        if (columnIndex == 1){
            return llaves[rowIndex];
        }
        else {
            return families.get(llaves[rowIndex]);
        }
    }

    public void remove(String family) {
        families.remove(family);
        this.fireTableDataChanged();
    }

    public void reset() {
        families = new HashMap<String,Color>();
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
