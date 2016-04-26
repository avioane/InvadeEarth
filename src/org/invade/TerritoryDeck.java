/*
 * TerritoryDeck.java
 *
 * Created on July 10, 2005, 6:15 PM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Jonathan Crosmer
 */
public class TerritoryDeck extends Deck<Territory> {
        
    public Territory drawNotOwnedBy(Player player) {
        List<Territory> territories = new ArrayList<Territory>();
        Territory result = null;
        while( result == null
                && ! getCards().isEmpty() ) {
            result = draw();
            if( result != null && result.getOwner() == player ) {
                territories.add(result);
                result = null;
            }
        }
        for( Territory alreadyOwn : territories ) {
           discard(alreadyOwn);
        }
        return result;
    }
    
}
