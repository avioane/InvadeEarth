/*
 * AbstractRules.java
 *
 * Created on July 30, 2005, 9:17 PM
 *
 */

package org.invade.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import org.invade.Board;
import org.invade.MapIcon;
import org.invade.MoveVerifier;
import org.invade.PlayableDeck;
import org.invade.Rules;
import org.invade.SpecialUnit;
import org.invade.Player;

/**
 *
 * @author Jonathan Crosmer
 */
public abstract class AbstractRules implements Rules {
    
    private int maxAttackDice = 3;
    private int maxDefenseDice = 2;
    private boolean allowLongFreeMoves = false;
    
    public int getMaxAttackDice() {
        return maxAttackDice;
    }
    
    public void setMaxAttackDice(int maxAttackDice) {
        this.maxAttackDice = maxAttackDice;
    }
    
    public int getMaxDefenseDice() {
        return maxDefenseDice;
    }
    
    public void setMaxDefenseDice(int maxDefenseDice) {
        this.maxDefenseDice = maxDefenseDice;
    }
    
    public boolean isAllowLongFreeMoves() {
        return allowLongFreeMoves;
    }
    
    public void setAllowLongFreeMoves(boolean allowLongFreeMoves) {
        this.allowLongFreeMoves = allowLongFreeMoves;
    }
    
    public List<PlayableDeck> getStartingDecks(Board board) {
        return new ArrayList<PlayableDeck>();
    }
    
    public MoveVerifier getMoveVerifier() {
        return new DefaultMoveVerifier();
    }
    
    public List<SpecialUnit> getUnitsForPurchase() {
        return Collections.emptyList();
    }
    
    public List<SpecialUnit> getUnitsUsed() {
        return getUnitsForPurchase();
    }
    
    public SpecialUnit getUnitWithName(String name) {
        for( SpecialUnit unit : getUnitsUsed() ) {
            if( unit.toString().equals(name) ) {
                return unit;
            }
        }
        return SpecialUnit.UNKNOWN;
    }
    
    public String getCurrencyName() {
        return null;
    }
    
    public String getTurnName() {
        return "Turn";
    }
    
    public boolean canAcknowledge(Board board, Player player) {
        return true;
    }

    public Icon getDevastatedIcon() {
        return MapIcon.DEVASTATION_MARKER;
    }
    
}
