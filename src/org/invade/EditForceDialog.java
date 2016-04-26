/*
 * EditForceDialog.java
 *
 * Created on February 23, 2006, 4:39 PM
 */

package org.invade;

import javax.swing.DefaultListModel;
import org.invade.rules.DefaultRules;

/**
 *
 * @author  jcrosm
 */
public class EditForceDialog extends javax.swing.JDialog {
    
    private Force force;
    private Territory territory;
    private DefaultListModel model;
    
    
    /** Creates new form EditForceDialog */
    public EditForceDialog(java.awt.Frame parent, Territory territory, Rules rules) {
        super(parent);
        initComponents();
        
        this.territory = territory;
        force = (Force)territory.getForce().clone();
        
        unitComboBox.removeAllItems();
        for(SpecialUnit unit : rules.getUnitsUsed()) {
            unitComboBox.addItem(unit);
        }
        unitComboBox.setRenderer(SpecialUnit.RENDERER);
        
        model = new DefaultListModel();
        for(SpecialUnit unit : force.getSpecialUnits()) {
            model.addElement(unit);
        }
        unitList.setModel(model);
        unitList.setCellRenderer(SpecialUnit.RENDERER);
        
        regularUnitField.setText(Integer.toString(force.getRegularUnits()));
        
        WindowCenteringUtility.center(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        regularUnitField = new javax.swing.JTextField();
        regularUnitLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        unitList = new javax.swing.JList();
        unitComboBox = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Force");
        setModal(true);

        regularUnitLabel.setText("Regular units:  ");

        jScrollPane1.setViewportView(unitList);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, 0, 0, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(regularUnitLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(regularUnitField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                    .add(unitComboBox, 0, 143, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                        .add(removeButton))
                    .add(layout.createSequentialGroup()
                        .add(okButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 31, Short.MAX_VALUE)
                        .add(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(regularUnitLabel)
                    .add(regularUnitField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeButton)
                    .add(addButton))
                .add(27, 27, 27)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        Object selection = unitComboBox.getSelectedItem();
        if(selection != null) {
            model.addElement(selection);
            force.getSpecialUnits().add((SpecialUnit)selection);
        }
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Object selection = unitList.getSelectedValue();
        if(selection != null) {
            model.removeElement(selection);
            force.getSpecialUnits().remove((SpecialUnit)selection);
        }
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            force.setRegularUnits(Integer.parseInt(regularUnitField.getText()));
        } catch (NumberFormatException ex) {}
        territory.getForce().clear();
        territory.getForce().add(force);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField regularUnitField;
    private javax.swing.JLabel regularUnitLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox unitComboBox;
    private javax.swing.JList unitList;
    // End of variables declaration//GEN-END:variables
    
}