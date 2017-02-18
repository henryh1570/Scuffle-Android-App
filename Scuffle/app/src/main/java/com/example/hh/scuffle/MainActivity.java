package com.example.hh.scuffle;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    //Global variable declarations
    ImageButton computerCard1;
    ImageButton computerCard2;
    ImageButton computerCard3;
    ImageButton computerCard4;
    ImageButton computerCard5;
    ImageButton playerCard1;
    ImageButton playerCard2;
    ImageButton playerCard3;
    ImageButton playerCard4;
    ImageButton playerCard5;
    ImageButton playerBoardCard;
    ImageButton computerBoardCard;
    ImageButton selectedImageButton;
    ImageButton computerSelectedCard;
    Button continueButton;
    Button specialButton;
    Button resetButton;
    Button tipsButton;
    TextView round;
    TextView playerHonor;
    TextView computerHonor;
    TextView selectedCardText;
    TextView info;
    TextView tips;
    int roundNumber = 1;
    int playerHonorNumber = 0;
    int computerHonorNumber = 0;
    int honorWaged = 1;
    int toolTips = 0;
    int specialUseCount = 0;
    final int HAND_SIZE = 5;
    final int SPECIAL_LIMIT = 2;
    boolean isTargetingPhase = false;
    boolean mageHasRevealed = false;
    boolean isDiscardRequired = false;
    Random random = new Random();
    Bitmap archerBitmap;
    Bitmap mageBitmap;
    Bitmap warriorBitmap;
    Bitmap beastBitmap;
    Bitmap thiefBitmap;
    Bitmap emptyBitmap;
    Bitmap mysteryBitmap;
    Bitmap boardArcherBitmap;
    Bitmap boardMageBitmap;
    Bitmap boardWarriorBitmap;
    Bitmap boardBeastBitmap;
    Bitmap boardThiefBitmap;
    Bitmap boardEmptyBitmap;
    CardFactory cardFactory = new CardFactory();
    Stack<Card> playerDeck;
    Stack<Card> computerDeck;
    ArrayList<Card> previouslyPlayedCards = new ArrayList<Card>();
    HashMap<ImageButton, Card> playerHand = new HashMap<ImageButton, Card>();
    HashMap<ImageButton, Card> computerHand = new HashMap<ImageButton, Card>();
    final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization of buttons and imageButtons.
        computerCard1 = (ImageButton) findViewById(R.id.computerCard1);
        computerCard2 = (ImageButton) findViewById(R.id.computerCard2);
        computerCard3 = (ImageButton) findViewById(R.id.computerCard3);
        computerCard4 = (ImageButton) findViewById(R.id.computerCard4);
        computerCard5 = (ImageButton) findViewById(R.id.computerCard5);
        playerCard1 = (ImageButton) findViewById(R.id.playerCard1);
        playerCard2 = (ImageButton) findViewById(R.id.playerCard2);
        playerCard3 = (ImageButton) findViewById(R.id.playerCard3);
        playerCard4 = (ImageButton) findViewById(R.id.playerCard4);
        playerCard5 = (ImageButton) findViewById(R.id.playerCard5);
        playerBoardCard = (ImageButton) findViewById(R.id.playerBoardCard);
        computerBoardCard = (ImageButton) findViewById(R.id.computerBoardCard);
        continueButton = (Button) findViewById(R.id.continueButton);
        specialButton = (Button) findViewById(R.id.specialButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        tipsButton = (Button) findViewById(R.id.tipsButton);

        //Initialize step for textViews to update text.
        round = (TextView) findViewById(R.id.roundNumberTextView);
        playerHonor = (TextView) findViewById(R.id.playerHonorTextView);
        computerHonor = (TextView) findViewById(R.id.computerHonorTextView);
        selectedCardText = (TextView) findViewById(R.id.selectedCardTextView);
        info = (TextView) findViewById(R.id.infoTextView);
        tips = (TextView) findViewById(R.id.tipsTextView);

        //Additional initial steps for disabling unused buttons, resizing images, etc.
        resizeBitmaps();
        initializeHandKeySets();
        setEnabledComputerButtons(false);
        computerBoardCard.setEnabled(false);
        playerBoardCard.setEnabled(false);
        info.setTextColor(Color.parseColor("#5C5C5C"));
        playerDeck = cardFactory.createBalancedDeck();
        computerDeck = cardFactory.createBalancedDeck();
        fillHands();
        redrawBoard();
        redrawHands();

        //Methods below control the action of the buttons on the screen. Clicking player card
        //will set the reference of selectedImageButton to that card and display special tips.

        playerCard1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                selectedImageButton = playerCard1;
                playerButtonFunction();
            }
        });

        playerCard2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                selectedImageButton = playerCard2;
                playerButtonFunction();

            }
        });

        playerCard3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                selectedImageButton = playerCard3;
                playerButtonFunction();

            }
        });

        playerCard4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                selectedImageButton = playerCard4;
                playerButtonFunction();

            }
        });

        playerCard5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                selectedImageButton = playerCard5;
                playerButtonFunction();

            }
        });

        //The opponent's cards are available to click during an archer targeting effect phase.
        //Sets the selectedImageButton to the corresponding computer card.

        computerCard1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                computerSelectedCard = computerCard1;
                computerButtonFunction();
            }
        });

        computerCard2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                computerSelectedCard = computerCard2;
                computerButtonFunction();
            }
        });

        computerCard3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                computerSelectedCard = computerCard3;
                computerButtonFunction();
            }
        });

        computerCard4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                computerSelectedCard = computerCard4;
                computerButtonFunction();
            }
        });

        computerCard5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                computerSelectedCard = computerCard5;
                computerButtonFunction();
            }
        });

        //The continueButton will match the two players' selected cards, update scores, refill hands,
        //update the texts, draw the images on screen, re-enable buttons, and check for last round.
        //If continue is clicked during archer targeting, it will instead check if a computer card
        //is successfully chosen.
        continueButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (isTargetingPhase == false) {
                    Card selectedCard = playerHand.get(selectedImageButton);

                    if (selectedCard != null) {
                        previouslyPlayedCards.add(selectedCard);
                        computerSelectValidCard();
                        drawBoardCard(playerBoardCard, selectedCard);
                        drawBoardCard(computerBoardCard, computerHand.get(computerSelectedCard));
                        matchCards();
                        specialButton.setEnabled(true);
                        roundNumber += 1;

                        //Clean up phase
                        if (roundNumber != 11) {
                            round.setText(String.valueOf(roundNumber));
                            computerSelectedCard = null;
                            honorWaged = 1;
                            specialUseCount = 0;
                            fillHands();
                            redrawHands();
                            setEnabledPlayerButtons(true);
                            mageHasRevealed = false;
                        } else {
                            endMatch();
                        }
                        selectedCardText.setText("");
                    } else {
                        info.setText(R.string.Missing_Select_Continue);
                        playAudio("fail_select.mp3");
                    }
                    selectedImageButton = null;
                    computerSelectedCard = null;
                } else {
                    //Archer/Thief Effect is in place
                    if (computerSelectedCard != null) {
                        //For archer, redraw it to empty after shooting special effect.
                        if(playerHand.get(selectedImageButton).getCardType().equals(Card.Type.ARCHER)) {
                            archerEffectShooting();
                            replaceHandCard(playerHand, selectedImageButton, new Card(Card.Type.OTHER));
                            selectedImageButton.setImageBitmap(emptyBitmap);
                            selectedImageButton.setEnabled(false);
                        } else {
                            thiefSwap();
                        }
                        computerSelectedCard = null;
                        selectedImageButton = null;

                        selectedCardText.setText("");
                        specialUseCount++;
                        if (specialUseCount == SPECIAL_LIMIT) {
                            specialButton.setEnabled(false);
                        }
                    } else {
                        String message = ("Select a valid target.");
                        info.setText(message);
                        playAudio("fail_select.mp3");
                    }
                }
            }
        });

        //Activate a special via discarding a card from your hand.
        specialButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                info.setTextColor(Color.parseColor("#5C5C5C"));
                if(selectedImageButton != null){
                    specialEffects();
                    if(isTargetingPhase == false) {
                        specialUseCount++;
                    }
                    if (specialUseCount == SPECIAL_LIMIT) {
                        specialButton.setEnabled(false);
                    }
                    selectedCardText.setText("");

                    //Used for mage/warrior special. Draws the card discarded as empty.
                    if(isDiscardRequired == true) {
                        replaceHandCard(playerHand, selectedImageButton, new Card(Card.Type.OTHER));
                        selectedImageButton.setImageBitmap(emptyBitmap);
                        selectedImageButton.setEnabled(false);
                        isDiscardRequired = false;
                        selectedImageButton = null;
                    }

                } else {
                    info.setText(R.string.Missing_Select_Special);
                    playAudio("fail_select.mp3");
                }
            }
        });

        //Reset the game stats.
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                resetAll();
                playAudio("reset.mp3");
            }
        });

        //Scroll through tool tips.
        tipsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setTips();
                playAudio("tips.mp3");
            }
        });
    }

    /**
     * Uses the mediaPlayer object to play a sound file after a button is clicked.
     * @param audioFileName is the name of the desired audio file to be played.
     * Audio files should be mp3 files located in /assets.
     */
    public void playAudio(String audioFileName) {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd(audioFileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls the flow of special effects with a simple switch statement.
     */
    public void specialEffects() {
        switch(playerHand.get(selectedImageButton).getCardType()) {
            case ARCHER:
                archerEffectTargeting();
                playAudio("archer_special.mp3");
                break;
            case MAGE:
                mageEffect();
                isDiscardRequired = true;
                playAudio("mage_special.mp3");
                break;
            case WARRIOR:
                warriorEffect();
                isDiscardRequired = true;
                playAudio("warrior_special.mp3");
                break;
            case THIEF:
                thiefEffectTargeting();
                playAudio("thief_special.mp3");
                break;
            case BEAST:
                beastEffect();
                playAudio("beast_special.mp3");
                break;
            default:
                break;
        }
    }

    /**
     * Play the basic card sound effect when selecting a card to play or activate special.
     */
    public void playBasicCardSound() {
        switch(playerHand.get(selectedImageButton).getCardType()) {
            case ARCHER:
                playAudio("archer.mp3");
                break;
            case MAGE:
                playAudio("mage.mp3");
                break;
            case WARRIOR:
                playAudio("warrior.mp3");
                break;
            case THIEF:
                playAudio("thief.mp3");
                break;
            case BEAST:
                playAudio("beast.mp3");
                break;
            default:
                break;
        }
    }

    /**
     * The archer effect eliminates a random card from the opponent's hand. First the archer
     * card is discarded and the player enters a targeting phase.
     */
    public void archerEffectTargeting() {
        isTargetingPhase = true;
        String message = ("Select a target from your opponent's hand.");
        info.setText(message);
        specialButton.setEnabled(false);
        setEnabledComputerButtons(true);
        setEnabledPlayerButtons(false);
        disableEmptyButtons();
    }

    /**
     * Replace the targeted computer card with an empty card, redraw it,
     * then re-enable buttons. Exit the targeting phase.
     */
    public void archerEffectShooting() {
        String message = ("Your archer specialty eliminated the opponent's "
                +computerHand.get(computerSelectedCard).getName()+".");
        info.setText(message);
        replaceHandCard(computerHand, computerSelectedCard, new Card(Card.Type.OTHER));
        computerSelectedCard.setImageBitmap(emptyBitmap);
        specialButton.setEnabled(true);
        setEnabledComputerButtons(false);
        setEnabledPlayerButtons(true);
        disableEmptyButtons();
        isTargetingPhase = false;
    }

    /**
     * The mage effect reveals a specified amount of cards from the computer's hand.
     */
    public void mageEffect() {
        for(ImageButton imageButton: computerHand.keySet()) {
            drawHandCard(imageButton, computerHand.get(imageButton));
        }
        String message = ("Your mage speciality revealed the opponent's cards.");
        info.setText(message);
        mageHasRevealed = true;
    }

    /**
     * The warrior effect doubles the honor staked for the round.
     */
    public void warriorEffect() {
        honorWaged = honorWaged * 2;
        String message = ("Your warrior doubled the stakes of honor for the round.");
        info.setText(message);
    }

    /**
     * Enter the targeting phase for a thief swap effect.
     */
    public void thiefEffectTargeting() {
        isTargetingPhase = true;
        String message = ("Select a target from your opponent's hand.");
        info.setText(message);
        specialButton.setEnabled(false);
        setEnabledComputerButtons(true);
        setEnabledPlayerButtons(false);
        disableEmptyButtons();
    }

    /**
     * Perform the thief swap with a selected card from the computer's hand.
     */
    public void thiefSwap() {
        Card computerCard = computerHand.get(computerSelectedCard);
        String message = ("Your thief swaps with the opponent's "
                +computerHand.get(computerSelectedCard).getName()+".");
        info.setText(message);

        //The swap via copying each other's cards.
        replaceHandCard(playerHand, selectedImageButton, computerCard);
        replaceHandCard(computerHand, computerSelectedCard, new Card(Card.Type.THIEF));

        //Redraw them visually to show the change.
        drawHandCard(selectedImageButton, computerCard);
        computerSelectedCard.setImageBitmap(thiefBitmap);

        specialButton.setEnabled(true);
        setEnabledComputerButtons(false);
        setEnabledPlayerButtons(true);
        disableEmptyButtons();
        isTargetingPhase = false;
    }

    /**
     * The beast will mimic a randomly played card from the player.
     * If Used on first turn, the beast will mimic a random card.
     */
    public void beastEffect() {
        int k;
        Card card;
        String message;

        if (previouslyPlayedCards.size() != 0) {
            k = Math.abs(random.nextInt() % previouslyPlayedCards.size());
            card = previouslyPlayedCards.get(k);
            message = ("Your beast card mimics your past played "+card.getName()+" card.");
        } else {
            do {
                card = cardFactory.createRandomCard();
            } while (card.getCardType() == Card.Type.BEAST);
            message = ("Your beast card mimics a random card. The "+card.getName()+" card.");
        }
        info.setText(message);
        replaceHandCard(playerHand, selectedImageButton, card);
        drawHandCard(selectedImageButton, card);
        selectedImageButton = null;
    }

    /**
     * Perform the card match comparison via CardFactory's functionality. Distribute points and
     * updates the info text view.
     */
    public void matchCards() {
        Card selectedCard = playerHand.get(selectedImageButton);
        Card computerCard = computerHand.get(computerSelectedCard);
        int result = cardFactory.matchCards(selectedCard, computerCard);
        String strResult = "";

        if (result == 1) {
            playerHonorNumber += honorWaged;
            if (roundNumber != 10) {
                strResult = ("Your " + selectedCard.getName() + " card defeated the computer's "
                        + computerCard.getName() + " card.");
                info.setTextColor(Color.BLUE);
                playAudio("round_won.mp3");
            }
        } else if (result == -1){
            computerHonorNumber += honorWaged;
            if (roundNumber != 10) {
                strResult = ("The computer's " + computerCard.getName() + " card defeated your "
                        + selectedCard.getName() + " card.");
                info.setTextColor(Color.RED);
                playAudio("round_loss.mp3");
            }
        } else {
            if(roundNumber != 10) {
                strResult = ("The two cards were equal in strength. No honor was gained.");
                info.setTextColor(Color.DKGRAY);
                playAudio("round_tie.mp3");
            }
        }
        info.setText(strResult);
        playerHonor.setText(String.valueOf(playerHonorNumber));
        computerHonor.setText(String.valueOf(computerHonorNumber));
    }

    /**
     * The computer will select a random card in its hand that is not discarded by a player's
     * archer special effect.
     */
    public void computerSelectValidCard() {
        ArrayList<ImageButton> buttons = new ArrayList<ImageButton>(computerHand.keySet());
        while(computerSelectedCard == null ||
                computerHand.get(computerSelectedCard).getCardType().equals(Card.Type.OTHER)) {
            int k = Math.abs(random.nextInt() % HAND_SIZE);
            computerSelectedCard = buttons.get(k);
        }
    }

    /**
     * Clicking on a player button will update selectedCardText's name, play card sound fx,
     * and update special info.
     */
    public void playerButtonFunction() {
        selectedCardText.setText(playerHand.get(selectedImageButton).getName());
        if(specialUseCount != SPECIAL_LIMIT) {
            setSpecialInfo();
        }
        info.setTextColor(Color.parseColor("#5C5C5C"));
        playBasicCardSound();
    }

    /**
     * Clicking on a computer button will update selectedCardText's name and info field.
     */
    public void computerButtonFunction() {
        String card = "???";
        if(mageHasRevealed == true) {
            card = computerHand.get(computerSelectedCard).getName();
        }
        String message = ("Opponent's "+card);
        selectedCardText.setText(message);
        message = ("Target set.");
        info.setText(message);
        info.setTextColor(Color.parseColor("#5C5C5C"));
    }

    /**
     * Sets tips for explaining card special abilities when selecting a card.
     */
    public void setSpecialInfo() {
        switch(playerHand.get(selectedImageButton).getCardType()) {
            case ARCHER:
                info.setText(R.string.InfoArcherSpecial);
                break;
            case MAGE:
                info.setText(R.string.InfoMageSpecial);
                break;
            case WARRIOR:
                info.setText(R.string.InfoWarriorSpecial);
                break;
            case THIEF:
                info.setText(R.string.InfoThiefSpecial);
                break;
            case BEAST:
                info.setText(R.string.InfoBeastSpecial);
                break;
            default:
                break;
        }
    }

    /**
     * Sets the tooltips for the tips text view. Scrolls through several strings and wraps around.
     */
    public void setTips(){
        switch(toolTips) {
            case 0:
                tips.setText(R.string.TipWeaknesses);
                break;
            case 1:
                tips.setText(R.string.TipStrengths);
                break;
            case 2:
                tips.setText(R.string.Tip1);
                break;
            case 3:
                tips.setText(R.string.Tip2);
                break;
            case 4:
                tips.setText(R.string.Tip3);
                break;
            case 5:
                tips.setText(R.string.Tip4);
                break;
            case 6:
                tips.setText(R.string.Tip5);
                break;
            case 7:
                tips.setText(R.string.Tip6);
                break;
            case 8:
                tips.setText(R.string.Tip7);
                break;
            case 9:
                tips.setText("");
                break;
        }
        if (toolTips != 9) {
            toolTips++;
        } else {
            toolTips = 0;
        }
    }

    /**
     * Clears out both player's hands and repopulates them with new cards.
     */
    public void fillHands() {
        ArrayList<ImageButton> buttons = new ArrayList<ImageButton>(playerHand.keySet());
        playerHand.clear();
        for (ImageButton imageButton : buttons) {
            playerHand.put(imageButton, playerDeck.pop());
        }

        buttons = new ArrayList<ImageButton>(computerHand.keySet());
        computerHand.clear();

        for (ImageButton imageButton : buttons) {
            computerHand.put(imageButton, computerDeck.pop());
        }
    }

    /**
     * An initialization method for populating the key sets of computer and player hand hash maps.
     * Used for optimization in refilling player hands and future referencing.
     */
    public void initializeHandKeySets(){
        playerHand.put(playerCard1, null);
        playerHand.put(playerCard2, null);
        playerHand.put(playerCard3, null);
        playerHand.put(playerCard4, null);
        playerHand.put(playerCard5, null);
        computerHand.put(computerCard1, null);
        computerHand.put(computerCard2, null);
        computerHand.put(computerCard3, null);
        computerHand.put(computerCard4, null);
        computerHand.put(computerCard5, null);
    }

    /**
     * Disables default buttons not for use during game. Includes computer hand buttons and
     * board card buttons.
     */
    public void setEnabledComputerButtons(boolean b) {
        for(ImageButton imageButton: computerHand.keySet()) {
            imageButton.setEnabled(b);
        }
    }

    /**
     * Re-enables the player's buttons after every round or reset.
     */
    public void setEnabledPlayerButtons(boolean b) {
        for(ImageButton imageButton: playerHand.keySet()) {
            imageButton.setEnabled(b);
        }
    }

    /**
     * Cards that have been discarded or eliminated via archer special are disabled.
     */
    public void disableEmptyButtons() {
        for(ImageButton imageButton: computerHand.keySet()) {
            if (computerHand.get(imageButton).getCardType().equals(Card.Type.OTHER)) {
                imageButton.setEnabled(false);
            }
        }

        for(ImageButton imageButton: playerHand.keySet()) {
            if (playerHand.get(imageButton).getCardType().equals(Card.Type.OTHER)) {
                imageButton.setEnabled(false);
            }
        }
    }

    /**
     * Re-sizes several bitmaps used in the game for board display and hand display.
     */
    public void resizeBitmaps() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int bSize = (int)(width/2.5);   //Board image size
        int cSize = (int)(width/8.0);   //Hand image size

        Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mystery);
        mysteryBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        boardEmptyBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        emptyBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.archer);
        boardArcherBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        archerBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.warrior);
        boardWarriorBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        warriorBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mage);
        boardMageBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        mageBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thief);
        boardThiefBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        thiefBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);

        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beast);
        boardBeastBitmap = Bitmap.createScaledBitmap(tempBitmap, bSize, bSize, false);
        beastBitmap = Bitmap.createScaledBitmap(tempBitmap, cSize, cSize, false);
    }

    /**
     * Resets the boards functionality, scores, texts, buttons, hands and decks.
     */
    public void resetAll() {
        //Reset number values
        roundNumber = 1;
        playerHonorNumber = 0;
        computerHonorNumber = 0;
        honorWaged = 1;
        specialUseCount = 0;
        //Reset texts to defaults
        round.setText("1");
        playerHonor.setText("0");
        computerHonor.setText("0");
        selectedCardText.setText("");
        info.setText(R.string.InfoDefault);
        info.setTextColor(Color.parseColor("#5C5C5C"));
        //Reset buttons
        selectedImageButton = null;
        computerSelectedCard = null;
        continueButton.setEnabled(true);
        specialButton.setEnabled(true);
        setEnabledPlayerButtons(true);
        setEnabledComputerButtons(false);
        specialButton.setEnabled(true);
        //Redraw buttonImages and refill decks
        playerDeck = cardFactory.createBalancedDeck();
        computerDeck = cardFactory.createBalancedDeck();
        previouslyPlayedCards.clear();
        fillHands();
        redrawBoard();
        redrawHands();
        //Reset Booleans
        mageHasRevealed = false;
        isTargetingPhase = false;
        isDiscardRequired = false;
    }

    /**
     * Checks the final status of the game after round 10. Displays the result to the screen
     * and disables buttons.
     */
    public void endMatch() {
        if (playerHonorNumber > computerHonorNumber) {
            info.setText(R.string.Result_Victory);
            info.setTextColor(Color.parseColor("#0C65F5"));
            playAudio("victory.mp3");
        } else if (computerHonorNumber > playerHonorNumber) {
            info.setText(R.string.Result_Defeat);
            info.setTextColor(Color.parseColor("#A30000"));
            playAudio("defeat.mp3");
        } else {
            info.setText(R.string.Result_Tie);
            info.setTextColor(Color.parseColor("#5C7378"));
            playAudio("stalemate.mp3");
        }

        for(ImageButton imageButton: playerHand.keySet()) {
            imageButton.setEnabled(false);
        }
        continueButton.setEnabled(false);
        specialButton.setEnabled(false);
    }

    /**
     * Replaces a card from either the player or the computer's hand with another card.
     * Used for refilling the computer and player's hand cards from their decks.
     * Also used for some card special effects.
     */
    public void replaceHandCard(HashMap<ImageButton, Card> hand, ImageButton imageButton, Card card) {
        hand.remove(imageButton);
        hand.put(imageButton, card);
    }

    /**
     * Detects the imageButton and redraws its corresponding card image.
     * @param imageButton is either a player or computer's card imageButton.
     * @param card is the imageButton's corresponding card.
     */
    public void drawHandCard(ImageButton imageButton, Card card) {
        switch(card.getCardType()) {
            case ARCHER:
                imageButton.setImageBitmap(archerBitmap);
                break;
            case MAGE:
                imageButton.setImageBitmap(mageBitmap);
                break;
            case WARRIOR:
                imageButton.setImageBitmap(warriorBitmap);
                break;
            case THIEF:
                imageButton.setImageBitmap(thiefBitmap);
                break;
            case BEAST:
                imageButton.setImageBitmap(beastBitmap);
                break;
            default:
                imageButton.setImageBitmap(emptyBitmap);
                break;
        }
    }

    /**
     * Detects the imageButton for a board card and draws its corresponding card image.
     * @param imageButton is either the player or computer's board card representation.
     * @param card is the corresponding card of imageButton.
     */
    public void drawBoardCard(ImageButton imageButton, Card card) {
        switch(card.getCardType()) {
            case ARCHER:
                imageButton.setImageBitmap(boardArcherBitmap);
                break;
            case MAGE:
                imageButton.setImageBitmap(boardMageBitmap);
                break;
            case WARRIOR:
                imageButton.setImageBitmap(boardWarriorBitmap);
                break;
            case THIEF:
                imageButton.setImageBitmap(boardThiefBitmap);
                break;
            case BEAST:
                imageButton.setImageBitmap(boardBeastBitmap);
                break;
            default:
                imageButton.setImageBitmap(boardEmptyBitmap);
                break;
        }
    }

    /**
     * Redraws the board's default image.
     */
    public void redrawBoard() {
        playerBoardCard.setImageBitmap(boardEmptyBitmap);
        computerBoardCard.setImageBitmap(boardEmptyBitmap);
    }

    /**
     * Redraws the computer and player's hand card images. Used every round or reset.
     */
    public void redrawHands() {
        for(ImageButton imageButton: playerHand.keySet()) {
            drawHandCard(imageButton, playerHand.get(imageButton));
        }

        for(ImageButton imageButton: computerHand.keySet()) {
            imageButton.setImageBitmap(mysteryBitmap);
        }
    }
}