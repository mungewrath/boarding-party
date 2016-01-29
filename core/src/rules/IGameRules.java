package rules;

import core.Effect;
import core.GameState;
import core.Player;

public interface IGameRules {
	boolean canUseCardEffect(Player p, Effect e);
	boolean useCardEffect(Player p, Effect e);
	boolean passTurn(Player p);
	boolean moveCard(Player p);
	boolean changeTurnState(Player p, String newState);
	
	String getStartState();
	boolean isGameOver();
	void initialize(GameState state) throws RulesException;
}
