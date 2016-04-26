/*
 * AutomaticCard.java
 *
 * Created on August 4, 2005, 12:00 PM
 *
 */

package org.invade;

/* A marker interface for Card objects that indicates an agent may play a
 * particular Card without understanding any additional semantics.
 * For example, a Card that requires the agent to select three distinct,
 * friendly territories of a certain type would not be an AutomaticCard;
 * a card that allows an additional free move could implement AutomaticCard,
 * since agents should already understand free moves.  However, a Card
 * is never required to implement this interface.  If a Card that takes effect
 * without further input may prove to be detrimental to the player in
 * a significant number of cases, it might be better if agents were forced
 * to explicitly understand the Card.  */
public interface AutomaticCard extends Card {}
