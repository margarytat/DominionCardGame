package cards;
import java.util.*;

import commands.*;

public interface IActionCard extends ICard {
	public int GetNumCards();
	public int GetNumActions();
	public int GetNumBuys();
	public int GetNumMoney();
	
	public void AddActionCommand(ICommand command);	
	public List<ICommand> GetCommands();
	public void ExecuteActionCommands(boolean displayExecution);
	
	// public void AddActionCommand(ICommand command);
	// cards that cause a player to draw and then discard or discard and then draw will have a list of commands to execute
	
	public Boolean TrashesCards();
	public Boolean ReplacesCards();
	
}
