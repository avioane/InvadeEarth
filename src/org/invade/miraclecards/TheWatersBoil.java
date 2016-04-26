/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EdgeType;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class TheWatersBoil extends DeathCard {
    
    public TheWatersBoil(int powerUpCost) {
        super(powerUpCost, "The Waters Boil");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            for( Territory adjacent : territory.getAdjacent() ) {
                if( territory.getEdgeType(adjacent) != null &&
                        territory.getEdgeType(adjacent).equals(EdgeType.SECONDARY) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory from = (Territory)move;
                boolean hasWaterEdge = false;
                for( Territory adjacent : from.getAdjacent() ) {
                    if( EdgeType.SECONDARY.equals(from.getEdgeType(adjacent)) ) {
                        hasWaterEdge = true;
                    }
                }
                verify( hasWaterEdge, "Choose a territory with a water connection");
            }
        });
        final Territory from = (Territory)gameThread.take();
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory to = (Territory)move;
                verify( EdgeType.SECONDARY.equals(from.getEdgeType(to)),
                        "Choose a territory connected by water");
            }
        });
        Territory to = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage("A maelstrom arises between " + from.getName()
        + " and " + to.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        from.setUndirectedEdge(to, EdgeType.MAELSTROM);
    }
    
    public String getDescriptionString() {
        return "Choose a water connection.  Place the maelstrom marker on "
                + "the connection.  It becomes permanently impassable.";
    }
    
}
