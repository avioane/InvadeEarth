/*
 * CommonBoardEvents.java
 *
 * Created on July 20, 2005, 11:01 AM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.List;
import org.invade.miraclecards.Heaven;
import org.invade.rules.GodstormRules;

/* This class contains convenience methods to handle events that are common in
 * Risk games.  GameThread objects can use these methods to simplify their own
 * code.  The methods in this class should only be called from a thread that
 * is a GameThread; otherwise, they may block the thread that executes them.
 * Card objects may execute these methods in their "play" methods, since
 * the contract of "play" requires that a GameThread invoke it.
 */
public class CommonBoardEvents {
    private CommonBoardEvents() {}
    
    public static void receiveStartingRegularUnits(Board board, int count) {
        Force starting = new Force();
        starting.addRegularUnits(count);
        for( Player player : board.getPlayers() ) {
            player.getReinforcements().add(starting);
        }
    }
    
    public static void checkForDestroyed(Board board, GameThread gameThread,
            Territory territory) throws EndGameException {
        int available = territory.getForce().getMobileIndependentSize();
        if( territory.getNumberToDestroy() > available ) {
            territory.setNumberToDestroy(available);
        }
        if( territory.getNumberToDestroy() > 0 ) {
            board.setCurrentPlayer(territory.getOwner());
            board.setTurnMode(TurnMode.CHOOSE_DESTROYED);
            board.setDamagedTerritory(territory);
            Force force = (Force)gameThread.take();
            territory.destroyUnits(force);
            board.setDamagedTerritory(null);
        }
    }
    
    public static void checkCardsInPlay(Board board, GameThread gameThread)
    throws EndGameException {
        /* Make copy so we do not get concurrent modification exceptions
         * when cards are "used up" */
        List<Card> cards = new ArrayList<Card>(board.getCardsInPlay());
        for( Card card : cards ) {
            card.checkForAction(board, gameThread);
        }
    }
    
    /* The turnMode should be a mode that requires acknowledgements but can
     * be reset by cards; for example, TurnMode.ACKNOWLEDGE_INVASIONS.
     * Only players in the list "consider" will be asked.
     * If anyone plays a card, acknowledgement will be required
     * of each player again, starting over from the first in the list. */
    public static void getAcknowledgements(Board board, GameThread gameThread,
            TurnMode turnMode, List<Player> consider) throws EndGameException {
        Player current = board.getCurrentPlayer();
        boolean haveAcknowledgements = false;
        while( !haveAcknowledgements ) {
            haveAcknowledgements = true;
            for( Player player : consider ) {
                board.setCurrentPlayer(player);
                board.setTurnMode(turnMode);
                Object acknowledge = gameThread.take();
                if( acknowledge != SpecialMove.ACKNOWLEDGE ) {
                    if( acknowledge instanceof Card ) {
                        ((Card)acknowledge).play(board, gameThread);
                    }
                    haveAcknowledgements = false;
                    break;
                }
            }
        }
        board.setCurrentPlayer(current);
    }
    
    public static void getAcknowledgement(Board board, GameThread gameThread,
            TurnMode turnMode) throws EndGameException {
        while(true) {
            board.setTurnMode(turnMode);
            Object acknowledge = gameThread.take();
            if( acknowledge != SpecialMove.ACKNOWLEDGE ) {
                if( acknowledge instanceof Card ) {
                    ((Card)acknowledge).play(board, gameThread);
                }
            } else {
                break;
            }
        }
    }
    
    public static void placeReinforcements(Board board, GameThread gameThread)
    throws EndGameException {
        board.setTurnMode(TurnMode.REINFORCEMENTS);
        while( !board.getCurrentPlayer().getReinforcements().isEmpty() ) {
            ForcePlacement duple = (ForcePlacement)gameThread.take();
            Territory territory = duple.getTerritory();
            Force force = duple.getForce();
            board.getCurrentPlayer().getReinforcements().subtract(force);
            territory.getForce().add(force);
        }
    }
    
    public static void removeMODs(Board board, GameThread gameThread, Player player, int count)
    throws EndGameException {
        Player current = board.getCurrentPlayer();
        for( int i = 0;
        i < count && CommonBoardMethods.hasMOD(board, player);
        ++i ) {
            board.setTurnMode(TurnMode.DESTROY_A_REGULAR_UNIT);
            board.setCurrentPlayer(player);
            Territory territory = (Territory)gameThread.take();
            territory.getForce().addRegularUnits(-1);
            territory.update();
        }
        board.setCurrentPlayer(current);
    }
    
    public static void sufferPlague(Board board, Territory territory) {
        if( territory.isPlague() ) {
            territory.getForce().getSpecialUnits().remove(GodstormRules.WAR);
            territory.getForce().getSpecialUnits().remove(GodstormRules.MAGIC);
            territory.getForce().getSpecialUnits().remove(GodstormRules.DEATH);
            territory.getForce().getSpecialUnits().remove(GodstormRules.SKY);
            int kill = territory.getForce().getRegularUnits() / 2;
            territory.getForce().setRegularUnits(territory.getForce().getRegularUnits() - kill);
            Heaven heaven = Heaven.getHeaven(board, territory.getOwner());
            if( heaven != null ) {
                heaven.addUnits(kill);
            }
        }
    }
        
}
