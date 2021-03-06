/*
 * ForcePanel.java
 *
 * Created on June 24, 2005, 2:40 PM
 */

package org.invade;

import java.awt.event.KeyEvent;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author  jcrosm
 */
public class ForcePanel extends javax.swing.JPanel {
    
    private Force available;
    
    /** Creates new form ForcePanel */
    public ForcePanel() {
        initComponents();
        
        specialUnitTable.setTableHeader(null);
        specialUnitTable.getColumnModel().getColumn(0).setMaxWidth(
                specialUnitTable.getRowHeight() );
        
        
        select1.setMnemonic(KeyEvent.VK_1);
        select2.setMnemonic(KeyEvent.VK_2);
        select3.setMnemonic(KeyEvent.VK_3);
        
        setTitle("Force");
        
        setAvailable(new Force());
    }
    
    public void setAvailable(Force available) {
        this.available = available;
        int regular = available.getRegularUnits();
        regularUnitSlider.setMaximum(regular);
        regularUnitSlider.setValue(regular);
        
        select1.setEnabled(regular >= 1);
        select2.setEnabled(regular >= 2);
        select3.setEnabled(regular >= 3);
        
        if( specialUnitTable.getModel() instanceof DefaultTableModel ) {
            DefaultTableModel model = (DefaultTableModel)specialUnitTable.getModel();
            model.setRowCount(0);
            for( SpecialUnit unit : available.getSpecialUnits() ) {
                model.addRow(new Object[]{Boolean.TRUE, unit});
            }
        }
    }
    
    public Force getAvailable() {
        return available;
    }
    
    /* The semantics of "setting" a ForcePanel selection programmatically
     * are not obvious.  For example, suppose the panel had three of the same
     * SpecialUnit available, of which one is to be selected; which one?
     * This method first unselects all special units, then selects one box
     * per instance of a unit in the given Force.  If the requested units are
     * not actually available, they are ignored.
     */
    public void setSelected(Force selected) {
        regularUnitSlider.setValue(selected.getRegularUnits());
        if( specialUnitTable.getModel() instanceof DefaultTableModel ) {
            DefaultTableModel model = (DefaultTableModel)specialUnitTable.getModel();
            for( int i = 0; i < model.getRowCount(); ++i ) {
                model.setValueAt(Boolean.FALSE, i, 0);
            }
            for( SpecialUnit special : selected.getSpecialUnits() ) {
                for( int i = 0; i < model.getRowCount(); ++i ) {
                    if( model.getValueAt(i, 1).equals(special)
                    && model.getValueAt(i, 0).equals(Boolean.FALSE) ) {
                        model.setValueAt(Boolean.TRUE, i, 0);
                        break;
                    }
                }
            }
        }
    }
    
    public Force getSelected() {
        Force result = new Force();
        result.setRegularUnits(regularUnitSlider.getValue());
        for( int i = 0; i < specialUnitTable.getRowCount(); ++i ) {
            if( specialUnitTable.getValueAt(i, 0).equals(Boolean.TRUE) ) {
                result.getSpecialUnits().add( (SpecialUnit)specialUnitTable.getValueAt(i, 1) );
            }
        }
        return result;
    }
    
    public String getTitle() {
        if( getBorder() instanceof TitledBorder ) {
            return ((TitledBorder)getBorder()).getTitle();
        }
        return null;
    }
    
    public void setTitle(String title) {
        if( getBorder() instanceof TitledBorder ) {
            ((TitledBorder)getBorder()).setTitle(title);
        }
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        regularUnitSlider.setEnabled(enabled);
        specialUnitTable.setEnabled(enabled);
    }
    
    public void selectRegularUnits(int regular) {
        Force force = new Force();
        force.addRegularUnits(regular);
        setSelected(force);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        regularUnitSlider = new javax.swing.JSlider();
        jScrollPane = new javax.swing.JScrollPane();
        specialUnitTable = new javax.swing.JTable();
        selectRegularPanel = new javax.swing.JPanel();
        select1 = new javax.swing.JButton();
        select2 = new javax.swing.JButton();
        select3 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createTitledBorder("Force"));
        setMaximumSize(new java.awt.Dimension(168, 168));
        setPreferredSize(new java.awt.Dimension(168, 168));
        regularUnitSlider.setMajorTickSpacing(5);
        regularUnitSlider.setMaximum(0);
        regularUnitSlider.setMinorTickSpacing(1);
        regularUnitSlider.setPaintLabels(true);
        regularUnitSlider.setPaintTicks(true);
        regularUnitSlider.setSnapToTicks(true);
        regularUnitSlider.setMinimumSize(new java.awt.Dimension(128, 47));
        regularUnitSlider.setPreferredSize(new java.awt.Dimension(128, 47));
        regularUnitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                regularUnitSliderStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(regularUnitSlider, gridBagConstraints);

        specialUnitTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "X", "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        specialUnitTable.setFocusable(false);
        specialUnitTable.setRowSelectionAllowed(false);
        specialUnitTable.setShowHorizontalLines(false);
        specialUnitTable.setShowVerticalLines(false);
        jScrollPane.setViewportView(specialUnitTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane, gridBagConstraints);

        select1.setText("1");
        select1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select1ActionPerformed(evt);
            }
        });

        selectRegularPanel.add(select1);

        select2.setText("2");
        select2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select2ActionPerformed(evt);
            }
        });

        selectRegularPanel.add(select2);

        select3.setText("3");
        select3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select3ActionPerformed(evt);
            }
        });

        selectRegularPanel.add(select3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(selectRegularPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void select3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select3ActionPerformed
        selectRegularUnits(3);
    }//GEN-LAST:event_select3ActionPerformed
    
    private void select2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select2ActionPerformed
        selectRegularUnits(2);
    }//GEN-LAST:event_select2ActionPerformed
    
    private void select1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select1ActionPerformed
        selectRegularUnits(1);
    }//GEN-LAST:event_select1ActionPerformed
    
    private void regularUnitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_regularUnitSliderStateChanged
        regularUnitSlider.setToolTipText(String.valueOf(regularUnitSlider.getValue()));
    }//GEN-LAST:event_regularUnitSliderStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSlider regularUnitSlider;
    private javax.swing.JButton select1;
    private javax.swing.JButton select2;
    private javax.swing.JButton select3;
    private javax.swing.JPanel selectRegularPanel;
    private javax.swing.JTable specialUnitTable;
    // End of variables declaration//GEN-END:variables
    
}
