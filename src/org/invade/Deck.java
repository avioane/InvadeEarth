/*
 * Deck.java
 *
 * Created on July 10, 2005, 6:15 PM
 *
 */

package org.invade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Jonathan Crosmer
 */
public class Deck<E> {
    
    private List<E> cards = new ArrayList<E>();
    private List<E> discarded = new ArrayList<E>();
    private String name = "Deck";
    
    public void shuffle(Random random) {
        Collections.shuffle(cards, random);
    }
    
    public E draw() {
        if( getCards().isEmpty() ) {
            recycleDiscarded();
            if( getCards().isEmpty() ) {
                return null;
            }
        }
        return cards.remove(cards.size() - 1);
    }
    
    public List<E> getCards() {
        return cards;
    }
    
    public void discard(E card) {
        discarded.add(card);
    }
    
    public void recycleDiscarded() {
        Collections.reverse(discarded);
        cards.addAll(discarded);
        discarded.clear();
    }
    
    public void add(E ... moreCards) {
        for( E card : moreCards ) {
            cards.add(card);
        }
    }
    
    public String toString() {
        return getName();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
