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
public class HumanPlayer extends Player {
	
	public HumanPlayer(ICardTable table, String name, int indexAtTable) {
		super(table, name, indexAtTable);
	}
	
	@Override
	public void PlayTurnPart2()
	{
		if (deck.GetAllActionCardsInHand().isEmpty())
			TextDisplay.GetInstance().DisplayNoActionsInHand(this);
		else
			PlayActions();
		
		BuyCards();
		deck.Cleanup(NUM_CARDS_HAND);
	}

	@Override
	public void BuyCards() 
	{
		List<ICard> cards = table.MakeCopyOfAffordableNonEmptyCards(this.GetMoney());
		while (this.numBuys > 0)
		{
			// TODO: let player decide which treasure cards to play
			cards = table.MakeCopyOfAffordableNonEmptyCards(this.GetMoney());
			ICard c = TextDisplay.GetInstance().RequestCardToBuy(this, cards);
			if (c == null)
				return;
			
			this.numBuys--;
			deck.BuyCard(c.GetName());
		}
	}

	@Override
	public void PlayActions()
	{ 
		while (this.numActions > 0 && !deck.GetAllActionCardsInHand().isEmpty())
		{	
			List<IActionCard> actionCards = new ArrayList<IActionCard>();
			deck.GetAllActionCardsInHand().forEach(a -> actionCards.add((IActionCard)CardFactory.makeCard(a.GetName())));
			
			IActionCard card = TextDisplay.GetInstance().RequestActionCardToPlay(numActions, actionCards);
			if (card == null)
				return;

			this.numActions--;
			deck.PlayActionCard(card, true);
		}
	}
	
	@Override
	public void TrashFromOneToXCards(int maxNumCardsToTrash, boolean display) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void TrashUpToNumberOfCards(int maxNumCards, boolean display) {
		TextDisplay.GetInstance().DisplayHand(this);
		int numToTrash = TextDisplay.GetInstance().AskForUpToNumCardsToTrashFromHand(deck.GetHand(), maxNumCards);
		// TODO: check if we're trashing all cards, and
		boolean trashHand = false;
		if (numToTrash == deck.GetHand().size())
			trashHand = true;
		
		while (numToTrash > 0)
		{
			ICard c;
			if (trashHand)
			{
				Iterator<ICard> iter = deck.GetHand().iterator();
				c = iter.next();
			}
			else
				c = TextDisplay.GetInstance().AskToChooseOneOfCards(deck.GetHand());
			numToTrash--;
			deck.TrashCardFromHand(c);
			TextDisplay.GetInstance().DisplayTrashingCard(this, c);
		}
	}

	@Override
	public void DiscardAndThenDrawAnyNumCards(boolean display) {
		int numDiscarded = 0;
		TextDisplay.GetInstance().DisplayHand(this);
		int numToDiscard = TextDisplay.GetInstance().AskForNumCardsToDiscardFromHand(deck.GetHand());
		boolean discardHand = false;
		if (numToDiscard == deck.GetHand().size())
			discardHand = true;
		
		while (numDiscarded < numToDiscard)
		{
			ICard card;
			if (discardHand)
			{
				Iterator<ICard> iter = deck.GetHand().iterator();
				card = iter.next();
			}
			else
				card = TextDisplay.GetInstance().AskToChooseOneOfCards(deck.GetHand());
			
			numDiscarded++;
			deck.DiscardCardFromHand(card);
			if (display)
				TextDisplay.GetInstance().DisplayDiscardingCard(this, card);
		}
		deck.DrawCards(numDiscarded, display);
		TextDisplay.GetInstance().PrintLine();
	}

	@Override
	public boolean OptionallyTrashCardFromHand(String card, boolean display) {
		boolean shouldTrash = TextDisplay.GetInstance().AskIfPlayerWantsToTrashCardFromHand(card);
		if (shouldTrash == true)
		{
			ICard c = CardFactory.makeCard(card);	
			if (display == true)
				TextDisplay.GetInstance().DisplayTrashingCard(this, c);
			this.deck.TrashCardFromHand(c);
			return true;
		}
		return false;
	}
	
	public boolean BlockAttack() {
		return false;
	}

	@Override
	public boolean TryToBlockAttack(ICommand attackCommand) {
		List<IReactionCard> reactionCards = deck.GetReactionCardsInHand();
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

	@Override
	public boolean TryToBlockAttack(ICommand attackCommand, boolean displayExecution) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void DiscardFromHand(int i) {
		// TODO Auto-generated method stub
		
	}
}
