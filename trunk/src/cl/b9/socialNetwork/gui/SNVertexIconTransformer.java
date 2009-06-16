/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import javax.swing.Icon;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
   public class SNVertexIconTransformer<SNActor> extends DefaultVertexIconTransformer<SNActor> implements Transformer<SNActor,Icon>{
        private Logger logger = Logger.getLogger(SNVertexIconTransformer.class);
       
        public javax.swing.Icon transform(cl.b9.socialNetwork.model.SNActor actor) {
            logger.debug("iconMaker");
            return actor.getIcon();
        }
        
    }