/*
 * AbstractMissionCard.java
 *
 * Created on July 10, 2005, 5:30 PM
 *
 */

package org.invade.miraclecards;
import java.awt.Frame;
import org.invade.AllowInvasion;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.Player;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.rules.GodstormRules;

/**
 *
 * @author Jonathan Crosmer
 */
public class Heaven implements Card, AllowInvasion {
    
    private PlayableDeck deck;
    private Player player;
    private int units;
    
    private static final Heaven VOID_HEAVEN = new Heaven();
    
    /**
     * Creates a new instance of Card
     */
    public Heaven() {
        setDeck(null);
        setUnits(0);
    }
    
    public boolean canPlay(Board board) {
        return true;
    }
    
    /* Play and doAction should only be called by a GameThread. */
    public void play(Board board, GameThread gameThread) throws EndGameException {
        setPlayer(board.getCurrentPlayer());
        board.getCardsInPlay().add(this);
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {}
    
    public String getName(boolean hidden, boolean inPlay) {
        return "Heaven (" + getPlayer() + ": " + getUnits() + ")";
    }
    
    public String toString() {
        return getName(false, false);
    }
    
    public PlayableDeck getDeck() {
        return deck;
    }
    
    public void setDeck(PlayableDeck deck) {
        this.deck = deck;
    }    
    
    public Player getPlayer() {
        return player;
    }
    
    protected void setPlayer(Player player) {
        this.player = player;
    }
        
    public String getDescriptionString() {
        return "";
    }
    
    public void displayCard(Frame parent, MapCanvas mapCanvas) {}

    public boolean canDisplayWhenActive(Board board) {
        return false;
    }
    
    public static Heaven getHeaven(Board board, Player player) {
        for( Card card : board.getCardsInPlay() ) {
            if( card instanceof Heaven
                    && ((Heaven)card).getPlayer() == player ) {
                return (Heaven)card;
            }
        }
        return VOID_HEAVEN;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }
    
    public void addUnits(int units) {
        this.units += units;
    }

    public boolean allows(Territory from, Territory to) {
        return from.getType() == TerritoryType.HEAVEN
                && to.getForce().getSpecialUnits().contains(GodstormRules.GATE);
    }
    
}
