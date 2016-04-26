/*
 * RemoteAgent.java
 *
 * Created on July 24, 2005, 4:15 PM
 *
 */

package org.invade.agents;

import org.invade.Agent;

/**
 *
 * @author Jonathan Crosmer
 */
public class RemoteAgent extends Agent {
    
    public boolean isLocal() { return false; }
    public boolean isAI() { return false; }
    
}
