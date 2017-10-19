/**
 * 
 */
package game;

import java.util.*;

import players.*;
import ui.TextDisplay;

/**
 * @author margaryta
 * makes players take turns and knows when the game is over
 */
public class Game {
	
	public static final int NUM_EMPTY_STACKS_TO_END_GAME = 3;
	public static final String NAME_EMPTY_STACK_TO_END_GAME = CardFactory.PROVINCE;
	
	private ICardTable table;
	private List<IPlayer> players;
	private List<String> stacksToEndGame;
	private int indexOfNextPlayer;
	private int turnNum;
	
	public Game(int numHumanPlayers, int numPlayers, List<String> chosenCards)
	{
		turnNum = 0;
		players = new ArrayList<IPlayer>();
		stacksToEndGame = new ArrayList<String>();
		stacksToEndGame.add(NAME_EMPTY_STACK_TO_END_GAME);
		table = new CardTable(numPlayers, chosenCards);	
		
		List<String> defaultHumanNames = Arrays.asList("Margaryta", "Human 1", "Human 2", "Human 3");
		for (int i = 0; i < numHumanPlayers; i++)
			players.add(new HumanPlayer(table, defaultHumanNames.get(i), i));
		for (int i = players.size(); i < numPlayers; i++)
			players.add(new Bot(table, "Bot " + (i + 1- players.size()), i));
		
		players.forEach(p -> p.TakeStartingDeckFromTableAndDrawFirstHand());
		List<IObservablePlayer> temp = new ArrayList<IObservablePlayer>(); //I can't pass in an IPlayer where an IObservablePlayer is needed even though P extends OP
		players.forEach(p -> temp.add(p));
		table.RegisterPlayers(temp);
		indexOfNextPlayer = 0;
	}
	
	public boolean IsGameOver()
	{
		if (table.GetNumberOfEmptyStacks() >= NUM_EMPTY_STACKS_TO_END_GAME)
			return true;
		if (table.GetStackCardCount(stacksToEndGame.get(0)) == 0)
			return true;
		return false;
	}
	
	public void PlayGame()
	{
		this.turnNum++;
		TextDisplay.GetInstance().DisplayGameStart();
		TextDisplay.GetInstance().DisplayGameTurn(turnNum);

		while (!IsGameOver())
		{
			TextDisplay.GetInstance().DisplayCardStacks(table.GetCardStacks());
			IPlayer currentPlayer = players.get(indexOfNextPlayer);
			currentPlayer.PlayTurn();
			
			// if (!currentPlayer.OutpostTurn){
			if (indexOfNextPlayer == players.size() - 1)
			{
				indexOfNextPlayer = 0;
				turnNum++;
				TextDisplay.GetInstance().DisplayGameTurn(turnNum);
			}
			else
				indexOfNextPlayer++;
		}
		TextDisplay.GetInstance().DisplayGameEnd(players);
	}
}
