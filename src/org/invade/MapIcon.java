/*
 * MapIcons.java
 *
 * Created on February 22, 2006, 9:18 AM
 *
 */

package org.invade;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.invade.resources.ResourceAnchor;

/* The primary purpose of this class is to hold static fields with Icon objects. */
public class MapIcon extends ImageIcon {
    
    public static final Icon DEVASTATION_MARKER = new MapIcon("nuked.png");
    public static final Icon SUNK_MARKER = new MapIcon("sunk.png");
    public static final Icon DARK_GRAY  = new MapIcon("darkgray.png");
    public static final Icon AMOEBA  = new MapIcon("amoeba.png");
    public static final Icon ATTACK = new MapIcon("attack.gif");
    public static final Icon DEFENSE = new MapIcon("defense.gif");
    public static final Icon PLAGUE = new MapIcon("plague.png");
    public static final Icon FLAG = new MapIcon("exclyellow.gif");
    
    private MapIcon(String iconName) {
        super(ResourceAnchor.class.getResource("icons/" + iconName));
    }
    
}
