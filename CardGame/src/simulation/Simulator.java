package simulation;

import java.util.*;

import cards.*;
import game.CardTable;
import game.ICardTable;
import players.*;

public class Simulator implements ISimulator {
	
	private ICardTable table;
	private int numTimes;
	private SimBot bot;
	
	public static final int DEFAULT_NUM_SIMS = 5000;
	
	public Simulator()
	{
		this.table = new CardTable(2, null);
	}
	
	// used by the bot to copy current game and run simulations for choosing cards to play and/or buy
	public Simulator(int mainBotIndexAtTable, List<ICard> mainBotDeck, ICardTable table, List<IObservablePlayer> otherPlayers, int currentTurn) {
		numTimes = DEFAULT_NUM_SIMS;
		this.table = table;
		
		bot = new SimBot(table, mainBotDeck, mainBotIndexAtTable);
		List<IObservablePlayer> players = new ArrayList<IObservablePlayer>();
		players.add(bot);
		otherPlayers.forEach(p -> {
			p.JoinTable(this.table);
			players.add(p);
		});
		// sort in ascending order by index at table, so relative order is preserved
		// TODO should also keep track of who is supposed to go next
		Collections.sort(players, new Comparator<IObservablePlayer>() {
			public int compare (IObservablePlayer p1, IObservablePlayer p2) {
				return p1.GetIndexAtTable() - p2.GetIndexAtTable();
			}
		});
		table.RegisterPlayers(players);
	}
	
	// used by the console simulator
	public Simulator(int numPlayers, List<String> chosenCards,  int numSimulations)
	{
		numTimes = numSimulations;
		table = new CardTable(numPlayers, chosenCards);
		bot = new SimBot(table);
		table.RegisterPlayer(bot);
	}
	
	public void InitializeStandardDecks()
	{
		table.GetPlayersAtTable().forEach(p -> p.TakeStartingDeckFromTableAndDrawFirstHand());
	}
	
	public void CreateAndRegisterBot(Map<String, Integer> deck)
	{
		SimBot nextBot = new SimBot(table);
		deck.forEach((k,v) -> {
			for (int i = 0; i < v; i++) 
			{
				nextBot.TakeCardFromTable(k, false);
			}
		});
		table.RegisterPlayer(nextBot);
	}
	
	public void AddCardsToMainBotDeck(Map<String, Integer> mainBotDeck) {
		mainBotDeck.forEach((k,v) -> {
			for (int i = 0; i < v; i++) 
			{
				AddCardToDeck(k);
			}
		});
	}

	public void AddCardToDeck(String cardName)
	{
		bot.TakeCardFromTable(cardName, false);
	}
	
	// TODO: write simulations for multiple turns and record more data for each turn:
	// average hand value, average treasure card value, average number of unplayable cards
	// For the bot to decide how to play action cards, the simulations will need to record the decisions made clearly so that they could easily be reproduced
	
	// Will I need undoing beyond simulation purposes? Then I suppose it's a list of GameEvent objects that record players' actions. 
	// The IGameEvent will have do and undo methods that will manipulate the card table, players, and decks
	// The action card commands will implement IGameEvent. A BuyCommand will be added to encapsulate current buying code.
	

	@Override
	public double RunSimulationForAverageHandValue() {
		Map<String, List<ICard>> botDecks = new HashMap<String, List<ICard>>();
		table.GetPlayersAtTable().forEach(p -> {
			botDecks.put(p.GetName(), p.GetCopyOfDeckCards());
		});

		double sum = 0;
		for (int i = 0; i < numTimes; i++)
		{
			bot.UndoEffectsOfPlayedHand(botDecks.get(bot.GetName()));
			bot.GetOtherPlayers().forEach(p -> {
				((SimBot)p).UndoEffectsOfPlayedHand(botDecks.get(p.GetName()));
			});
			bot.PlayActions();
			sum += bot.GetMoney();
		}
		return sum/numTimes;
	}
}
