/**
 * 
 */
package cards;

import commands.ICommand;

/**
 * @author margaryta
 *
 */
public class ReactionCard extends ActionCard implements IReactionCard {
	// all cards but moat will have a reaction command. Moat is a hack that expresses its command thru canBlockAttack and is the only one that can
	protected ICommand reactionCommand;
	private boolean canBlockAttack;
	
	public ReactionCard(String name, int cost, int money, int victoryPoints, int numCards, int numActions, int numBuys, int numMoney, boolean canBlockAttack) 
	{
		super(name, cost, money, victoryPoints, numCards, numActions, numBuys, numMoney);
		this.isReactionCard = true;
		this.canBlockAttack = canBlockAttack;
	}

	@Override
	public void SetReactionCommand(ICommand command) {
		reactionCommand = command;	
	}
	
	public ICommand GetActionCommand() {
		return command;
	}
	
	public ICommand GetReactionCommand() {
		return reactionCommand;
	}

	@Override
	public boolean CanBlockAttack() {
		return canBlockAttack;
	}

}
