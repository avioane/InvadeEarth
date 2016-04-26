/*
 * RelicTerritoryBonus.java
 *
 * Created on June 1, 2006, 1:12 PM
 *
 */

package org.invade.miraclecards;

import java.util.HashMap;
import java.util.Map;
import org.invade.Board;
import org.invade.Card;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.Territory;
import org.invade.TurnMode;

public class RelicTerritoryBonus extends RuleCard {
    
    private Map<String, String> territoryMap = new HashMap<String, String>();
    private Map<String, Integer> bonusMap = new HashMap<String, Integer>();
    
    private boolean awardedBonuses = false;
    
    public void addBonus(String relicName, String territoryName, int bonusValue) {
        territoryMap.put(relicName, territoryName);
        bonusMap.put(relicName, bonusValue);
    }

    public void checkForAction(Board board, GameThread gameThread) throws EndGameException {
        if( board.getTurnMode() == TurnMode.ACKNOWLEDGE_GAME_OVER
                && ! awardedBonuses ) {
            awardedBonuses = true;
            for( Card card : board.getCardsInPlay() ) {
                if(card instanceof MagicCard) {
                    MagicCard relic = (MagicCard)card;
                    for( String key : territoryMap.keySet() ) {
                        if( relic.getName().startsWith(key) ) {
                            for( Territory territory : board.getTerritoriesOwned(relic.getPlayer())) {
                                if( territory.getName().equals(territoryMap.get(key)) ) {
                                    relic.getPlayer().setBonusPoints(
                                            relic.getPlayer().getBonusPoints()
                                            + bonusMap.get(key));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getName(boolean hidden, boolean inPlay) {
        return "Relic Territory Bonuses";
    }
            
}
