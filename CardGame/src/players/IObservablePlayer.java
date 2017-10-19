package players;

import java.util.*;

import game.*;
import cards.*;
import commands.*;

// For players to interact with each other
// (future: and analyze each other's decks if strategies take that into account)
public interface IObservablePlayer {
	public String GetName();
	public List<ICard> GetCopyOfDeckCards();
	public int GetIndexAtTable();
	
	public int GetNumVictoryPoints();
	public int GetNumActions();
	public int GetNumBuys();
	public int GetMoneyAccumulatedThisTurn();
	
	public void JoinTable(ICardTable table);
	public void TakeStartingDeckFromTableAndDrawFirstHand();
	public void TakeCardFromTable(String cardName, boolean displayExecution);
	public boolean TryToBlockAttack(ICommand attackCommand, boolean displayExecution);
	public void DiscardFromHand(int i);
}
