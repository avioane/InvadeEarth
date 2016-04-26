/*
 * BlockInvasions.java
 *
 * Created on July 22, 2005, 2:52 PM
 *
 */

package org.invade;

/* When a card that implements this interface is active, blocks() must be 
   invoked to determine if a territory may be invaded. */
public interface BlockInvasion {
    public boolean blocks(Territory from, Territory to);
}