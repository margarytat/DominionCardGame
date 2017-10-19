package ui;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.Game;
import game.ICardTable;
import players.IPlayer;
import simulation.ISimulator;
import simulation.Simulator;

import java.io.*;

/**
 * @author margaryta
 */
public class GameSetup {
	
	public List<IPlayer> CurrentPlayers;
	public ICardTable Table;
	public int NumPlayers;
	public List<String> HumanPlayerNames;
	

	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Welcome to Dominion. Press Enter to start a game with a human and a bot. Press S for the simulator. Press anything else for more options. ");
		int numPlayers = 0;
		int numHumanPlayers = 0;
		int numSimulations = 0;
		
		try {
			String input = br.readLine();
			// Simulation
			if (input.equalsIgnoreCase("S"))
			{
				System.out.println("Enter selected cards:"); 
				List<String> chosenCards = Arrays.asList(br.readLine().split(","));
				Collections.sort(chosenCards);
	            
	            System.out.println("Enter the number of players (1 - 4):");
	            numPlayers = Integer.parseInt(br.readLine());
	            if (numPlayers < 1 || numPlayers > 4)
	            	System.err.println("Invalid number!");
	            
	            System.out.println("Enter the number of simulations (or nothing for " + Simulator.DEFAULT_NUM_SIMS + "):"); 
	            String numSimStr = br.readLine();
	            if (numSimStr.isEmpty())
	            	numSimulations = Simulator.DEFAULT_NUM_SIMS;
	            else
	            	numSimulations = Integer.parseInt(numSimStr);
	            
				System.out.println("Press nothing for all players to have the default starting decks. The single player will use a 2-player deck."); 
				String customGame = br.readLine();
				
				ISimulator sim = new Simulator(numPlayers, chosenCards, numSimulations);
				if (customGame.isEmpty()) 
				{
					sim.InitializeStandardDecks();
					System.out.println("Enter cards for the main bot to add:"); 
					String moreCards = br.readLine();
					if (!moreCards.isEmpty())
					{
						Map<String, Integer> cards = TextDisplay.GetInstance().GetCardsByNameFromInput(moreCards);
						sim.AddCardsToMainBotDeck(cards);
					}
				}
				else 
				{
					System.out.println("Otherwise, enter deck for the simbot in the following format [name #,..., name #] (No # = 1): "); 
					
					Map<String, Integer> mainBotDeck = TextDisplay.GetInstance().GetCardsByNameFromInput(br.readLine());
					sim.AddCardsToMainBotDeck(mainBotDeck);
					
					for (int i = 1; i < numPlayers; i++) 
					{
						System.out.println("Enter deck for the next bot: "); 
						Map<String, Integer> deck = TextDisplay.GetInstance().GetCardsByNameFromInput(br.readLine());
						sim.CreateAndRegisterBot(deck);
					}
				}
				
				double value = sim.RunSimulationForAverageHandValue();
				System.out.println("The average hand value for the main bot is: " + value); 
				
				// TODO: add loop to let user continue simulating with 
				// * commands to print out state of table and players' decks
				// * adding or removing cards from players' decks
				// * completely changing players' decks
				// * changing the chosen cards and number of players completely
				return;
			}
			// Game
			else
			{
				if (input.isEmpty())
				{
					numPlayers = 2;
					numHumanPlayers = 1;
				}
				else
				{
					System.out.println("Enter the number of players (2 - 4):");
					try 
					{   
			            numPlayers = Integer.parseInt(br.readLine());
			            if (numPlayers < 2 || numPlayers > 4)
			            	System.err.println("Invalid number!");
				            
			            System.out.println("Enter the number of human players (0 - " + numPlayers + "):");
			            numHumanPlayers = Integer.parseInt(br.readLine());
			            if (numHumanPlayers < 0 || numHumanPlayers > numPlayers)
			            	System.err.println("Invalid number!");
			            	
			        } catch (NumberFormatException nfe) {
			            System.err.println("Invalid format!");
			        } catch (IOException e) {
						e.printStackTrace();
					}
				}
				
	            System.out.println("Enter selected cards:"); 
	            List<String> chosenCards = new ArrayList<String>();
	            Arrays.asList(br.readLine().split(",")).forEach(c -> {
	            	chosenCards.add(c.trim());
	            });
	            System.out.println();
	            
	            Collections.sort(chosenCards);
	            
	            Game game = new Game(numHumanPlayers, numPlayers, chosenCards);
	            game.PlayGame();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
