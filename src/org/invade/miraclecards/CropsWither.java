/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import java.util.ArrayList;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.TurnMode;
import org.invade.rules.GodstormRules;

public class CropsWither extends DeathCard {
    
    public CropsWither(int powerUpCost) {
        super(powerUpCost, "Crops Wither");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        board.setTurnMode(TurnMode.CHOOSE_PLAYER);
        Player player = (Player)gameThread.take();
        board.sendMessage(player.getName() + " experiences a famine");
        
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Card card : new ArrayList<Card>(player.getCards()) ) {
            if( ! (card instanceof MagicCard) ) {
                player.getCards().remove(card);
                card.getDeck().discard(card);
            }
        }
    }
    
    public String getDescriptionString() {
        return "Choose a player.  That player discards all of his or her cards, "
                + "except for relics.";
    }
    
}
