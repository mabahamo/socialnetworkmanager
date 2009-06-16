/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import java.awt.Color;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author manuel
 */
public class RelationsTableModel extends AbstractTableModel{

    private static RelationsTableModel instance;
    private HashMap<String,Color> relations;
    
    private RelationsTableModel(){
        relations = new HashMap<String,Color>();
        
    }
    public static RelationsTableModel getInstance(){
        if (instance == null){
            instance = new RelationsTableModel();
        }
        return instance;
    }

    public void add(String family, Color color) {
        relations.put(family, color);
        this.fireTableDataChanged();
    }
    
    public int getRowCount() {
        return relations.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] llaves = (Object[]) relations.keySet().toArray();
        if (columnIndex == 1){
            return llaves[rowIndex];
        }
        else {
            return relations.get(llaves[rowIndex]);
        }
    }

    public void remove(String family) {
        relations.remove(family);
        this.fireTableDataChanged();
    }

    public void reset() {
        relations = new HashMap<String,Color>();
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column){
        switch(column){
            case 0:
                return "";
            case 1:
                return "Relaciones";
        }
        return "undefined";
    }
    
}
