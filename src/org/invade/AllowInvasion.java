/*
 * AllowInvasion.java
 *
 * Created on July 22, 2005, 3:58 PM
 *
 */

package org.invade;

/* When a card that implements this interface is active, any invasion that would
 * not otherwise be possible is passed to allows() for inspection.  If 
 * allows() returns true, the attacking player will be allowed to invade as if
 * there were a normal, directed edge between the specified territories. */
public interface AllowInvasion {
    public boolean allows(Territory from, Territory to);
}
