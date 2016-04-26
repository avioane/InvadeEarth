/*
 * CommonBoardMethods.java
 *
 * Created on July 22, 2005, 12:17 PM
 *
 */

package org.invade;
import java.util.Collections;
import java.util.List;

/* This class contains convenience methods to handle calculations that are common in
 * Risk games.  These are safe for any thread to execute, as long the handling
 * of the Board object is threadsafe.
 */
public class CommonBoardMethods {
    private CommonBoardMethods() {}
    
    public static int getContinentBonuses(Board board, Player player) {
        int result = 0;
        for( Continent continent : board.getContinents() ) {
            boolean giveBonus = true;
            for( Territory territory : board.getContinent(continent) ) {
                if( territory.getOwner() != player ) {
                    giveBonus = false;
                }
            }
            if( giveBonus ) {
                result += continent.getBonus();
            }
        }
        return result;
    }
    
    public static int getBasicSupply(Board board, Player player) {
        return Math.max(3, (board.getTerritoriesOwned(player).size()
        - board.getTerritoriesOwned(player, TerritoryType.UNDERWORLD).size()) / 3);
    }
    
    public static int getMobileUnitCount(Board board, Player player) {
        int result = 0;
        for(Territory territory : board.getTerritories()) {
            if( territory.getOwner() == player ) {
                result += territory.getForce().getMobileIndependentSize();
            }
        }
        return result;
    }
    
    public static boolean hasMOD(Board board, Player player) {
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() == player
                    && territory.getForce().getRegularUnits() > 0 ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isFrequencyJamAgainst(Board board, Player player) {
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof BlockCards
                    && card.getPlayer() == player ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isInvasionBlockedByCard(Board board, Territory from, Territory to) {
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof BlockInvasion
                    && ((BlockInvasion)card).blocks(from, to) ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyInPlay(Board board, Class cardType) {
        for( Card card : board.getCardsInPlay() ) {
            if( cardType.isInstance(card) ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isInvasionAllowedByCard(Board board, Territory from, Territory to) {
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof AllowInvasion
                    && ((AllowInvasion)card).allows(from, to) ) {
                return true;
            }
        }
        return false;
    }
    
    /* Checks whether any territories in the given list have room for a certain
     * number of a specified unit.  This method does not consider any 
     * information other than the maximum number per territory and currently 
     * stationed units in each territory; for example, it does not consider 
     * whether the player owning each territory may own more of the same unit.  */
    public static boolean hasRoomFor(Board board, SpecialUnit special,
            List<Territory> territories, int countToPlace) {
        if( special.getMaxPerTerritory() < 0 ) {
            return !territories.isEmpty();
        }
        int slots = 0;
        for( Territory territory : territories ) {
            slots += special.getMaxPerTerritory()
            - Collections.frequency(territory.getForce().getSpecialUnits(), special);
        }
        return slots >= countToPlace;
    }
    
    public static boolean areEnemyCommanders(Board board, Player player) {
        for( Territory territory : board.getTerritories() ) {
            if( territory.getOwner() != player
                    && territory.getForce().getMobileForce().getSpecialUnits().size() > 0 ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean areMultiplePlayersAlive(Board board) {
        int alive = 0;
        for(Player player : board.getPlayers()) {
            if(player.isAlive()) {
                ++alive;
            }
            if(alive > 1) {
                return true;
            }
        }
        return false;
    }
    
}
