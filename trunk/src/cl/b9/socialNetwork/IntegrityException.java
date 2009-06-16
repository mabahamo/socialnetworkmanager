/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

/**
 *
 * @author manuel
 */
public class IntegrityException extends Exception {

    public IntegrityException(String message){
        super("Error de integridad: " + message);
    }
}
