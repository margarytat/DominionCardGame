/**
 * 
 */
package ui;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cards.IActionCard;
import cards.ICard;
import cards.IReactionCard;
import commands.ICommand;
import game.CardFactory;
import players.IPlayer;

/**
 * @author margaryta
 * Console display
 */
public class TextDisplay {
	
	private static TextDisplay instance = null;
	protected TextDisplay(){}
	public static TextDisplay GetInstance() {
		if (instance == null) {
			instance = new TextDisplay();
		}
		return instance;
	}
	
	public void DisplayMessage(String message)
	{
		System.out.println(message);
	}
	
	public void PrintLine()
	{
		System.out.println();
	}
	
	public void DisplayCardStacks(Map<String, List<ICard>> cardStacks) 
	{
		System.out.println();
		cardStacks.forEach((k,v) -> System.out.println(k + ": " + v.size()));
		System.out.println();
	}

	public void DisplayGameStart() {
		System.out.println("Starting game.");
	}
	
	public void DisplayGameTurn(int turnNum) {
		System.out.println("--------- TURN " + turnNum + " ---------");
	}
	
	public void DisplayPlayerVictoryPoints(IPlayer player) {
		System.out.println(player.GetName() + " [" + player.GetNumVictoryPoints() + " V]");
	}

	public void DisplayGameEnd(List<IPlayer> players) {
		System.out.println("Game over.");
		players.forEach(p -> System.out.println(p.GetName() + ": " + p.GetNumVictoryPoints()));
		Collections.sort(players, new Comparator<IPlayer>() {
			public int compare (IPlayer p1, IPlayer p2) {
				return p2.GetNumVictoryPoints() - p1.GetNumVictoryPoints();
			}
		});
		System.out.println(players.get(0).GetName() + " won");
	}
	
	public void DisplayAllCards(IPlayer player)
	{
		System.out.println("All of " + player.GetName() + "'s cards:");
		System.out.println(player.GetAllCardsAsString());
	}
	
	public void DisplayHand(IPlayer player)
	{
		System.out.println("Hand: ");
		System.out.println(player.GetHandAsString());
		System.out.println();
	}
	
	public void DisplayPlayingActionCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " played " + AddArticleToName(card.GetName()));
		System.out.println();
	}
	
	public void DisplayUsingReactionCardToBlockAttack(IPlayer player, IReactionCard card) {
		System.out.println(player.GetName() + " revealed " + AddArticleToName(card.GetName()) + " to block attack");
		System.out.println();
	}
	
	public void DisplayPlayerNumActionsBuysMoney(IPlayer player) {
		System.out.println(player.GetName() + ": " + player.GetNumActions() + " A, " + player.GetNumBuys() + " B, " + player.GetMoneyAccumulatedThisTurn() + " M.");
	}
	
	public void DisplaySimulatedHandValuesPerCard(SortedMap<Double, String> handValues) {
		System.out.println("Simulation results: ");
		Iterator<Entry<Double, String>> it = handValues.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Double, String> e = it.next();
			System.out.println(e.getValue() + ": ahv - " + e.getKey());
		}
		System.out.println();
	}
	
	public void DisplayBuyingCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " bought " + AddArticleToName(card.GetName()));
		System.out.println();
	}
	
	public void DisplayTakingCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " took " + AddArticleToName(card.GetName()) + " from the table");
	}
	
	public void DisplayDrawingCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " drew " + AddArticleToName(card.GetName()));
	}
	
	public void DisplayDiscardingCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " discarded " + AddArticleToName(card.GetName()));
	}
	
	public void DisplayTrashingCard(IPlayer player, ICard card) {
		System.out.println(player.GetName() + " trashed " + AddArticleToName(card.GetName()));
	}
	
	public void DisplayDiscardingHandAndCardsInPlay(IPlayer player, int handSize, int cardsInPlaySize) {
		System.out.println(player.GetName() + " discarded hand (" + handSize + ") and cards in play (" + cardsInPlaySize + ")");
	}
	
	public void DisplayNoActionsInHand(IPlayer player)
	{
		System.out.println(player.GetName() + " has no actions in hand.");
		System.out.println();
	}
	
	public void DisplayNothingBought(IPlayer player) {
		System.out.println(player.GetName() + " bought nothing");
		System.out.println();
	}
	
	public String AddArticleToName(String cardName)
	{
		if (cardName.equalsIgnoreCase(CardFactory.ESTATE))
			 return "an " + cardName;
		else
			return "a " + cardName;
	}
	
	public IActionCard RequestActionCardToPlay(int numActions, List<IActionCard> actionCards) {
		List<ICard> cards = new ArrayList<ICard>();
		actionCards.forEach(c -> cards.add(c));
		System.out.println(numActions + "A: choose action from " + this.GetListOfDistinctCards(cards));
		return (IActionCard)GetMatchingCardFromList(cards);
	}
	
	public IReactionCard AskForReactionCardToUse(List<IReactionCard> reactionCards) {
		List<ICard> cards = new ArrayList<ICard>();
		reactionCards.forEach(c -> cards.add(c));
		System.out.println("You are under attack. Choose reaction card to use from " + this.GetListOfDistinctCards(cards));
		return (IReactionCard)GetMatchingCardFromList(cards);
	}
	
	public ICard RequestCardToBuy(IPlayer p, List<ICard> cards) {
		System.out.println(p.GetNumBuys() + " B, " + p.GetMoney() + " M : choose purchase from " + this.GetListOfDistinctCards(cards)); 
		return this.GetMatchingCardFromList(cards);
	}
	
	public boolean AskIfPlayerWantsToTrashCardFromHand(String card) {
		System.out.println("Would you like to trash " + this.AddArticleToName(card) + " from your hand? Y to trash.");
		return ProcessYesOrNoResponse() ;
	}
	
	public int AskForNumCardsToDiscardFromHand(List<ICard> hand) {
		System.out.println("Enter number of cards to discard from hand (0 - " + hand.size() + "):");
		return this.ProcessNumberFromZeroToMax(hand.size());
	}
	
	public int AskForUpToNumCardsToTrashFromHand(List<ICard> hand, int maxNumCards) {
		System.out.println("Enter number of cards to trash from hand (0 - " + maxNumCards + "):");
		return this.ProcessNumberFromZeroToMax(maxNumCards);
	}

	
	public int ProcessNumberFromZeroToMax(int max)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try 
		{
			int num = Integer.parseInt(br.readLine());
			if (num > 0 && num <= max )
				return num;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public ICard AskToChooseOneOfCards(List<ICard> cards)
	{
		System.out.println("Choose one of " + this.GetListOfDistinctCards(cards) + " or enter nothing to choose nothing");
		return this.GetMatchingCardFromList(cards);
	}

	public String GetListOfDistinctCards(List<ICard> cards)
	{
		Set<ICard> distinctCards = new HashSet<ICard>();
		cards.forEach(c -> distinctCards.add(c));
		return this.FormatCollectionOfCards(distinctCards);
	}
	
	public String FormatCollectionOfCards(Collection<ICard> cards)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		cards.forEach(c -> sb.append(c.GetName() + ", "));
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		return sb.toString();
	}
	
	public Map<String, Integer> GetCardsByNameFromInput(String input)
	{
		List<String> deckCards = Arrays.asList(input.split(","));
		Map<String, Integer> cardsByName = new HashMap<String, Integer>();
		Pattern nameAndQuantity = Pattern.compile("(?<name>[a-zA-Z]+)\\s*(?<number>\\d+)");
		deckCards.forEach(c -> {
			Matcher matcher = nameAndQuantity.matcher(c);
			if (matcher.matches())
			{
				matcher.find();
				String name = matcher.group("name");
				int number = Integer.parseInt(matcher.group("number"));
				cardsByName.put(name, number);
			}
			else
				cardsByName.put(c.trim(), 1);
		});
		return cardsByName;
	}
	
	// the required input is the minimum number of initial characters needed to uniquely identify the card in the given list by name
	private ICard GetMatchingCardFromList(List<ICard> cards) {
		Set<ICard> distinctCards = new HashSet<ICard>();
		cards.forEach(c -> distinctCards.add(CardFactory.makeCard(c)));
		boolean gotValidResponse = false;
		while (!gotValidResponse)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				String input = br.readLine();
				if (input.isEmpty())
					return null;
				
				boolean mismatchDetected = false;
				for (int i = 1; i <= input.length() && mismatchDetected == false; i++)
				{
					String sub = input.substring(0,  i).toLowerCase();
					distinctCards.removeIf(c -> !(c.GetName().toLowerCase().startsWith(sub)));
					
					if (distinctCards.size() == 1)
					{
						Iterator<ICard> iter = distinctCards.iterator();
						ICard card = iter.next();
						return card;
					}
					else if (distinctCards.isEmpty())
					{
						mismatchDetected = true;
						System.out.println("The provided input doesn't match a card in the list. Try again.");
					}
				}
				if (distinctCards.size() > 1)
				{
					List<ICard> candidates = new ArrayList<ICard>();
					distinctCards.forEach(c -> candidates.add(CardFactory.makeCard(c)));
					System.out.println("The provided input is ambiguous and matches one of " + GetListOfDistinctCards(candidates) + ". Try again.");
				}
				cards.forEach(c -> distinctCards.add(CardFactory.makeCard(c)));
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	// Y is interpreted as Yes, anything else -- as No
	private boolean ProcessYesOrNoResponse() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String input = br.readLine();
			if (input.equalsIgnoreCase("y"))
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

