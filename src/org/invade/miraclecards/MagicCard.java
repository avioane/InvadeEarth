/*
 * SkyCard.java
 *
 * Created on March 22, 2006, 9:34 AM
 *
 */

package org.invade.miraclecards;

import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public abstract class MagicCard extends AbstractMiracleCard {
    
    private int currentTurn = -1;
    
    public MagicCard(String name) {
        super(GodstormRules.MAGIC, 0, name);
    }
    
    public String getWhenString() {
        return "Play immediately.";
    }    

    public void doAction(Board board, GameThread gameThread) throws EndGameException {        
        if( ! board.getCardsInPlay().contains(this) ) {
            setPlayer(board.getCurrentPlayer());
            board.getCardsInPlay().add(this);
        }
    }

    public void checkForAction(Board board, GameThread gameThread) throws EndGameException {
        super.checkForAction(board, gameThread);
        if( board.getTurnMode() == TurnMode.TURN_START
                && currentTurn != board.getYear()
                && getPlayer() == board.getCurrentPlayer() ) {
            currentTurn = board.getYear();
            doAtTurnStart(board, gameThread);
            board.setTurnMode(TurnMode.TURN_START);
        }
    }
    
    public void doAtTurnStart(Board board, GameThread gameThread) throws EndGameException {}

    public boolean canPlay(Board board) {
        return super.canPlay(board) && board.getCurrentPlayer().getCards().contains(this);
    }

    public String getName() {
        String result = super.getName();
        return result + " (" + getPlayer() + ")";
    }
    
}
