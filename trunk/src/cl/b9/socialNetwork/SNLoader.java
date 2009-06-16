/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 * Cargador de redes sociales en formatos externos
 * @author manuel
 */
public class SNLoader extends Thread{
    private PajekNetReader pnr;
    private File file;
    private Graph graph;
    private VisualizationViewer vv;
    private JProgressBar progress;
    
    public SNLoader(PajekNetReader pnr, File file, Graph g, VisualizationViewer vv,JProgressBar progress){
        this.pnr = pnr;
        this.file = file;
        this.graph = g;
        this.vv = vv;
        this.progress = progress;
    }
    
    @Override
    public void run(){
        progress.setIndeterminate(true);
        progress.setVisible(true);
  
        try {
            pnr.load(file.getAbsolutePath(), graph);
        } catch (IOException ex) {
            Logger.getLogger(SNLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("new layout");
        FRLayout f = new FRLayout(graph);
        f.initialize();
        f.done();
        vv.setGraphLayout(f);
        vv.repaint();
        
        progress.setVisible(false);
    }

}
