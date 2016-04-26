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
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Player;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class AShrineIsPlundered extends WarCard {
    
    public AShrineIsPlundered(int powerUpCost) {
        super(powerUpCost, "A Shrine is Plundered");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof MagicCard
                    && card.getPlayer() != board.getCurrentPlayer() ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        List<Card> myCards = new ArrayList<Card>(board.getCurrentPlayer().getCards());
        board.getCurrentPlayer().getCards().clear();
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof MagicCard
                    && card.getPlayer() != board.getCurrentPlayer() ) {
                board.getCurrentPlayer().getCards().add(card);
            }
        }
        board.setTurnMode(TurnMode.FORCE_PLAY_CARD);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Card card = (Card)move;
                verify(board.getCurrentPlayer().getCards().contains(card),
                        "You may not take that card");
            }
        });
        Card card = (Card)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.getCurrentPlayer().getCards().clear();
        board.getCurrentPlayer().getCards().addAll(myCards);
        board.sendMessage(board.getCurrentPlayer() + " steals " + card.toString());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        ((MagicCard)card).setPlayer(board.getCurrentPlayer());
        
    }
    
    public String getDescriptionString() {
        return "Steal a relic from another player.";
    }
    
}
