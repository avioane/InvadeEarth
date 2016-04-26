/*
 * AbstractMissionCard.java
 *
 * Created on July 10, 2005, 5:30 PM
 *
 */

package org.invade.classic;
import java.awt.Frame;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.Player;

/**
 *
 * @author Jonathan Crosmer
 */
public abstract class AbstractMissionCard implements Card {
    
    protected String name;
    private PlayableDeck deck;
    private Player player;
    private int missionPoints;
    
    public static final String DEFAULT_DECK_NAME = "Secret Mission Card";
    
    /**
     * Creates a new instance of Card
     */
    public AbstractMissionCard(String name, int missionPoints) {
        this.name = name;
        this.missionPoints = missionPoints;
        setDeck(null);
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
    throws EndGameException {
        if(board.getCurrentPlayer() == getPlayer() && isComplete(board)) {
            getPlayer().setBonusPoints(missionPoints);
            board.sendMessage(getPlayer() + " completed the Secret Mission " +
                    "\"" + getName(false, true) + "\"\n" + getDescriptionString());
            throw new EndGameException();
        }
    }
    
    public abstract boolean isComplete(Board board);
    
    public String getName(boolean hidden, boolean inPlay) {
        if( hidden ) {
            return ((deck == null) ? DEFAULT_DECK_NAME : deck.toString());
        }
        return name;
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
        return "(No description)";
    }
    
    public void displayCard(Frame parent, MapCanvas mapCanvas) {
        new MissionCardDisplay(parent, this).setVisible(true);
    }

    public boolean canDisplayWhenActive(Board board) {
        return getPlayer() == board.getCurrentPlayer();
    }
    
}
