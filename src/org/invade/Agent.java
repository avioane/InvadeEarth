/*
 * Agent.java
 *
 * Created on June 25, 2005, 1:28 PM
 *
 */

package org.invade;



/**
 *
 * @author Jonathan Crosmer
 */
public abstract class Agent {
    
    public boolean allowGUI() { return false; }
    @SuppressedProperty public boolean isLocal() { return true; }
    
    /* This method should return true only if isLocal() is also true.
     * We do not care if a remote agent is human or AI. */
    @SuppressedProperty public boolean isAI() { return true; }
       
    /* This method will only be invoked if isAI() is true. */
    public Object getMove(Board board) {
        return null;
    }
    
}