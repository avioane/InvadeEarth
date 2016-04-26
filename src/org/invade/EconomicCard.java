/*
 * EconomicCard.java
 *
 * Created on March 14, 2006, 2:54 PM
 *
 */

package org.invade;

/* For cards that alter global values such as the price of cards. */
public interface EconomicCard {
    public int getCardPriceChange(Board board);
    public int getCardPowerUpChange(Board board);
    public int getBidChange(Board board);
}
