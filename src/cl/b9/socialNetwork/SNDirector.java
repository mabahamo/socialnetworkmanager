/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork;

import cl.b9.socialNetwork.gui.Popup;
import cl.b9.socialNetwork.gui.RelationsTableModel;
import cl.b9.socialNetwork.jung.SNLabeler;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.model.SNEdge;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import cl.b9.socialNetwork.persistence.ObjectManager;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.io.PajekNetWriter;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * Este componente se encarga de coordinar el trabajo de creación de actores y relaciones según la presentación gráfica (GraphPanel) y la base de datos (ObjectManager)
 * @author manuel
 */
public class SNDirector {

    private static SNDirector instance = null;
    private static Logger logger = Logger.getLogger(SNDirector.class);
    private GraphPanel mainGraph;
    private ObjectManager storage;
    private boolean debug;
    private boolean showBinaryRelationsAsNodes = false;

    private SNDirector(){
        storage = ObjectManager.getInstance();
    }

    /**
     * Agrega un nuevo participación a una relación ya existente.
     * @param actor
     * @param rol
     * @param relation
     */public void addParticipant(SNActor actor, String rol, SNRelation relation)throws SQLException {
        storage.createParticipation(actor,rol,relation);
        redrawFromDatabase();
        //mainGraph.repaint();
//        if (relation.getParticipants().size() > storage.getrelationfamily){
            //si la cantidad de participantes es mayor que lo que soporta la relationfamily, entonces debo actualizarla
//            XXX
//        }
    }

    /**
     * Centra la vista en el actor especificado, además lo deja seleccionado
     * @param actorId
     */public void centerAtActor(int actorId) {
         mainGraph.centerAtActor(storage.getActor(actorId));
    }

    public void changeLayout(Class layout) {
        mainGraph.changeLayout(layout);
        update();
    }
     
    //(actorType,label,imageIcon,p);
    public SNActor createActor(String family, String label, ImageIcon image, Point2D p) throws SQLException{        
        SNActor n = storage.createActor(family,label,image,p);
        mainGraph.add(n);
        return n;
    }

    /** 
     * Utiliza la familia de los actores para deducir el rol de cada uno en esta nueva relación binaria
     * @param relationFamily
     * @param actor1
     * @param actor2
     */public void createRelation(String relationFamily, SNActor actor1, SNActor actor2) throws SQLException, IntegrityException {
          SNRelation relation = ObjectManager.getInstance().createRelation(relationFamily, actor1, actor2);

          mainGraph.add(relation, showBinaryRelationsAsNodes);
    }
    
    /** 
     * Crea una nueva relacion binaria, estableciendo los roles de los actores involucrados
     * @param label Etiqueta para la nueva relación
     * @param actor1
     * @param rol1
     * @param actor2
     * @param rol2
     * @throws java.sql.SQLException
     * @throws cl.bahamondez.socialNetwork.IntegrityException
     */public void createRelation(String label, SNActor actor1, String rol1, SNActor actor2, String rol2, Color color) throws SQLException, IntegrityException {
            SNRelation relation = ObjectManager.getInstance().createRelation(label,actor1,rol1,actor2,rol2,color);
            RelationsTableModel.getInstance().add(label, color);
            mainGraph.add(relation, showBinaryRelationsAsNodes);
        
        
    }
    
    
    public static SNDirector getInstance(){
        if (instance == null){
            instance = new SNDirector();
        }
        return instance;
    }

    /**
     * Verifica que un tipo de relación exista en la base de datos
     * @param label etiqueta del tipo de relación
     * @return verdadero si es que existe
     */public boolean existsRelation(String label) {
        return storage.existsRelation(label);
    }

    public File getDump() throws SQLException {
        return storage.getDump();
    }

    public Collection<LayoutContainer> getLayouts() {
        Vector<LayoutContainer> v = new Vector<LayoutContainer>();
        /*
         * KKLayout - The Kamada-Kawai algorithm for node layout
         * FRLayout - The Fruchterman-Rheingold algorithm
         * SpringLayout - A simple force-directed spring-embedder
         * ISOMLayout - Meyer's "Self-Organizing Map" layout.
         * CircleLayout - A simple layout places vertices randomly on a circle 
         * 
        v.add(new KKLayout(mainGraph.getGraph()));
        v.add(new FRLayout(mainGraph.getGraph()));
        v.add(new SpringLayout(mainGraph.getGraph()));
        v.add(new ISOMLayout(mainGraph.getGraph()));
        v.add(new CircleLayout(mainGraph.getGraph()));
        */
        v.add(new LayoutContainer("Kamada-Kawai", "The Kamada-Kawai algorithm for node layout", KKLayout.class));
        v.add(new LayoutContainer("Fruchterman-Rheingold", "The Fruchterman-Rheingold algorithm", FRLayout.class));
        v.add(new LayoutContainer("Spring", "A simple force-directed spring-embedder", SpringLayout.class));
        v.add(new LayoutContainer("ISOM", "Meyer's \"Self-Organizing Map\" layout.", ISOMLayout.class));
        v.add(new LayoutContainer("Circle", "A simple layout places vertices randomly on a circle", CircleLayout.class));
        
        return v;
    }

   

    

    public Point2D inverseTransform(Point point) {
        return mainGraph.inverseTransform(point);
    }

    public boolean isDebugEnabled() {
        return this.debug;
    }

    public void loadPajek(File f) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Elimina un arco y la relación que representa
     * @param edge
     */public void remove(SNEdge edge) throws SQLException {
        remove(edge.getRelation());
        mainGraph.remove(edge);
    }

    /**
     * Elimina un nodo (actor o relacion)
     * @param node
     * @throws java.sql.SQLException
     */public void remove(SNNode node) throws SQLException {
        ObjectManager.getInstance().remove(node);
        mainGraph.remove(node);
    }

     /**
      * Elimina todos los nodos que pertenecen a una misma familia (y sus participaciones en relaciones)
      * @param family
      * @throws java.sql.SQLException
      */
    public void removeActorFamily(String family) throws SQLException {
        ObjectManager.getInstance().removeActorFamily(family);
        mainGraph.repaint();
        redrawFromDatabase();
    }

    public void rename(SNNode relation, String input) {
        logger.debug("Rename " + relation + " to " + input);
        if (relation instanceof SNRelation){
            ObjectManager.getInstance().renameRelation(relation,input);
            mainGraph.renameRelation(relation,input);
        }else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public void repaint() {
       this.mainGraph.repaint();
    }

    public void reset() {
      ObjectManager.getInstance().reset();
      mainGraph.reset();
      
    }

    public void setBinaryRelationsAsNodes(boolean selected) {
        this.showBinaryRelationsAsNodes = selected;
        this.redrawFromDatabase();
    }

    public void setDebug(boolean selected) {
        this.debug = selected;
        mainGraph.repaint();
    }

    public void setMainGraph(GraphPanel graph){
        this.mainGraph = graph;
    }
    
    public void load(File file) throws SQLException, IOException {
        ObjectManager.getInstance().load(file);
        redrawFromDatabase();
    }
    


    public void setNewRelationMode(boolean selected) {
        if (selected)
            ((ModalGraphMouse)mainGraph.getViewer().getGraphMouse()).setMode(Mode.EDITING);
        else 
            ((ModalGraphMouse)mainGraph.getViewer().getGraphMouse()).setMode(Mode.PICKING);
    }

    public void setScale(float f) {
        mainGraph.setScale(f);
    }

    public void updateActorFamily(SNActorFamily actorFamily) {
        Collection<SNNode> c = mainGraph.getLayout().getGraph().getVertices();
        Iterator<SNNode> it = c.iterator();
        while(it.hasNext()){
            SNNode node = it.next();
            if (node instanceof SNActor){
                SNActor actor = (SNActor)node;
                if (actor.getActorType().equals(actorFamily.getName())){
                    actor.setColor(actorFamily.getColor());
                }
            }
        }
        mainGraph.repaint();
    }

    /**
     * Propaga los cambios en relacion a todos las demas relaciones que pertenecen a la misma familia
     * @param relation
     */public void updateRelation(SNRelation relation) {
        Collection<SNEdge> c = mainGraph.getLayout().getGraph().getEdges();
        Iterator<SNEdge> it = c.iterator();
        while(it.hasNext()){
            SNEdge e = it.next();
            if (e.getRelation().getLabel().equals(relation.getLabel())){
                logger.debug(e.getRelation().hashCode());
                e.setColor(relation.getColor());
            }
        }
        
        mainGraph.repaint();
    }

    public void writeImage(File f) throws AWTException, IOException {
        logger.debug("writeImage " + f);
        this.repaint();
        VisualizationViewer vv = mainGraph.getViewer();
        BufferedImage image = new BufferedImage(this.mainGraph.getDimension().width,this.mainGraph.getDimension().height, BufferedImage.BITMASK);
       // vv.setSize(mainGraph.getDimension());
        vv.update(image.getGraphics());
        int width = image.getWidth();
        int height = image.getHeight();
        int minx = width-1;
        int miny = height-1;
        int maxx = 0;
        int maxy = 0;
        for(int x=0;x<image.getWidth();x++){
            for(int y=0;y<image.getHeight();y++){
                int b = image.getRGB(x, y);
                
                if (b != 0xFFFFFFFF){
                    minx = Math.min(minx, x);
                    miny = Math.min(miny, y);
                    maxx = Math.max(maxx, x);
                    maxy = Math.max(maxy, y);
                }
            }
        }
        maxx += 10;
        maxy += 10;
        minx = Math.max(0, minx-10);
        miny = Math.max(0, miny-10);
        image.flush();
        logger.debug("cortando " + minx + "," +miny +"," + maxx +"," +maxy);
        ImageIO.write(image.getSubimage(minx, miny, Math.min(width-minx, maxx-minx), Math.min(height-miny, maxy-miny)), "png", f);
        

    }

    public void writePajek(File f) throws IOException {
        PajekNetWriter pnw = new PajekNetWriter();
        pnw.save(mainGraph.getGraph(), f.getAbsolutePath(),new SNLabeler(),new SNWeighter());
    }

    /**
     * Almacena en la base de datos todos los nodos
     */void update() {
        Collection<SNNode> c = mainGraph.getLayout().getGraph().getVertices();
        logger.debug("Actualizando nodos : " + c.size());
        Iterator<SNNode> it = c.iterator();
        while(it.hasNext()){
            SNNode node = it.next();            
            try {
                ObjectManager.getInstance().update(node);
            } catch (SQLException ex) {
                Popup.showError(null, "Error al almacenar en base de datos: " + ex.getLocalizedMessage());
                java.util.logging.Logger.getLogger(SNDirector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Borra el gráfico existente y dibuja uno nuevo utilizando el último estado de la base de datos.
     */public void redrawFromDatabase() {
        mainGraph.reset();
        logger.debug("Cargando actores");
        Vector<SNActor> actors = ObjectManager.getInstance().getActors();
        Iterator<SNActor> it = actors.iterator();
        while(it.hasNext()){
            mainGraph.add(it.next());
        }
        logger.debug("Cargando relaciones");
        Vector<SNRelation> relations = ObjectManager.getInstance().getRelations();
        Iterator<SNRelation> it2 = relations.iterator();
        while(it2.hasNext()){
            mainGraph.add(it2.next(), this.showBinaryRelationsAsNodes);
        }
        
        logger.debug("Actualiza lista de familias");
        ObjectManager.getInstance().refreshActorFamilies();
        logger.debug("Actualiza lista de relaciones");
        ObjectManager.getInstance().refreshRelations();
    }

    
    
    
}
