/**
 * 
 */
package players;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;

import cards.*;
import commands.*;
import game.*;
import simulation.*;
import ui.TextDisplay;



/**
 * @author margarytaMacbook
 *
 */
public class Bot extends Player {
	public static final String DECK_AS_IS = "DECK_AS_IS";

// more thoughts on bot strategy
	// 
	// META:
	// Bots will keep track of aspects of the deck's composition 
	// 		unplayable cards as portion of total
	// 		treasure cards (same)
	// 		action cards (same)
	// All bots will want to maximize average victory point value of card (provinces over estates) 
	// and the average money value of card
	//
	// I don't yet know how much common strategy there will be when it comes to the action cards. 
	// Probably something about keeping the ratio of them high.
	// Also some ratio of terminal action cards (those that don't have any extra actions) to other cards
	// Bots focusing on different styles of play of those action cards will focus on 
	//		average # of actions per action card
	//		# of cards
	//		money
	//		buys
	// Evil bots will prefer attack cards over other types.
	// Villager (action engine) bots will want cards generating extra actions and extra cards (and keep them balanced)
	// Deck groomer bots will trash and remodel
	// What will money bots do w.r.t. actions? 
	// Could have a smart bot that varies strategy depending on the deck
	//
	// CARD STRATEGY
	// are there cards that only make sense together?
	// fools' golds, treasure maps, saunas and avantos
	// 
	// There could be a strategy class with a priority level:  urgent for above examples (maybe not for fools' golds), 
	// high for other things, medium for the rest. A player could have several of those. 
	// Victory card acquisition could start off low-priority and rise in importance as the game progresses
	// 
	
	private double maxNonPlayablePortionOfDeck;
	private int maxNumTerminalActionCardsInDeck;
	private static int counter = 0;
	
	public Bot() {
		super("to be changed");
		this.GiveName(GenerateName());
		SetDeckHeuristics();
	}
	
	public Bot(String name) {
		super(name);
		SetDeckHeuristics();
	}
	
	public Bot(ICardTable table) {
		super("to be changed");
		this.JoinTable(table);
		this.GiveName(GenerateName());
		SetDeckHeuristics();
	}
	
	public Bot(ICardTable table, int indexAtTable) {
		super("to be changed", indexAtTable);
		this.JoinTable(table);
		this.GiveName(GenerateName());
		SetDeckHeuristics();
	}
	
	public Bot(ICardTable table, String name, int indexAtTable) {
		super(table, name, indexAtTable);
		SetDeckHeuristics();
	}
	
	private void SetDeckHeuristics()
	{
		// first try at this: limit it to half a deck. TODO: observe how this works when Witch is implemented
		maxNonPlayablePortionOfDeck = 0.5;	
		// TODO: more sophisticated way to address that. Should sum up all the actions of the action cards in deck 
				// and calculate probability of getting them in hand given current deck (consider number of unplayable victory cards,
				// treasures, laboratory  + [village-library/witch] card-drawing card engines)
		maxNumTerminalActionCardsInDeck = 1;
	}
	
	protected String GenerateName()
	{
		counter++;
		return "Bot " + counter;
	}
	
	@Override
	public void PlayTurnPart2()
	{
		// TODO:
		// Search the space of possible outcomes by trying all combinations of possible moves in hand, going from short to long. Just buys, then terminal action cards 
		// and those without any extra cards. For some, outcomes will be completely known in advance 
		// (skipping actions and buying cards only, playing actions not involving drawing any cards [note that effects of attack cards depend on presence 
		// of reaction cards in the deck]. Winning by giving an opponent the last curse on this turn when you hold a Witch is guaranteed with no reaction cards in 
		// his/her/its deck. 
		
		// If there isn't a win in the above search space, include action cards that give cards and outcome probabilities.

		List<IActionCard> actionsInHand = deck.GetAllActionCardsInHand();
		if (actionsInHand.isEmpty())
			TextDisplay.GetInstance().DisplayNoActionsInHand(this);
		else
			PlayActions();
		BuyCards();
		deck.Cleanup(NUM_CARDS_HAND);
	}
	
	public void PlayActions()
	{ 
		List<IActionCard> actionsInHand = deck.GetAllActionCardsInHand();
		while (this.numActions > 0 && !actionsInHand.isEmpty())
		{	
			// TODO: just had a situation when a bot had 2 extra actions and had to choose between smithy and woodcutter 
			// the bot played the woodcutter first - bad idea. it picked up a village with the smithy.
			// Playing the cards in the right order should be taken care of with simulation, but cases like these 
			// should be pruned. Don't waste time playing woodcutter and then smithy.
			
			// sort by number of extra actions in descending order (not taking anything else into account right now)
			Collections.sort(actionsInHand, new Comparator<IActionCard>() {
				public int compare (IActionCard c1, IActionCard c2) {
					return c2.GetNumActions() - c1.GetNumActions();
			}});
			this.numActions--;
			deck.PlayActionCard(actionsInHand.get(0), true);
			actionsInHand = deck.GetAllActionCardsInHand();
			if (numActions == 0 && actionsInHand.isEmpty()) // print line after last action played
				TextDisplay.GetInstance().PrintLine();
		}
	}

	@Override
	public void BuyCards() 
	{
		// looping through buys doesn't let us consider the combination of cards we're buying when we have multiple buys - I believe I address this in comments in PlayTurn, 
		// at least when it comes to finishing the game by buying multiple cards
		
		// TODO: have the bot decide which treasure to play
		
		while (this.numBuys > 0)
		{
			if (deck.CanBuyCardWithThisHand(CardFactory.PROVINCE))	
			{
				int myScore = deck.GetNumVictoryPoints();
				int numPlayersFarAhead = 0;
				
				for (IObservablePlayer p : this.GetOtherPlayers())
				{
					// TODO: After gardens is implemented, construct a deck = yours + last province, and ask that deck for its victory points to get the delta
					if (myScore + CardFactory.GetVictoryPoints(CardFactory.PROVINCE) <= p.GetNumVictoryPoints())
						numPlayersFarAhead++;
				}
				
				if (table.GetStackCardCount(CardFactory.PROVINCE) == 1)
				{  
					if (numPlayersFarAhead == 0)
						deck.BuyCard(CardFactory.PROVINCE);
					else
					{ 
						int numDuchies = table.GetStackCardCount(CardFactory.DUCHY);
						int numEstates = table.GetStackCardCount(CardFactory.ESTATE);
						
						if (numDuchies > 1 || numDuchies == 1 && table.GetNumberOfEmptyStacks() < 2)
							deck.BuyCard(CardFactory.DUCHY);
						else if (numEstates > 1 || numEstates == 1 && table.GetNumberOfEmptyStacks() < 2)
							deck.BuyCard(CardFactory.ESTATE);
						else
							BuyActionOrTreasure();
					}
				}
				// If we're behind by more than a province, buying one now makes us vulnerable to loss on the next turn (unless we're buying both of them on this one - see comment at top of method)
//				else if (table.GetStackCardCount(CardFactory.PROVINCE) == 2 && numPlayersFarAhead > 0)
//					BuyActionOrTreasure();
				else
				{
					if (deck.GetNumUnplayableVictoryCards()/deck.GetNumCards() < maxNonPlayablePortionOfDeck) 
						deck.BuyCard(CardFactory.PROVINCE);
					else
						BuyActionOrTreasure();
				}
			}
			else 
				BuyActionOrTreasure();

			numBuys--;
		}
	}
	
	public List<IObservablePlayer> MakeBotCopiesOfOtherPlayers()
	{
		List<IObservablePlayer> copies = new ArrayList<IObservablePlayer>();
		this.GetOtherPlayers().forEach(p -> copies.add(new SimBot(p.GetCopyOfDeckCards(), p.GetIndexAtTable())));
		return copies;
	}
	
	protected SortedMap<String, SimResults> GetSimResultsForCurrentDeckAndWithEachCardAdded(List<String> cardNames)
	{
		return null;
	}
	
	protected SortedMap<Double, String> GetAverageHandValueOfCurrentDeckAndWithEachCardAddedDescending(List<String> cardNames)
	{
		SortedMap<Double,String> results = new TreeMap<Double,String>(new Comparator<Double>() {
			public int compare (Double c1, Double c2) {
				return Double.compare(c2, c1);
		}});
		
		cardNames.add(DECK_AS_IS);
		for (String c : cardNames)
		{
			ICardTable copyTable = new CardTable(table);
			List<IObservablePlayer> copyPlayers = MakeBotCopiesOfOtherPlayers();
			Simulator sim = new Simulator(indexAtTable, deck.GetCopyOfDeckCards(), copyTable, copyPlayers, turnNum);
			if (!c.equalsIgnoreCase(DECK_AS_IS))
				sim.AddCardToDeck(c);
			double value = sim.RunSimulationForAverageHandValue();
			results.put(value, c);
		}
		return results;
	}
	
	protected void BuyActionOrTreasure()
	{
		int money = this.money + deck.GetSumOfTreasuresInHand();
		List<String> cardNames = table.GetNamesOfAffordableNonEmptyActionCardsAndTreasures(money);
		
		if (cardNames.isEmpty())
		{
			TextDisplay.GetInstance().DisplayNothingBought(this);
			return;
		}
		else 
		{
			
			// TODO: we will want to know more than just average hand value. There will be changes to things like average treasure value, average victory card value, 
			// ratios of cards of various types in deck, depending on which card we buy on this turn. There might be changes to those numbers during simulations as well and we should record both
			//
			// to it. We want to report those we know average number of buys per hand
			// the benefit of treasure and action cards that are also victory cards should be reflected
			SortedMap<Double, String> handValues = GetAverageHandValueOfCurrentDeckAndWithEachCardAddedDescending(cardNames);
			
			TextDisplay.GetInstance().DisplaySimulatedHandValuesPerCard(handValues);
			
			for (Iterator<Entry<Double,String>> iter = handValues.entrySet().iterator(); iter.hasNext();)
			{
				Entry<Double,String> handValuePerCard = iter.next();
				if (handValuePerCard.getValue().equals(DECK_AS_IS))
				{
					TextDisplay.GetInstance().DisplayNothingBought(this);
					return;
				}
				ICard card = CardFactory.makeCard(handValuePerCard.getValue());
				if (card.IsTreasure() && (card.GetMoney() > deck.GetAverageTreasureValue()))
				{
					if (card.GetMoney() > deck.GetAverageTreasureValue())
					{
						deck.BuyCard(card.GetName());
						return;
					}
					else
					{
						TextDisplay.GetInstance().DisplayMessage("Attempted to purchase " + card.GetName() + " aborted. Average hand value was " + handValuePerCard.getKey());
						TextDisplay.GetInstance().DisplayNothingBought(this);
						return;
					}
				}
				else if (card.IsAction()) // when would we not buy it?
				{
					deck.BuyCard(card.GetName());
					return;
				}
			}
		}
	}

//	09/21/2017 uh oh, just saw this:
//		Bot 2 hand: 
//			Silver: 2
//			Estate: 1
//			Copper: 2
//			Bot 2 has no actions in hand.
//			Bot 2 bought a Silver
		
//		
//		// TODO: if we have curses, we should prioritize buying an improver or trashing card if we don't have one (or enough) already
//		
//		if (deck.GetNumActionCardActions() >= deck.GetNumActionCardCards())
//		{
//			Collections.sort(affordableCards, (a,b) -> {
//				return CompareCardsByCostAndActionCardComparison(a, b, (c,d) -> CompareActionCardsByExtraCardsActionsMoneyBuysSpecialCommands(c,d));
//			});
//
//		}
//		else
//		{
//			Collections.sort(affordableCards, (a,b) -> {
//				return CompareCardsByCostAndActionCardComparison(a, b, (c,d) -> CompareActionCardsByExtraActionsCardsMoneyBuysSpecialCommands(c,d));
//			});
//		}

		
	// assumes the list of cards passed in is not empty and only affordable available cards are passed in
	protected void BuyBestTreasure(List<ICard> affordableTreasures) {	
		// sort list by treasure value
		Collections.sort(affordableTreasures, new Comparator<ICard>() {
			public int compare(ICard c1, ICard c2) {
				return c2.GetMoney() - c1.GetMoney();
			}
		});
		deck.BuyCard(affordableTreasures.get(0).GetName());
	}
	
	@Override
	// currently only used by cellar
	// TODO: 
	public void DiscardAndThenDrawAnyNumCards(boolean displayExecution) {
		int numDiscarded = 0;
		List<ICard> hand = this.deck.GetHand();
		for (ICard c : hand)
		{
			if (c.IsUnplayableVictory()) // unless we're planning to remodel or trash?
			{
				if (displayExecution == true)
					TextDisplay.GetInstance().DisplayDiscardingCard(this, c);
				this.deck.DiscardCardFromHand(c);
				numDiscarded++;
			}
		}
		
		for (ICard c: hand)
		{
			if (c.IsTreasure())
			{
				// TODO: calculate expected money value of next card and if it's higher than c's money value, discard c
				// what if the next card is more likely to be an unplayable victory?
				// it matters whether we have extra actions when we're playing cellar
			}
		}
		
		// if we have no actions left to play action cards with, we discard them all. Should we ever not do that?
		if (this.numActions == 0)
		{
			for (ICard c : hand)
			{
				if (c.IsAction())
				{
					if (displayExecution == true)
						TextDisplay.GetInstance().DisplayDiscardingCard(this, c);
					this.deck.DiscardCardFromHand(c);
					numDiscarded++;
				}
			}
		}
		deck.DrawCards(numDiscarded, displayExecution);
		if (displayExecution)
			TextDisplay.GetInstance().PrintLine();
		
	}
	
// ****************
// 08/19/2017
// in my current online game, the bot is stuck in a loop of Peddler, Peddler, Junk Dealer to buy a silver and trash it when it can.
// there have been other cases of bots continuously playing cards that trash cards to destroy their own deck
// a simple idea that comes to mind is that you're only allowed to start trashing coppers once you can buy a province with your deck
// then keep buying best treasure until you can buy a province again
// it's generally a bad idea to trash expensive cards (unless you're replacing them)
// ****************

	@Override
	public void TrashUpToNumberOfCards(int maxNumCards, boolean displayExecution) 
	{
		// should trash low value cards
		// * coppers, as long as trashing them raises the average value of a treasure in our deck. Lower bound: must keep enough treasures in the deck to be able to buy at least a silver
		// * estates, except under certain conditions (it's the end of a close game; we're planning to remodel them, but then the goal is not to find ourselves in this situation - one where we have both a chapel and 
		// 		a remodel)
		// * what else is low value? chapels when our deck only has good cards? moneylenders when all the coppers are gone
		
		// how to decide between chapel and remodel? chapel and moneylender? which deck-improving strategies to pursue when there are multiple available? need to keep an eye on the ratio 
		// of deck-improving cards, esp. since in the base deck all are terminal
		
		int numTrashed = 0;
		List<ICard> hand = deck.GetHand();
		// sort hand by victory points, ascending
		Collections.sort(hand, new Comparator<ICard>() {
			public int compare(ICard c1, ICard c2) {
				return c1.GetVictoryPoints() - c2.GetVictoryPoints();
			}});
		for (ICard c: hand)
		{
			if (numTrashed < maxNumCards)
			{
				if (c.IsUnplayableVictory()) 
				{
					double averageVictoryPoints = deck.GetAverageVictoryCardValue();
					// (#provinces > 2) is a quick hack for "it's not the end of a close game". TODO: fix
					boolean notTheEndOfCloseGame = true;
					if (table != null && table.GetStackCardCount(CardFactory.PROVINCE) <= 2)
						notTheEndOfCloseGame = false;
					if (c.GetVictoryPoints() < averageVictoryPoints && notTheEndOfCloseGame 
							|| c.GetVictoryPoints() == averageVictoryPoints && averageVictoryPoints == CardFactory.GetVictoryPoints(CardFactory.ESTATE)) 
					{
						if (displayExecution == true)
							TextDisplay.GetInstance().DisplayTrashingCard(this, c);
						this.deck.TrashCardFromHand(c);
						numTrashed++;
					}
				}
			}
		}
		// sort hand by treasure value, ascending
		Collections.sort(hand, new Comparator<ICard>() {
			public int compare(ICard c1, ICard c2) {
				return c1.GetMoney() - c2.GetMoney();
			}});
		for (ICard c: hand) 
		{
			if (numTrashed < maxNumCards)
			{
				if (c.IsTreasure())
				{
					double averageValue = deck.GetAverageTreasureValue();
					if ((c.GetMoney() < averageValue  || c.GetMoney() == averageValue && averageValue == CardFactory.GetMoney(CardFactory.COPPER))
							&& deck.CanBuyCardWithTreasuresOnlyInOneHand(CardFactory.GetCost(CardFactory.SILVER), NUM_CARDS_HAND))	// TODO: take into account action cards
					{
						if (displayExecution == true)
							TextDisplay.GetInstance().DisplayTrashingCard(this, c);
						this.deck.TrashCardFromHand(c);
					}
				}
			}
		}
		// TODO: trash useless action cards
		// e.g. moneylenders when the coppers are gone - unless we're planning to remodel them, sigh
	}

	@Override
	public void TrashFromOneToXCards(int maxNumCardsToTrash, boolean displayExecution) {
		// TODO Auto-generated method stub
		
	}
	
	// Sort in descending order by # actions, cards, money, buys. Break ties by preferring special commands over generic commands.
	private int CompareActionCardsByExtraActionsCardsMoneyBuysSpecialCommands (IActionCard c1, IActionCard c2) {
		int i = Integer.compare(c2.GetNumActions(), c1.GetNumActions());
		if (i == 0)
			i = Integer.compare(c2.GetNumCards(), c1.GetNumCards());
		if (i == 0)
			i = Integer.compare(c2.GetNumMoney(), c1.GetNumMoney());
		if (i == 0)
			i = Integer.compare(c2.GetNumBuys(), c1.GetNumBuys());
		if (i == 0)
			i = CompareActionCardsByFirstCommand(c1, c2);
		return i;
	}
	// Sort in descending order by # cards, actions, money, buys.Break ties by preferring special commands over generic commands.
	private Integer CompareActionCardsByExtraCardsActionsMoneyBuysSpecialCommands (IActionCard c1, IActionCard c2) {
		int i = Integer.compare(c2.GetNumCards(), c1.GetNumCards());
		if (i == 0)
			i = Integer.compare(c2.GetNumActions(), c1.GetNumActions());
		if (i == 0)
			i = Integer.compare(c2.GetNumMoney(), c1.GetNumMoney());
		if (i == 0)
			i = Integer.compare(c2.GetNumBuys(), c1.GetNumBuys());
		if (i == 0)
			i = CompareActionCardsByFirstCommand(c1, c2);
		return i;
	}
	
	// Sort in descending order by # buys, actions, cards, money. Break ties by preferring special commands over generic commands.
	private Integer CompareActionCardsByExtraBuysActionsCardsMoneySpecialCommands (IActionCard c1, IActionCard c2) {
		int i = Integer.compare(c2.GetNumBuys(), c1.GetNumBuys());
		if (i == 0)
			i = Integer.compare(c2.GetNumActions(), c1.GetNumActions());
		if (i == 0)
			i = Integer.compare(c2.GetNumCards(), c1.GetNumCards());
		if (i == 0)
			i = Integer.compare(c2.GetNumMoney(), c1.GetNumMoney());
		if (i == 0)
			i = CompareActionCardsByFirstCommand(c1, c2);
		return i;
	}
	
	private Integer CompareActionCardsByFirstCommand(IActionCard c1, IActionCard c2) {
		if ((c1.GetCommands().get(0) instanceof GenericCommand) && !(c2.GetCommands().get(0) instanceof GenericCommand))
		{
			// prefer c2
			return -1;
		}
		else if (!(c1.GetCommands().get(0) instanceof GenericCommand) && (c2.GetCommands().get(0) instanceof GenericCommand))
		{
			// prefer c1
			return 1;
		}
		return 0;
	}
	
	private int CompareCardsByCostAndActionCardComparison(ICard c1, ICard c2, BiFunction<IActionCard, IActionCard, Integer> comp) {
		Integer costComparison = Integer.compare(c2.GetCost(), c1.GetCost());
		// in the future, if two non-action cards cost the same, but one or both give you extra buys or another ability (like Royal Seal), need to compare those
		if (costComparison != 0 || !c1.IsAction() && !c2.IsAction())
			return costComparison;
		// prefer action cards to non-action cards of the same cost
		else if (c1.IsAction() && !c2.IsAction())
			return -1;
		else if (!c1.IsAction() && c2.IsAction())
			return 1;
		// compare action cards with the passed-in function
		else return comp.apply((IActionCard)c1, (IActionCard)c2);
	}
	
	// if there is a beneficial action card among those we can afford, buy it
	// pre-condition: only affordable cards that are available are passed in
	// not using now and probably will not use in the future
	private boolean BuyBestAction(List<IActionCard> affordableActions) {
		// should we remove all completely undesirable cards first? 
		// TODO: come up with a better way to figure out how many terminal cards to have. If we have lots of cards with extra actions 
		// and lots of cards that draw cards, it might be ok to have multiple terminal action cards. We should look at ratios instead of absolute numbers
		int numExtraActionsInDeck = deck.GetNumActionCardActions();
		int numExtraCardsInDeck = deck.GetNumActionCardCards();
		int numTerminalCards = deck.GetNumTerminalActionCards();
		if (numTerminalCards > 0)
		{
			affordableActions.removeIf(c -> c.GetNumActions() == 0);
		}
		int numImproverCards = deck.GetNumDeckImproverCards();
		if (numImproverCards > 0)
		{
			affordableActions.removeIf(c -> c.TrashesCards() || c.ReplacesCards());
		}
		
		if (affordableActions.size() == 1)
		{
			deck.BuyCard(affordableActions.get(1).GetName());
			return true;
		}
		else if (affordableActions.size() > 1) 
		{
			// sort in some way
		}
		
		// thoughts for the near future: when we have multiple buys, how to decide between buying one great card 
		// and buying two good ones? If we're playing the gardens strategy, we probably want two good ones.
		return false;
	}
	
	// TODO: take into account the money Moneylender provides when deciding whether to trash a copper
	// we might not be able to buy a gold with all the treasures in the deck, but could buy it if we trash a copper in the hand and get +$3

	@Override
	public boolean OptionallyTrashCardFromHand(String cardToTrash, boolean displayExecution) {
		boolean trashed = false;
		// moneylender 
		ICard c = CardFactory.makeCard(cardToTrash);			
		if (c.IsTreasure())
		{
			double averageValue = deck.GetAverageTreasureValue();
			if ((c.GetMoney() < averageValue  || c.GetMoney() == averageValue && averageValue == CardFactory.GetMoney(CardFactory.COPPER))
					&& deck.CanBuyCardWithTreasuresOnlyInOneHand(CardFactory.GetCost(CardFactory.SILVER), NUM_CARDS_HAND)) // TODO: take into account action cards 
			{
				if (displayExecution == true)
					TextDisplay.GetInstance().DisplayTrashingCard(this, c);
				this.deck.TrashCardFromHand(c);
				trashed = true;
			}
		}
		return trashed;
	}

	@Override
	public boolean TryToBlockAttack(ICommand attackCommand, boolean displayExecution) {
		for (IReactionCard c : deck.GetReactionCardsInHand())
		{
			if (c.CanBlockAttack())
			{
				if (displayExecution)
					TextDisplay.GetInstance().DisplayUsingReactionCardToBlockAttack(this, c);
				return true;
			}
		}
		// in the future, the bot will see if one of the non-moat reaction cards in hand should be played and play it
		return false;
	}

	@Override
	public void DiscardFromHand(int i) {
		// TODO Auto-generated method stub
		
	}
}
