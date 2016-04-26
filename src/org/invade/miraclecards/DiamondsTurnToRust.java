/*
 * RagnarokCometh.java
 *
 * Created on March 22, 2006, 3:08 PM
 *
 */

package org.invade.miraclecards;

import java.util.ArrayList;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Player;
import org.invade.rules.GodstormRules;

public class DiamondsTurnToRust extends DeathCard implements AutomaticCard {
    
    public DiamondsTurnToRust(int powerUpCost) {
        super(powerUpCost, "Diamonds Turn To Rust");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        if( GodstormRules.checkForBlock(board, gameThread) ) {
            return;
        }
        for( Card card : new ArrayList<Card>(board.getCardsInPlay()) ) {
            if( card instanceof MagicCard ) {
                board.getCardsInPlay().remove(card);
                for(Player player : board.getPlayers()) {
                    player.getCards().remove(card);
                }
            }
        }
    }
    
    public String getDescriptionString() {
        return "Destroy all relics in play.";
    }
    
}
