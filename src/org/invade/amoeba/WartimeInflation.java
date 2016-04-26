/*
 * WartimeInflation.java
 *
 * Created on March 14, 2006, 2:48 PM
 *
 */

package org.invade.amoeba;

import org.invade.Board;
import org.invade.EconomicCard;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.commandcards.AbstractCommandCard;
import org.invade.rules.DefaultRules;

public class WartimeInflation extends AbstractCommandCard implements EconomicCard {
    
    private int costShift = 1;
    
    private int year = 0;
    
    public WartimeInflation(int costShift) {
        super(DefaultRules.DIPLOMAT, 0, "Wartime Inflation");
        this.costShift = costShift;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        year = board.getYear();
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {
        if( board.getYear() != year ) {
            board.getCardsInPlay().remove(this);
        }
    }
    
    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.isBeforeFirstInvasion()
        && board.isBeforeFirstCard();
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        return super.getName(false, true);
    }
    
    public int getCardPriceChange(Board board) {
        return costShift;
    }
    
    public int getCardPowerUpChange(Board board) {
        return costShift;        
    }
    
    public int getBidChange(Board board) {
        return 0;
    }
    
    public String getDescriptionString() {
        return "For the remainder of the year, all energy costs (purchase costs"
                + " and effect costs) are increased by 1. ";
    }
    
    public String getWhenString() {
        return BEFORE_FIRST_INVASION + "You may not play this card if you have"
                + " played another Command Card this turn.";
    }
    
}
