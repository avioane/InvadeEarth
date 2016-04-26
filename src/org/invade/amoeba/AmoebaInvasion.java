/*
 * AmoebaInvasion.java
 *
 * Created on March 14, 2006, 8:54 AM
 *
 */

package org.invade.amoeba;

import java.util.Collections;
import java.util.Set;
import org.invade.AbstractMoveVerifier;
import org.invade.Board;
import org.invade.CommonBoardEvents;
import org.invade.DiceType;
import org.invade.Die;
import org.invade.EndGameException;
import org.invade.Force;
import org.invade.GameThread;
import org.invade.IllegalMoveException;
import org.invade.Player;
import org.invade.Territory;
import org.invade.TerritoryType;
import org.invade.TurnMode;
import org.invade.rules.AmoebaRules;
import org.invade.rules.DefaultMoveVerifier;

public class AmoebaInvasion extends AbstractAmoebaCard {
    
    private TerritoryType territoryType;
    private int units;
    
    public AmoebaInvasion(TerritoryType territoryType, int units) {
        super(territoryType.toString() + " Invasion");
        this.territoryType = territoryType;
        this.units = units;
    }
    
    public void doAction(Board board, GameThread gameThread) throws EndGameException {
        Territory mothership = new Territory();
        mothership.setName("The Mothership");
        mothership.setOwner(AmoebaRules.ALIENS);
        mothership.getForce().addRegularUnits(units);
        mothership.getForce().getSpecialUnits().add(AmoebaRules.ALIEN_LEADER);        
        
        final Territory invaded = board.getTerritoryDeck(territoryType).draw();
        if( invaded != null ) {
            
            board.setAttackingTerritory(mothership);
            board.setDefendingTerritory(invaded);
            
            board.sendMessage(units + " aliens invade " + invaded.getName());
            if( board.getPlayers().contains(invaded.getOwner())
            && invaded.getForce().getMobileForce().getSize() > 0 ) {
                final Set<Territory> adjacent = board.getFreeMoveAdjacent(invaded);
                if( !adjacent.isEmpty() ) {
                    board.sendMessage("Defend " + invaded + " or retreat to an adjacent territory?");
                    board.setCurrentPlayer(invaded.getOwner());
                    board.setTurnMode(TurnMode.CHOOSE_TERRITORY);
                    board.setMoveVerifier(new AbstractMoveVerifier(){
                        public void verifyWithAssumptions(Board board, Object move)
                        throws IllegalMoveException {
                            Territory territory = (Territory)move;
                            DefaultMoveVerifier.verify(adjacent.contains(territory)
                            || territory == invaded,
                                    "You may not retreat there");
                        }
                    });
                    Territory territory = (Territory)gameThread.take();
                    board.setMoveVerifier(board.getRules().getMoveVerifier());
                    if( territory != invaded ) {
                        board.sendMessage(territory.getOwner() + " retreats to " + territory.getName());
                        territory.getForce().add(invaded.getForce().getMobileForce());
                        invaded.getForce().clear();
                    }
                }
            }
            
            if( invaded.getOwner() == Player.NEUTRAL ) {
                invaded.getForce().clear();
                invaded.getForce().add(mothership.getForce());
                invaded.setOwner(AmoebaRules.ALIENS);
            } else if( invaded.getOwner() == AmoebaRules.ALIENS ) {
                invaded.getForce().add(mothership.getForce());
            }
            
            // Remaining force defends
            while( invaded.getOwner() != AmoebaRules.ALIENS
                    && mothership.getForce().getRegularUnits() > 0 ) {
                
                Force attackForce = mothership.getForce();
                Force defenseForce = invaded.getForce();
                
                board.getAttackerDice().clear();
                for( DiceType dieType : board.getRules().getAttackDice(attackForce,
                        board.getAttackingTerritory(), board.getDefendingTerritory()) ) {
                    board.getAttackerDice().add(new Die(board.getRandom(), DiceType.EIGHT_SIDED));
                }
                Collections.sort(board.getAttackerDice());
                Collections.reverse(board.getAttackerDice());
                board.getDefenderDice().clear();
                for( DiceType dieType : board.getRules().getDefenseDice(defenseForce,
                        board.getAttackingTerritory(), board.getDefendingTerritory()) ) {
                    board.getDefenderDice().add(new Die(board.getRandom(), dieType));
                }
                Collections.sort(board.getDefenderDice());
                Collections.reverse(board.getDefenderDice());
                
                for( int i = 0; i < Math.min(board.getAttackerDice().size(),
                        board.getDefenderDice().size()); ++i ) {
                    if( board.getAttackerDice().get(i).getValue()
                    > board.getDefenderDice().get(i).getValue()) {
                        board.getDefendingTerritory().addNumberToDestroy(1);
                    } else {
                        board.getAttackingTerritory().addNumberToDestroy(1);
                    }
                }
                board.setCurrentPlayer(board.getDefendingTerritory().getOwner());
                board.setTurnMode(TurnMode.BATTLE_RESULTS);
                gameThread.take();
                
                CommonBoardEvents.checkForDestroyed(board, gameThread, board.getDefendingTerritory());
                
                Force destroyed = new Force();
                destroyed.addRegularUnits(board.getAttackingTerritory().getNumberToDestroy());
                board.getAttackingTerritory().destroyUnits(destroyed);
                
                if( board.getDefendingTerritory().getForce().getMobileIndependentSize() <= 0 ) {
                    board.sendMessage(board.getDefendingTerritory().getName()
                    + " falls");
                    board.getDefendingTerritory().setOwner(AmoebaRules.ALIENS);
                    board.getDefendingTerritory().getForce().add(mothership.getForce());
                }
                
                board.getTerritoryDeck(territoryType).discard(invaded);
                
            }
            board.setAttackingTerritory(null);
            board.setDefendingTerritory(null);
        }
    }
    
}