/*
 * VigilanteAgent.java
 *
 * Created on March 10, 2006, 9:19 AM
 *
 */

package org.invade.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.invade.Board;
import org.invade.Player;
import org.invade.Territory;

public class VigilanteAgent extends BetaAgent {
    
    public String toString() {
        return "Vigilante Agent";
    }
    
    private double vigilantism = 4.0;
        
    protected TerritoryValueMap evaluate(Board board) {
        TerritoryValueMap result = super.evaluate(board);
        List<Player> players = new ArrayList<Player>(board.getPlayers());
        final Map<Player, Double> playerStrengthMap = new HashMap<Player, Double>();
        for(Player player : players) {
            playerStrengthMap.put(player, (double)board.getTerritoriesOwned(player).size());
        }
        // Sort from weakest to strongest
        Collections.sort(players, new Comparator<Player>() {
           public int compare(Player first, Player second) {
               return (int)(playerStrengthMap.get(first) - playerStrengthMap.get(second));
           } 
        });
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() != Player.NEUTRAL
                && territory.getOwner() != board.getCurrentPlayer() ) {
                result.addValue(territory, players.indexOf(territory.getOwner())                
                * getVigilantism());
            }
        }
        return result;
    }

    public double getVigilantism() {
        return vigilantism;
    }

    public void setVigilantism(double vigilantism) {
        this.vigilantism = vigilantism;
    }
}
