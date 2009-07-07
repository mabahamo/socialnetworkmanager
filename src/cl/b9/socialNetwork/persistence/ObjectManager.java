package cl.b9.socialNetwork.persistence;

import cl.b9.socialNetwork.IntegrityException;
import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.gui.FamiliesTableModel;
import cl.b9.socialNetwork.gui.Popup;
import cl.b9.socialNetwork.gui.SearchTableModel;
import cl.b9.socialNetwork.model.Participant;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
public class ObjectManager implements Observer {

    private static ObjectManager instance;
    private static Logger logger = Logger.getLogger(ObjectManager.class);
    private DBManager db;
    private HashMap<Integer, SNActor> actors = new HashMap<Integer, SNActor>();
    private HashMap<Integer, SNRelation> relations = new HashMap<Integer, SNRelation>();

    private ObjectManager() {
        db = new DBManager();
    }

    public static ObjectManager getInstance() {
        if (instance == null) {
            instance = new ObjectManager();
        }
        return instance;
    }

    public SNActor createActor(SNActorFamily family, String label, Point2D p) throws SQLException {
        return createActor(family.getId(),label,p);
    }

    private SNActor createActor(int familyId, String label,Point2D p) throws SQLException {

        SNActor n = new SNActor(familyId, label);
        n.setPosition((int) p.getX(), (int) p.getY());
        db.store(n);
        n.addObserver(this);
        actors.put(n.getId(), n);
        logger.debug("Nuevo actor " + n);
        logger.debug("observers " + n.countObservers());
        return n;
    }

    public SNActorFamily createActorFamily(String text, Color color) throws SQLException {
        SNActorFamily f = new SNActorFamily(text, color);
        db.store(f);
        FamiliesTableModel.getInstance().add(f);
        return f;
    }

    public void createParticipation(SNActor actor, String rol, SNRelation relation) throws SQLException {
        db.addParticipant(actor,rol,relation);
        db.update(relation);

    }

    public File getDump() throws SQLException {
        return db.getDump();
    }

    public void removeActorFamily(int familyId) throws SQLException {
        db.removeActorFamily(familyId);
        FamiliesTableModel.getInstance().remove(familyId);
    }

    /**
     * Buscar un actor en la base de datos y con el resultado puebla el modelo entregado como argumento
     * @param p patron a buscar
     * @param family
     * @param searchTableModel modelo
     */
    public void searchActor(String p, Object family, SearchTableModel searchTableModel) throws SQLException {
        db.searchActor(p, family, searchTableModel);
    }

    public SNRelation createRelation(String relationFamily, SNActor actor1, SNActor actor2) throws SQLException, IntegrityException {
        db.selectParticipationFamilyByRelationFamily.setString(1, relationFamily);
        ResultSet rs = db.selectParticipationFamilyByRelationFamily.executeQuery();

        try {
            rs.next();
            String rol1 = rs.getString("sourceRole");
            String rol2 = rs.getString("targetRole");
            Color color = new Color(rs.getInt("color"));
            return this.createRelation(relationFamily, actor1, rol1, actor2, rol2, color);
        } catch (NullPointerException e) {
            throw new IntegrityException(e.getLocalizedMessage());
        }


    }

    public SNRelation createRelation(String label, SNActor actor1, String rol1, SNActor actor2, String rol2, Color color) throws SQLException, IntegrityException {
        //revisamos si es que la familia de la relacion ya existe
        if (!existsRelation(label)) {
            db.createRelationFamily(label, actor1.getFamily(), rol1, actor2.getFamily(), rol2, color);
        } else {
   //         if (!db.isValidRelation(label, actor1.getFamily(), rol1, actor2.getFamily(), rol2)) {
   //             throw new IntegrityException("Relación no valida");
    //        }
        }

        SNRelation relation = new SNRelation(label, color);
        relation.setPosition((actor1.getPosition().x + actor2.getPosition().x) / 2, (actor1.getPosition().y + actor2.getPosition().y) / 2);
        db.store(relation);
        db.addParticipant(actor1, rol1, relation);
        db.addParticipant(actor2, rol2, relation);
        relations.put(relation.getId(), relation);
        relation.addObserver(this);
        return relation;

    }

    public boolean existsRelation(String text) {
        return db.existsRelationFamily(text);
    }

    public Collection<SNActorFamily> refreshActorFamilies() {
        return db.refreshActorFamilies();
    }

    public void refreshRelations() {
        db.refreshRelations();
    }

    public SNActorFamily getFamily(int familyId) {
        SNActorFamily af = db.getActorFamily(familyId);
        af.addObserver(this);
        return af;
    }

    /**
     * Recupera todas las relaciones en que el actor de origen es de la familia @source y el de destino es de la familia @dest
     * @param source family
     * @param dest family
     */
    public Collection<String> getRelationsFamily(SNActorFamily source, SNActorFamily dest) {
        logger.debug("getRelationsFamily from " + source + " to " + dest);
        return db.getRelationsFamily(source, dest);
    }

    public void remove(SNNode node) throws SQLException {
        db.remove(node);
        if (node instanceof SNRelation) {
            relations.remove(node.getId());
        }
        if (node instanceof SNActor) {
            //si borro un actor tengo q borrar tambien todas sus participaciones
            relations.remove(node.getId());
        }
    }

    public void renameRelation(SNNode relation, String input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Reinicia la base de datos,ademas borra las tablas de memoria
     */
    public void reset() {
        actors = new HashMap<Integer, SNActor>();
        relations = new HashMap<Integer, SNRelation>();
        db.destroyDB();
        db.initDB();
        this.refreshActorFamilies();
        this.refreshRelations();

    }

    /**
     * Carga una red social desde un archivo generado por este software
     * @param file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void load(File file) throws FileNotFoundException, IOException, SQLException {
        this.reset();
        db.load(file);
    }

    /**
     * Recupera una relacion segun su id
     * @param relationId
     * @return SNRelation
     */
    public SNRelation getRelation(int relationId) {
        if (!relations.containsKey(relationId)) {
            SNRelation relation = db.getRelation(relationId);
            relations.put(relationId, relation);
            return relation;
        }
        return relations.get(relationId);
    }

    /**
     * Recupera un actor segun su id
     * @param actorId
     */
    public SNActor getActor(int actorId) {
        if (!actors.containsKey(actorId)) {
            SNActor actor = db.getActor(actorId);
            if (actor == null) {
                return null;
            }
            actor.addObserver(this);
            actors.put(actorId, actor);
            return actor;
        }
        return actors.get(actorId);

    }

    public SNActor getActor(SNActorFamily family, String label) {
        for(int i=0;i<actors.size();i++){
            if (actors.get(i).getLabel().equals(label) && actors.get(i).getFamily().equals(family)){
                return actors.get(i);
            }
        }
        return null;
    }



    /**
     * Retorna un vector con todos los actores
     */
    public Vector<SNActor> getActors() {
        Vector<SNActor> aux = new Vector<SNActor>();
        try {
            ResultSet rs = db.selectAllActors.executeQuery();
            //"CREATE TABLE NA ( id IDENTITY, Subject VARCHAR, Predicate VARCHAR, Object VARCHAR, x INTEGER, y INTEGER,IMAGE OTHER)"
            while (rs.next()) {
                int nodeId = rs.getInt(1);
                if (!actors.containsKey(nodeId)) {
                    SNActor actor = db.getActor(nodeId, rs);
                    actor.addObserver(this);
                    actors.put(nodeId, actor);
                    aux.add(actor);
                } else {
                    aux.add(actors.get(nodeId));
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    /**
     * Retorna un vector con todas las relaciones
     */
    public Vector<SNRelation> getRelations() {
        Vector<SNRelation> aux = new Vector<SNRelation>();
        try {
            ResultSet rs = db.selectAllRelations.executeQuery();
            while (rs.next()) {
                int nodeId = rs.getInt(1);
                if (!relations.containsKey(nodeId)) {
                    SNRelation relation = db.getRelation(rs.getInt(1), rs);
                    relations.put(nodeId, relation);
                    relation.addObserver(this);
                    aux.add(relation);
                } else {
                    aux.add(relations.get(nodeId));
                }

            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public void shutdown() {
        db.shutdown();
    }

    /**
     * Almacena el estado actual de este nodo en la base de datos.
     * @param node
     */
    public void update(SNNode node) throws SQLException {
        db.update(node);
    }

    /**
     * Observador de las estructuras de datos utilizadas. Si algun modelo es modificado entonces este método se encarga de solicitar a la base de datos que almacene los cambios
     * @param o
     * @param arg
     */
    public void update(Observable o, Object arg) {
        logger.debug("Observable was modified " + o);
        if (o instanceof SNActor) {
            try {
                db.update((SNActor) o);
                SNDirector.getInstance().repaint();
            } catch (SQLException ex) {
                logger.warn(ex.getLocalizedMessage());
            }
            return;
        }
        if (o instanceof SNActorFamily) {
            logger.debug("ActorFamily " + o);
            try {
                db.update((SNActorFamily) o);
                this.refreshActorFamilies();
                SNDirector.getInstance().repaint();
            } catch (SQLException ex) {
                Popup.showError(null, "Error al actualizar familia: " + ex.getLocalizedMessage());
                logger.warn(ex.getLocalizedMessage());
            }
            return;
        }

        if (o instanceof SNRelation) {
            logger.debug("Relation " + o);
            SNDirector.getInstance().repaint();
            try {
                db.update((SNRelation) o);
            } catch (SQLException ex) {
                Popup.showError(null, "Error al actualizar relacion: " + ex.getLocalizedMessage());
                logger.warn(ex.getLocalizedMessage());
            }

            return;
        }

        logger.warn("Objeto modificado no esta siendo almacenado correctamente " + o);
    }

    public Vector<Participant> getParticipants(SNRelation relation) {
        return db.getParticipants(relation);
    }

    public void joinNodes(SNActor newActor, SNActor[] actores, String label, Point2D p) throws SQLException {
        db.updateParticipation(newActor,actores);

        
    }


}
