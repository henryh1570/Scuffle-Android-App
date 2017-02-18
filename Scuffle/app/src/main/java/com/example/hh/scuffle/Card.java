package com.example.hh.scuffle;

/**
 * Created by HH on 8/20/2016.
 * The Card class holds card types and is the program representation of cards in the game.
 */
public class Card {

    private Type cardType;

    public Card(Type type) {
        cardType = type;
    }

    public enum Type {
        ARCHER, MAGE, WARRIOR, THIEF, BEAST, OTHER
    }

    public Type getCardType() {
        return cardType;
    }

    public String getName() {
        switch(cardType) {
            case ARCHER:
                return "Archer";
            case MAGE:
                return "Mage";
            case WARRIOR:
                return "Warrior";
            case THIEF:
                return "Thief";
            case BEAST:
                return "Beast";
            default:
                return "Other";
        }
    }
}