/*
 * EnemiesConvert.java
 *
 * Created on March 22, 2006, 9:37 AM
 *
 */

package org.invade.miraclecards;

import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.PlayableDeck;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class FaithShallDeliver extends WarCard implements AutomaticCard {
    
    private int strength;
    
    public FaithShallDeliver(int powerUpCost, int strength) {
        super(powerUpCost, "Faith Shall Deliver");
        this.strength = strength;
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( PlayableDeck deck : board.getDecks() ) {
            if( deck.canDraw(board) ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.setTurnMode(TurnMode.DRAW_CARD);
        PlayableDeck deck = (PlayableDeck)gameThread.take();
        for( int i = 0; (i < strength) && !deck.getCards().isEmpty(); ++i ) {
            board.getCurrentPlayer().getCards().add(deck.draw());
        }
    }
    
    public String getDescriptionString() {
        return "Draw " + strength + " cards from any miracle card deck.";
    }
    
}
