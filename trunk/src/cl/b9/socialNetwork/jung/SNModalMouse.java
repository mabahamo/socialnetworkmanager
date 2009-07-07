/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.jung;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
public class SNModalMouse extends PluggableGraphMouse implements ModalGraphMouse {
    protected Mode mode;
    private static Logger logger = Logger.getLogger(SNModalMouse.class);
    private ScalingGraphMousePlugin scaling = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f);
    private GraphMousePlugin picking = new SNPickingGraphMousePlugin();
    private GraphMousePlugin menu = new MenuGraphMousePlugin();
    private RelationsGraphMousePlugin relations = new RelationsGraphMousePlugin();
    private GraphMousePlugin editing = new EditGraphMousePlugin();
    private ModeListener modeListener;
    
    public SNModalMouse(){
        super();
        this.add(scaling); //la rueda del mouse siempre responde al zoom
        this.add(editing);
        this.setMode(Mode.PICKING);

    }

  
    
    public void setMode(Mode mode) {
        if (this.mode == mode){
            return;
        }
        this.mode = mode;
        if (mode == Mode.PICKING){
            relations.reset();
            this.remove(relations);
            this.add(picking);
            this.add(menu);
        }
        if (mode == Mode.EDITING){
            this.remove(picking);
            this.remove(menu);
            this.remove(scaling);
            this.add(relations); //creador de relaciones se activa con alt
        }
    }

    /**
     * listener to set the mode from an external event source
     */
    class ModeListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            logger.debug(e);
            setMode((Mode) e.getItem());
        }
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#getModeListener()
     */
    public ItemListener getModeListener() {
		if (modeListener == null) {
			modeListener = new ModeListener();
		}
		return modeListener;
	}



}
