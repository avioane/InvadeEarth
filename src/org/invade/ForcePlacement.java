/*
 * ForcePlacement.java
 *
 * Created on August 4, 2005, 3:42 PM
 *
 */

package org.invade;

public class ForcePlacement {
    private Territory territory;
    private Force force;
    public ForcePlacement(Territory territory, Force force) {
        this.setTerritory(territory);
        this.setForce(force);
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Force getForce() {
        return force;
    }

    public void setForce(Force force) {
        this.force = force;
    }
    
    public String toString() {
        return "(" + territory.getName() + ", " + force + ")";
    }
}
