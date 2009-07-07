/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.model.SNNode;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Manuel
 */
class PajekNullLocation implements Transformer<SNNode, Point2D> {

    public Point2D transform(SNNode arg0) {
        return new Point2D.Double(0.5,0.5);
    }

}
