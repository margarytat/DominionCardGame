package cards;

import game.*;

/**
 * 
 */

/**
 * @author margaryta
 *
 *
 */
public class Card implements ICard {
	protected String name;
	protected int cost;
	protected int money;
	protected int victoryPoints;
	protected Boolean isActionCard;
	protected Boolean isReactionCard;
	
	public Card(String name, int cost, int money, int victoryPoints)
	{
		this.isActionCard = false;
		this.isReactionCard = false;
		this.name = name;
		this.cost = cost;
		this.money = money;
		this.victoryPoints = victoryPoints;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Card)) return false;
	    Card otherCard = (Card)other;
	    return otherCard.GetName().equalsIgnoreCase(this.name);
	}
	
	@Override
	public int hashCode() {
	    return name.hashCode();
	  }
	
	public boolean IsAction() {
		return isActionCard;
	}
	
	public boolean isReaction() {
		return isReactionCard;
	}
	
	public boolean IsTreasure() {
		return (money > 0);
	}
	
	public boolean IsVictory() {
		return (victoryPoints > 0 || name.equalsIgnoreCase(CardFactory.CURSE));
	}
	
	public boolean IsUnplayable() {
		return !isActionCard && (money < 1);
	}
	
	public boolean IsUnplayableVictory()
	{
		return IsVictory() && !isActionCard && (money < 1);
	}
	
	public String GetName() {
		return this.name;
	}
	
	public int GetMoney() {
		return this.money;
	}
	
	public int GetVictoryPoints() {
		return this.victoryPoints;
	}
	
	public int GetCost() {
		return this.cost;
	}
}


