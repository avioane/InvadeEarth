/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import java.util.ArrayList;
import java.util.List;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class ParadiseIsLost extends DeathCard {
    
    public ParadiseIsLost(int powerUpCost) {
        super(powerUpCost, "Paradise is Lost");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.isPlague() ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verify( territory.isPlague(),
                        "Choose a territory afflicted by the plague");
            }
        });
        Territory old = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        Territory newPlague = null;
        List<Territory> discardThese = new ArrayList<Territory>();
        while(newPlague == null) {
            newPlague = board.getTerritoryDeck(TerritoryType.LAND).draw();
            discardThese.add(newPlague);
            if(newPlague.isPlague()) {
                newPlague = null;
            }
        }
        for(Territory territory : discardThese) {
            board.getTerritoryDeck(TerritoryType.LAND).discard(territory);
        }
        
        board.sendMessage("Plague strikes " + newPlague.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        old.setPlague(false);
        newPlague.setPlague(true);
    }
    
    public String getDescriptionString() {
        return "Choose a plague marker in play.  Trun over the top card from "
                + "the territory deck and move the plague marker to the "
                + "territory shown.";
    }
    
}
