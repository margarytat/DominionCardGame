package simulation;

public class SimResults {

	// 2 simulation types: action stage and buying stage
	// 
	double handValue; 
	
	double averageNumCardsPerDeck; // seems like we'd never be interested in this directly
	double averageVictoryPointValueOfDeck;
	double averageMoneyValueOfDeck;
	double averageTreasureValue;
	double averageVictoryCardValue;
	double averageUnplayableVictoryCardValue;
	double averageUnplayableCardPortionOfDeck;
	double averageActionsPerActionCard;
	double averageCardsPerActionCard;
	double averageBuysPerActionCard;
	double averageMoneyPerActionCard;
	
	double averageDiffInVictoryPointsBetweenSelfAndAnotherPlayer; // a way for bot to "get" curses
	
}

//public int GetNumUnplayableVictoryCards();
//public int GetNumCards();
//public int GetNumCards(String cardName);
//public int GetNumActionCards();
//public int GetNumDeckImproverCards();
//public int GetNumActionCardsThatTrashCards();
//public int GetNumTerminalActionCards();
//
//public int GetNumVictoryPoints();
//public int GetNumActionCardActions();
//public int GetNumActionCardCards();		
//public int GetNumActionCardBuys();
//public double GetAverageTreasureValue();
//public double GetAverageVictoryCardValue();
// GetAverageUnplayableVictoryCardValue()?
