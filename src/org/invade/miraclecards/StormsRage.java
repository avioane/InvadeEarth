/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
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
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class StormsRage extends WarCard {
    
    private int strength;
    
    public StormsRage(int powerUpCost, int strength) {
        super(powerUpCost, "Storms Rage");
        this.strength = strength;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyHasUnits(territory);
                DefaultMoveVerifier.verifyNotDevastated(territory);
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        int effect = Math.min(board.getRandom().nextInt(strength) + 1,
                territory.getForce().getRegularUnits());
        board.sendMessage("Storms rage in " + territory.getName() + " ("
                + effect + ")");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.getForce().addRegularUnits( - effect );
        Heaven.getHeaven(board, territory.getOwner()).addUnits(effect);
        territory.update();
    }
    
    public String getDescriptionString() {
        return "Choose a territory.  Roll one of the dice and destroy that "
                + "many armies in the territory.";
    }
    
}
