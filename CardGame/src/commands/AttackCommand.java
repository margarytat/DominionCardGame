package commands;

import java.util.*;
import java.util.function.*;

import players.*;
import cards.*;

public class AttackCommand extends Command implements IAttackCommand {
	private BiConsumer<IObservablePlayer, Boolean>  thingToDoWithCard;
	private BiConsumer<IObservablePlayer, Boolean> thingToDoANumberOfTimes;

	public AttackCommand(IActionCard card, ICommand attackCommand) {
		super(card);
	}
	
	public AttackCommand(IActionCard card) {
		super(card);
	}
	
	public void SetEvilCardCommand(BiConsumer<IObservablePlayer, Boolean> doThingWithCard) {
		this.thingToDoWithCard = doThingWithCard;
	}
	
	public void SetEvilNumberCommand(BiConsumer<IObservablePlayer, Boolean> doThingANumberOfTimes) {
		this.thingToDoANumberOfTimes = doThingANumberOfTimes;
	}

	public boolean Execute(boolean displayExecution) {
		for (IObservablePlayer p : player.GetOtherPlayers()) 
		{
			boolean attackBlocked = p.TryToBlockAttack(this, displayExecution);
			if (!attackBlocked)
			{
				if (thingToDoWithCard != null)
				{
					thingToDoWithCard.accept(p, displayExecution);
					return true;
				}
				else if (this.thingToDoANumberOfTimes != null)
				{
					this.thingToDoANumberOfTimes.accept(p, displayExecution);
					return true;
				}
				
			}
		}
		return false;
	}
}
