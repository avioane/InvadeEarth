/*
 * ConnectDialog.java
 *
 * Created on July 25, 2005, 2:42 PM
 */

package org.invade;

/**
 *
 * @author  jcrosm
 */
public class ConnectDialog extends javax.swing.JDialog implements ConnectionListener {
    
    private ConnectionListener connectionListener;
    
    /** Creates new form ConnectDialog */
    public ConnectDialog(java.awt.Frame parent, HasClient clientHolder, ConnectionListener connectionListener) {
        super(parent);
        initComponents();
        connectPanel.setClientHolder(clientHolder);
        connectPanel.setConnectionListener(this);
        this.connectionListener = connectionListener;
        WindowCenteringUtility.center(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        connectPanel = new org.invade.ConnectPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Connect");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        connectPanel.setMinimumSize(new java.awt.Dimension(1, 1));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.invade.ConnectPanel connectPanel;
    // End of variables declaration//GEN-END:variables
    
    public void connectionMade() {
        connectionListener.connectionMade();
        close();        
    }
    
    public void close() {
        connectPanel.disposeServerListDialog();
        dispose();
    }
    
}
