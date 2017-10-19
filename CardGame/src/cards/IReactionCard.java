package cards;

import commands.ICommand;

public interface IReactionCard extends IActionCard, ICard {
	public void SetReactionCommand(ICommand command);
	public ICommand GetReactionCommand();
	public boolean CanBlockAttack();
}
