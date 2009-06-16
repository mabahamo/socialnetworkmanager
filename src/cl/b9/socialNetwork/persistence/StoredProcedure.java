/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.persistence;

/**
 * Stores procedure para ser utilizados en HSQLDB
 * @author manuel
 */
public class StoredProcedure {

    /**
     * HSQLDB no posee el comando de postgres ilike por lo cual implementamos una version similar en java
     * @param target
     * @param search
     * @return verdadero si target ilike search
     */public static boolean containsMatch(String target, String search) {
        if (target!=null){
            target = target.replace(" ", "");
        }
        return target.toLowerCase().contains(search.toLowerCase());
    }
    
}
