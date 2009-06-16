/*
 * NewRelationDialog.java
 *
 * Created on September 7, 2008, 7:15 AM
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.SNDirector;
import cl.b9.socialNetwork.model.SNActor;
import cl.b9.socialNetwork.model.SNRelation;
import java.awt.Color;
import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;

/**
 *
 * @author  manuel
 */
public class RelationDialog extends javax.swing.JDialog {



    //private Participant p1,p2;
    private SNActor actor1,actor2;
    private Color color = Color.BLACK;
    private boolean edit = false;
    private SNRelation relation;

    public RelationDialog(SNRelation relation) {
        super((Frame)null, false);
        edit = true;
        this.relation = relation;
        initComponents();
        this.actor1 = relation.getParticipants().get(0).getActor();
        this.actor2 = relation.getParticipants().get(1).getActor();
        txtRolActor1.setText(relation.getParticipants().get(0).getRole());
        txtRolActor2.setText(relation.getParticipants().get(1).getRole());
        txtRelation.setText(relation.getLabel());
        color = relation.getColor();
        lblColor.setOpaque(true);
        lblColor.setForeground(color);
        lblColor.setBackground(color);
        lblColor.setText(" ");
        btnCreate.setText("Aplicar");
    }
    
    public RelationDialog(Frame parent, SNActor actor1, SNActor actor2){
        super(parent,true);
        initComponents();
  //      miniGraph = new GraphPanel(parent,200,300);
        this.actor1 = actor1;
        this.actor2 = actor2;
        color = RandomColorGenerator.nextColor();
        lblColor.setOpaque(true);
        lblColor.setBackground(color);
        lblColor.setForeground(color);
        lblColor.setText(" ");
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtRolActor1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtRolActor2 = new javax.swing.JTextField();
        btnCreate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtRelation = new javax.swing.JTextField();
        chkboxDirected = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        lblColor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cl.b9.socialNetwork.SocialNetworksApp.class).getContext().getResourceMap(RelationDialog.class);
        txtRolActor1.setText(resourceMap.getString("txtRolActor1.text")); // NOI18N
        txtRolActor1.setName("txtRolActor1"); // NOI18N
        txtRolActor1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRolActor1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRolActor1FocusLost(evt);
            }
        });
        txtRolActor1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtRolActor1InputMethodTextChanged(evt);
            }
        });
        txtRolActor1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRolActor1KeyReleased(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtRolActor2.setText(resourceMap.getString("txtRolActor2.text")); // NOI18N
        txtRolActor2.setName("txtRolActor2"); // NOI18N
        txtRolActor2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRolActor2FocusLost(evt);
            }
        });
        txtRolActor2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRolActor2KeyReleased(evt);
            }
        });

        btnCreate.setText(resourceMap.getString("btnCreate.text")); // NOI18N
        btnCreate.setName("btnCreate"); // NOI18N
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtRelation.setText(resourceMap.getString("txtRelation.text")); // NOI18N
        txtRelation.setName("txtRelation"); // NOI18N
        txtRelation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRelationFocusLost(evt);
            }
        });
        txtRelation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRelationKeyReleased(evt);
            }
        });

        chkboxDirected.setSelected(true);
        chkboxDirected.setText(resourceMap.getString("chkboxDirected.text")); // NOI18N
        chkboxDirected.setEnabled(false);
        chkboxDirected.setName("chkboxDirected"); // NOI18N
        chkboxDirected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkboxDirectedActionPerformed(evt);
            }
        });

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        lblColor.setText(resourceMap.getString("lblColor.text")); // NOI18N
        lblColor.setName("lblColor"); // NOI18N
        lblColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(chkboxDirected)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtRolActor1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRolActor2, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addGap(89, 89, 89)
                        .addComponent(btnCancel)
                        .addGap(123, 123, 123))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblColor, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRolActor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRolActor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRelation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(chkboxDirected)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jLabel6))
                    .addComponent(lblColor, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnCreate))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void txtRolActor1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtRolActor1InputMethodTextChanged

}//GEN-LAST:event_txtRolActor1InputMethodTextChanged

private void txtRolActor1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRolActor1FocusLost
//    p1.setRole(txtRolActor1.getText());
   // miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRolActor1FocusLost

private void txtRolActor1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRolActor1FocusGained

}//GEN-LAST:event_txtRolActor1FocusGained

private void txtRolActor1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRolActor1KeyReleased
//    p1.setRole(txtRolActor1.getText());
//    miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRolActor1KeyReleased

private void txtRolActor2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRolActor2KeyReleased
//    p2.setRole(txtRolActor2.getText());
   // miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRolActor2KeyReleased

private void txtRolActor2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRolActor2FocusLost
//    p2.setRole(txtRolActor2.getText());
   // miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRolActor2FocusLost

private void txtRelationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRelationKeyReleased
    //previewRelation.setLabel(txtRelation.getText());
 //   miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRelationKeyReleased

private void txtRelationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRelationFocusLost
    //previewRelation.setLabel(txtRelation.getText());
//    miniGraph.getViewer().repaint();
}//GEN-LAST:event_txtRelationFocusLost

private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        try {
            if (edit){
                relation.setColor(color);
                SNDirector.getInstance().updateRelation(relation);
                this.dispose();
            }
            else {
                if (!SNDirector.getInstance().existsRelation(txtRelation.getText())){
                    SNDirector.getInstance().createRelation(txtRelation.getText(), actor1, txtRolActor1.getText(), actor2, txtRolActor2.getText(),color);
                    this.dispose();
                }
                else {
                    Popup.showError(this, "Ya existe una relación con este nombre: " + txtRelation.getText());
                }
            }
        } catch (Exception ex) {
            Popup.showError(this, "Error al crear relación :\n" + ex.getLocalizedMessage());
            
            Logger.getLogger(RelationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}//GEN-LAST:event_btnCreateActionPerformed

private void chkboxDirectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkboxDirectedActionPerformed
 //   if (chkboxDirected.isSelected())
 //       r.setEdgeType(EdgeType.DIRECTED);    
 //   else 
 //       r.setEdgeType(EdgeType.UNDIRECTED);
 //   miniGraph.repaint();
}//GEN-LAST:event_chkboxDirectedActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    this.dispose();
}//GEN-LAST:event_btnCancelActionPerformed

private void lblColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorMouseClicked
    Color aux = JColorChooser.showDialog(this, "Color para relacion " + txtRelation.getText(), color);
    if (aux!=null){
        color = aux;
    }
    lblColor.setBackground(color);
    lblColor.setForeground(color);
}//GEN-LAST:event_lblColorMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreate;
    private javax.swing.JCheckBox chkboxDirected;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblColor;
    private javax.swing.JTextField txtRelation;
    private javax.swing.JTextField txtRolActor1;
    private javax.swing.JTextField txtRolActor2;
    // End of variables declaration//GEN-END:variables

}
