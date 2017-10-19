/**
 * 
 */
package game;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import cards.IActionCard;
import cards.ICard;
import cards.IReactionCard;
import commands.ICommand;
import players.IPlayer;
import ui.TextDisplay;

/**
 * @author margaryta
 *
 *	thoughts on decoupling UI from implementation:
 *	Doing a console imitation of an MVC architecture would result in too much output.
 *	MVC framework: Player, Deck, Card Table, etc. are models
 *	They would have corresponding views that would be updated as they changed
 *	With console output, the equivalent of updating the views is printing out the state out. We want the opposite - 
 *		"Player 1 bought a Gold" instead of printing out the player's deck and the card stacks on the table.
 *	Console output will be done with a singleton that will do all the printing. Not ideal, but easy to remove.
 */
public class Deck implements IDeck {
	private IPlayer player;
	protected ICardTable table;
	
	protected Queue<ICard> deck;
	protected List<ICard> discardPile;
	protected List<ICard> hand;
	protected List<ICard> cardsInPlay;
	// private List<Card> cardsInEffectOverMultipleTurns;
	
	public Deck()
	{
		deck = new LinkedList<ICard>();
		discardPile = new ArrayList<ICard>();
		hand = new ArrayList<ICard>();
		cardsInPlay = new ArrayList<ICard>();
	}
	
	public Deck(IPlayer player)
	{
		this();
		this.player = player;
	}
	
	public Deck(IPlayer player, List<ICard> startingDeck) {
		this(player);
		ShuffleAndAddCardsToDeck(startingDeck);
	}
	
	public Deck(IPlayer player, ICardTable table)
	{
		this();
		this.table = table;
		this.player = player;
	}
	
	public Deck(IPlayer player, ICardTable table, List<ICard> startingDeck) 
	{
		this(player, table);
		ShuffleAndAddCardsToDeck(startingDeck);
	}
	
	public Deck(IPlayer player, ICardTable table, Map<String, Integer> startingDeck) 
	{
		this(player, table);
		List<ICard> allCards = new ArrayList<ICard>();
		startingDeck.forEach((k, v) -> {
			allCards.addAll(CardFactory.makeStack(k, v));
		});
		ShuffleAndAddCardsToDeck(allCards);
	}
	
	public void GiveTable(ICardTable table) {
		this.table = table;
	}
	
	private void ShuffleAndAddCardsToDeck (List<ICard> cards) {
		Collections.shuffle(cards);
		deck.addAll(cards);
	}
	
	public void DrawCards(int numCards, boolean display) {
		if (deck.size() >= numCards)
		{
			for (int i = 0; i < numCards; i++)
			{
				ICard c = deck.remove();
				hand.add(c);
				if (display)
					TextDisplay.GetInstance().DisplayDrawingCard(player, c);
			}
		}
		else
		{
			int remainder = numCards - deck.size();
			DrawCards(deck.size(), display);
			ShuffleDiscardPileAndMoveIntoDeck();
			for (int i = numCards - remainder; i < numCards && !deck.isEmpty(); i++)
			{	
				ICard c = deck.remove();
				hand.add(c);
				if (display)
					TextDisplay.GetInstance().DisplayDrawingCard(player, c);
			}
		}
	}
	
	public void Cleanup(int numCardsInHand) 
	{
		DiscardHandAndCardsInPlay();
		DrawCards(numCardsInHand, false);
	}
	
	public void DiscardHandAndCardsInPlay()
	{
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			discardPile.add(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = cardsInPlay.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			discardPile.add(card);
			iter.remove();
		}
	}
	
	public void DiscardCardFromHand(ICard cardToDiscard)
	{
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			if (card.GetName().equalsIgnoreCase(cardToDiscard.GetName())) 
			{
				discardPile.add(card);
				iter.remove();
				return;
			}
		}
	}
	
	public void TrashCardFromHand(ICard cardToTrash) {
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			if (card.GetName().equalsIgnoreCase(cardToTrash.GetName())) 
			{
				table.PutCardInTrash(card);
				iter.remove();
				return;
			}
		}
	}
	
	public void ShuffleDiscardPileAndMoveIntoDeck()
	{
		List<ICard> tempPile = new ArrayList<ICard>();
		for (Iterator<ICard> iter = discardPile.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			tempPile.add(card);
			iter.remove();
		}
		Collections.shuffle(tempPile);
		deck.addAll(tempPile);
	}
	
	public void PutAllCardsBackOnTable() {
		for (Iterator<ICard> iter = discardPile.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			table.AddCardToStack(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = deck.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			table.AddCardToStack(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = hand.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			table.AddCardToStack(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = cardsInPlay.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			table.AddCardToStack(card);
			iter.remove();
		}
		
	}
	
	public void ConsolidateAllCardsIntoDeckShuffleAndDraw(int numCards) {
		List<ICard> tempPile = new ArrayList<ICard>();
		for (Iterator<ICard> iter = discardPile.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			tempPile.add(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = deck.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			tempPile.add(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = hand.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			tempPile.add(card);
			iter.remove();
		}
		for (Iterator<ICard> iter = cardsInPlay.iterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			tempPile.add(card);
			iter.remove();
		}
		Collections.shuffle(tempPile);
		deck.addAll(tempPile);
		
		DrawCards(numCards, false);
	}
	
	public void PlayActionCard(IActionCard actionCard, boolean displayPlayingCard) {
		List<ICommand> commands = actionCard.GetCommands();
		commands.forEach(c -> c.SetPlayer(this.player));
		
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard c = iter.next();
			if (c.GetName().equalsIgnoreCase(actionCard.GetName()))
			{	
				this.cardsInPlay.add(c);
				iter.remove();
				if (displayPlayingCard == true)
					TextDisplay.GetInstance().DisplayPlayingActionCard(player, c);
				actionCard.ExecuteActionCommands(displayPlayingCard);
				return;
			}
		}
	}
	
	protected void PlayAllTreasuresInHand()
	{
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard card = iter.next();
			if (card.IsTreasure())
			{
				this.cardsInPlay.add(card);
				player.AddMoney(card.GetMoney());
				iter.remove();
			}
		}		
	}
	
	public void PlayTreasures(List<ICard> treasuresToPlay)
	{		
		for (Iterator<ICard> iter = hand.listIterator(); iter.hasNext();)
		{
			ICard c = iter.next();
			if (treasuresToPlay.contains(c))
			{
				cardsInPlay.add(c);
				iter.remove();
				treasuresToPlay.remove(c);
			}
		}
		if (!treasuresToPlay.isEmpty())
		{
			throw new IllegalArgumentException("Deck asked to play treasures not in hand: " + TextDisplay.GetInstance().FormatCollectionOfCards(treasuresToPlay));
		}
	}
	
	public void BuyCard(String name, List<String> treasuresToPlay)
	{
		
	}
	
	// looks like at this point we assume we can afford the card. Do we also assume it exists in the card stacks?
	public void BuyCard(String name)
	{
		int moneyPoints = player.GetMoneyAccumulatedThisTurn();
		int cost = CardFactory.GetCost(name);
		if (cost > moneyPoints)
			PlayAllTreasuresInHand();

		player.AddMoney(-cost);
		ICard c = table.RemoveCardFromStack(name);
		discardPile.add(c);
		TextDisplay.GetInstance().DisplayBuyingCard(player, c);
	}
	
	public void TakeCardFromTable(String name, boolean display)
	{
		if (table.GetStackCardCount(name) > 0)
		{
			ICard c = table.RemoveCardFromStack(name);
			discardPile.add(c);
			if (display)
				TextDisplay.GetInstance().DisplayTakingCard(player, c);
		}
	}
	
	public String GetAllCardsAsString() {
		return GetCardsAsGroupedString(this.GetAllCards());
	}
	
	public String GetDeckAsString() {
		return this.GetCardsAsGroupedString(deck);
	}

	// cards in descending order: action cards by num extra actions, then treasures by money, then victory cards by victory points
	public String GetHandAsString() {
		Collections.sort(this.hand, new Comparator<ICard>() {
			public int compare (ICard c1, ICard c2) {
				if (c1.IsAction() && !c2.IsAction())
					return -1;
				else if (!c1.IsAction() && c2.IsAction())
					return 1;
				else if (c1.IsTreasure() && c2.IsUnplayable())
					return -1;
				else if (c1.IsUnplayable() && c2.IsTreasure())
					return 1;
				else if (c1.IsAction() && c2.IsAction())
					return Integer.compare(((IActionCard)c2).GetNumActions(), ((IActionCard)c2).GetNumActions());
				else if (c1.IsTreasure() && c2.IsTreasure())
					return Integer.compare(c2.GetMoney(), c1.GetMoney());
				else if (c1.IsVictory() && c2.IsVictory())
					return Integer.compare(c2.GetVictoryPoints(), c1.GetVictoryPoints());
				else
					return c1.GetName().compareTo(c2.GetName());
			}
		});
		return this.GetCardsAsGroupedString(hand);
	}
	
	public Map<String, Integer> GetCardsByName(Collection<ICard> cards) {
		Map<String, Integer> cardsByName = new HashMap<String, Integer>();
		for (ICard card : cards)
		{
			if (cardsByName.containsKey(card.GetName()))
			{
				cardsByName.put(card.GetName(), cardsByName.get(card.GetName()).intValue() + 1);
			}
			else
			{
				cardsByName.put(card.GetName(), 1);
			}
		}
		return cardsByName;
	}
	
	public Map<String, Integer> GetCardsByName() {
		return GetCardsByName(this.GetAllCards());
	}
	
	// groups passed-in cards by name
	private String GetCardsAsGroupedString(Collection<ICard> cards)
	{
		StringBuilder sb = new StringBuilder();
		this.GetCardsByName(cards).forEach((k,v) -> sb.append(k + ": " + v + "\n"));
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public List<ICard> GetAllCards()
	{
		List<ICard> allCards = new ArrayList<ICard>(this.deck);
		allCards.addAll(discardPile);
		allCards.addAll(hand);
		allCards.addAll(cardsInPlay);
		return allCards;
	}
	
	public List<ICard> GetCopyOfDeckCards()
	{
		List<ICard> allCards = new ArrayList<ICard>();
		deck.forEach(c -> allCards.add(CardFactory.makeCard(c)));
		discardPile.forEach(c -> allCards.add(CardFactory.makeCard(c)));
		hand.forEach(c -> allCards.add(CardFactory.makeCard(c)));
		cardsInPlay.forEach(c -> allCards.add(CardFactory.makeCard(c)));
		return allCards;
	}
	
	private List<ICard> GetAllTreasuresInDeck()
	{
		List<ICard> treasures = new ArrayList<ICard>();
		GetAllCards().forEach(c -> {
			if (c.IsTreasure())
					treasures.add(c);
		});
		return treasures;
	}
	
	private List<IActionCard> GetAllActionCardsInCollection(List<ICard> cards)
	{
		List<IActionCard> actionCards = new ArrayList<IActionCard>();
		for (ICard c : cards)
		{
			if (c.IsAction())
			{
				actionCards.add((IActionCard) c);
			}
		}
		return actionCards;
	}
	
	private List<IActionCard> GetAllActionCards()
	{
		return GetAllActionCardsInCollection(GetAllCards());
	}
	
	public List<ICard> GetHand() {
		return new ArrayList<ICard>(hand);
	}
	
	public List<IActionCard> GetAllActionCardsInHand() {
		return GetAllActionCardsInCollection(hand);
	}
	
	public List<IReactionCard> GetReactionCardsInHand() {
		List<IReactionCard> reactionCards = new ArrayList<IReactionCard>();
		for (ICard c : hand)
		{
			if (c.isReaction())
			{
				reactionCards.add((IReactionCard)c);
			}
		}
		return reactionCards;
	}
	
	public boolean CanBuyProvinceWithThisHand()
	{
		return CanBuyCardWithThisHand(CardFactory.PROVINCE);
	}
	
	public boolean CanBuyCardWithThisHand(String cardName)
	{
		int allMoney = player.GetMoneyAccumulatedThisTurn() + GetSumOfTreasuresInHand();
		return (allMoney >= CardFactory.GetCost(cardName));
	}
	
	// 3G, 2G + 1S, 4S, 3S + 2C
	// TODO: if it ever gets used again, take action cards into account
	public boolean CanBuyProvinceWithOneHand(int numCardsInHand)
	{
		return CanBuyCardWithTreasuresOnlyInOneHand(CardFactory.GetCost(CardFactory.PROVINCE), numCardsInHand);
	}
	
	// takes only treasures into account.
	// TODO: (1) calculate probability of drawing an action card that gives extra money and being able to play it 
	// (2) calculate probability of drawing an action card that gives extra cards, being able to play it, 
	//		and drawing treasure(s) with it
	public boolean CanBuyCardWithTreasuresOnlyInOneHand(int cardCost, int numCardsInHand)
	{
		List<ICard> allTreasures = this.GetAllTreasuresInDeck();
		// sort by money in descending order
		Collections.sort(allTreasures, new Comparator<ICard>() {
			public int compare (ICard c1, ICard c2) {
				return c2.GetMoney() - c1.GetMoney();
		}});
		
		int i = 0;
		int purchasingPower = 0;
		while (purchasingPower < cardCost && i < allTreasures.size())
		{
			purchasingPower = purchasingPower + allTreasures.get(i).GetMoney();
			i++;
		}
		
		if (purchasingPower < cardCost) // can't buy the card with all the treasures in the deck
			return false;

		return (i < numCardsInHand);
	}
	
	public int GetNumVictoryCards()
	{
		AtomicInteger num = new AtomicInteger();
		GetAllCards().forEach(c -> {
			if (c.IsVictory())
				num.incrementAndGet();
		});
		return num.get();
	}
	
	public int GetNumUnplayableVictoryCards()
	{
		AtomicInteger num = new AtomicInteger();
		GetAllCards().forEach(c -> {
			if (c.IsUnplayableVictory())
				num.incrementAndGet();
		});
		return num.get();
	}

	public int GetNumCards() {
		return GetAllCards().size();
	}
	
	public int GetNumCards(String cardName) {
		AtomicInteger num = new AtomicInteger();
		GetAllCards().forEach(c -> {
			if (c.GetName().equalsIgnoreCase(cardName))
				num.incrementAndGet();
		});
		return num.get();
	}

	public int GetNumVictoryPoints() {
		int numPoints = 0;
		for (ICard c : GetAllCards())
			numPoints += c.GetVictoryPoints();
		return numPoints;
	}
	
	public int GetSumOfTreasuresInHand()
	{
		int sumOfTreasures = 0;
		for (ICard c : hand)
		{
			sumOfTreasures += c.GetMoney();
		}
		return sumOfTreasures;
	}
	
	public int GetSumOfAllTreasures() {
		int sum = 0;
		for (ICard c : GetAllTreasuresInDeck())
		{
			sum += c.GetMoney();
		}
		return sum;
	}

	public double GetAverageTreasureValue() {
		int count = GetAllTreasuresInDeck().size();
		int sum = GetSumOfAllTreasures();

		return sum/count;
	}

	public double GetAverageVictoryCardValue() {
		int sum = 0;
		int count = 0;
		for (ICard c : GetAllCards())
		{
			if (c.IsVictory())
			{
				count ++;
				sum += c.GetVictoryPoints();
			}
		}
		return sum/count;
	}
	
	public double GetAverageUnplayableVictoryCardValue() {
		int sum = 0;
		int count = 0;
		for (ICard c : GetAllCards())
		{
			if (c.IsUnplayableVictory())
			{
				count ++;
				sum += c.GetVictoryPoints();
			}
		}
		return sum/count;
	}

	public int GetNumActionCards() {
		return GetAllActionCards().size();
	}

	public int GetNumActionCardsThatTrashCards() {
		int num = 0;
		for (IActionCard c : GetAllActionCards())
		{
			if (c.TrashesCards())
				num++;
		}
		return num;
	}
	
	public int GetNumDeckImproverCards() {
		int num = 0;
		for (IActionCard c : GetAllActionCards())
		{
			if (c.TrashesCards() || c.ReplacesCards())
				num++;
		}
		return num;
	}

	public int GetNumTerminalActionCards() {
		int num = 0;
		for (IActionCard c : GetAllActionCards())
		{
			if (c.GetNumActions() < 1)
				num++;
		}
		return num;
	}
	


	public int GetNumActionCardActions() {
		int numExtraActions = 0;
		List<IActionCard> actionCards = GetAllActionCards();
		for (IActionCard c : actionCards)
			numExtraActions += c.GetNumActions();
		return numExtraActions;
	}

	public int GetNumActionCardCards() {
		int numExtraCards = 0;
		List<IActionCard> actionCards = GetAllActionCards();
		for (IActionCard c : actionCards)
				numExtraCards += c.GetNumCards();
		return numExtraCards;
	}

	// TODO: expansions have treasure cards with extra buys. ICard will be updated to include extra buys and this method will become GetNumBuysInDeck()
	public int GetNumActionCardBuys() {
		int numExtraBuys = 0;
		List<IActionCard> actionCards = GetAllActionCards();
		for (IActionCard c : actionCards)
			numExtraBuys += c.GetNumBuys();
		return numExtraBuys;
	}

	public boolean CanBuyCardWithOneHand(String cardName, int numCardsInHand) {
		// TODO - implement!
		return false;
	}
	
	
//	// takes only treasures into account.
//	// TODO: (1) calculate probability of drawing an action card that gives extra money and being able to play it 
//	// (2) calculate probability of drawing an action card that gives extra cards, being able to play it, 
//	//		and drawing treasure(s) with it
//	public boolean CanBuyCardWithOneHand(int cardCost, int numCardsInHand)
//	{

//	}
}
