/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author manuel
 */
public class Popup {

    public static void showError(Component parent, String message){
        JOptionPane.showMessageDialog(parent, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
