
package org.invade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import javax.swing.*;


public class ViewMenu extends JMenu implements ActionListener {
    
    private Component window;
    private MapCanvas mapCanvas;
    
    private JMenuItem setZoom;
    private JMenuItem zoomIn;
    private JMenuItem zoomOut;
    private JMenuItem zoom100Percent;
    private JMenu showEdgeTypesMenu;
    private JMenuItem showNoEdges;
    private JMenuItem showAllEdges;
    private JMenuItem neutralColor;
    private JMenuItem setBackgroundImage;
    private JMenuItem setNoBackgroundImage;
    
    private JFileChooser fileChooser;
    
    public static final double MIN_ZOOM = 0.015625D;
    public static final double MAX_ZOOM = 16D;
    public static final double ZOOM_FACTOR = 1.5D;
    
    public ViewMenu(Component window, MapCanvas mapCanvas) {
        
        super("View");
        this.window = window;
        this.mapCanvas = mapCanvas;
        
        fileChooser = new JFileChooser();
        
        setZoom = new JMenuItem("Set Zoom...");
        setZoom.addActionListener(this);
//        setZoom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK,
//                InputEvent.CTRL_MASK));
        add(setZoom);
        
        zoomIn = new JMenuItem("Zoom In");
        zoomIn.addActionListener(this);
//        zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
//                InputEvent.CTRL_MASK));
        add(zoomIn);
        
        zoomOut = new JMenuItem("Zoom Out");
        zoomOut.addActionListener(this);
//        zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
//                InputEvent.CTRL_MASK));
        add(zoomOut);
        
        zoom100Percent = new JMenuItem("Zoom to 100%");
        zoom100Percent.addActionListener(this);
//        zoom100Percent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK,
//                InputEvent.CTRL_MASK));
        add(zoom100Percent);
        
        addSeparator();
        
        addBooleanItem("Show Borders", "ShowBorders");        
        addBooleanItem("Highlight Selected Territory", "ShowHighlight");        
        addBooleanItem("Draw Borders Over Image", "ShowBordersOverImage");
        addSeparator();        
        addBooleanItem("Fill Territories", "ShowShapes");
        addBooleanItem("Show Continents", "ShowContinents");        
        addBooleanItem("Show Territory Types", "ShowTerritoryTypes");
        addSeparator();        
        addBooleanItem("Show Territory Names", "ShowNames");
        addBooleanItem("Show Center", "ShowCenter");        
        addBooleanItem("Show Landing Sites", "ShowLandingSites");
        addSeparator();        
        addBooleanItem("Indicate Owner", "ShowOwnerCircle");
        addBooleanItem("Show Number of Units", "ShowUnits");
        addBooleanItem("Show Special Unit Icons", "ShowSpecialIcons");
        addBooleanItem("Count All Mobile Units", "IncludeSpecialUnitsInCount");        
        addSeparator();
        addBooleanItem("Show Territory Status", "IconsEnabled");
        addBooleanItem("Scroll to Selected Territory", "ScrollToHighlighted");
        addSeparator();
        
        showEdgeTypesMenu = new JMenu("Show Edges");
        for( final EdgeType type : EdgeType.values() ) {
            final JMenuItem item = new JCheckBoxMenuItem();
            item.setText(type.toString());
            item.setSelected(mapCanvas.getShowEdgeTypes().contains(type));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    updateShowEdgeTypes(type, item.isSelected());
                }
            });
            showEdgeTypesMenu.add(item);
        }
        add(showEdgeTypesMenu);
        
        showNoEdges = new JMenuItem("Show No Edges");
        showNoEdges.addActionListener(this);
        add(showNoEdges);
        
        showAllEdges = new JMenuItem("Show All Edges");
        showAllEdges.addActionListener(this);
        add(showAllEdges);
        
        addSeparator();
        
        addColorItem("Set Text Color...", "TextColor");
        addColorItem("Set Border Color...", "BorderColor");
        addColorItem("Set Highlight Color...", "HighlightColor");
        addColorItem("Set Center Color...", "CenterColor");
        addColorItem("Set Background Color...", "BackgroundColor");
        addColorItem("Set Plague Color...", "PlagueColor");
        
        neutralColor = new JMenuItem("Set Neutral Color...");
        neutralColor.setIcon(new ColorIcon() {
            public Color getColor() {
                return Player.NEUTRAL.getColor();
            }
        });
        neutralColor.addActionListener(this);
        add(neutralColor);
        
        addSeparator();
        
        setBackgroundImage = new JMenuItem("Set Background Image...");
        setBackgroundImage.addActionListener(this);
        add(setBackgroundImage);
        
        setNoBackgroundImage = new JMenuItem("Clear Background");
        setNoBackgroundImage.addActionListener(this);
        setNoBackgroundImage.setEnabled(false);
        add(setNoBackgroundImage);
        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == setZoom) {
            Object result = JOptionPane.showInputDialog(window, "Zoom factor", "Set Zoom", 3, null, null, Double.valueOf(mapCanvas.getZoomFactor()));
            if(result != null) {
                try {
                    mapCanvas.setZoomFactor(Math.max(0.015625D, Math.min(16D, Double.parseDouble(result.toString()))));
                } catch(NumberFormatException e) {
                } // Ignore bad user input; keep previous value
            }
        } else if(actionEvent.getSource() == zoomIn) {
            mapCanvas.setZoomFactor(Math.min(16D, mapCanvas.getZoomFactor() * 1.5D));
        } else if(actionEvent.getSource() == zoomOut) {
            mapCanvas.setZoomFactor(Math.max(0.015625D, mapCanvas.getZoomFactor() / 1.5D));
        } else if(actionEvent.getSource() == zoom100Percent) {
            mapCanvas.setZoomFactor(1.0D);
        } else if(actionEvent.getSource() == showNoEdges) {
            mapCanvas.setShowEdgeTypes(EnumSet.noneOf(EdgeType.class));
            for( int i = 0; i < showEdgeTypesMenu.getItemCount(); ++i ) {
                showEdgeTypesMenu.getItem(i).setSelected(false);
            }
        } else if(actionEvent.getSource() == showAllEdges) {
            mapCanvas.setShowEdgeTypes(EnumSet.allOf(EdgeType.class));
            for( int i = 0; i < showEdgeTypesMenu.getItemCount(); ++i ) {
                showEdgeTypesMenu.getItem(i).setSelected(true);
            }
        } else if(actionEvent.getSource() == neutralColor) {
            Player.NEUTRAL.setColor(chooseColor(Player.NEUTRAL.getColor()));
        } else if(actionEvent.getSource() == setBackgroundImage) {
            if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
                mapCanvas.setBackgroundImage(Toolkit.getDefaultToolkit()
                .createImage(fileChooser.getSelectedFile().toString()));
                setNoBackgroundImage.setEnabled(true);
            }
        } else if(actionEvent.getSource() == setNoBackgroundImage) {
            mapCanvas.setBackgroundImage(null);
            setNoBackgroundImage.setEnabled(false);
        }
        mapCanvas.repaint();
    }
    
    public Color chooseColor(Color initial) {
        Color result = JColorChooser.showDialog(window, "Select Color", initial);
        if(result == null) {
            return initial;
        } else {
            return result;
        }
    }
    
    public void updateShowEdgeTypes(EdgeType edgeType, boolean show) {
        if(show) {
            mapCanvas.getShowEdgeTypes().add(edgeType);
        } else {
            mapCanvas.getShowEdgeTypes().remove(edgeType);
        }
        mapCanvas.repaint();
    }
    
    /* Adds a JCheckBoxMenuItem to toggle a boolean value using "get" and "is"
     * prefixed methods from mapCanvas.  Note that memberName should begin
     * with a capital letter, since the prefixes will be the first "words"
     * in the method names. */
    public void addBooleanItem(String menuName, String memberName) {
        String isMethodName = "is" + memberName;
        final String setMethodName = "set" + memberName;
        for( Method method : mapCanvas.getClass().getMethods() ) {
            if( method.getName().equals( isMethodName ) ) {
                try {
                    final Method finalMethod = method;
                    final JCheckBoxMenuItem item =
                            new JCheckBoxMenuItem(menuName, 
                            (Boolean)method.invoke(mapCanvas, new Object[]{}) );
                    item.addActionListener( new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            if( actionEvent.getSource() == item ) {
                                try {
                                    Method setMethod = ViewMenu.this.mapCanvas.getClass().getMethod(
                                            setMethodName, Boolean.TYPE );
                                    setMethod.invoke(mapCanvas, item.isSelected());
                                    mapCanvas.repaint();
                                } catch(NoSuchMethodException e) { // Should never happen
                                } catch(IllegalAccessException e) { // Should never happen
                                } catch(InvocationTargetException e) { // Should never happen
                                }
                            }
                        }
                    });
                    add(item);
                } catch(IllegalAccessException e) { // Should never happen
                } catch(InvocationTargetException e) { // Should never happen
                }
            }
        }
    }
    
    /* Adds a JMenuItem with a color icon to let the user set a particular
     * color value using "get" and "set" prefixed methods from mapCanvas.
     * Note that memberName should begin with a capital letter, since the
     * prefixes will be the first "words" in the method names. */
    public void addColorItem(String menuName, String memberName) {
        String getMethodName = "get" + memberName;
        final String setMethodName = "set" + memberName;
        for( final Method method : mapCanvas.getClass().getMethods() ) {
            if( method.getName().equals( getMethodName ) ) {
                final JMenuItem item = new JMenuItem(menuName);
                item.setIcon(new ColorIcon() {
                    public Color getColor() {
                        try {
                            return (Color)method.invoke(mapCanvas, new Object[]{});
                        } catch(IllegalAccessException e) { // Should never happen
                        } catch(InvocationTargetException e) { // Should never happen
                        }
                        return null;
                    }
                });
                item.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        if( actionEvent.getSource() == item ) {
                            try {
                                Method setMethod = ViewMenu.this.mapCanvas.getClass().getMethod(
                                        setMethodName, Color.class );
                                Color old = (Color)method.invoke(mapCanvas, new Object[]{});
                                setMethod.invoke(mapCanvas, chooseColor(old) );
                                mapCanvas.repaint();
                            } catch(NoSuchMethodException e) { // Should never happen
                            } catch(IllegalAccessException e) { // Should never happen
                            } catch(InvocationTargetException e) { // Should never happen
                            }
                        }
                    }
                });
                add(item);
            }
        }
    }
}
