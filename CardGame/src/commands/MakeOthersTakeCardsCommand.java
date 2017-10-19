package commands;

import java.util.*;

import cards.*;
import players.*;

public class MakeOthersTakeCardsCommand extends Command {
	
	private String cardToTake;

	public MakeOthersTakeCardsCommand(IActionCard card) {
		super(card);
	}
	
	public MakeOthersTakeCardsCommand(IActionCard card, String cardName) {
		super(card);
		cardToTake = cardName;	
	}

	@Override
	public boolean Execute(boolean displayExecution) {
		List<IObservablePlayer> others = player.GetOtherPlayers();
		others.forEach(p -> p.TakeCardFromTable(cardToTake, displayExecution));
		return true;
	}

}
