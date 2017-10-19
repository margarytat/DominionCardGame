package players;

import java.util.*;

import cards.*;
import game.*;

/**
 * @author margaryta
 */
public interface IPlayer extends IObservablePlayer {
//	public List<Card> GetDeck();
//	public List<Card> GetHand();
//	public List<Card> GetCardsInPlay();
	
	// public PreTurn preNextTurn; 
	// can we express preTurn as a function to pass?
	
	public String GetAllCardsAsString();
	public String GetHandAsString();
	
	public List<IObservablePlayer> GetOtherPlayers();
	
	public void PlayTurn();
	public void PlayTurnPart2();
	public void PlayActions();
	public void BuyCards();
	
	public int GetMoney();
	
	// this will potentially move into observablePlayer when council room is implemented
	public void DrawCards(int num, boolean displayExecution);
	
	public void AddActions(int num);
	public void AddMoney(int num);
	public void AddBuys(int num);
	
	public void TrashUpToNumberOfCards(int maxNumCards, boolean displayExecution);
	public void TrashFromOneToXCards(int maxNumCardsToTrash, boolean displayExecution);
	public void DiscardAndThenDrawAnyNumCards(boolean displayExecution);
	public boolean OptionallyTrashCardFromHand(String card, boolean displayExecution);

	
}
