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
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.GodstormRules;

public class HeroesAreBorn extends WarCard {
    
    protected int strength;
    
    public HeroesAreBorn(int powerUpCost, int strength) {
        this(powerUpCost, strength, "Heroes are Born");
    }
    
    public HeroesAreBorn(int powerUpCost, int strength, String name) {
        super(powerUpCost, name);
        this.strength = strength;
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board)
        && ! board.getTerritoriesOwned(board.getCurrentPlayer(), TerritoryType.LAND).isEmpty();
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
        board.setMoveVerifier(new AbstractMoveVerifier(){
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Territory territory = (Territory)move;
                DefaultMoveVerifier.verifyNotDevastated(territory);
                DefaultMoveVerifier.verifyIsFriendly(territory, board.getCurrentPlayer());
                DefaultMoveVerifier.verifyType(territory, TerritoryType.LAND);
            }
        });
        Territory territory = (Territory)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.sendMessage(territory.getName() + " received reinforcements");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        territory.getForce().addRegularUnits(strength);
    }
    
    public String getDescriptionString() {
        return "Gain " + strength + " armies in any one territory of your choice.";
    }
    
}
