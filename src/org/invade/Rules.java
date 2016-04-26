/*
 * Rules.java
 *
 * Created on July 12, 2005, 11:04 AM
 *
 */

package org.invade;

import java.util.List;
import javax.swing.Icon;

public interface Rules {
    public int getMaxAttackDice();
    public int getMaxDefenseDice();
    public boolean isAllowLongFreeMoves();
    public List<PlayableDeck> getStartingDecks(Board board);
    public GameAlgorithm createGameAlgorithm();
    public MoveVerifier getMoveVerifier();
    public String toString();  // Should return a descriptive rule set name
    public List<DiceType> getDefenseDice(Force force, Territory from, Territory to);
    public List<DiceType> getAttackDice(Force force, Territory from, Territory to);
    public List<SpecialUnit> getUnitsForPurchase();
    public List<SpecialUnit> getUnitsUsed();
    public SpecialUnit getUnitWithName(String name);
    public String getCurrencyName();
    public String getTurnName();
    public boolean canAcknowledge(Board board, Player player);
    public Icon getDevastatedIcon();
}
