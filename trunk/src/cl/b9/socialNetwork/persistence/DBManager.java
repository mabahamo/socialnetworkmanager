package cl.b9.socialNetwork.persistence;

import cl.b9.socialNetwork.gui.FamiliesTableModel;
import cl.b9.socialNetwork.gui.RelationsTableModel;
import cl.b9.socialNetwork.gui.SearchTableModel;
import cl.b9.socialNetwork.model.Participant;
import cl.b9.socialNetwork.model.ParticipantType;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.model.SNNode;
import cl.b9.socialNetwork.model.SNRelation;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * Capa de conexión a la base de datos, implementada para HSQLDB pero en caso de necesitarse mayor potencia puede ser migrada a postgres
 * @author manuel
 */
public class DBManager {

    private static String DBCONNECTION = "jdbc:hsqldb:";
    private final static String USERNAME = "sa";
    private final static String PASSWORD = "";
 ;
    private Connection connection;
    private Logger logger = Logger.getLogger(DBManager.class);
    private PreparedStatement insertActorFamily;
    private PreparedStatement selectActorFamily;
    private PreparedStatement selectAllActorFamily;
    private PreparedStatement selectRelationFamily;
    private PreparedStatement insertRelationFamily;
    private PreparedStatement insertActor, deleteActor;
    protected PreparedStatement selectAllActors;
    private PreparedStatement insertRelation,deleteRelation;
    protected PreparedStatement selectAllRelations;
    private PreparedStatement insertParticipation;
    private PreparedStatement selectActor;
    private PreparedStatement selectParticipantsByRelationId;
    private PreparedStatement updateRelation;
//    private Object updateRelationFamily;
    private PreparedStatement dimensions;
    protected PreparedStatement insertParticipationFamily,selectParticipationFamily,selectParticipationFamilyByActorsFamily,selectParticipationFamilyByRelationFamily,
              deleteRelationsFromActorFamily,deleteParticipationsFromActorFamily;
 
    private PreparedStatement updateActor,deleteParticipationByActorId,selectAllRelationsFamily,selectParticipationByActorId,selectRelationById;
    private PreparedStatement searchActorByName,deleteActorFamily, deleteActorFromFamily;
    private PreparedStatement updateActorFamily;


    

    public DBManager() {
        DBCONNECTION = DBCONNECTION + System.getProperty("java.io.tmpdir") + "socialnetwork_db";
        try {
            // Load the HSQL Database Engine JDBC driver
            // hsqldb.jar should be in the class path or made part of the current jar
            logger.debug("Conectando a la base de datos :" + DBCONNECTION);
            Class.forName("org.hsqldb.jdbcDriver");

            connection = DriverManager.getConnection(DBCONNECTION, USERNAME, PASSWORD);
            initDB();
            insertActorFamily = connection.prepareStatement("INSERT INTO ACTORFAMILY(Name,Color) VALUES (?,?)");
            updateActorFamily = connection.prepareStatement("UPDATE ACTORFAMILY SET name = ?, color = ? where id = ?");
            selectActorFamily = connection.prepareStatement("SELECT * from ACTORFAMILY WHERE id = ?");
            deleteActorFamily = connection.prepareStatement("DELETE FROM ACTORFAMILY WHERE id = ?");
            selectAllActorFamily = connection.prepareStatement("SELECT * from ACTORFAMILY");
           // "CREATE TABLE RELATIONTYPE (id IDENTITY, Name VARCHAR, Cardinality INTEGER, ActorsFamilySupported VARCHAR, Participants OTHER)",
            insertRelationFamily = connection.prepareStatement("INSERT INTO RELATIONFAMILY(Name,Color,Cardinality) VALUES (?,?,?)");
            selectAllRelationsFamily = connection.prepareStatement("SELECT * FROM RELATIONFAMILY");
            selectRelationFamily = connection.prepareStatement("SELECT * FROM RELATIONFAMILY WHERE name = ?");
            selectParticipationFamilyByActorsFamily = connection.prepareStatement("SELECT PF1.actorFamily as sourceActor, PF1.role as sourceRole, PF2.actorFamily as targetActor, PF2.role as targetRole, PF1.relationFamily as relationFamily FROM ParticipationFamily as PF1, ParticipationFamily as PF2 WHERE PF1.relationFamily = PF2.relationFamily and PF2.id > PF1.id and PF1.actorFamily = ? and PF2.actorFamily = ? ORDER BY PF1.id ASC");
            selectParticipationFamilyByRelationFamily = connection.prepareStatement("SELECT PF1.actorFamily as sourceActor, PF1.role as sourceRole, PF2.actorFamily as targetActor, PF2.role as targetRole, PF1.relationFamily as relationFamily, color FROM ParticipationFamily as PF1, ParticipationFamily as PF2, RelationFamily WHERE PF1.relationFamily = PF2.relationFamily and PF1.relationFamily = RelationFamily.Name and PF2.id > PF1.id and PF1.relationFamily = ? ORDER BY PF1.id ASC");
            insertParticipationFamily = connection.prepareStatement("INSERT INTO PARTICIPATIONFAMILY(actorFamily,role,relationFamily) values(?,?,?)");
            selectParticipationFamily = connection.prepareStatement("SELECT actorFamily,role FROM PARTICIPATIONFAMILY WHERE relationFamily = ? ORDER BY id ASC");
            //"CREATE TABLE NA ( id IDENTITY, Subject VARCHAR, Predicate VARCHAR, Object VARCHAR, x TINYINT, y TINYINT)",
            insertActor = connection.prepareStatement("INSERT INTO NA(Subject,Predicate,ObjectId,x,y,Image) VALUES(?,?,?,?,?,?)");
            updateActor = connection.prepareStatement("UPDATE NA set subject = ?, predicate = ?, objectId = ?, x = ?, y = ?,image = ? where id = ?");
            selectActor = connection.prepareStatement("SELECT * FROM NA WHERE ID = ?");
            deleteActor = connection.prepareStatement("DELETE FROM NA WHERE ID = ?");
            deleteActorFromFamily = connection.prepareStatement("DELETE FROM NA WHERE ObjectID = ?");
            deleteRelationsFromActorFamily = connection.prepareStatement("DELETE FROM NR WHERE id IN (SELECT objectid FROM P WHERE subjectID IN (SELECT id from NA WHERE ObjectID  = ?))");
            deleteParticipationsFromActorFamily = connection.prepareStatement("DELETE FROM P WHERE subjectID IN (SELECT id from NA WHERE ObjectID = ?)");
            
            selectAllActors = connection.prepareStatement("SELECT * FROM NA");
            searchActorByName = connection.prepareStatement("SELECT id,Subject,ObjectId,ActorFamily.NAME as family FROM NA,ACTORFAMILY WHERE NA.OBJECTID = ActorFamily.ID AND \"cl.b9.socialNetwork.persistence.StoredProcedure.containsMatch\"(Subject,?)");
           // "CREATE TABLE NR ( id IDENTITY, Subject VARCHAR, Predicate VARCHAR, Object VARCHAR, RELATIONTYPE INTEGER, x INTEGER, y INTEGER)",
            insertRelation = connection.prepareStatement("INSERT INTO NR(Subject,Predicate,Object,x,y,Color) values(?,?,?,?,?,?)");
            updateRelation = connection.prepareStatement("UPDATE NR set subject = ?, x = ?, y = ?, color = ? where id = ?");
            selectRelationById = connection.prepareStatement("SELECT * FROM NR WHERE ID = ?");
            selectAllRelations = connection.prepareStatement("SELECT * FROM NR");
            deleteRelation = connection.prepareStatement("DELETE FROM NR WHERE ID = ?");
            //"CREATE TABLE P ( SubjectID INTEGER, Predicate VARCHAR, ObjectID INTEGER )",
            insertParticipation = connection.prepareStatement("INSERT INTO P(subjectID,predicate,objectID) VALUES(?,?,?)");
            selectParticipantsByRelationId = connection.prepareStatement("SELECT * FROM P WHERE ObjectId = ?");
            selectParticipationByActorId = connection.prepareStatement("SELECT * FROM P WHERE subjectID = ?");
            deleteParticipationByActorId = connection.prepareStatement("DELETE FROM P WHERE subjectID = ?");

            
            
 //           updateRelationFamily = connection.prepareStatement("UPDATE RELATIONFAMILY set cardinality = ?, actorsfamilysupported = ?, participants = ? where name = ? ");
            
            //GET DIMENSIONS
            dimensions = connection.prepareStatement("SELECT min(NR.x) as minrx, min(NA.x) as minax, max(NR.x) as maxrx,max(NA.x) as maxax, min(NR.Y) as minry, min(NA.Y) as minay, max(NR.Y) as maxry,max(NA.Y) as maxay from nr,na");


            
        } catch (Exception ex) {
            logger.fatal(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }


    }



    
    
    
    public void addParticipant(SNActor actor, String rol, SNRelation relation) throws SQLException {
        logger.debug("Almacenando participante " + actor + " ---" + rol + "--> " + relation);
        this.insertParticipation.setLong(1, actor.getId());
        this.insertParticipation.setString(2, rol);
        this.insertParticipation.setLong(3, relation.getId());
        this.insertParticipation.executeUpdate();
        logger.debug("participant commited");
    }


    public Dimension getDimension() {
        try {
            //"SELECT min(NR.x) as minrx, min(NA.x) as minax, max(NR.x) as maxrx,max(NA.x) as maxax, min(NR.Y) as minry, min(NA.Y) as minay, max(NR.Y) as maxry,max(NA.Y) as maxay from nr,na");
            ResultSet rs = this.dimensions.executeQuery();
            rs.next();
            int maxx = Math.max(rs.getInt("maxrx"), rs.getInt("maxax"));
            int maxy = Math.max(rs.getInt("maxry"), rs.getInt("maxay"));
            Dimension dimension = new Dimension(maxx+300,maxy+300);
            logger.debug("New dimension is " + dimension);
            return dimension;
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public void remove(SNNode node) throws SQLException{
       if (node instanceof SNRelation){
           remove((SNRelation) node);
       }
       if (node instanceof SNActor){
           remove((SNActor)node);
       }
    }
    
    public void remove(SNRelation relation) throws SQLException {
        logger.info("Removing relation " + relation);
        deleteRelation.setInt(1, relation.getId());
        deleteRelation.executeUpdate();
        
    }
    public void remove(SNActor node) throws SQLException {
        logger.info("removing participations ");
        deleteParticipationByActorId.setInt(1,node.getId());
        deleteParticipationByActorId.executeUpdate();
        
        logger.info("Removing nove " + node);
        deleteActor.setInt(1, node.getId());
        deleteActor.executeUpdate();
        
        
    }



    void addParticipantFamily(String relationType, SNActorFamily actorFamily, String rol) throws SQLException {
        this.insertParticipationFamily.setInt(1,actorFamily.getId());
        this.insertParticipationFamily.setString(2,rol);
        this.insertParticipationFamily.setString(3,relationType);
        this.insertParticipationFamily.executeUpdate();
    }

    void addRelationFamily(String label, Color color, int i) throws SQLException {
        this.insertRelationFamily.setString(1,label);
        this.insertRelationFamily.setInt(2, color.getRGB());
        this.insertRelationFamily.setInt(3, i);
        insertRelationFamily.executeUpdate();
    }

    void createRelationFamily(String label, SNActorFamily actorFamily1,String rol1, SNActorFamily actorFamily2, String rol2,Color color ) throws SQLException {
            this.addRelationFamily(label, color,2);
            this.addParticipantFamily(label,actorFamily1,rol1);
            this.addParticipantFamily(label,actorFamily2,rol2);
    }

    boolean existsRelationFamily(String label) {
        try {
            selectRelationFamily.setString(1, label);
            ResultSet rs = selectRelationFamily.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
       return false;
    }

    boolean isValidRelation(String label, String actorType1, String rol1, String actorType2, String rol2) {
    try {
       if (existsRelationFamily(label)){
                //si la relacion existe entonces debe soportar las familias de los actores
           this.selectParticipationFamily.setString(1,label);
           ResultSet rs = selectParticipationFamily.executeQuery();
           //condicion caso relacion binaria
                if (rs.next()) {
                    boolean a = rs.getString("actorFamily").equals(actorType1);
                    boolean b = rs.getString("role").equals(rol1);
                    if (!rs.next()) return false;
                    boolean c = rs.getString("actorFamily").equals(actorType2);
                    boolean d = rs.getString("role").equals(rol2);
                    return a && b && c && d;
                } else {
                    return false;
                }
        }else {
           return true;
        }
       
       
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        return false;
    
    }


    void searchActor(String p, Object family, SearchTableModel searchTableModel) throws SQLException {
        family = null;
        if (family == null){
         searchActorByName.setString(1, p);
         searchTableModel.reset();
         ResultSet rs = searchActorByName.executeQuery();
            while(rs.next()){
                Integer id = rs.getInt("id");
                String a = rs.getString("Subject");
                String f = rs.getString("family");
                searchTableModel.add(id,a,f);
            }
            
        }
        
    }

 

   




  


    private String hashSupportedActorType(Vector<ParticipantType> participants){
       if (participants.size() == 2) {
            ParticipantType source = participants.get(0);
            ParticipantType dest = participants.get(1);
            return "#" + source.getActorType() + "#" + dest.getActorType() + "#";
        } else {
            HashSet hs = new HashSet();
            Iterator<ParticipantType> it = participants.iterator();
            while (it.hasNext()) {
                //inserto en un hashset para eliminar actores duplicados
                hs.add(it.next().getActorType());
            }
            String supportedActorType = "#";
            Iterator<String> it2 = hs.iterator();
            while (it2.hasNext()) {
                supportedActorType += it2.next() + "#";
            }
            return supportedActorType;
        }
    }
    

    
    protected SNActor getActor(int actorId){
        return this.getActor(actorId,null);
    }

    public File getDump() throws SQLException {
       String tmpPath = System.getProperty("java.io.tmpdir");
       String separator = System.getProperty("file.separator");
       if (!tmpPath.endsWith(separator)){
           tmpPath += separator;
       }
       tmpPath += "snapshot.sql";
       File f = new File(tmpPath);
       if (f.exists()){
           f.delete();
       }
       this.connection.createStatement().execute("SCRIPT '" + tmpPath + "'");
       f = new File(tmpPath);
       return f;
       
    }


    
    public void load(File file) throws FileNotFoundException, IOException, SQLException {
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line;
        while ((line = br.readLine())!= null){
            if (line.indexOf("INSERT INTO ")==0){
                logger.debug("Load: " + line);
                this.connection.createStatement().execute(line);
            }
        }
        this.connection.commit();
    }

    public void update(SNActorFamily actorFamily) throws SQLException{
        logger.debug("Actualizando " + actorFamily);
        updateActorFamily.setString(1,actorFamily.getName());
        updateActorFamily.setInt(2,actorFamily.getColor().getRGB());
        updateActorFamily.setInt(3,actorFamily.getId());
        updateActorFamily.execute();
        connection.commit();
    }

    public void update(SNRelation relation) throws SQLException {
            //updateRelation = connection.prepareStatement("UPDATE NR set subject = ?, x = ?, y = ? where id = ?");
            updateRelation.setString(1, relation.getLabel());
            updateRelation.setInt(2, relation.getPosition().x);
            updateRelation.setInt(3, relation.getPosition().y);
            updateRelation.setInt(4, relation.getColor().getRGB());
            updateRelation.setInt(5, relation.getId());

            int e = updateRelation.executeUpdate();
            //logger.debug("execute Update " + e);
            //updateRelationFamily "UPDATE RELATIONFAMILY set cardinality = ?, actorsfamilysupported = ?, participants = ? where name = ? ");
        //    RelationFamily rf = relation.getRelationFamily();
            int c = relation.getParticipants().size();
         //   if (rf.getCardinality < c){
         //       rf.setCardinality(c);
                
         //   }
            //TODO: Actualizar el conteo de participantes en relationsfamiliy
            connection.commit();
      
        
    }

    protected SNActor getActor(int actorId,ResultSet rs) {
        logger.debug("Buscando ActorID " + actorId);
        try { 
            logger.debug("Construyendo actor " + actorId);
            if (rs!=null){
                SNActorFamily actorType = this.getActorFamily(rs.getInt("ObjectID"));
                SNActor actor = new SNActor(actorType,rs.getString("subject"));
                actor.setId(rs.getInt(1));
                actor.setPosition(rs.getInt("x"),rs.getInt("y"));
                return actor;
            }

            this.selectActor.setLong(1, actorId);
            ResultSet rs2 = this.selectActor.executeQuery();
            if (rs2.next()){
                SNActor actor = new SNActor(rs2.getInt("ObjectID"), rs2.getString("Subject"));
                //CREATE TABLE NA ( id IDENTITY, Subject VARCHAR, Predicate VARCHAR, Object VARCHAR, x INTEGER, y INTEGER,IMAGE OTHER)",
                actor.setId(rs2.getInt(1));
                actor.setPosition(rs2.getInt("x"), rs2.getInt("y"));
                return actor;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
        
    }

    public Vector<Participant> getParticipants(SNRelation relation) {
        Vector<Participant> v = new Vector<Participant>();
        try{
            this.selectParticipantsByRelationId.setInt(1,relation.getId());
            ResultSet rs = selectParticipantsByRelationId.executeQuery();
            while (rs.next()){
                Participant p = new Participant(rs.getInt("SubjectID"),rs.getString("Predicate"),rs.getInt("ObjectID"));
                v.add(p);
            }
         } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return v;
    }

    protected SNRelation getRelation(int relationId) {
        return this.getRelation(relationId, null);
    }

  



    public void shutdown() {
        logger.debug("Shutting down database");
        Statement st;
        try {
            st = connection.createStatement();
              st.execute("SHUTDOWN");
        connection.close();    // if there are no other open connection
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // db writes out to files and performs clean shuts down
        // otherwise there will be an unclean shutdown
        // when program ends
        File f = new File(System.getProperty("java.io.tmpdir") + "socialnetwork_db.script");
        f.delete();
        f = new File(System.getProperty("java.io.tmpdir") + "socialnetwork_db.properties");
        f.delete();
        f = new File(System.getProperty("java.io.tmpdir") + "socialnetwork_db.tmp");
        f.delete();
    }



    private void executeSQL(String expression) {
        try {
            Statement st = null;
            st = connection.createStatement();    // statements
            logger.debug("update: " + expression);
            int i = st.executeUpdate(expression);    // run the query
            if (i == -1) {
                System.out.println("db error : " + expression);
            }
            st.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Crea las tablas necesarias en la base de datos.
     */
    public void initDB() {
        String[] init = {"CREATE TABLE NA (id IDENTITY, Subject VARCHAR, Predicate VARCHAR, ObjectID Integer, x INTEGER, y INTEGER, Image Other)",
            "CREATE TABLE NR ( id IDENTITY, Subject VARCHAR, Predicate VARCHAR, Object VARCHAR, x INTEGER, y INTEGER,color integer)",
            "CREATE TABLE P ( id IDENTITY, SubjectID INTEGER, Predicate VARCHAR, ObjectID INTEGER )",
            "CREATE TABLE ACTORFAMILY(id IDENTITY, Name VARCHAR, Color INTEGER) ",
            "CREATE TABLE RELATIONFAMILY (Name VARCHAR PRIMARY KEY, Color INTEGER, Cardinality INTEGER)",
            "CREATE TABLE PARTICIPATIONFAMILY (id IDENTITY, actorFamily INTEGER, role VARCHAR, relationFamily VARCHAR)"
        };
        for (int i = 0; i < init.length; i++) {
            executeSQL(init[i]);
        }
    }
    /**
     * Elimina las tablas desde la base de datos 
     */
    public void destroyDB() {
        String[] init = {"NA","NR","P","ACTORFAMILY","RELATIONFAMILY", "PARTICIPATIONFAMILY"};
        for (int i = 0; i < init.length; i++) {
            executeSQL("DROP TABLE " + init[i]);
        }
    }
    

    
    void removeActorFamily(int familyId) throws SQLException {

        deleteRelationsFromActorFamily.setInt(1,familyId);
        deleteRelationsFromActorFamily.executeUpdate();
        deleteParticipationsFromActorFamily.setInt(1,familyId);
        deleteParticipationsFromActorFamily.executeUpdate();

        deleteActorFromFamily.setInt(1,familyId);
        deleteActorFromFamily.executeUpdate();
        
        this.deleteActorFamily.setInt(1,familyId);
        deleteActorFamily.executeUpdate();

        removeUncompleteRelations();
    }
    
    SNActorFamily getActorFamily(int id) {
        try {
            this.selectActorFamily.setInt(1, id);
            ResultSet rs = this.selectActorFamily.executeQuery();
            if (rs.next()) {
                Color color = new Color(rs.getInt("color"));
                String name = rs.getString("name");
                SNActorFamily t = new SNActorFamily(id,name,color);
                
                //TODO: Recuperar los atributos de type
                return t;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    Collection<SNActorFamily> refreshActorFamilies() {
        FamiliesTableModel.getInstance().reset();
        Collection<SNActorFamily> aux = new Vector<SNActorFamily>();
        try {
            ResultSet rs = this.selectAllActorFamily.executeQuery();
            while (rs.next()) {
                aux.add(getActorFamily(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        FamiliesTableModel.getInstance().add(aux);
        return aux;
    }
    
    void refreshRelations() {
        RelationsTableModel.getInstance().reset();
        try {

            ResultSet rs = this.selectAllRelationsFamily.executeQuery();
            while(rs.next()){
                RelationsTableModel.getInstance().add(rs.getString("name"), new Color(rs.getInt("color")));
            }
        }catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


   
    
    /**
     * Recupera todas las relaciones en que el actor de origen es de la familia @source y el de destino es de la familia @dest
     * @param source family
     * @param dest family
     */public Vector<String> getRelationsFamily(SNActorFamily source, SNActorFamily dest) {
         //FIXME: Implementar esto
        Vector<String> relations = new Vector<String>();
        try {
            this.selectParticipationFamilyByActorsFamily.setInt(1, source.getId());
            this.selectParticipationFamilyByActorsFamily.setInt(2, dest.getId());
            
            ResultSet rs = this.selectParticipationFamilyByActorsFamily.executeQuery();
            
            while (rs.next()) {
                relations.add(rs.getString("relationFamily"));
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return relations;
    }

    public void store(SNActorFamily type) throws SQLException {
        this.insertActorFamily.setString(1, type.getName());
        this.insertActorFamily.setInt(2, type.getColor().getRGB());
        int i = this.insertActorFamily.executeUpdate();
        type.setId(this.getIdentity());
        logger.debug("Insertado " + type + " - return code : " + i);
    //TODO Insertar los atributos de type en la base de datos
    }

     public void store(SNRelation relation) throws SQLException {
        //"INSERT INTO NR(Subject,Predicate,Object,x,y) values(?,?,?,?,?,?)");
         logger.debug("Almacena relacion " + relation);
         this.insertRelation.setString(1, relation.getLabel());
         this.insertRelation.setString(2, "isR");
         this.insertRelation.setString(3, relation.getLabel());
         this.insertRelation.setInt(4, relation.getPosition().x);
         this.insertRelation.setInt(5, relation.getPosition().y);
         this.insertRelation.setInt(6,relation.getColor().getRGB());
         this.insertRelation.executeUpdate();
         relation.setId(this.getIdentity());
    }
     
    public void update(SNNode node) throws SQLException {
        if (node instanceof SNActor){
            update((SNActor)node);
        }
        else if (node instanceof SNRelation){
            update((SNRelation)node);
        }
    }
    
    public void update(SNActor actor) throws SQLException {
        //"UPDATE NA set subject = ?, predicate = ?, objectId = ?, x = ?, y = ?,image = ? where id = ?");
        this.updateActor.setString(1, actor.getLabel());
        this.updateActor.setString(2, "isA");
        this.updateActor.setInt(3, actor.getFamily().getId());
        this.updateActor.setInt(4, actor.getPosition().x);
        this.updateActor.setInt(5, actor.getPosition().y);
        this.updateActor.setObject(6, actor.getIcon());
        this.updateActor.setInt(7, actor.getId());
        int e = this.updateActor.executeUpdate();
        //logger.debug("execute Update " + e);
       
    }
    
    /**
     * Almacena un actor en la base de datos, el modelo considera que pueden existir 
     * dos nodos con el mismo nombre. Para no alterar la representacion gráfica también 
     * almacenamos las coordenadas de donde esta ubicado el nodo
     * @param n nodo a agregar
     * @throws java.sql.SQLException
     */
    public void store(SNActor n) throws SQLException {
        //insertActor = connection.prepareStatement("INSERT INTO NA(Subject,Predicate,Object,x,y,IMAGE) VALUES(?,?,?,?,?,?)");
        logger.debug("Almacenando actor " + n);
        this.insertActor.setString(1, n.getLabel());
        this.insertActor.setString(2, "isA");
        this.insertActor.setInt(3, n.getFamily().getId());
        this.insertActor.setInt(4, n.getPosition().x);
        this.insertActor.setInt(5, n.getPosition().y);
        if (n.getIcon()!= null){
            this.insertActor.setObject(6, n.getIcon());
        }
        this.insertActor.executeUpdate();
        n.setId(this.getIdentity());
       
        
    }

    private int getIdentity() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("CALL IDENTITY()");
        if (rs.next()){
            return rs.getInt(1);
        }
        return -1;
    }

    protected SNRelation getRelation(int relationId, ResultSet rs) {
        try {        
            if (rs!=null){
                SNRelation relation = new SNRelation(rs.getString("SUBJECT"), new Color(rs.getInt("color")));
                relation.setId(rs.getInt(1));
                relation.setPosition(rs.getInt("x"),rs.getInt("y"));
                relation.setColor(new Color(rs.getInt("color")));
                return relation;
            }
            else {
                logger.warn("No implementado");
            }
        }
            catch (SQLException ex) {
                java.util.logging.Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
    }

    /**
     * Elimina relaciones que poseen menos de un participante
     */
    private void removeUncompleteRelations() {
        logger.fatal("Implementamente! removeUncompleteRelations");
        //TODO: Implementar esto
                throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Cambia los links de participacion, reemplazando las relaciones en las 
     * que participan @code actores por @code newActor
     * @param newActor
     * @param actores arreglo conteniendo los actores a modificar
     */
    void updateParticipation(SNActor newActor, SNActor[] actores) throws SQLException {
        String aux = "";
        for(int i=0;i<actores.length-1;i++){
             aux += actores[i].getId() + ",";
        }
        aux += actores[actores.length-1].getId();
        PreparedStatement st = connection.prepareStatement("UPDATE P SET subjectID = " + newActor.getId() + "where subjectID in (" + aux + ")");
        st.execute();

        st = connection.prepareStatement("DELETE FROM NA WHERE ID IN (" + aux + ")");
        st.execute();

        connection.commit();
    }
}
