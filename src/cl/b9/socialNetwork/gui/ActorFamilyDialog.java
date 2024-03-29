/*
 * ActorTypeDialog.java
 *
 * Created on October 13, 2008, 4:35 PM
 */

package cl.b9.socialNetwork.gui;

import cl.b9.socialNetwork.model.SNActorFamily;
import cl.b9.socialNetwork.persistence.ObjectManager;
import java.awt.Color;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

/**
 * Di&aacute;logo para creaci&oacute;n y edici&oacute;n de familias de actores.
 * @author  manuel
 */
public class ActorFamilyDialog extends javax.swing.JDialog {

    private boolean edit = false;
    private Color color;
    private SNActorFamily originalFamily;

    
    /** Creates new form ActorTypeDialog */
    public ActorFamilyDialog() {
        super();
        initComponents();
        this.color = RandomColorGenerator.nextColor();
        colorPreview.setOpaque(true);
        colorPreview.setBackground(color);
    }
    
    public ActorFamilyDialog(int familyId){
        super();        
        edit = true;
        initComponents();

        originalFamily = ObjectManager.getInstance().getFamily(familyId);
        this.txtName.setText(originalFamily.getName());
        color = originalFamily.getColor();
        colorPreview.setOpaque(true);
        colorPreview.setBackground(color);
        this.btnCreate.setText("Editar");
        this.setTitle("Editar Familia");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnCreate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        colorPreview = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cl.b9.socialNetwork.SocialNetworksApp.class).getContext().getResourceMap(ActorFamilyDialog.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtName.setName("txtName"); // NOI18N
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        btnCreate.setMnemonic('c');
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

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        colorPreview.setText(resourceMap.getString("colorPreview.text")); // NOI18N
        colorPreview.setName("colorPreview"); // NOI18N
        colorPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                colorPreviewMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(193, 193, 193)
                        .addComponent(jLabel3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(colorPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(colorPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnCreate))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
    
    try {
        if (edit){
            if (!color.equals(originalFamily.getColor())){
                int o = JOptionPane.showConfirmDialog(this, "El nuevo color reemplazará a todos los integrantes de esta familia, esta seguro que desea continuar?", "Actualizar familia", JOptionPane.YES_NO_OPTION);
                if (o != JOptionPane.YES_OPTION){
                    return;
                }
            }
            originalFamily.setColor(color);
            originalFamily.setName(txtName.getText());
            originalFamily.notifyObservers();
        }   
        else {
            SNActorFamily actor = ObjectManager.getInstance().createActorFamily(txtName.getText(), color);
        }
            this.dispose();
    } catch (SQLException ex) {
            Popup.showError(this, ex.getLocalizedMessage());
            Logger.getLogger(ActorFamilyDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    
}//GEN-LAST:event_btnCreateActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
this.dispose();
}//GEN-LAST:event_btnCancelActionPerformed

private void colorPreviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colorPreviewMouseClicked
    Color c = JColorChooser.showDialog(this, "Color familia " + txtName.getText(), Color.pink);
    if (c!=null){
        this.color = c;
        this.colorPreview.setBackground(c);
    }
}//GEN-LAST:event_colorPreviewMouseClicked

private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
    btnCreate.doClick();
}//GEN-LAST:event_txtNameActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreate;
    private javax.swing.JLabel colorPreview;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

}
