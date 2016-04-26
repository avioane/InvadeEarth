/*
 * ForceVacuum.java
 *
 * Created on July 28, 2005, 1:45 PM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.List;

/* This class can be used to insure that a player never receives reinforcements.
 * This class will eat any attempts
 * to modify the current values of its members, always returning data
 * consistent with an empty Force object.
 */
public class ForceVacuum extends Force {
    public int getRegularUnits() {
        return 0;
    }    
    public List<SpecialUnit> getSpecialUnits() {
        return new ArrayList<SpecialUnit>();
    }
    public void setRegularUnits(int regularUnits) {}
    public void addRegularUnits(int addend) {}
    public void add(Force addend) {}    
    public void subtract(Force subtrahend) {}
}
