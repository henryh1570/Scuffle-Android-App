package com.example.hh.scuffle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

/**
 * Created by HH on 8/20/2016.
 * The CardFactory class is designed to exist as a singleton object; providing central card and
 * deck generation as well as calculating 2 card matchups via a weakness hashmap.
 */
public class CardFactory {

    HashMap<Card.Type, HashMap<Card.Type, Integer>> weaknesses;

    public CardFactory() {
        weaknesses = new HashMap<Card.Type, HashMap<Card.Type, Integer>>();
        initializeWeaknesses();
    }

    /**
     * Performs the match up calculation for two cards via
     * retrieving the corresponding value in the weakness hash map.
     */
    public int matchCards(Card firstCard, Card secondCard) {
        Card.Type firstCardType = firstCard.getCardType();
        Card.Type secondCardType = secondCard.getCardType();
        return weaknesses.get(firstCardType).get(secondCardType);
    }

    /**
     * Creates a weakness hash map that can be visualized by an adjacency matrix.
     * The weakness hash map holds the match up values for each card's interaction with another.
     */
    public void initializeWeaknesses() {
        HashMap<Card.Type, Integer> mageMap = new HashMap<Card.Type, Integer>();
        mageMap.put(Card.Type.MAGE, 0);
        mageMap.put(Card.Type.WARRIOR, -1);
        mageMap.put(Card.Type.ARCHER, 1);
        mageMap.put(Card.Type.THIEF, -1);
        mageMap.put(Card.Type.BEAST, 1);

        HashMap<Card.Type, Integer> warriorMap = new HashMap<Card.Type, Integer>();
        warriorMap.put(Card.Type.MAGE, 1);
        warriorMap.put(Card.Type.WARRIOR, 0);
        warriorMap.put(Card.Type.ARCHER, -1);
        warriorMap.put(Card.Type.THIEF, 1);
        warriorMap.put(Card.Type.BEAST, -1);

        HashMap<Card.Type, Integer> archerMap = new HashMap<Card.Type, Integer>();
        archerMap.put(Card.Type.MAGE, -1);
        archerMap.put(Card.Type.WARRIOR, 1);
        archerMap.put(Card.Type.ARCHER, 0);
        archerMap.put(Card.Type.THIEF, -1);
        archerMap.put(Card.Type.BEAST, 1);

        HashMap<Card.Type, Integer> thiefMap = new HashMap<Card.Type, Integer>();
        thiefMap.put(Card.Type.MAGE, 1);
        thiefMap.put(Card.Type.WARRIOR, -1);
        thiefMap.put(Card.Type.ARCHER, 1);
        thiefMap.put(Card.Type.THIEF, 0);
        thiefMap.put(Card.Type.BEAST, -1);

        HashMap<Card.Type, Integer> beastMap = new HashMap<Card.Type, Integer>();
        beastMap.put(Card.Type.MAGE, -1);
        beastMap.put(Card.Type.WARRIOR, 1);
        beastMap.put(Card.Type.ARCHER, -1);
        beastMap.put(Card.Type.THIEF, 1);
        beastMap.put(Card.Type.BEAST, 0);

        weaknesses.put(Card.Type.MAGE, mageMap);
        weaknesses.put(Card.Type.WARRIOR, warriorMap);
        weaknesses.put(Card.Type.ARCHER, archerMap);
        weaknesses.put(Card.Type.THIEF, thiefMap);
        weaknesses.put(Card.Type.BEAST, beastMap);
    }

    /**
     * Creates a deck with an equal distribution of each card type.
     */
    public Stack<Card> createBalancedDeck() {
        Stack<Card> deck = new Stack<Card>();
        for(int i = 0; i < 10; i++) {
            deck.add(new Card(Card.Type.MAGE));
            deck.add(new Card(Card.Type.WARRIOR));
            deck.add(new Card(Card.Type.ARCHER));
            deck.add(new Card(Card.Type.THIEF));
            deck.add(new Card(Card.Type.BEAST));
        }
        Collections.shuffle(deck);
        return deck;
    }

    public Card createRandomCard() {
        Random random = new Random();
        int k = Math.abs(random.nextInt()%5);
        switch(k) {
            case 0:
                return(new Card(Card.Type.MAGE));
            case 1:
                return(new Card(Card.Type.WARRIOR));
            case 2:
                return(new Card(Card.Type.ARCHER));
            case 3:
                return(new Card(Card.Type.THIEF));
            case 4:
                return(new Card(Card.Type.BEAST));
            default:
                return(new Card(Card.Type.OTHER));
        }
    }

    /**
     * Creates a deck with a random distribution of card types.
     */
    public Stack<Card> createRandomDeck() {
        Stack<Card> deck = new Stack<Card>();
        Random random = new Random();
        for(int i = 0; i < 50; i++) {
            deck.add(createRandomCard());
        }
        return deck;
    }
}