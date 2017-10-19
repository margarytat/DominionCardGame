/**
 * 
 */
package game;

import java.util.*;

import cards.IActionCard;
import cards.ICard;
import cards.IReactionCard;

/**
 * @author margaryta
 * This class has all of the player's cards in various states: Deck, Discard, Hand, In Play (in the future: on an Island, etc.).
 * It handles card mechanics: discarding hand and cards in play; drawing new hand; playing, discarding, and buying (taking from the table) specific cards
 * It can answer questions about numbers and ratios of various types of cards but makes no decisions w.r.t. choosing cards to play/discard/buy
 */
public interface IDeck {
	public void Cleanup(int numCardsToDraw);
	public void DrawCards(int numCards, boolean displayDrawingEachCard);
	public void DiscardCardFromHand(ICard cardToDiscard);
	public void BuyCard(String name);
	public void BuyCard(String name, List<String> treasuresToPlay);
	public void TakeCardFromTable(String name, boolean display);
	public void PlayActionCard(IActionCard iActionCard, boolean displayPlayingCard);
	public void TrashCardFromHand(ICard c);
	
	public List<ICard> GetHand();
	public List<IActionCard> GetAllActionCardsInHand();
	public List<IReactionCard> GetReactionCardsInHand();
	public List<ICard> GetAllCards();
	public List<ICard> GetCopyOfDeckCards();
	public Map<String, Integer> GetCardsByName(Collection<ICard> cards);
	public Map<String, Integer> GetCardsByName();
	
	public int GetNumUnplayableVictoryCards();
	public int GetNumCards();
	public int GetNumCards(String cardName);
	public int GetNumActionCards();
	public int GetNumDeckImproverCards();
	public int GetNumActionCardsThatTrashCards();
	public int GetNumTerminalActionCards();
	
	public int GetNumVictoryPoints();
	public int GetNumActionCardActions();
	public int GetNumActionCardCards();		
	public int GetNumActionCardBuys();
	
	public int GetSumOfTreasuresInHand();
	public int GetSumOfAllTreasures();

	public double GetAverageTreasureValue();
	public double GetAverageVictoryCardValue();
	public double GetAverageUnplayableVictoryCardValue();
	
	public boolean CanBuyProvinceWithOneHand(int numCardsHand);
	public boolean CanBuyCardWithTreasuresOnlyInOneHand(int cardCost, int numCardsInHand);
	public boolean CanBuyCardWithOneHand(String cardName, int numCardsInHand);
	public boolean CanBuyCardWithThisHand(String cardName);
	
	public String GetAllCardsAsString();
	public String GetDeckAsString();
	public String GetHandAsString();
	
	// for simulation
	public void ConsolidateAllCardsIntoDeckShuffleAndDraw(int numCards);
	public void GiveTable(ICardTable table);
	public void PutAllCardsBackOnTable();
	
	
//	public double GetProbabilityOfDrawingCard(String cardName, int numCardsToDraw);

	
	

}
