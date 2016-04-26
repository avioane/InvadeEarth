/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;
import org.invade.SpecialUnit;
import java.util.List;

public class FireRainsDown extends DeathCard {
    
    public FireRainsDown(int powerUpCost) {
        super(powerUpCost, "Fire Rains Down");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritories() ) {
            if( territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE) ) {
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
                verify( territory.getForce().getSpecialUnits().contains(GodstormRules.TEMPLE),
                        "Choose a territory with a temple");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage("Fire rains down upon " + territory.getName());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        
        // Rain destruction!
        
        territory.getForce().getSpecialUnits().remove(GodstormRules.TEMPLE);
        
        destroyOne(territory, board);
        for( Territory adjacent : territory.getAdjacent() ) {
            destroyOne(adjacent, board);
        }
    }
    
    private void destroyOne(Territory territory, Board board) {
        if( territory.getForce().getRegularUnits() > 0 ) {
            territory.getForce().addRegularUnits(-1);
            Heaven.getHeaven(board, territory.getOwner()).addUnits(1);
            territory.update();
        }
    }
    
    public String getDescriptionString() {
        return "Destroy a temple of your choice.  Destroy 1 army in the "
                + "temple's territory, as well as each adjacent territory.";
    }
    
}
