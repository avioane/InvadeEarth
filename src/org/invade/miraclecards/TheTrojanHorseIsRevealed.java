/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class TheTrojanHorseIsRevealed extends DeathCard {
    
    private int strength;
    
    public TheTrojanHorseIsRevealed(int powerUpCost, int strength) {
        super(powerUpCost, "The Trojan Horse Is Revealed");
        this.strength = strength;
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Territory territory : board.getTerritoriesHostileTo(board.getCurrentPlayer(), TerritoryType.LAND) ) {
            if( territory.getForce().getRegularUnits() == 1
                    && territory.getForce().getSpecialUnits().isEmpty() ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new DefaultMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                verifyIsEnemy(territory, board.getCurrentPlayer());
                verify( territory.getForce().getRegularUnits() == 1
                        && territory.getForce().getSpecialUnits().isEmpty(),
                        "Choose a territory with only one army");
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        int effect = board.getRandom().nextInt(strength) + 1;
        board.sendMessage("The Trojan Horse is revealed in " + territory.getName()
        + " (" + effect + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.getForce().clear();
        Heaven.getHeaven(board, territory.getOwner()).addUnits(1);
        territory.setOwner(board.getCurrentPlayer());
        territory.getForce().setRegularUnits(effect);
    }
    
    public String getDescriptionString() {
        return "Choose another player's territory with only 1 army.  Destroy "
                + "that army and roll one of the dice.  Gain that many of "
                + "your own armies in the territory.";
    }
    
}
