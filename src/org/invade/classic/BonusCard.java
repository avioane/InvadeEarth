/*
 * BonusCard.java
 *
 * Created on August 1, 2005, 8:51 AM
 *
 */

package org.invade.classic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.invade.AutomaticCard;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.Player;
import org.invade.Territory;
import org.invade.TurnMode;

public class BonusCard implements AutomaticCard {
    
    public enum Type {INFANTRY("Infantry"), CAVALRY("Cavalry"), ARTILLERY("Artillery");
    String name;
    Type(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
    };
    
    public static final int SET_SIZE = Type.values().length;
    
    private Type type;
    private Territory territory;
    private PlayableDeck deck;
    private Player player;
    
    public static final String WILD_NAME = "Wild";
    public static final String DEFAULT_DECK_NAME = "Bonus Card";
    
    public BonusCard() {
        // Create a wild card
        type = null;
        territory = null;
    }
    
    public BonusCard(Type type, Territory territory) {
        this.type = type;
        this.territory = territory;
    }
    
    public boolean canPlay(Board board) {
        if( ! (board.isBeforeFirstInvasion()
        || board.getTurnMode().equals(TurnMode.FORCE_PLAY_CARD)) ) {
            return false;
        }
        List<Card> required = new ArrayList<Card>(board.getCardsInPlay());
        required.add(this);
        List<Card> optional = new ArrayList<Card>(board.getCurrentPlayer().getCards());
        optional.remove(this);
        return canFormSet(getBonusCards(required), getBonusCards(optional));
    }
    
    public String toString() {
        return getName(false, false);
    }
    
    public void displayCard(java.awt.Frame parent, MapCanvas mapCanvas) {
        new BonusCardDisplay(parent, this, mapCanvas).setVisible(true);
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( hidden && ! inPlay ) {
            return (deck == null) ? DEFAULT_DECK_NAME : deck.toString();
        }
        if( isWild() ) {
            return WILD_NAME;
        }
        return type.toString() + " (" + territory.getName() + ")";
    }
    
    public void play(Board board, GameThread gameThread) throws EndGameException {
        Player current = board.getCurrentPlayer();
        board.sendMessage(current.getName() + " plays " + getName(false, true));
        current.getCards().remove(this);
        setPlayer(current);
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {}
    
    public PlayableDeck getDeck() {
        return deck;
    }
    
    public void setDeck(PlayableDeck deck) {
        this.deck = deck;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public boolean isWild() {
        return type == null;
    }
    
    public Type getType() {
        return type;
    }
    
    public Territory getTerritory() {
        return territory;
    }
    
    public static List<BonusCard> getBonusCards(List<Card> cards) {
        List<BonusCard> result = new ArrayList<BonusCard>();
        for( Card card : cards ) {
            if( card instanceof BonusCard ) {
                result.add((BonusCard)card);
            }
        }
        return result;
    }
    
    public static boolean canFormSet(List<BonusCard> required, List<BonusCard> optional) {
        List<BonusCard> available = new ArrayList<BonusCard>();
        available.addAll(required);
        available.addAll(optional);
        Type firstType = null;
        for( BonusCard card : required ) {
            if( ! card.isWild() ) {
                if( firstType == null ) {
                    firstType = card.getType();
                } else {
                    if( firstType.equals(card.getType()) ) {
                        return areAllSameType(required) && hasSetSameType(available, firstType);
                    } else {
                        return areAllDifferentTypes(required) && hasSetDifferentTypes(available);
                    }
                }
            }
        }
        if( firstType != null ) {
            return hasSetSameType(available, firstType) || hasSetDifferentTypes(available);
        }
        return hasAnySet(available);
    }
    
    public static boolean hasAnySet(List<BonusCard> cards) {
        for( Type type : Type.values() ) {
            if( hasSetSameType(cards, type) ) {
                return true;
            }
        }
        return hasSetDifferentTypes(cards);
    }
    
    public static boolean hasSetSameType(List<BonusCard> cards, Type type) {
        int sameTypeCount = 0;
        int wilds = 0;
        for( BonusCard bonusCard : cards ) {
            if( bonusCard.isWild() ) {
                wilds++;
            } else if( bonusCard.getType().equals(type) ) {
                sameTypeCount++;
            }
        }
        return sameTypeCount + wilds >= SET_SIZE;
    }
    
    public static boolean hasSetDifferentTypes(List<BonusCard> cards) {
        Set<Type> differentTypesOwned = new HashSet<Type>();
        int wilds = 0;
        for( BonusCard bonusCard : cards ) {
            if( bonusCard.isWild() ) {
                wilds++;
            } else {
                differentTypesOwned.add(bonusCard.getType());
            }
        }
        if( differentTypesOwned.size() + wilds >= SET_SIZE ) {
            return true;
        }
        return false;
    }
    
    public static boolean areAllDifferentTypes(List<BonusCard> cards) {
        Set<Type> differentTypesOwned = new HashSet<Type>();
        for( BonusCard bonusCard : cards ) {
            if( ! (bonusCard.isWild()
            || differentTypesOwned.add(bonusCard.getType()) ) ) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean areAllSameType(List<BonusCard> cards) {
        Type firstType = null;
        for( BonusCard bonusCard : cards ) {
            if( firstType == null ) {
                firstType = bonusCard.getType();
            } else if( ! (bonusCard.isWild()
            || bonusCard.getType().equals(firstType)) ) {
                return false;
            }
        }
        return true;
    }

    public boolean canDisplayWhenActive(Board board) {
        return true;
    }
    
}
