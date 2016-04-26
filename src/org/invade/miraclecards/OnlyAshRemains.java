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
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class OnlyAshRemains extends WarCard {
    
    public OnlyAshRemains(int powerUpCost) {
        super(powerUpCost, "Only Ash Remains");
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
                        "You may not destroy that card");
            }
        });
        Card card = (Card)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        board.getCurrentPlayer().getCards().clear();
        board.getCurrentPlayer().getCards().addAll(myCards);
        board.sendMessage(board.getCurrentPlayer() + " destroys " + card.toString());
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        board.getCardsInPlay().remove(card);
        card.getDeck().discard(card);
    }
    
    public String getDescriptionString() {
        return "Destroy a relic of your choice.";
    }
    
}
