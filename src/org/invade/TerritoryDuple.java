/*
 * TerritoryDuple.java
 *
 * Created on August 4, 2005, 3:40 PM
 *
 */

package org.invade;

public class TerritoryDuple {
    private Territory first;
    private Territory second;
    public TerritoryDuple(Territory first, Territory second) {
        setFirst(first);
        setSecond(second);
    }

    public Territory getFirst() {
        return first;
    }

    public void setFirst(Territory first) {
        this.first = first;
    }

    public Territory getSecond() {
        return second;
    }

    public void setSecond(Territory second) {
        this.second = second;
    }
    
    public String toString() {
        return "(" + first.getName() + ", " + second.getName() + ")";
    }
}
