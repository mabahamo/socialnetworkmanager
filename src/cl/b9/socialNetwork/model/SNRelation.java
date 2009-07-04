/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import cl.b9.socialNetwork.persistence.DBManager;
import cl.b9.socialNetwork.persistence.ObjectManager;
import cl.b9.socialNetwork.util.PolygonUtils;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author manuel
 */
public class SNRelation extends Observable implements SNNode{
    
    private EdgeType edgeType = EdgeType.DIRECTED;
    private String name;
    private int id = -1;
    private static Logger logger = Logger.getLogger(SNRelation.class);
    private Color color = Color.BLACK;
    private Point point = null;

    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return this.id;
    }
    
    public SNRelation(String name, Color color){
        this.name = name;
        this.color = color;
    }
    
    public String getLabel(){
        return this.name;
    }
 
    
    
    public EdgeType getEdgeType(){
        return this.edgeType;
    }
    
    public Vector<Participant> getParticipants(){
        return ObjectManager.getInstance().getParticipants(this);
    }
    
    public void setLabel(String label){
        this.name = label;
    }
    
    
    @Override
    public String toString(){
        return this.name;
    }

    
    
    public void setPosition(int x, int y) {
        this.point = new Point(x,y);
        this.setChanged();
        this.notifyObservers();
    }
    
    private void recalcPosition(){
        Vector<Participant> p = this.getParticipants();
        if (p.size()<2){
            logger.error("No existen suficientes participantes en esta relacion, error de integridad");
            return;
        }
        if (p.size()==2){
            int x = (p.get(0).getActor().getPosition().x + p.get(1).getActor().getPosition().x)/2 ;
            int y = (p.get(0).getActor().getPosition().y + p.get(1).getActor().getPosition().y)/2 ;
            this.setPosition(x, y);
        }
        else {
            Polygon polygon = new Polygon();
            Iterator<Participant> it = p.iterator();
            while(it.hasNext()){
                Participant p1 = it.next();
                polygon.addPoint(p1.getActor().getPosition().x,p1.getActor().getPosition().y);
            }
            Point p1 = PolygonUtils.polygonCenterOfMass(polygon);
            this.setPosition(p1.x, p1.y);
        }
    }
    
    public Point getPosition(){
        if (this.point == null ){
            this.recalcPosition();
        }
        return this.point;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
        this.setChanged();
        this.notifyObservers();
    }
  
}
