/**
 * 
 */
package commands;

import cards.*;

/**
 * @author margaryta
 * to be used by Action cards that allow/require trashing cards from hand
 * TODO: decide if I want to express trashing with complex parameters or subclass TrashCommand
 *
 */
public class TrashCommand extends Command {
	private int exactNumCardsToTrash;
	private int minNumCardsToTrash;
	private int maxNumCardsToTrash;
	private String cardToTrash;
	private Boolean trashingIsMandatory;

	
	public TrashCommand(IActionCard card, int numCards)
	{
		super(card);
		exactNumCardsToTrash = numCards;
		trashingIsMandatory = true;
	}
	
	public TrashCommand(IActionCard card, int minNumCardsToTrash, int maxNumCardsToTrash)
	{
		super(card);
		this.minNumCardsToTrash = minNumCardsToTrash;
		this.maxNumCardsToTrash = maxNumCardsToTrash;
	}
	
	public TrashCommand(IActionCard card, String cardName, boolean mustTrash)
	{
		super(card);
		this.cardToTrash = cardName;
		this.trashingIsMandatory = mustTrash;
	}

	@Override
	public boolean Execute(boolean displayExecution) {
		if (exactNumCardsToTrash > 0)
		{
			return true;
		}
		else if (maxNumCardsToTrash > 0)
		{
			if (minNumCardsToTrash == 0)
			{
				player.TrashUpToNumberOfCards(maxNumCardsToTrash, displayExecution);
				return true;
			}
			else
			{
				player.TrashFromOneToXCards(maxNumCardsToTrash, displayExecution);
				return true;
			}
		}
		else if (cardToTrash != null && !this.trashingIsMandatory)
		{ // moneylender - you may trash a copper
			return player.OptionallyTrashCardFromHand(cardToTrash, displayExecution);
		}
		return false;
	}


	@Override
	public boolean IsTrashCommand() {
		return true;
	}

}
