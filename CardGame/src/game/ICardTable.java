package game;

import java.util.*;

import cards.*;
import players.*;

public interface ICardTable {

	public void RegisterPlayers(List<IObservablePlayer> players);
	public void RegisterPlayer(IObservablePlayer Player);
	public void AddCardToStack(ICard card);
	public ICard RemoveCardFromStack(String cardName);
	public List<ICard> RemoveInitialPlayerDeck();
	public void PutCardInTrash(ICard card);
	
	public List<IObservablePlayer> GetPlayersAtTable();

	public int GetNumberOfEmptyStacks();
	public int GetStackCardCount(String cardName);
	public List<String> GetNamesOfAffordableNonEmptyActionCardsAndTreasures(int money);
	
	public Map<String, List<ICard>> GetCardStacks();
	
	public Map<String, List<ICard>> MakeCopyOfCardStacks();
	public List<String> MakeCopyOfChosenCards();
	public List<ICard> MakeCopyOfTrashedCards();
	public List<ICard> MakeCopyOfAffordableNonEmptyActionCardsAndTreasures(int money);
	public List<ICard> MakeCopyOfAffordableNonEmptyCards(int money);
}