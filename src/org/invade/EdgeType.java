/*
 * EdgeType.java
 *
 * Created on June 22, 2005, 11:13 AM
 *
 */

package org.invade;

import java.awt.Stroke;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.*;
import java.awt.Color;

/**
 * Primary edges are those that are most important visually; for example, edges 
 * between water colonies.  Secondary edges may be understood through other
 * visual cues, like physical adjacency of land or moon territories.
 * Wrapping edges appear to "wrap" around the visible board, moving out of the
 * board on one side and reappearing on the other.
 * @author jcrosm
 */
public enum EdgeType {    
    PRIMARY( "Primary", new BasicStroke(), Color.WHITE ),
    SECONDARY( "Secondary", new BasicStroke(1.0f, CAP_BUTT, JOIN_MITER, 10.0f,
    new float[]{5.0f, 5.0f}, 0.0f), Color.WHITE ),
    WRAP_HORIZONTAL( "Wrapping", new BasicStroke(1.0f, CAP_BUTT, JOIN_MITER, 10.0f,
    new float[]{5.0f, 5.0f}, 0.0f), Color.WHITE ),
    MAELSTROM( "Maelstrom (Impassable)", new BasicStroke(1.0f, CAP_BUTT, JOIN_MITER, 10.0f,
    new float[]{2.0f, 2.0f}, 0.0f), Color.BLUE );    
    
    private String name;
    private Stroke stroke;
    private Color color;
    
    EdgeType(String name, Stroke stroke, Color color) {
        this.name = name;
        this.stroke = stroke;
        this.color = color;
    }
    
    public String toString() {
        return name;
    }
    
    public Stroke getStroke() {
        return stroke;
    }
    
    public Color getColor() {
        return color;
    }

    public boolean isWrapHorizontal() {
        return equals(WRAP_HORIZONTAL);
    }
    
    public boolean isPassable() {
        return ! equals(MAELSTROM);
    }
    
}
