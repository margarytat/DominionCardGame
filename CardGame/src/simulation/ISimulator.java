package simulation;

import java.util.Map;

public interface ISimulator 
{
	public void AddCardToDeck(String cardName);
	public double RunSimulationForAverageHandValue();
	public void AddCardsToMainBotDeck(Map<String, Integer> mainBotDeck);
	public void CreateAndRegisterBot(Map<String, Integer> deck);
	public void InitializeStandardDecks();
}