/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import cl.b9.socialNetwork.persistence.ObjectManager;

    /**
     * @author manuel
     */public class Participant{
        private int actorId, relationId;
        private String role;
        
        public Participant(int actorId, String role, int relationId){
            this.actorId = actorId;
            this.role = role;
            this.relationId = relationId;
        }
        
        public SNActor getActor(){
            return ObjectManager.getInstance().getActor(actorId);
        }
        
        public SNRelation getRelation(){
            return ObjectManager.getInstance().getRelation(relationId);
        }
        
        public String getRole(){
            return this.role;
        }
        
        public void setRole(String role){
            this.role = role;
        }
        
    }
