/*
 * AbstractCommandCard.java
 *
 * Created on July 10, 2005, 5:30 PM
 *
 */

package org.invade.commandcards;
import java.awt.Frame;
import org.invade.Board;
import org.invade.Card;
import org.invade.PlayableDeck;
import org.invade.CommonBoardMethods;
import org.invade.EconomicCard;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.MapCanvas;
import org.invade.Player;
import org.invade.SpecialUnit;
import org.invade.TerritoryType;


/**
 *
 * @author Jonathan Crosmer
 */
public abstract class AbstractCommandCard implements Card {
    
    private SpecialUnit requiredCommander;
    private int powerUpCost;
    private String name;
    private PlayableDeck deck;
    private Player player;
    
    public static final String BEFORE_FIRST_INVASION = "Play on your turn " +
            "before your first invasion is declared.";
    public static final String END_OF_TURN = "Play at the end of your turn.";
    public static final String END_OF_GAME = "Play at the end of the game.";
    
    /**
     * Creates a new instance of Card
     */
    public AbstractCommandCard(SpecialUnit requiredCommander, int powerUpCost, String name) {
        this.requiredCommander = requiredCommander;
        this.powerUpCost = powerUpCost;
        this.name = name;
        setDeck(null);
    }
    
    public boolean canPlay(Board board) {
        return (board.getUnitCount(board.getCurrentPlayer(), requiredCommander) > 0
                || requiredCommander == null)        
                && board.getCurrentPlayer().getEnergy() >= getPowerUpCost(board)
                && ! CommonBoardMethods.isFrequencyJamAgainst(board,
                board.getCurrentPlayer());
    }
    
    public String getPlayMessage(String playerName, String name) {
        return playerName + " plays " + name;
    }
    
    /* Play and doAction should only be called by a GameThread. */
    public void play(Board board, GameThread gameThread) throws EndGameException {
        Player current = board.getCurrentPlayer();
        board.sendMessage(getPlayMessage(current.getName(), name));
        Card previous = board.getLastCardUsed();
        board.setLastCardUsed(this);
        board.getCurrentPlayer().addEnergy( - getPowerUpCost(board));
        current.getCards().remove(this);
        
        doAction(board, gameThread);
        
        board.setCurrentPlayer(current);
        board.setLastCardUsed(previous);
    }
    
    /* Performs the action associated with this card.*/
    public abstract void doAction(Board board, GameThread gameThread) throws EndGameException;
    
    // Ignore any board state
    public String getName() {
        return name;
    }
    
    public String getName(boolean hidden, boolean inPlay) {
        if( hidden && ! inPlay ) {
            return (deck == null) ?
                (requiredCommander.toString().split(" ")[0] + " Command Card")
                : deck.toString();
        }
        return getName();
    }
    
    public String toString() {
        return getName();
    }
    
    public PlayableDeck getDeck() {
        return deck;
    }
    
    public void setDeck(PlayableDeck deck) {
        this.deck = deck;
    }
    
    public void checkForAction(Board board, GameThread gameThread)
    throws EndGameException {}
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public SpecialUnit getRequiredCommander() {
        return requiredCommander;
    }
    
    public void setRequiredCommander(SpecialUnit requiredCommander) {
        this.requiredCommander = requiredCommander;
    }
    
    // Ignore any board state
    public int getPowerUpCost() {
        return powerUpCost;
    }
    
    public int getPowerUpCost(Board board) {
        int extra = 0;
        for(Card card : board.getCardsInPlay()) {
            if(card instanceof EconomicCard) {
                extra += ((EconomicCard)card).getCardPowerUpChange(board);
            }
        }
        return powerUpCost + extra;
    }
    
    public String getWhenString() {
        return BEFORE_FIRST_INVASION;
    }
    
    public String getDescriptionString() {
        return "(No description)";
    }
    
    public void displayCard(Frame parent, MapCanvas mapCanvas) {
        new CommandCardDisplay(parent, this).setVisible(true);
    }
    
    public static String getOnInvasionString(TerritoryType territoryType) {
        return "Play after an opponent declares an invasion into a "
                + territoryType.getNoun().toLowerCase() + " territory.";
    }
    
    public static String getOnInvadeYouString(TerritoryType territoryType) {
        return "Play after an opponent declares an invasion into a"
                + (territoryType == null ? "" :
                    " " + territoryType.getNoun().toLowerCase())
                    + " territory you occupy.";
    }
    
    public boolean canDisplayWhenActive(Board board) {
        return true;
    }
    
}
