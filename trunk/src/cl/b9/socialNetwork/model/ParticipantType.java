/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.model;

import java.io.Serializable;

    public class ParticipantType implements Serializable{
        private String actor;
        private String role;
        public ParticipantType(String actor, String role){
            this.actor = actor;
            this.role = role;
        }
        public String getActorType(){
            return this.actor;
        }
        public String getRole(){
            return this.role;
        }
        public void setRole(String newRole){
            this.role = newRole;
        }
    }
