/*
 * Board.java
 *
 * Created on June 20, 2005, 12:29 PM
 *
 */

package org.invade;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import org.invade.rules.DefaultMoveVerifier;
import org.invade.rules.DefaultRules;



public class Board {
    
    private List<Territory> territories = new ArrayList<Territory>();
    private Dimension size = new Dimension( 640, 480 );
    private List<Continent> continents = new ArrayList<Continent>();
    private String mapImage = null;
    
    private List<Player> players = new ArrayList<Player>();
    private Rules rules = new DefaultRules();
    private int year = 0;
    private TurnMode turnMode = TurnMode.NONE;
    private Player currentPlayer = null;
    private Territory attackingTerritory = null;
    private Territory defendingTerritory = null;
    private Territory damagedTerritory = null;
    private List<Die> attackerDice = new ArrayList<Die>();
    private List<Die> defenderDice = new ArrayList<Die>();
    private List<Integer> turnChoices = new ArrayList<Integer>();
    private boolean beforeFirstInvasion = false;
    private boolean beforeFirstCard = false;
    private Random random = new Random();
    private int numberToPlace = 1;
    private List<PlayableDeck> decks = new ArrayList<PlayableDeck>();
    private List<Card> allCards = new ArrayList<Card>();
    private Map<TerritoryType, TerritoryDeck> territoryDecks = new TreeMap<TerritoryType, TerritoryDeck>();
    private List<Card> cardsInPlay = new ArrayList<Card>();
    private Card lastCardUsed = null;
    private List<BoardMessageListener> listeners = new ArrayList<BoardMessageListener>();
    private MoveVerifier moveVerifier = new DefaultMoveVerifier();
    
    public List<Territory> getTerritories() {
        return territories;
    }
    
    public List<Continent> getContinents() {
        return continents;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public Dimension getSize() {
        return size;
    }
    
    
    /* Returns the last territory in territories that contains
     * point within its boundaries or null if no territory contains point */
    public Territory getTerritoryAt(Point point) {
        Territory result = null;
        for( Territory territory : territories ) {
            if( territory.getShape().contains(point) ) {
                result = territory;
            }
        }
        return result;
    }
    
    /* Removes a territory and all edges connected to it from this board .*/
    public void removeTerritory(Territory territory) {
        territories.remove(territory);
        for( Territory from : territories ) {
            from.setDirectedEdge(territory, null);
        }
    }
    
    public void removeContinent(Continent continent) {
        continents.remove(continent);
        for( Territory territory : territories ) {
            if( territory.getContinent() == continent ) {
                territory.setContinent(null);
            }
        }
    }
    
    public List<Territory> getContinent(Continent continent) {
        List<Territory> result = new ArrayList<Territory>();
        for( Territory territory : territories ) {
            if( territory.getContinent() == continent && ! territory.isDevastated() ) {
                result.add(territory);
            }
        }
        return result;
    }
    
    
    public List<Territory> getTerritoriesOwned(Player player) {
        List<Territory> result = new ArrayList<Territory>();
        for( Territory territory : territories ) {
            if( territory.getOwner() == player ) {
                result.add(territory);
            }
        }
        return result;
    }
    
    public List<Territory> getTerritoriesOwned(Player player, TerritoryType type) {
        List<Territory> result = new ArrayList<Territory>();
        for( Territory territory : territories ) {
            if( territory.getOwner() == player && territory.getType().equals(type)
            && (!territory.isDevastated()) ) {
                result.add(territory);
            }
        }
        return result;
    }
    
    public List<Territory> getTerritoriesHostileTo(Player player, TerritoryType type) {
        List<Territory> result = new ArrayList<Territory>();
        for( Territory territory : territories ) {
            if( territory.getOwner() != player
                    && territory.getOwner() != Player.NEUTRAL
                    && territory.getType().equals(type)
                    && (!territory.isDevastated()) ) {
                result.add(territory);
            }
        }
        return result;
    }
    
    // If player is null, returns the total for all players
    public int getUnitCount(Player player, SpecialUnit unitType) {
        int result = 0;
        for( Territory territory : territories ) {
            if( territory.getOwner() == player || player == null ) {
                result += Collections.frequency(territory.getForce().getSpecialUnits(), unitType);
            }
        }
        if( player != null ) {
            result += Collections.frequency(player.getReinforcements().getSpecialUnits(), unitType);
        }
        return result;
    }
    
    public List<Player> getLivingPlayers() {
        List<Player> result = new ArrayList<Player>();
        for( Player player : players ) {
            if(player.isAlive()) {
                result.add(player);
            }
        }
        return result;
    }
    
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public TurnMode getTurnMode() {
        return turnMode;
    }
    
    public void setTurnMode(TurnMode turnMode) {
        this.turnMode = turnMode;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public void nextPlayer() {
        setCurrentPlayer( getPlayers().get( (getPlayers().indexOf(getCurrentPlayer())
        + 1) % getPlayers().size() ) );
    }
    
    public Territory getAttackingTerritory() {
        return attackingTerritory;
    }
    
    public void setAttackingTerritory(Territory attackingTerritory) {
        this.attackingTerritory = attackingTerritory;
    }
    
    public Territory getDefendingTerritory() {
        return defendingTerritory;
    }
    
    public void setDefendingTerritory(Territory defendingTerritory) {
        this.defendingTerritory = defendingTerritory;
    }
    
    /* Gets a set of the territories that can complete free moves from the
     * given territory.
     * If enough units are available, the result is the transitive closure
     * of the "free move adjacency" relation on the given territory. */
    public Set<Territory> getFreeMoves(Territory from) {
        if( ! getRules().isAllowLongFreeMoves() ) {
            return getFreeMoveAdjacent(from);
        }
        List<Territory> owned = getTerritoriesOwned(from.getOwner());
        Set<Territory> result = new HashSet<Territory>();
        Set<Territory> needTesting;
        Set<Territory> willNeedTesting = new HashSet<Territory>();
        willNeedTesting.add(from);
        while( !willNeedTesting.isEmpty() ) {
            needTesting = willNeedTesting;
            result.addAll(needTesting);
            willNeedTesting = new HashSet<Territory>();
            for( Territory territory : needTesting ) {
                willNeedTesting.addAll( getFreeMoveAdjacent(territory, owned) );
            }
            willNeedTesting.removeAll(result);
        }
        return result;
    }
    
    /* Gets the territories that are "adjacent" to a given territory for
     * the purposes of a free move; this includes territories that have an edge
     * from the given territory and territories that have a space station or
     * landing site when the given territory has respectively the other.
     * All territories must be owned by the owner of the given territory.
     */
    public Set<Territory> getFreeMoveAdjacent(Territory from) {
        Set<Territory> result = getFreeMoveAdjacent(from,
                getTerritoriesOwned(from.getOwner()));
        result.remove(from);
        return result;
    }
    
    private Set<Territory> getFreeMoveAdjacent(Territory from, Collection<Territory> consider) {
        Set<Territory> result = new HashSet<Territory>(from.getAdjacent());
        result.retainAll(consider);
        if( from.getForce().getSpecialUnits().contains(DefaultRules.SPACE_STATION) ) {
            for( Territory territory : consider ) {
                if( (!territory.isDevastated()) && territory.isLandingSite() ) {
                    result.add(territory);
                }
            }
        }
        if( from.isLandingSite() ) {
            for( Territory territory : consider ) {
                if( (!territory.isDevastated())
                && territory.getForce().getSpecialUnits().contains(DefaultRules.SPACE_STATION) ) {
                    result.add(territory);
                }
            }
        }
        return result;
    }
    
    public boolean canFreeMove(Territory from, Territory to) {
        return getFreeMoves(from).contains(to);
    }
    
    public List<Die> getAttackerDice() {
        return attackerDice;
    }
    
    public List<Die> getDefenderDice() {
        return defenderDice;
    }
    
    public List<Integer> getTurnChoices() {
        return turnChoices;
    }
    
    public boolean isBeforeFirstInvasion() {
        return beforeFirstInvasion && turnMode.equals(TurnMode.DECLARE_INVASIONS);
    }
    
    public void setBeforeFirstInvasion(boolean beforeFirstInvasion) {
        this.beforeFirstInvasion = beforeFirstInvasion;
    }
    
    public Random getRandom() {
        return random;
    }
    
    public void setRandom(Random random) {
        this.random = random;
    }
    
    public int getNumberToPlace() {
        return numberToPlace;
    }
    
    public void setNumberToPlace(int numberToPlace) {
        this.numberToPlace = numberToPlace;
    }
    
    public Rules getRules() {
        return rules;
    }
    
    public void setRules(Rules rules) {
        this.rules = rules;
        setMoveVerifier(rules.getMoveVerifier());
    }
    
    public List<PlayableDeck> getDecks() {
        return decks;
    }
    
    public void setDecks(List<PlayableDeck> decks) {
        this.decks = decks;
    }
    
    public TerritoryDeck getTerritoryDeck(TerritoryType territoryType) {
        return territoryDecks.get(territoryType);
    }
    
    public List<Card> getAllCards() {
        return allCards;
    }
    
    public List<Card> getCardsInPlay() {
        return cardsInPlay;
    }    

    public Card getLastCardUsed() {
        return lastCardUsed;
    }

    public void setLastCardUsed(Card lastCardUsed) {
        this.lastCardUsed = lastCardUsed;
    }
    
    public void resetDecks() {
        allCards.clear();
        setDecks(getRules().getStartingDecks(this));
        for( PlayableDeck deck : getDecks() ) {
            allCards.addAll(deck.getCards());
            Collections.shuffle(deck.getCards(), random);
        }
        territoryDecks.clear();
        for( TerritoryType type : EnumSet.allOf(TerritoryType.class) ) {
            TerritoryDeck deck = new TerritoryDeck();
            deck.setName(type.toString() + " Territory Card");
            territoryDecks.put(type, deck);
        }
        for( Territory territory : territories ) {
            if( !territory.isDevastated() ) {
                getTerritoryDeck(territory.getType()).add(territory);
            }
        }
        for( TerritoryDeck deck : territoryDecks.values() ) {
            Collections.shuffle(deck.getCards(), random);
        }
    }
    
    public void addBoardMessageListener(BoardMessageListener listener) {
        listeners.add(listener);
    }
    
    public void removeBoardMessageListener(BoardMessageListener listener) {
        listeners.remove(listener);
    }
    
    public void sendMessage(String message) {
        for( BoardMessageListener listener : listeners) {
            listener.receiveBoardMessage(message);
        }
    }
    
    public MoveVerifier getMoveVerifier() {
        return moveVerifier;
    }
    
    public void setMoveVerifier(MoveVerifier moveVerifier) {
        this.moveVerifier = moveVerifier;
    }
    
    public Territory getDamagedTerritory() {
        return damagedTerritory;
    }
    
    public void setDamagedTerritory(Territory damagedTerritory) {
        this.damagedTerritory = damagedTerritory;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }

    public Map<TerritoryType, TerritoryDeck> getTerritoryDecks() {
        return territoryDecks;
    }

    public boolean isBeforeFirstCard() {
        return beforeFirstCard;
    }

    public void setBeforeFirstCard(boolean beforeFirstCard) {
        this.beforeFirstCard = beforeFirstCard;
    }
    
}
