package rules;

import core.Effect;
import core.GameState;
import core.Player;

public interface IGameRules {
	boolean useCardEffect(Player p, Effect e);
	boolean passTurn(Player p);
	boolean moveCard(Player p);
	boolean changeTurnState(Player p, String newState);
	
	String getStartState();
	void initialize(GameState state) throws RulesException;
}
