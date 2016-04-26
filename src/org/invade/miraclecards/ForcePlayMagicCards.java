/*
 * ForcePlayMagicCards.java
 *
 * Created on March 28, 2006, 3:15 PM
 *
 */

package org.invade.miraclecards;

import java.awt.Frame;
import java.util.ArrayList;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.PlayableDeck;
import org.invade.Player;

public class ForcePlayMagicCards extends RuleCard {
    
    public void checkForAction(Board board, GameThread gameThread) throws EndGameException {
        for(Player player : board.getPlayers()) {
            for(Card card : new ArrayList<Card>(player.getCards())) {
                if(card instanceof MagicCard
                        && (!board.getCardsInPlay().contains(card))
                        && card.canPlay(board)) {
                    card.play(board, gameThread);
                }
            }
        }
    }

    public String getName(boolean hidden, boolean inPlay) {
        return "Play all Magic Cards";
    }
    
}
