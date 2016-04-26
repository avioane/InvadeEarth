/*
 * TerritoryType.java
 *
 * Created on June 20, 2005, 12:46 PM
 *
 */

package org.invade;

import java.awt.Color;

public enum TerritoryType {
    LAND(new Color(32, 196, 0), "Land", "Continent", "Continents", "Land" ),
    WATER(Color.BLUE, "Water", "Water Colony", "Water Colonies", "Water" ),
    MOON(Color.MAGENTA, "Lunar", "Lunar Colony", "Lunar Colonies", "Moon" ),
    UNDERWORLD(new Color(48, 48, 48), "Underworld", "Underworld Circle", 
    "Underworld Circles", "Underworld"),
    HEAVEN(new Color(255, 255, 96), "Heaven", "Sphere of Heaven", 
    "Spheres of Heaven", "Heaven");
    
    private Color color;
    private String name;
    private String singularGroup;
    private String pluralGroup;
    private String noun;
    
    TerritoryType(Color color, String name, String singularGroup, 
            String pluralName, String noun) {
        this.color = color;
        this.name = name;
        this.singularGroup = singularGroup;
        this.pluralGroup = pluralName;
        this.noun = noun;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String toString() {
        return name;
    }
    
    public String getSingularGroupName() {
        return singularGroup;
    }
    
    public String getPluralGroupName() {
        return pluralGroup;
    }

    public String getNoun() {
        return noun;
    }
}
