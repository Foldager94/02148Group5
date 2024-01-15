package dk.dtu.game.round;

import dk.dtu.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dk.dtu.game.Card;
import dk.dtu.game.commands.enums.GamePhaseType;
import static dk.dtu.game.GameSettings.*;

public class RoundState {
    private int smallBlindPrice = START_SMALL_BLIND;
    private int bigBlindPrice = START_BIG_BLIND;
    private int roundId;
    private String peerId;
    private String smallBlind; // id
    private String bigBlind; // id
    private String dealer; // id
    private int pot;
    private List<Player> players;
    private List<Card> communityCards;
    private List<Integer> bets;
    private String lastRaise = null;
    private String firstPlayer = null;
    private GamePhaseType gamePhaseType = null;
    boolean isMyTurn = false;

    public RoundState(int roundId, String peerId, List<Player> players, String smallBlind, String bigBlind, String dealer, String firstPlayer){
        this.roundId = roundId;
        this.peerId = peerId;
        this.players = players;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.dealer = dealer;    
        this.bets = new ArrayList<>();    
        this.pot = 0;
        System.out.println("First player is initially " + firstPlayer);
        this.firstPlayer = firstPlayer;
        communityCards = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            bets.add(0);
        }
    }

    
    public List<Player> getPlayers(){
        return players;
    }

    public void addToPot(int amount){
        pot += amount;
    }

    public int getPot(){
        return pot;
    }

    public List<Integer> getBets(){
        return bets;
    }

    public int addBet(int bet){
        return bet;
    }
    public List<Integer> addBetToListOfBets(int bet) {
       int bet2bAdded = addBet(bet);
       bets.add(bet2bAdded);
       return bets;
    }

    public String getFirstPlayerId(){
        return firstPlayer;
    }

    public int findPlayerIndexById(String targetId) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getId().equals(targetId)) {
                return i; // Return ID of player
            }
        }
        return -1; // Return -1, if no player was found
    }

    public void setGamePhaseType(GamePhaseType phaseType){
        gamePhaseType = phaseType;
    }

    public GamePhaseType getGamePhaseType(){
        return gamePhaseType;
    }

    public int getroundId() {
        return roundId;
    }

    public String getSmallBlind() {
        return smallBlind;
    }
    
    public String getBigBlind() {
        return bigBlind;
    }

    public String getDealer() {
        return dealer;
    }

    public String getLastRaise(){
        return lastRaise;
    }

    public void setLastRaise(String lastRaise){
        this.lastRaise =lastRaise;
    }

    public List<Card> getCommunityCards(){
        return communityCards;
    }
    
    public Player getOwnPlayerObject() {
        return getPlayer(peerId);
    }
    public Player getPlayer(String id) {
        for (Player player : players) {
        if (player.getId().equals(id)) {
            return player; 
        }
            }
            return null;
    }
    // Calculates small blind and big blind
    public void calculateBlinds() {
        if (roundId % BLIND_LENGTH == 0) {
            smallBlindPrice *= 2;
            bigBlindPrice *= 2;
        }
    }
    public String nextPlayer(String playerId){
        int smallBlindint= Integer.parseInt(playerId);
        int nextIndex = smallBlindint + 1;
        if(nextIndex < players.size()){
            return players.get(nextIndex).id;
        }
        return players.get(0).id;
    }

    public void calculateBlindsBet() {
        calculateBlinds();
        Player SB = getPlayer(smallBlind);
        Player BB = getPlayer(bigBlind);
        bets.set(Integer.parseInt(SB.id),smallBlindPrice);
        bets.set(Integer.parseInt(BB.id),bigBlindPrice);
        pot = smallBlindPrice + bigBlindPrice;
        SB.removeFromBalance(smallBlindPrice);
        BB.removeFromBalance(bigBlindPrice);
    }

    public void setNewFirstPlayer(String previousFirstId){ // called if the previos first player folds
        String pId = previousFirstId;
        while(true) {
            pId = nextPlayer(pId);
            if (getPlayer(pId).getInRound()) { // if the player has not folded
                firstPlayer = pId;
                return;
            }
        }
    }

    public void addCardsToCommunityCards(List<Card> cards) {
        communityCards.addAll(cards);
    }

    
    public void setPlayerFolded(String peerId) {
        Player player = getPlayer(peerId);
        player.fold();
        if(peerId.equals(firstPlayer)){
            setNewFirstPlayer(peerId);
        }
    }
    
    public void calcPlayerRaise(String peerId, int amount){
        Player p = getPlayer(peerId);

        int topBet = Collections.max(bets);
        int myBet = bets.get(findPlayerIndexById(peerId));
        int amountNeededToCall = topBet - myBet;


        p.removeFromBalance(amountNeededToCall+amount);
        addToPlayerBet(peerId, amountNeededToCall+amount);
        lastRaise = peerId;
        pot += amount+amountNeededToCall;
        System.out.println(pot);
        
    }

    public boolean getIsMyTurn(){
        return isMyTurn;
    }
    public void setIsMyTurn(boolean val){
        isMyTurn = val;
    }

    public boolean hasPlayerFolded(String peerId){
        Player p = getPlayer(peerId);
        return !p.getInRound();
    }

    public void calcPlayerCall(String peerId){
        Player p = getPlayer(peerId);


        int topBet = Collections.max(bets);
        int myBet = bets.get(findPlayerIndexById(peerId));
        int amountNeededToCall = topBet - myBet;

        
        System.out.println(topBet+ " " + myBet + " " + amountNeededToCall);
        if(amountNeededToCall > p.getBalance()){
            amountNeededToCall = p.getBalance();
        }
        p.removeFromBalance(amountNeededToCall);
        addToPlayerBet(peerId, amountNeededToCall);
        pot += amountNeededToCall;
    }

    public String getNextNonFoldedPlayer(String peerId) {
        String nextId = peerId;
        while (true) {
            nextId = nextPlayer(nextId);
            if (getPlayer(nextId).getInRound()) {
                return nextId;
            }
        }

    }
    
    public void addToPlayerBet(String peerId, int amount){
        int pIndex = findPlayerIndexById(peerId);
        System.out.println("YOU HAVE NOW BETTER: " + amount + bets.get(pIndex));
        bets.set(pIndex, amount + bets.get(pIndex));
    }

    @Override
    public String toString() {
        return "Round ID: " + roundId + "\nPeer ID: " + peerId + "\nSmall Blind: " + smallBlind
                + "\nBig Blind: " + bigBlind + "\nDealer: " + dealer + "\nPlayersCount: " + players.size()
                + "\nCommunity Cards: " + ((communityCards.isEmpty() ? "None" : communityCards) +  "\nPot: "+ String.valueOf(pot));
    }
  
}