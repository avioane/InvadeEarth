/*
 * CustomizerDialog.java
 *
 * Created on July 30, 2005, 8:59 AM
 */

package org.invade;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author  Jonathan Crosmer
 */
public class CustomizerDialog extends javax.swing.JDialog {
    
    public static Object showDialog(Frame parent, Object initialObject,
            Iterable<Class> availableClasses) {
        CustomizerDialog dialog = new CustomizerDialog(parent, initialObject, availableClasses);
        dialog.setVisible(true);
        return dialog.currentObject;
    }
    
    public static void showDialog(Frame parent, Object modifiableObject, boolean editable) {
        CustomizerDialog dialog = new CustomizerDialog(parent, modifiableObject, editable);
        dialog.setVisible(true);
    }
    
    private Object currentObject;
    private List<Class> propertyTypes;
    boolean comboBoxReady;
    
    private List<Object> availableObjects = null;
    
    /**
     * Creates new form CustomizerDialog
     */
    private CustomizerDialog(java.awt.Frame parent, Object initialObject,
            Iterable<Class> availableClasses) {
        super(parent);
        initComponents();
        
        if( availableClasses != null ) {
            availableObjects = new ArrayList<Object>();
            for( Class availableClass : availableClasses ) {
                try {
                    availableObjects.add(availableClass.newInstance());
                } catch(InstantiationException e) {
                } catch(IllegalAccessException e) {
                }
            }
        }
        
        currentObject = initialObject;
        comboBoxReady = false;
        propertyTypes = new ArrayList<Class>();
        
        propertyTable.getColumnModel().getColumn(0).setMinWidth(128);
        
        updateClassComboBox();
        updatePropertyTable();
        
        WindowCenteringUtility.center(this);
    }
    
    private CustomizerDialog(java.awt.Frame parent, Object modifiableObject,
            boolean editable) {
        this(parent, modifiableObject, null);
        classComboBox.setEnabled(false);
        if( ! editable ) {
            propertyTable.setEnabled(false);
            resetButton.setEnabled(false);
            applyButton.setEnabled(false);
        }
    }
    
    public void updateClassComboBox() {
        comboBoxReady = false;
        classComboBox.removeAllItems();
        if( availableObjects == null ) {
            classComboBox.setEnabled(false);
            classComboBox.addItem(currentObject);
        } else {
            classComboBox.setEnabled(true);
            for( Object availableObject : availableObjects ) {
                if(availableObject.getClass().equals(currentObject.getClass())) {
                    classComboBox.addItem(currentObject);
                    classComboBox.setSelectedItem(currentObject);
                } else {
                    classComboBox.addItem(availableObject);
                }
            }
        }
        comboBoxReady = true;
    }
    
    public void updatePropertyTable() {
        DefaultTableModel model = (DefaultTableModel)propertyTable.getModel();
        model.setRowCount(0);
        propertyTypes.clear();
        Method methods[] = currentObject.getClass().getMethods();
        for( Method method : methods ) {
            try {
                if( method.isAnnotationPresent(SuppressedProperty.class)
                && method.getAnnotation(SuppressedProperty.class).value() ) {
                    continue;
                }
                String propertyName = null;
                if( method.getName().startsWith("get") ) {
                    propertyName = method.getName().substring(3);
                } else if( method.getName().startsWith("is") ) {
                    propertyName = method.getName().substring(2);
                }
                if( propertyName != null ) {
                    if( method.getReturnType().equals(int.class)
                    || method.getReturnType().equals(boolean.class)
                    || method.getReturnType().equals(double.class)
                    || method.getReturnType().equals(String.class) ) {
                        model.addRow(new Object[]{propertyName, method.invoke(currentObject)});
                    } else if( method.getReturnType().equals(int[].class) ) {
                        model.addRow(new Object[]{propertyName, toString((int[])method.invoke(currentObject))});
                    } else if( method.getReturnType().equals(int[][].class) ) {
                        model.addRow(new Object[]{propertyName, toString((int[][])method.invoke(currentObject))});
                    } else {
                        continue;
                    }
                    propertyTypes.add(method.getReturnType());
                }
            } catch(IllegalAccessException e) {
            } catch(InvocationTargetException e) {}
        }
        pack();
    }
    
    public void save() {
        DefaultTableModel model = (DefaultTableModel)propertyTable.getModel();
        Method methods [] = currentObject.getClass().getMethods();
        for( int i = 0; i < model.getRowCount(); ++i ) {
            String propertyName = (String)model.getValueAt(i, 0);
            for( Method method : methods ) {
                try {
                    if( method.getName().equals("set" + propertyName) ) {
                        String value = propertyTable.getValueAt(i, 1).toString();
                        Class type = propertyTypes.get(i);
                        if( type.equals(int.class) ) {
                            method.invoke(currentObject, Integer.parseInt(value));
                        } else if( type.equals(boolean.class) ) {
                            method.invoke(currentObject, Boolean.parseBoolean(value));
                        } else if( type.equals(double.class) ) {
                            method.invoke(currentObject, Double.parseDouble(value));
                        } else if( type.equals(String.class) ) {
                            method.invoke(currentObject, value);
                        } else if( type.equals(int[].class) ) {
                            method.invoke(currentObject, parseIntArray(value));
                        } else if( type.equals(int[][].class) ) {
                            method.invoke(currentObject, (Object)parseIntArray2D(value));
                        }
                    }
                } catch(IllegalAccessException e) {
                } catch(InvocationTargetException e) {
                } catch(NumberFormatException e) {}
            }
        }
    }
    
    public String toString(int values[]) {
        if( values.length == 0 ) {
            return "";
        }
        StringBuilder result = new StringBuilder(Integer.toString(values[0]));
        for( int i = 1; i < values.length; ++i ) {
            result.append(", ").append(values[i]);
        }
        return result.toString();
    }
    
    public int[] parseIntArray(String string) {
        String values[] = string.split(",");
        List<Integer> integers = new ArrayList<Integer>();
        for( String value : values ) {
            integers.add(Integer.parseInt(value.trim()));
        }
        int result[] = new int[integers.size()];
        for(int i = 0; i < result.length; ++i) {
            result[i] = integers.get(i);
        }
        return result;
    }
    
    public String toString(int values[][]) {
        if( values.length == 0 ) {
            return "";
        }
        StringBuilder result = new StringBuilder(toString(values[0]));
        for( int i = 1; i < values.length; ++i ) {
            result.append("; ").append(toString(values[i]));
        }
        return result.toString();
    }
    
    public int[][] parseIntArray2D(String string) {
        String values[] = string.split(";");
        List<int[]> arrays = new ArrayList<int[]>();
        for( String value : values ) {
            arrays.add(parseIntArray(value.trim()));
        }
        int result[][] = new int[arrays.size()][];
        for(int i = 0; i < result.length; ++i) {
            result[i] = arrays.get(i);
        }
        return result;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        propertyTable = new javax.swing.JTable();
        okButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        classComboBox = new javax.swing.JComboBox();
        resetButton = new javax.swing.JButton();
        spacingPanel = new javax.swing.JPanel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Rules");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 300));
        propertyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(propertyTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        okButton.setText("Close");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(okButton, gridBagConstraints);

        applyButton.setText("Check");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(applyButton, gridBagConstraints);

        classComboBox.setMinimumSize(new java.awt.Dimension(196, 22));
        classComboBox.setPreferredSize(new java.awt.Dimension(196, 22));
        classComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                classComboBoxItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(classComboBox, gridBagConstraints);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(resetButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(spacingPanel, gridBagConstraints);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        save();
        dispose();
    }//GEN-LAST:event_formWindowClosing
    
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        try {
            currentObject = currentObject.getClass().newInstance();
            updatePropertyTable();
        } catch(InstantiationException e) {
        } catch(IllegalAccessException e) {
        }
    }//GEN-LAST:event_resetButtonActionPerformed
    
    private void classComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_classComboBoxItemStateChanged
        if( comboBoxReady ) {
            Object selected = classComboBox.getSelectedItem();
            if( ! selected.getClass().equals(currentObject.getClass()) ) {
                currentObject = selected;
                updatePropertyTable();
            }
        }
    }//GEN-LAST:event_classComboBoxItemStateChanged
    
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        save();
        updatePropertyTable();
    }//GEN-LAST:event_applyButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JComboBox classComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JTable propertyTable;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel spacingPanel;
    // End of variables declaration//GEN-END:variables
    
}
