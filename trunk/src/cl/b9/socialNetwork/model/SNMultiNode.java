/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
class SNMultiNode {
    Logger logger = Logger.getLogger("SNMultiNode");

    public SNMultiNode(Object[] selectedObjects) {
        logger.debug("Cargando multinodo " + selectedObjects.length );
    }

}
