package players;

import java.util.*;

import cards.*;
import game.*;

// SimBot needs to be a player to be able to play cards. When an action card is about to be played, it sets its player to the deck's player.

// in regular games, the table is created first, with the right number of cards for the given number of players
// then players are created with a reference to it, and they remove their initial decks from it.

// for simulation, we create a simbot with the same deck as the current player and then a table that's a copy of the current table
// the players at the sim table are bot copies of the other players at the real table
// 
public class SimBot extends Bot
{	
	private static int counter = 0;
	
	public SimBot(List<ICard> deck, int indexAtTable)
	{
		super("to be changed");
		GiveName(GenerateName());
		this.deck = new Deck(this, table, deck);
	}
	
	public SimBot(ICardTable table) {
		super(table);
		GiveName(GenerateName());
	}
	
	public SimBot(ICardTable table, List<ICard> deck, int indexAtTable)
	{
		super(table, indexAtTable);
		GiveName(GenerateName());
		this.deck = new Deck(this, table, deck);
	}
	
	public SimBot(ICardTable table, Map<String, Integer> groupedDeck)
	{
		super(table);
		GiveName(GenerateName());
		this.deck = new Deck(this, table, groupedDeck);
	}

	@Override
	protected String GenerateName()
	{
		counter++;
		return "SimBot " + counter;
	}
	
	public void UndoEffectsOfPlayedHand(List<ICard> originalDeck)
	{
		numActions = INITIAL_ACTIONS;
		numBuys = INITIAL_BUYS;
		money = INITIAL_MONEY;
		deck.PutAllCardsBackOnTable();
		originalDeck.forEach(c -> deck.TakeCardFromTable(c.GetName(), false));
		// restore other players' original decks
		deck.ConsolidateAllCardsIntoDeckShuffleAndDraw(Player.NUM_CARDS_HAND);
		
	}
	
	// copied from Bot
	public void PlayActions()
	{ 
 		List<IActionCard> actionsInHand = deck.GetAllActionCardsInHand();
		while (this.numActions > 0 && !actionsInHand.isEmpty())
		{	
			// sort by number of extra actions in descending order (not taking anything else into account right now)
			Collections.sort(actionsInHand, new Comparator<IActionCard>() {
				public int compare (IActionCard c1, IActionCard c2) {
					return c2.GetNumActions() - c1.GetNumActions();
			}});
			this.numActions--;
			deck.PlayActionCard(actionsInHand.get(0), false);
			actionsInHand = deck.GetAllActionCardsInHand();
		}
	}
}
