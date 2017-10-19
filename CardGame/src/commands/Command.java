package commands;

import cards.IActionCard;
import players.IPlayer;

public abstract class Command implements ICommand {
	
	protected IActionCard card;
	protected IPlayer player;
	protected boolean isConditional;
	
	public Command(IActionCard card)
	{
		this.card = card;	
		isConditional = false;
	}

	public void SetPlayer(IPlayer player) {
		this.player = player;
	}
	
	public boolean IsConditionalOnPreviousEvent() {
		return isConditional;
	}
	
	// There are different kinds of action cards that trash other cards, from mandatory to optional trashing, to trashing of only certain card types or specific cards.
	// The simulation evaluation function will likely evolve from calculating the average money value of hand in the future, and that's where this could come into play
	// The average hand value seems like it would prioritize short-term benefits -- should try simulating several coming turns with combinations of purchases to see hand value trends over time.
	// My theory is that that's when we'll be able to see the value of deck-grooming cards
	public boolean IsTrashCommand() {
		return false;
	}

	public boolean IsReplaceCommand() {
		return false;
	}
	
	public boolean ExecuteConditionally(boolean displayExecution, boolean conditionSatisfied) {
		boolean executed = false;
		if (conditionSatisfied)
			executed = this.Execute(displayExecution);
		return executed;
	}

}
