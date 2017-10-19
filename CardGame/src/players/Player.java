/**
 * 
 */
package players;

import java.util.*;

import cards.*;
import commands.ICommand;
import game.*;
import ui.TextDisplay;

/**
 * @author margaryta
 *
 */
abstract public class Player implements IPlayer{	
	protected String name;
	protected IDeck deck;
	protected ICardTable table;
	protected int turnNum;
	protected int indexAtTable;
	
	protected int numActions;
	protected int numBuys;
	protected int money;
	
	public static final int NUM_CARDS_HAND = 5;
	public static final int INITIAL_ACTIONS = 1;
	public static final int INITIAL_BUYS = 1;
	public static final int INITIAL_MONEY = 0;
	
	public Player() {
		turnNum = 0;
		numActions = INITIAL_ACTIONS;
		numBuys = INITIAL_BUYS;
		money = INITIAL_MONEY;
	}
	
	public Player(String name) {
		this();
		this.name = name;
	}
	
	public Player(String name, int indexAtTable) {
		this(name);
		this.indexAtTable = indexAtTable;
	}
	
	public Player(ICardTable table, String name, int indexAtTable) {
		this(name);
		this.table = table;
		this.indexAtTable = indexAtTable;
	}
	
	public void JoinTable(ICardTable table)
	{
		this.table = table;
		if (deck != null)
			deck.GiveTable(table);
	}
	
	public void TakeStartingDeckFromTableAndDrawFirstHand() {
		deck = new Deck(this, table, table.RemoveInitialPlayerDeck());
		deck.Cleanup(NUM_CARDS_HAND);
	}
	
	public void GiveName(String newName) {
		name = newName;
	}
	
	public String GetName() {
		return name;
	}
	
	public int GetIndexAtTable() {
		return indexAtTable;
	}

	public int GetNumVictoryPoints() {
		return deck.GetNumVictoryPoints();
	}
	
	public int GetNumActions() {
		return this.numActions;
	}
	
	public int GetNumBuys() {
		return this.numBuys;
	}
	
	public int GetMoneyAccumulatedThisTurn()
	{
		return this.money;
	}
	
	public int GetMoney()
	{
		return this.money + deck.GetSumOfTreasuresInHand();
	}
	
	public void PlayTurn()
	{
		turnNum++;
		numActions = INITIAL_ACTIONS;
		numBuys = INITIAL_BUYS;
		money = INITIAL_MONEY;
		TextDisplay.GetInstance().DisplayPlayerVictoryPoints(this);
		TextDisplay.GetInstance().DisplayHand(this);
		PlayTurnPart2();
	}

	public void AddActions(int num)
	{
		numActions += num;
	}
	
	public void AddMoney(int num)
	{
		money += num;
	}
	public void AddBuys(int num)
	{
		numBuys += num;
	}
	
	public void DrawCards(int numCards, boolean displayExecution) {
		deck.DrawCards(numCards, displayExecution);
		if (displayExecution)
			TextDisplay.GetInstance().PrintLine();
	}
	
	public void TakeCardFromTable(String cardName, boolean displayExecution) {
		deck.TakeCardFromTable(cardName, displayExecution);			
	}
	
	public boolean TryToBlockAttack(ICommand attackCommand) {
		List<IReactionCard> reactionCards = deck.GetReactionCardsInHand();
		if (reactionCards.isEmpty())
			return false;
		if (!reactionCards.isEmpty())
		{
			IReactionCard card = TextDisplay.GetInstance().AskForReactionCardToUse(reactionCards);
			if (card == null)
				return false;
			if (card.CanBlockAttack())
			{
				TextDisplay.GetInstance().DisplayUsingReactionCardToBlockAttack(this, card);
				return true;
			}
			// in the future: execute the reaction(s) of the chosen card
			return false;
		}
		return false;
	}
	
	public String GetAllCardsAsString() {
		return deck.GetAllCardsAsString();
	}
	
	public String GetHandAsString() {
		return deck.GetHandAsString();
	}
	
	public List<ICard> GetCopyOfDeckCards() {
		return deck.GetCopyOfDeckCards();
	}
	
	public List<IObservablePlayer> GetOtherPlayers() {
		List<IObservablePlayer> otherPlayers = new ArrayList<IObservablePlayer>();
		table.GetPlayersAtTable().forEach(c -> {
			if (c.GetName().equalsIgnoreCase(this.name) == false)
				otherPlayers.add(c);
		});
		return otherPlayers;
	}
	
	

}
