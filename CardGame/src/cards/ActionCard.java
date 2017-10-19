/**
 * 
 */
package cards;

import commands.*;
import java.util.*;

/**
 * @author margaryta
 *	Note that all generic action cards aren't attack cards by default, since attack cards require more sophisticated handling
 */
public class ActionCard extends Card implements IActionCard {
	protected int numActions;
	protected int numCards;
	protected int numBuys;
	protected int numMoney;
	protected ICommand command;
	protected List<ICommand> commands;
	
	public ActionCard(String name, int cost, int money, int victoryPoints, int numCards, int numActions, int numBuys, int numMoney) 
	{
		super(name, cost, money, victoryPoints);
		this.isActionCard = true;
		this.numActions = numActions;
		this.numCards = numCards;
		this.numBuys = numBuys;
		this.numMoney = numMoney;
		commands = new ArrayList<ICommand>();
	}
	
	public int GetNumActions() {
		return numActions;
	}
	
	public int GetNumCards() {
		return numCards;
	}
	
	public int GetNumBuys() {
		return numBuys;
	}
	
	public int GetNumMoney() {
		return numMoney;
	}
	
	@Override
	public void AddActionCommand(ICommand command) {
		commands.add(command);
	}

	
	@Override
	public void ExecuteActionCommands(boolean displayExecution) {
		boolean conditionSatisfied = false;
		for (ICommand c : commands)
		{
			boolean newResult = false;
			if (c.IsConditionalOnPreviousEvent())
				newResult = c.ExecuteConditionally(displayExecution, conditionSatisfied);
			else
				newResult = c.Execute(displayExecution);
			
			conditionSatisfied = newResult;
		}
		
	}

	@Override
	public List<ICommand> GetCommands() {
		return commands;
	}

	@Override
	public Boolean TrashesCards() {
		if (command != null) 
			return command.IsTrashCommand();
		else
			return false;
	}

	@Override
	public Boolean ReplacesCards() {
		if (command != null) 
			return command.IsReplaceCommand();
		else
			return false;
	}

}

