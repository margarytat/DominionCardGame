package commands;

import cards.*;
import game.*;
import ui.TextDisplay;

/**
 * @author margaryta
 * executing actions for action cards that can be fully expressed with the following attributes:
 * 	{int numCards, int numActions, int numBuys, int numMoney}
 * e.g. Laboratory
 */
public class GenericCommand extends Command {
	public GenericCommand(IActionCard card)
	{
		super(card);	
	}
	
	public GenericCommand(IActionCard card, boolean isConditional)
	{
		this(card);	
		this.isConditional = isConditional;
	}

	public boolean IsTrashCommand() {
		return false;
	}

	@Override
	public boolean IsReplaceCommand() {
		return false;
	}

	@Override
	public boolean Execute(boolean displayExecution) {
		if (card.GetNumCards() > 0)
			player.DrawCards(card.GetNumCards(), displayExecution);
		player.AddActions(card.GetNumActions());
		player.AddBuys(card.GetNumBuys());
		player.AddMoney(card.GetNumMoney());
		if (displayExecution && (card.GetNumActions() + card.GetNumBuys() + card.GetNumMoney() > 0))
			TextDisplay.GetInstance().DisplayPlayerNumActionsBuysMoney(player);
		return true;
	}
	
	public boolean ExecuteConditionally(boolean displayExecution, boolean conditionSatisfied)
	{
		if (conditionSatisfied) 
			 return Execute(displayExecution);
		
		return false;
	}

}
