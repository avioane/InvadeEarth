/*
 * PlayersDialog.java
 *
 * Created on July 16, 2005, 2:39 PM
 */

package org.invade;
import com.retrogui.dualrpc.common.CallException;
import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.invade.agents.BetaAgent;
import org.jdom.Document;
import org.invade.agents.LocalHumanAgent;

/**
 *
 * @author  Jonathan Crosmer
 */
public class PlayersDialog extends javax.swing.JDialog implements ConnectionListener {
    
    private JFileChooser fileChooser = new JFileChooser();
    
    private DefaultListModel model;
    private Board board;
    private int requiredNumber;
    private HasClient clientHolder;
    private String basicTitle;
    
    /* These are not used by PlayersDialog, but they will be needed to
     * start the game, so they are passed in.  Not the greatest design;
     * it's here so we do not have to re-write this entire class. */
    private GameLoader gameLoader;
    private boolean newGame;
    private boolean replay;
    private boolean customizeRules;
    private Document document;
    private ConnectionListener connectionListener;
    
    private Color result;
    
    /** Creates new form PlayersDialog */
    public PlayersDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();
        basicTitle = getTitle();
    }
    
    public void showDialog(Board board, HasClient clientHolder,
            GameLoader gameLoader, boolean newGame, boolean replay,
            boolean customizeRules, Document document, ConnectionListener connectionListener) {
        this.gameLoader = gameLoader;
        this.newGame = newGame;
        this.replay = replay;
        this.customizeRules = customizeRules;
        this.document = document;
        
        this.board = board;
        this.clientHolder = clientHolder;
        this.connectionListener = connectionListener;
        
        connectPanel.setClientHolder(clientHolder);
        connectPanel.setConnectionListener(this);
        
        requiredNumber = board.getPlayers().size();
        if( requiredNumber > 0 ) {
            setTitle(basicTitle + " (need " + requiredNumber + ")" );
        } else {
            setTitle(basicTitle);
        }
        
        model = new DefaultListModel();
        playersList.setModel(model);
        
        if( requiredNumber > 1 ) {
            for( Player player : board.getPlayers() ) {
                model.addElement(player);
            }
        } else {
            addButton.doClick();
            addButton.doClick();
        }
        
        
        resetAgents();
        updateAgentBox();
        
        playersList.setSelectedIndex(0);
        
        
        WindowCenteringUtility.center(this);
        updateDisplay();
        
        setVisible(true);
    }
    
    public void updateDisplay() {
        Player player = (Player)playersList.getSelectedValue();
        removeButton.setEnabled(model.size() > 2);
        if( player != null ) {
            playerLabel.setText(player.getName());
            playersList.repaint(); //since a players name has changed, make sure the playersList also displays the change
            playerLabel.setForeground(player.getColor());
            moveUpButton.setEnabled(model.indexOf(player) > 0);
            moveDownButton.setEnabled(model.indexOf(player) < model.size() - 1);
            agentBox.setEnabled(true);
            customizeAgentButton.setEnabled(player.getAgent().isAI());
            selectAgentFor(player);
        } else {
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            removeButton.setEnabled(false);
            agentBox.setEnabled(false);
        }
        startButton.setEnabled(requiredNumber <= 0 || model.size() == requiredNumber);
    }
    
    public void updateAgentBox() {
        agentBox.removeAllItems();
        if( clientHolder.getClient() != null
                && clientHolder.getClient().isConnected() ) {
            try {
                Map<Long, String> map = clientHolder.getClient().getAllConnected();
                for( Entry<Long,String> entry : map.entrySet() ) {
                    agentBox.addItem(new NetworkPlayerItem(entry.getKey(), entry.getValue()));
                }
            } catch(CallException ex) {
                ex.printStackTrace();
            }
        } else {
            agentBox.addItem(new LocalHumanAgent());
        }
        for( Class availableClass : IncludedResources.AGENTS ) {
            try {
                agentBox.addItem(availableClass.newInstance());
            } catch(InstantiationException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    
    private long getSessionID(Object object) {
        if( object instanceof NetworkPlayerItem ) {
            return ((NetworkPlayerItem)object).getSessionID();
        }
        return 0L;
    }
    
    private void selectAgentFor(Player player) {
        Agent agent = player.getAgent();
        long id = player.getSessionID();
        boolean found = false;
        for( int i = 0; i < agentBox.getItemCount() && !found; ++i ) {
            if( (agent instanceof LocalHumanAgent
                    && getSessionID(agentBox.getItemAt(i)) == id)
                    || (agentBox.getItemAt(i).getClass().equals(agent.getClass()) ) ) {
                agentBox.setSelectedIndex(i);
                found = true;
            }
        }
        if( ! found ) {
            agentBox.setSelectedIndex(0);
            player.setSessionID(0);
            player.setAgent(new LocalHumanAgent());
        }
    }
    
    public void resetAgents() {
        for( Player player : board.getPlayers() ) {
            player.setAgent(new BetaAgent());
            player.setSessionID(0);
        }
    }
    
    public void bindUnassignedPlayersToLocalAgents() {
        if( clientHolder.getClient() != null
                && clientHolder.getClient().isConnected() ) {
            for(int i = 0; i < playersList.getModel().getSize(); ++i) {
                Player player = (Player)playersList.getModel().getElementAt(i);
                if( player.getSessionID() == 0 ) {
                    player.setSessionID(clientHolder.getClient().getConnectionID());
                }
            }
        }
    }
    
    public void connectionMade() {
        updateAgentBox();
        updateDisplay();
        resetAgents();
        connectionListener.connectionMade();
    }
    
    public void close(boolean acceptChoices) {
        if( isVisible() ) {
            connectPanel.disposeServerListDialog();
            if( acceptChoices ) {
                bindUnassignedPlayersToLocalAgents();
                board.getPlayers().clear();
                for( int i = 0; i < model.size(); ++i ) {
                    board.getPlayers().add((Player)model.get(i));
                }
                gameLoader.loadFile(newGame, replay, customizeRules, document);
            } else {
                gameLoader.clearGame();
            }
            setVisible(false);
        }
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
        jButton1 = new javax.swing.JButton();
        playersPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        playersList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        playerLabel = new javax.swing.JLabel();
        agentBox = new javax.swing.JComboBox();
        renameButton = new javax.swing.JButton();
        colorButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        customizeAgentButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        saveSetupButton = new javax.swing.JButton();
        loadSetupButton = new javax.swing.JButton();
        networkPanel = new javax.swing.JPanel();
        connectPanel = new org.invade.ConnectPanel();
        closePanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jButton1.setText("jButton1");

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Players");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        playersPanel.setLayout(new java.awt.GridBagLayout());

        playersPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(258, 130));
        playersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        playersList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                playersListValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(playersList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(jScrollPane1, gridBagConstraints);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(addButton, gridBagConstraints);

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(removeButton, gridBagConstraints);

        moveUpButton.setText("Move Up");
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(moveUpButton, gridBagConstraints);

        moveDownButton.setText("Move Down");
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(moveDownButton, gridBagConstraints);

        playerLabel.setText("Player Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 6, 6);
        playersPanel.add(playerLabel, gridBagConstraints);

        agentBox.setMaximumSize(new java.awt.Dimension(27, 22));
        agentBox.setMinimumSize(new java.awt.Dimension(27, 22));
        agentBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                agentBoxItemStateChanged(evt);
            }
        });
        agentBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agentBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(agentBox, gridBagConstraints);

        renameButton.setText("Rename...");
        renameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(renameButton, gridBagConstraints);

        colorButton.setText("Set Color...");
        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(colorButton, gridBagConstraints);

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(refreshButton, gridBagConstraints);

        customizeAgentButton.setText("Customize...");
        customizeAgentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customizeAgentButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        playersPanel.add(customizeAgentButton, gridBagConstraints);

        saveSetupButton.setText("Save...");
        saveSetupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSetupButtonActionPerformed(evt);
            }
        });

        jPanel2.add(saveSetupButton);

        loadSetupButton.setText("Load...");
        loadSetupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSetupButtonActionPerformed(evt);
            }
        });

        jPanel2.add(loadSetupButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        playersPanel.add(jPanel2, gridBagConstraints);

        getContentPane().add(playersPanel);

        networkPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6), javax.swing.BorderFactory.createTitledBorder("Network")));
        networkPanel.add(connectPanel);

        getContentPane().add(networkPanel);

        startButton.setText("Start");
        startButton.setAlignmentX(0.5F);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        closePanel.add(startButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        closePanel.add(cancelButton);

        getContentPane().add(closePanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void agentBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agentBoxActionPerformed

    }//GEN-LAST:event_agentBoxActionPerformed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close(false);
    }//GEN-LAST:event_formWindowClosing
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void loadSetupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSetupButtonActionPerformed
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Document document = XMLHandler.readDocument(fileChooser.getSelectedFile());
            if( document == null ) {
                JOptionPane.showMessageDialog(this, "Could not open file",
                        "Error", JOptionPane.ERROR_MESSAGE, null);
                return;
            }
            try {
                XMLHandler.parseOnlyPlayerList(document, board);
                if( board.getPlayers().size() > 1 ) {
                    DefaultListModel newModel = new DefaultListModel();
                    newModel.removeAllElements();
                    for( Player player : board.getPlayers() ) {
                        player.setSessionID(0);
                        newModel.addElement(player);
                    }
                    model = newModel;
                    playersList.setModel(model);
                    updateDisplay();
                }
            } catch(InvalidXMLException e) {
                JOptionPane.showMessageDialog(this, "Player list data is invalid",
                        "Error", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }//GEN-LAST:event_loadSetupButtonActionPerformed
    
    private void saveSetupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSetupButtonActionPerformed
        if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            List<Player> players = new ArrayList<Player>();
            for( int i = 0; i < model.size(); ++i ) {
                players.add((Player)model.get(i));
            }
            if( ! XMLHandler.writeDocument(fileChooser.getSelectedFile(),
                    XMLHandler.createPlayerListDocument(players) ) ) {
                JOptionPane.showMessageDialog(this, "Could not write to file",
                        "Error", JOptionPane.ERROR_MESSAGE, null);
            }
        }
    }//GEN-LAST:event_saveSetupButtonActionPerformed
    
    private void customizeAgentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customizeAgentButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        CustomizerDialog.showDialog((Frame)getParent(), player.getAgent(), true);
    }//GEN-LAST:event_customizeAgentButtonActionPerformed
    
    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        updateAgentBox();
    }//GEN-LAST:event_refreshButtonActionPerformed
    
    private void agentBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_agentBoxItemStateChanged
        int index = playersList.getSelectedIndex();
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            Object selected = agentBox.getSelectedItem();
            if( selected != null ) {
                if( selected instanceof Agent ) {
                    try {
                        player.setAgent( (Agent)((Agent)selected).getClass().newInstance());
                    } catch(InstantiationException e) {
                        e.printStackTrace();
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
//                    if( clientHolder.getClient() != null
//                            && clientHolder.getClient().isConnected() ) {
//                        player.setSessionID(clientHolder.getClient().getConnectionID());
//                    } else {
                        player.setSessionID(0);
//                    }
                } else {
                    // Agents will be handled later for network games
                    player.setSessionID(getSessionID(selected));
                    player.setAgent(new LocalHumanAgent());
                }
                if( getSessionID(selected) > 0 ) {                    
                    player.setName(((NetworkPlayerItem)selected).getName());
                    playersList.repaint();
                }
            }
            customizeAgentButton.setEnabled(player.getAgent().isAI());
        }
    }//GEN-LAST:event_agentBoxItemStateChanged
    
    /**
     *
     * @param evt
     */
    private void colorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            //this code displays the colorChooser that only has 10 icons in it to choose from
            /*
            final JColorChooser chooser = new JColorChooser();
             
            chooser.setPreviewPanel(new JPanel());
            ColorChooserPanel colorChooserPanel = new ColorChooserPanel();
            colorChooserPanel.setColor(player.getColor());
            AbstractColorChooserPanel panels[] = { colorChooserPanel };
            chooser.setChooserPanels(panels);
             
            JDialog dialog = JColorChooser.createDialog(this,
                    "Choose Player Color", true, chooser, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    result = chooser.getColor();
                }
            }, null);
            dialog.setVisible(true);
             */
            
            result = JColorChooser.showDialog(this, "Choose Player Color", player.getColor());
            if( result != null ) {
                player.setColor(result);
                updateDisplay();
            }
            
        }
    }//GEN-LAST:event_colorButtonActionPerformed
    
    private void renameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            String result = (String)JOptionPane.showInputDialog(this, "New name",  "Rename",
                    JOptionPane.QUESTION_MESSAGE, null, null, player.getName());
            if( result != null ) {
                player.setName(result);
                
                updateDisplay();
            }
        }
    }//GEN-LAST:event_renameButtonActionPerformed
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        close(true);
    }//GEN-LAST:event_startButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        Player player = new Player("Player " + (model.size()+1), Player.getDefaultColor(model.size()));
        player.setAgent(new BetaAgent());
        model.addElement(player);
        updateDisplay();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            int index = model.indexOf(player);
            model.remove(index);
            if( model.size() > index ) {
                playersList.setSelectedIndex(index);
            } else {
                playersList.setSelectedIndex(index - 1);
            }
        }
        updateDisplay();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            int index = model.indexOf(player);
            model.add(index, model.remove(index - 1));
        }
        updateDisplay();
    }//GEN-LAST:event_moveUpButtonActionPerformed
    
    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        Player player = (Player)playersList.getSelectedValue();
        if( player != null ) {
            int index = model.indexOf(player);
            model.add(index, model.remove(index + 1));
        }
        updateDisplay();
    }//GEN-LAST:event_moveDownButtonActionPerformed
    
    private void playersListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_playersListValueChanged
        updateDisplay();
    }//GEN-LAST:event_playersListValueChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox agentBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JButton colorButton;
    private org.invade.ConnectPanel connectPanel;
    private javax.swing.JButton customizeAgentButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadSetupButton;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JPanel networkPanel;
    private javax.swing.JLabel playerLabel;
    private javax.swing.JList playersList;
    private javax.swing.JPanel playersPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton renameButton;
    private javax.swing.JButton saveSetupButton;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
    
    
}


class NetworkPlayerItem {
    private long sessionID;
    private String name;
    public NetworkPlayerItem(long sessionID, String name) {
        this.sessionID = sessionID;
        this.name = name;
    }

    public long getSessionID() {
        return sessionID;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return getName();
    }
}