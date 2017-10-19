/**
 * 
 */
package game;

import java.util.*;

import cards.*;
import commands.*;
import players.*;

/**
 * @author margarytaMacbook
 * creates cards
 */

public class CardFactory {
	public static final String COPPER = "Copper";
	public static final String SILVER = "Silver";
	public static final String GOLD = "Gold";
	public static final String ESTATE = "Estate";
	public static final String DUCHY = "Duchy";
	public static final String PROVINCE = "Province";
	public static final String CURSE = "Curse";
	
	public static final String CELLAR = "Cellar";
	public static final String CHAPEL = "Chapel";
	public static final String MOAT = "Moat";
	public static final String CHANCELLOR = "Chancellor";
	public static final String VILLAGE = "Village";
	public static final String WOODCUTTER = "Woodcutter";
	public static final String WORKSHOP = "Workshop";
	public static final String BUREAUCRAT = "Bureaucrat";
	public static final String FEAST = "Feast";
	public static final String GARDENS = "Gardens";
	public static final String MILITIA = "Militia";
	public static final String MONEYLENDER = "Moneylender";
	public static final String REMODEL = "Remodel";
	public static final String SMITHY = "Smithy";
	public static final String SPY = "Spy";
	public static final String THIEF = "Thief";
	public static final String THRONE_ROOM = "Throne Room";
	public static final String COUNCIL_ROOM = "Council Room";
	public static final String FESTIVAL = "Festival";
	public static final String LABORATORY = "Laboratory";
	public static final String LIBRARY = "Library";
	public static final String MARKET = "Market";
	public static final String MINE = "Mine";
	public static final String WITCH = "Witch";
	public static final String ADVENTURER = "Adventurer";
	
	public static ICard makeCard(ICard card)
	{
		return makeCard(card.GetName());
	}
	
	public static ICard makeCard(String name)
	{
	// Standard treasures and victory cards
		// Card(name, cost, money, victoryPoints)
		if (name.equalsIgnoreCase(COPPER))
			return new Card(COPPER, 0, 1, 0);
		else if (name.equalsIgnoreCase(SILVER))
			return new Card(SILVER, 3, 2, 0);
		else if (name.equalsIgnoreCase(GOLD))
			return new Card(GOLD, 6, 3, 0);
		else if (name.equalsIgnoreCase(ESTATE))
			return new Card(ESTATE, 2, 0, 1);
		else if (name.equalsIgnoreCase(DUCHY))
			return new Card(DUCHY, 5, 0, 3);
		else if (name.equalsIgnoreCase(PROVINCE))
			return new Card(PROVINCE, 8, 0, 6);
		else if (name.equalsIgnoreCase(CURSE))
			return new Card(CURSE, 0, 0, -1);
	// Base deck
		// ActionCard (name, cost, money, victoryPoints, numCards, numActions, numBuys, numMoney) 
		else if (name.equalsIgnoreCase(CELLAR))
		{	
			IActionCard card = new ActionCard(CELLAR,2,0,0,0,1,0,0);
			card.AddActionCommand(new GenericCommand(card));
			card.AddActionCommand(new DiscardAndDrawCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(CHAPEL))
		{
			IActionCard card = new ActionCard(CHAPEL,2,0,0,0,0,0,0);
			card.AddActionCommand(new TrashCommand(card, 0,4));
			return card;
		}
		else if (name.equalsIgnoreCase(MOAT))
		{	
			IReactionCard card = new ReactionCard(MOAT, 2,0,0,2,0,0,0, true);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(VILLAGE))
		{
			IActionCard card = new ActionCard(VILLAGE,3,0,0,1,2,0,0);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(WOODCUTTER))
		{
			IActionCard card = new ActionCard(WOODCUTTER,3,0,0,0,0,1,2);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(MILITIA))
		{
			IActionCard card = new ActionCard(MILITIA,4,0,0,0,0,0,2);
			card.AddActionCommand(new GenericCommand(card, false));
			IAttackCommand makeDiscard = new AttackCommand(card);
			makeDiscard.SetEvilNumberCommand((IObservablePlayer p, Boolean d) -> {
				p.DiscardFromHand(2);
			card.AddActionCommand(makeDiscard);
			});
		}
		else if (name.equalsIgnoreCase(MONEYLENDER))
		{
			IActionCard card = new ActionCard(MONEYLENDER,4,0,0,0,0,0,3);
			ICommand trashCopper = new TrashCommand(card, COPPER, false);
			ICommand getReward = new GenericCommand(card, true);
			card.AddActionCommand(trashCopper);
			card.AddActionCommand(getReward);
			return card;
		}
		else if (name.equalsIgnoreCase(SMITHY))
		{
			IActionCard card = new ActionCard(SMITHY,4,0,0,4,0,0,0);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(LABORATORY))
		{
			IActionCard card = new ActionCard(LABORATORY,5,0,0,2,1,0,0);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(MARKET))
		{
			IActionCard card = new ActionCard(MARKET,5,0,0,1,1,1,1);
			card.AddActionCommand(new GenericCommand(card));
			return card;
			
		}
		else if (name.equalsIgnoreCase(FESTIVAL))
		{
			IActionCard card = new ActionCard(FESTIVAL,5,0,0,0,2,1,2);
			card.AddActionCommand(new GenericCommand(card));
			return card;
		}
		else if (name.equalsIgnoreCase(WITCH))
		{
			IActionCard card = new ActionCard(WITCH,5,0,0,2,0,0,0);
			card.AddActionCommand(new GenericCommand(card, false));
			
			IAttackCommand giveCurses = new AttackCommand(card);
			giveCurses.SetEvilCardCommand((IObservablePlayer p,  Boolean d) -> {
				p.TakeCardFromTable(CURSE, d);
			});
			card.AddActionCommand(giveCurses);
			return card;
		}

		return null;
	}
	
	public static List<ICard> makeStack(String name, int number)
	{
		List<ICard> cards = new ArrayList<ICard>();
		for (int i = 0; i < number; i++)
		{
			cards.add(CardFactory.makeCard(name));
		}
		return cards;
	}
	
	public static boolean IsTreasure(String cardName)
	{
		ICard card = makeCard(cardName);
		return card.IsTreasure();
	}
	
	public static int GetCost(String cardName)
	{
		ICard card = makeCard(cardName);
		return card.GetCost();
	}
	
	public static int GetVictoryPoints(String cardName) 
	{
		ICard card = makeCard(cardName);
		return card.GetVictoryPoints();
	}
	
	public static int GetMoney(String cardName)
	{
		ICard card = makeCard(cardName);
		return card.GetMoney();
	}

	public static String GetProperName(String cardName) {
		ICard card = makeCard(cardName);
		return card.GetName();
	}
}
