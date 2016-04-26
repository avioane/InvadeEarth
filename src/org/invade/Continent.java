/*
 * Continent.java
 *
 * Created on June 28, 2005, 2:55 PM
 *
 */

package org.invade;

import java.awt.Color;

public class Continent {
    
    private String name = "";
    private int bonus = 0;
    private Color color = Color.GRAY;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
    
    public String toString() {
        return name + " (" + bonus + ")";
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
}
