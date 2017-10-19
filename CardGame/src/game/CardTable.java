/**
 * 
 */
package game;

import java.util.*;
import java.util.Map.Entry;

import cards.*;
import players.*;

/**
 * @author margaryta
 * holds the stacks of standard and chosen cards
 * trashed cards
 * players in the game
 */
public class CardTable implements ICardTable {
	// quantities in the initial game box
	public static final int NUM_COPPERS = 60;
	public static final int NUM_SILVERS = 40;
	public static final int NUM_GOLDS = 30;
	public static final int NUM_ESTATES = 24;
	public static final int NUM_DUCHIES = 12;
	public static final int NUM_PROVINCES = 12;
	public static final int NUM_CURSES = 30;
	
	public static final int NUM_START_DECK_COPPERS = 7;
	public static final int NUM_START_DECK_ESTATES = 3;
	public static final int NUM_CUSTOM_CARDS_IN_STACK = 10;
	
	private List<IObservablePlayer> players;
	private int numPlayers;
	private Map<String, List<ICard>> cardStacks;
	private List<String> chosenCards;
	private List<String> standardVictoryCards;
	private List<String> standardTreasureCards;
	private List<ICard> estatesNotOnTable;
	private List<ICard> trashedCards;
	private int numEmptyStacks;
	
	static private Map<Integer, List<Integer>> victoryCardQuantitiesForGame;
	static private Map<String, Integer> treasureCardQuantities;

	// copies other table's card stacks and trashed cards. Does not copy players.
	public CardTable(ICardTable other)
	{
		cardStacks = other.MakeCopyOfCardStacks();
		players = new ArrayList<IObservablePlayer>();
		chosenCards = other.MakeCopyOfChosenCards();
		trashedCards = other.MakeCopyOfTrashedCards();
	}
	
	public CardTable()
	{
		// numPlayers, {numVictoryCards, numCurses}
		victoryCardQuantitiesForGame = new HashMap<Integer, List<Integer>>();
		victoryCardQuantitiesForGame.put(1, Arrays.asList(8, 10));
		victoryCardQuantitiesForGame.put(2, Arrays.asList(8, 10));
		victoryCardQuantitiesForGame.put(3, Arrays.asList(12, 20));
		victoryCardQuantitiesForGame.put(4, Arrays.asList(12, 30));
		// treasure card quantities are the same regardless of number of players
		treasureCardQuantities = new HashMap<String, Integer>();
		treasureCardQuantities.put(CardFactory.GOLD, NUM_GOLDS);
		treasureCardQuantities.put(CardFactory.SILVER, NUM_SILVERS);
		treasureCardQuantities.put(CardFactory.COPPER, NUM_COPPERS);
		
		cardStacks = new LinkedHashMap<String, List<ICard>>();
		players = new ArrayList<IObservablePlayer>();
		chosenCards = new ArrayList<String>();
		standardVictoryCards = new ArrayList<String>();
		standardTreasureCards = new ArrayList<String>();
		trashedCards = new ArrayList<ICard>();
	};

	public CardTable(int numPlayers, List<String> chosenCards) 
	{
		this();
		this.numPlayers = numPlayers;
		this.chosenCards.addAll(chosenCards);
		standardVictoryCards.addAll(Arrays.asList(CardFactory.PROVINCE, CardFactory.DUCHY, CardFactory.ESTATE, CardFactory.CURSE));
		standardTreasureCards.addAll(Arrays.asList(CardFactory.GOLD, CardFactory.SILVER, CardFactory.COPPER));
		InitializeStandardCardStacks();
		InitializeChosenCardStacks();
		estatesNotOnTable = CardFactory.makeStack(CardFactory.ESTATE, NUM_ESTATES - cardStacks.get(CardFactory.ESTATE).size());
	}

	private void InitializeStandardCardStacks()
	{
		standardVictoryCards.forEach(c -> {
			cardStacks.put(c, CardFactory.makeStack(c, victoryCardQuantitiesForGame.get(numPlayers).get(0)));
		});
		standardTreasureCards.forEach(c -> {
			cardStacks.put(c, CardFactory.makeStack(c, treasureCardQuantities.get(c)));
		});
	}
	
	private void InitializeChosenCardStacks() {
		for (String card : chosenCards)
			cardStacks.put(CardFactory.GetProperName(card), CardFactory.makeStack(card, NUM_CUSTOM_CARDS_IN_STACK));
	}
	
	public List<ICard> RemoveInitialPlayerDeck()
	{
		// coppers come from the table, but estates don't
		List<ICard> initialDeck = new ArrayList<ICard>();
		for (int i = 0; i < NUM_START_DECK_COPPERS; i++)
			initialDeck.add(cardStacks.get(CardFactory.COPPER).remove(0));
		for (int i = 0; i < NUM_START_DECK_ESTATES; i++)
			initialDeck.add(estatesNotOnTable.remove(0));
		return initialDeck;
	}
	
	public ICard RemoveCardFromStack(String cardName) {
		return cardStacks.get(cardName).remove(0);
	}
	
	public void AddCardToStack(ICard card) {
		cardStacks.get(card.GetName()).add(card);
		
	}
	
	public void PutCardInTrash(ICard c)
	{
		trashedCards.add(c);
	}
	
	@Override
	public void RegisterPlayer(IObservablePlayer playerToAdd)
	{
		players.add(playerToAdd);
	}
	
	@Override
	public void RegisterPlayers(List<IObservablePlayer> playersToAdd) 
	{
		playersToAdd.forEach(p -> RegisterPlayer(p));
	}
	
	@Override
	public List<String> MakeCopyOfChosenCards() {
		List<String> names = new ArrayList<String>();
		chosenCards.forEach(c -> names.add(c));
		return names;
	}
	
	public Map<String, List<ICard>> GetCardStacks() {
		return cardStacks;
	}
	
	@Override
	public Map<String, List<ICard>> MakeCopyOfCardStacks()
	{
		Map<String, List<ICard>> stacks = new LinkedHashMap<String, List<ICard>>();
		Set<Entry<String, List<ICard>>> set = cardStacks.entrySet();
		set.forEach(e -> {
			stacks.put(e.getKey(), CardFactory.makeStack(e.getKey(), e.getValue().size()));
		});
		return stacks;
	}
	
	@Override
	public List<ICard> MakeCopyOfTrashedCards() {
		List<ICard> cards = new ArrayList<ICard>();
		trashedCards.forEach(c -> cards.add(CardFactory.makeCard(c)));
		return cards;
	}
	
	public int GetStackCardCount(String cardName)
	{
		return cardStacks.get(cardName).size();
	}
	
	@Override
	public List<ICard> MakeCopyOfAffordableNonEmptyCards(int maxCost) {
		List<ICard> cards = new ArrayList<ICard>();
		cardStacks.forEach((k, v) -> {
			if (!v.isEmpty() && v.get(0).GetCost()<= maxCost)
				cards.add(CardFactory.makeCard(v.get(0)));
		});
		
		return cards;
	}
	
	public List<ICard> GetCopyOfAffordableNonEmptyTreasures(int maxCost)
	{
		List<ICard> treasures = new ArrayList<ICard>();
		cardStacks.forEach((k, v) -> {
			if (!v.isEmpty() && v.get(0).IsTreasure() && v.get(0).GetCost()<= maxCost)
				treasures.add(CardFactory.makeCard(v.get(0)));
		});
		return treasures;
	}
	
	public List<String> GetNamesOfAffordableNonEmptyTreasures(int maxCost)
	{
		List<String> names = new ArrayList<String>();
		cardStacks.forEach((k, v) -> {
			if (!v.isEmpty() && v.get(0).IsTreasure() && v.get(0).GetCost()<= maxCost)
				names.add(k);
		});
		return names;
	}
	
	public List<IActionCard> GetCopyOfAffordableNonEmptyActionCards(int maxCost)
	{
		List<IActionCard> cards = new ArrayList<IActionCard>();		
		cardStacks.forEach((k, v) -> {
			if (!v.isEmpty() && v.get(0).IsAction() && v.get(0).GetCost() <= maxCost)
				cards.add((IActionCard)CardFactory.makeCard(v.get(0)));
		});
		return cards;
	}
	
	public List<String> GetNamesOfAffordableNonEmptyActionCards(int maxCost)
	{
		List<String> names = new ArrayList<String>();
		cardStacks.forEach((k, v) -> {
			if (!v.isEmpty() && v.get(0).IsAction() && v.get(0).GetCost()<= maxCost)
				names.add(k);
		});
		return names;
	}
	
	public List<ICard> MakeCopyOfAffordableNonEmptyActionCardsAndTreasures(int maxCost) {
		List<ICard> cards = GetCopyOfAffordableNonEmptyTreasures(maxCost);
		cards.addAll(GetCopyOfAffordableNonEmptyActionCards(maxCost));
		return cards;
	}
	
	public List<String> GetNamesOfAffordableNonEmptyActionCardsAndTreasures(int maxCost) {
		List<String> names = this.GetNamesOfAffordableNonEmptyTreasures(maxCost);
		names.addAll(this.GetNamesOfAffordableNonEmptyActionCards(maxCost));
		return names;
	}
	
	public int GetNumberOfEmptyStacks()
	{
		numEmptyStacks = 0;
		cardStacks.forEach((k, v) -> {
			numEmptyStacks += (v.isEmpty()? 1 : 0);
		});
		return numEmptyStacks;
	}
	
	public List<IObservablePlayer> GetPlayersAtTable()
	{
		return players;
	}
}

