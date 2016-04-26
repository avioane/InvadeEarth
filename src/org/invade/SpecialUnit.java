/*
 * SpecialUnit.java
 *
 * Created on June 20, 2005, 12:44 PM
 *
 */

package org.invade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.plaf.ListUI;
import org.invade.resources.ResourceAnchor;

public class SpecialUnit {
    
    private String name;
    private Color color;
    private boolean mobile;
    private boolean independentUnit;
    private boolean neutral;
    private int price;
    private int maxOwnable;
    private int maxPerTerritory;
    private int maxTotal;
    private Icon icon;
    private Icon plainIcon;
    private DefaultListCellRenderer listCellRendererComponent;
    
    public static final javax.swing.ListCellRenderer RENDERER = new ListCellRenderer();
    private static final int ICON_SIZE = 15;
    
    public static final SpecialUnit UNKNOWN = new SpecialUnit("Unknown Unit", 0, Color.BLACK,
            false, false, true, -1, -1, -1, "spacestation.png");
    
    public SpecialUnit(String name, int price, final Color color, boolean mobile,
            boolean independentUnit, boolean neutral,
            int maxOwnable, int maxPerTerritory, int maxTotal, String iconName ) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.mobile = mobile;
        this.independentUnit = independentUnit;
        this.neutral = neutral;
        this.maxOwnable = maxOwnable;
        this.maxPerTerritory = maxPerTerritory;
        this.maxTotal = maxTotal;
        URL url = ResourceAnchor.class.getResource("icons/" + iconName);
        icon = new ImageIcon(url);
        
        // Icon would be distored badly if shrunk
        plainIcon = new Icon() {
            public int getIconWidth() { return ICON_SIZE; }
            public int getIconHeight() { return ICON_SIZE; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Color old = g.getColor();
                g.setColor(color);
                g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
                g.setColor(Color.WHITE);
                g.drawRect(x, y, ICON_SIZE, ICON_SIZE);
                g.setColor(old);
            }
        };
        
        setListCellRendererComponent(new DefaultListCellRenderer());
        getListCellRendererComponent().setText(toString());
        getListCellRendererComponent().setIcon(getIcon());
        getListCellRendererComponent().setOpaque(true);
        
    }
    
    public String toString() {
        return name;
    }
    
    public Color getColor() {
        return color;
    }
    
    public boolean isMobile() {
        return mobile;
    }
    
    public int getPrice() {
        return price;
    }
    
    public int getMaxOwnable() {
        return maxOwnable;
    }
    
    public int getMaxPerTerritory() {
        return maxPerTerritory;
    }
    
    public int getMaxTotal() {
        return maxTotal;
    }
    
    public boolean isIndependentUnit() {
        return independentUnit;
    }
    
    public boolean isNeutral() {
        return neutral;
    }
    
    public Icon getIcon() {
        return icon;
    }
    
    public Icon getPlainIcon() {
        return plainIcon;
    }
    
    private static class ListCellRenderer implements javax.swing.ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value == null) {
                return new DefaultListCellRenderer();
            }
            Component component = ((SpecialUnit)value).getListCellRendererComponent();
            if (isSelected) {
                component.setBackground(list.getSelectionBackground());
                component.setForeground(list.getSelectionForeground());
            } else {
                component.setBackground(list.getBackground());
                component.setForeground(list.getForeground());
            }
            return component;
        }
    }

    public DefaultListCellRenderer getListCellRendererComponent() {
        return listCellRendererComponent;
    }

    public void setListCellRendererComponent(DefaultListCellRenderer listCellRendererComponent) {
        this.listCellRendererComponent = listCellRendererComponent;
    }
    
}
