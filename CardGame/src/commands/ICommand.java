package commands;

import players.IPlayer;

public interface ICommand {
	public void SetPlayer(IPlayer player);
	
	// returns true if the execution was successful
	public boolean Execute(boolean displayExecution);
	public boolean ExecuteConditionally(boolean displayExecution, boolean conditionSatisfied);
	
	public boolean IsConditionalOnPreviousEvent();
	public boolean IsTrashCommand();
	public boolean IsReplaceCommand();

	
}
