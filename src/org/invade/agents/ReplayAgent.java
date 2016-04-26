/*
 * ReplayAgent.java
 *
 * Created on August 8, 2005, 4:00 PM
 *
 */

package org.invade.agents;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.invade.Agent;
import org.invade.Board;
import org.invade.XMLHandler;
import org.invade.InvalidXMLException;
import org.invade.Player;

public class ReplayAgent extends Agent {
    
    public String toString() {
        return "Replay";
    }
    
    public ReplayAgent() {}
    
    public ReplayAgent(List moves) {
        this.moves = moves;
    }
    
    private int index = 0;
    private List moves = new ArrayList();
    
    public Object getMove(Board board) {
        /* Must control all players or we refuse to move; otherwise our list 
         * would cease to be a "replay" and could produce illegal or (at best)
         * arbitrary moves. */
        for( Player player : board.getPlayers() ) {
            if( player.getAgent() != this ) {
                return null;
            }
        }
        if( index >= moves.size() ) {
            return null;
        }
        Element element = (Element)moves.get(index);
        index++;
        try {
            return XMLHandler.parseMove(element, board);            
        } catch(InvalidXMLException e) {            
        }
        return null;
    }
    
}
