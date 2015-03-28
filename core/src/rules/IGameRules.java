package rules;

import core.Player;

public interface IGameRules {
	boolean useCardEffect(Player p);
	boolean passTurn(Player p);
	boolean useGlobalEffect(Player p);
	boolean moveCard(Player p);
	boolean changeTurnState(Player p, String newState);
	
	String getStartState();
	void initialize();
}
