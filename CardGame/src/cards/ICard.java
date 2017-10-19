package cards;

public interface ICard 
{
	public String GetName();
	public int GetCost();
	public int GetMoney();
	public int GetVictoryPoints();

	public boolean IsVictory();
	public boolean IsUnplayableVictory();
	public boolean IsUnplayable();
	public boolean IsTreasure();
	public boolean IsAction();
	public boolean isReaction();
}