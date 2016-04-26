/*
 * PlayerChoice.java
 *
 * Created on June 30, 2005, 3:33 PM
 *
 */

package org.invade;

/** Maps Player objects to integers that may record bids, turn order choices, etc.
 *  The natural order is by choice value.
 */
public class PlayerChoice implements Comparable<PlayerChoice> {
    private Player player;
    private int choice;
    public PlayerChoice(Player player, int choice) {
        this.player = player;
        this.choice = choice;
    }
    public Player getPlayer() { return player; }
    public int getChoice() { return choice; }
    public int compareTo(PlayerChoice other) {
        return choice - other.choice;
    }
}
