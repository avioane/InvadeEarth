/*
 * AlienDevastation.java
 *
 * Created on March 14, 2006, 9:09 AM
 *
 */

package org.invade.amoeba;

import java.util.Set;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.EndGameException;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Player;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.DefaultMoveVerifier;

public class AlienDevastation extends AbstractAmoebaCard {
    
    private TerritoryType territoryType;
    
    public AlienDevastation(TerritoryType territoryType) {
        super("Alien " + territoryType.toString() + " Devastation");
        this.territoryType = territoryType;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        final Territory invaded = board.getTerritoryDeck(territoryType).draw();
        if( invaded != null ) {
            board.setAttackingTerritory(null);
            board.setDefendingTerritory(invaded);
            board.sendMessage("Aliens devastate " + invaded.getName());
            if( board.getPlayers().contains(invaded.getOwner())
            && invaded.getForce().getMobileForce().getSize() > 0 ) {
                final Set<Territory> adjacent = board.getFreeMoveAdjacent(invaded);
                if( !adjacent.isEmpty() ) {
                    board.sendMessage("To where will the units retreat?");
                    board.setCurrentPlayer(invaded.getOwner());
                    board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
                    board.setMoveVerifier(new AbstractMoveVerifier(){
                        public void verifyWithAssumptions(Board board, Object move)
                        throws IllegalMoveException {
                            Territory territory = (Territory)move;
                            DefaultMoveVerifier.verify(adjacent.contains(territory),
                                    "You may not retreat there");
                        }
                    });
                    Territory territory = (Territory)gameThread.take();
                    board.setMoveVerifier(board.getRules().getMoveVerifier());
                    board.sendMessage(territory.getOwner() + " retreats to " + territory.getName());
                    territory.getForce().add(invaded.getForce().getMobileForce());
                }
            }
            invaded.getForce().clear();
            invaded.setOwner(Player.NEUTRAL);
            invaded.setDevastated(true, board);
            board.getTerritoryDeck(territoryType).discard(invaded);
        }
    }
}
