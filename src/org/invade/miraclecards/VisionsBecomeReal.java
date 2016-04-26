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

public class VisionsBecomeReal extends WarCard {
    
    public VisionsBecomeReal(int powerUpCost) {
        super(powerUpCost, "Visions Become Real");
    }
    
    public boolean canPlay(Board board) {
        if( ! super.canPlay(board) ) {
            return false;
        }
        for( Player player : board.getLivingPlayers() ) {
            if( player != board.getCurrentPlayer()
            && ! player.getCards().isEmpty() ) {
                return true;
            }
        }
        return false;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_PLAYER);
        board.setMoveVerifier(new AbstractMoveVerifier() {
            public void verifyWithAssumptions(Board board, Object move)
            throws IllegalMoveException {
                Player player = (Player)move;
                verify( player != board.getCurrentPlayer(),
                        "Choose a different player");
                verify( ! player.getCards().isEmpty(),
                        "Choose a player with cards");
            }
        });
        Player target = (Player)gameThread.take();
        board.setMoveVerifier(board.getRules().getMoveVerifier());
        
        List<Card> myCards = new ArrayList<Card>(board.getCurrentPlayer().getCards());
        board.getCurrentPlayer().getCards().clear();
        board.getCurrentPlayer().getCards().addAll(target.getCards());
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
        board.sendMessage(board.getCurrentPlayer() + " takes a card from "
                + target);
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        target.getCards().remove(card);
        board.getCurrentPlayer().getCards().add(card);
    }
    
    public String getDescriptionString() {
        return "Look at another player's miracle cards in hand and take one "
                + "of those cards.";
    }
    
}
