package cl.b9.socialNetwork.api;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.model.SNRelation;
import cl.b9.socialNetwork.model.SNRelationFamily;
import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Clase principal del api que permite conectarse desde otros programas java para implementar una red social.
 *
 * @author Manuel
 */
public class SocialNetwork {

    private ObjectManager storage = ObjectManager.getInstance();
    /**
     * Crea una red social
     */
    public SocialNetwork() {
        
    }

    /**
     * Crea un nuevo tipo de familia para actores. Arrojará una excepción si la familia ya existe
     * @param label
     * @return
     * @throws SQLException
     */
    public SNActorFamily createActorFamily(String label) throws SQLException{
        return storage.createActorFamily(label, Color.BLUE);
    }

    /**
     * Crea un actor de la familia @code family, con la etiqueta @label
     * @param family
     * @param label
     * @return
     * @throws SQLException
     */
    public SNActor createActor(SNActorFamily family, String label) throws SQLException {
        return storage.createActor(family, label, new Point(100,100));
    }

    public boolean saveTo(String filename){
        File file = new File(filename);
        try {
            File dump = SNDirector.getInstance().getDump();
            InputStream in;
            OutputStream out = new FileOutputStream(file);
            GZIPOutputStream gzout = new GZIPOutputStream(out);
            byte[] buf = new byte[1024];
            int len;
            in = new FileInputStream(dump);
            while ((len = in.read(buf)) > 0){
              gzout.write(buf, 0, len);
            }
            in.close();
            gzout.close();
        } catch (Exception ex) {
            Logger.getLogger(SocialNetwork.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    //BUG: El primer argumento debe ser un relationfamily
    public SNRelation createRelation(String name, SNActor actor1, String rol1, SNActor actor2, String rol2) throws Exception {
        return storage.createRelation(name, actor1, rol1, actor2, rol2, Color.yellow);
    }

    
    public SNActor getActor(SNActorFamily investigadores, String label) {
        return storage.getActor(investigadores,label);
    }

    //public SNRelationFamily createRelationFamily(String string) {
    //    return storage.create
    //}

}
