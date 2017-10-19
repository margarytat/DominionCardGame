package commands;

import cards.*;

public class DiscardAndDrawCommand extends Command {
	private int numCardsToDiscard;
	private int numCardsToDraw;
	private int minNumCardsToDiscardAndDraw;
	private int maxNumCardsToDiscardAndDraw;
	private boolean playerDecidesHowManyCardsToDiscardAndDraw;
	
	
	public DiscardAndDrawCommand(IActionCard card)
	{
		super(card);
		playerDecidesHowManyCardsToDiscardAndDraw = true;
	}
	public DiscardAndDrawCommand(IActionCard card, int numCardsToDiscardAndDraw)
	{
		super(card);
		playerDecidesHowManyCardsToDiscardAndDraw = false;
		numCardsToDiscard = numCardsToDiscardAndDraw;
		numCardsToDraw = numCardsToDiscardAndDraw;
	}
	
	public boolean IsTrashCommand() {
		return false;
	}

	@Override
	public boolean IsReplaceCommand() {
		return false;
	}
	
	public void SetNumCardsToDiscardAndDraw(int num) {
		this.numCardsToDiscard = num;
		this.numCardsToDraw = num;
	}
	@Override
	public boolean Execute(boolean displayExecution) {
		if (playerDecidesHowManyCardsToDiscardAndDraw)
			player.DiscardAndThenDrawAnyNumCards(displayExecution);
		return true;
	}

	@Override
	public boolean IsConditionalOnPreviousEvent() {
		return false;
	}

}
