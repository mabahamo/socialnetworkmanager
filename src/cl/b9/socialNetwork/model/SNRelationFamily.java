/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.Vector;

/**
 *
 * @author manuel
 */
public class SNRelationFamily{

    private EdgeType edgeType = EdgeType.DIRECTED;
    private String name;
    private Vector<ParticipantType> participants = new Vector<ParticipantType>();
    
    public SNRelationFamily(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public ParticipantType addParticipantType(SNActorFamily actor,String rol){
        ParticipantType p = new ParticipantType(actor.getName(),rol);
        participants.add(p);
        return p;
    }
    
    public Vector<ParticipantType> getParticipants(){
        return this.participants;
    }
    
    public void setLabel(String label){
        this.name = label;
    }

    public void setParticipants(Vector<ParticipantType> vector) {
        this.participants = vector;
    }
    
    @Override
    public String toString(){
        return this.name;
    }

    public EdgeType getEdgeType(){
        return this.edgeType;
    }
  
}
