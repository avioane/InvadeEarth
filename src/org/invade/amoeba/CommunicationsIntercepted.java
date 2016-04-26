/*
 * CommunicationsIntercepted.java
 *
 * Created on March 13, 2006, 4:45 PM
 *
 */

package org.invade.amoeba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.TerritoryDeck;
import org.invade.TerritoryType;

public class CommunicationsIntercepted  extends AbstractAmoebaCard {
    
    public CommunicationsIntercepted() {
        super("Communications Intercepted");
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        for(TerritoryType type : board.getTerritoryDecks().keySet()) {
            TerritoryDeck deck = board.getTerritoryDeck(type);
            if( ! deck.getCards().isEmpty() ) {
                List<Territory> show = new ArrayList<Territory>(
                        deck.getCards().subList(deck.getCards().size()
                - Math.min(deck.getCards().size(), 5), deck.getCards().size()) );
                Collections.reverse(show);
                board.sendMessage("The " + deck + " deck shows " + show);
            }
        }
    }
    
}
