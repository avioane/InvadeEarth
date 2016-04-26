/*
 * LocalHumanAgent.java
 *
 * Created on July 24, 2005, 4:10 PM
 *
 */

package org.invade.agents;

import org.invade.Agent;

/**
 *
 * @author Jonathan Crosmer
 */
public class LocalHumanAgent extends Agent {
    
    public String toString() {
        return "Human";
    }
    
    public boolean allowGUI() { return true; }    
    public boolean isAI() { return false; }
    
}
