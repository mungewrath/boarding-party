package rules;

import java.util.HashMap;
import java.util.Map;

import core.Effect;
import core.GameState;
import core.Player;
import core.Resource;

public class JanKenPonGameRules implements IGameRules {
	private GameState state;
	private Map<Player, Effect> moves = new HashMap<Player, Effect>();
	
	private static final int PLAYER_NUM = 2; 
	
	private static String JAN = "Jan";
	private static String KEN = "Ken";
	private static String PON = "Pon";
	
	public enum JanKenPonState {
		STATE_CHOOSE_TYPE,
		STATE_PLAYER_1_WON,
		STATE_PLAYER_2_WON;
		
		int player;
		JanKenPonState() {
			player = -1;
		}
		
		JanKenPonState(int player) {
			setPlayer(player);
		}
		
		public void setPlayer(int idx) {
			player = idx;
		}
		public int getPlayer() {
			return player;
		}
		
		@Override
		public String toString() {
			if(player != -1) {
				return super.toString() + player;
			} else {
				return super.toString();
			}
		}
	}

	@Override
	public boolean changeTurnState(Player arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStartState() {
		return JanKenPonState.STATE_CHOOSE_TYPE.toString();
	}

	@Override
	public void initialize(GameState state) throws RulesException {
		this.state = state;
		
		if(state.getPlayerCount() != PLAYER_NUM) {
			throw new RulesException("Not the right number of players.");
		}
		
		Effect jan = new Effect(JAN, null, (e, p) -> {
			System.out.println(p.getName() + " is playing " + e.getEffectName());
			state.getResourceForPlayer(p, JAN).setQuantity(1);
			} );
		state.addGlobalEffect(jan);
		Effect ken = new Effect(KEN, null, (e, p) -> {
			System.out.println(p.getName() + " is playing " + e.getEffectName());
			state.getResourceForPlayer(p, KEN).setQuantity(1);
			} );
		state.addGlobalEffect(ken);
		Effect pon = new Effect(PON, null, (e, p) -> {
			System.out.println(p.getName() + " is playing " + e.getEffectName());
			state.getResourceForPlayer(p, PON).setQuantity(1);
			} );
		state.addGlobalEffect(pon);
		
		for(int i=0;i<state.getPlayerCount();i++) {
			state.addResource(new Resource(JAN, state.getPlayer(i)));
			state.addResource(new Resource(KEN, state.getPlayer(i)));
			state.addResource(new Resource(PON, state.getPlayer(i)));
		}
		
		state.setTurnState(getStartState());
	}

	@Override
	public boolean moveCard(Player arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean passTurn(Player arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useCardEffect(Player caster, Effect move) {
		if(!moves.containsKey(caster)) {
			moves.put(caster, move);
			move.getCode().exec(move, caster);
			if(moves.size() == PLAYER_NUM) {
				determineOutcome();
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void determineOutcome() {
		if(state.getResourceForPlayer(state.getPlayer(0), JAN).getQuantity() > 0) {
			if(state.getResourceForPlayer(state.getPlayer(1), KEN).getQuantity() > 0) {
				// Ken beats Jan
				aiWins();
			} else if(state.getResourceForPlayer(state.getPlayer(1), PON).getQuantity() > 0) {
				humanWins();
			} else {
				rematch();
			}
		} else if(state.getResourceForPlayer(state.getPlayer(0), KEN).getQuantity() > 0) {
			if(state.getResourceForPlayer(state.getPlayer(1), JAN).getQuantity() > 0) {
				// Ken beats Jan
				humanWins();
			} else if(state.getResourceForPlayer(state.getPlayer(1), PON).getQuantity() > 0) {
				aiWins();
			} else {
				rematch();
			}
		} else if(state.getResourceForPlayer(state.getPlayer(0), PON).getQuantity() > 0) {
			if(state.getResourceForPlayer(state.getPlayer(1), JAN).getQuantity() > 0) {
				// Jan beats Pon
				humanWins();
			} else if(state.getResourceForPlayer(state.getPlayer(1), KEN).getQuantity() > 0) {
				aiWins();
			} else {
				rematch();
			}
		}
	}
	
	private void humanWins() {
		state.setTurnState(JanKenPonState.STATE_PLAYER_1_WON.toString());
	}
	
	private void aiWins() {
		state.setTurnState(JanKenPonState.STATE_PLAYER_2_WON.toString());
	}
	
	private void rematch() {
		moves.clear();
		for(int i=0;i<state.getPlayerCount();i++) {
			state.getResourceForPlayer(state.getPlayer(i), JAN).setQuantity(0);
			state.getResourceForPlayer(state.getPlayer(i), KEN).setQuantity(0);
			state.getResourceForPlayer(state.getPlayer(i), PON).setQuantity(0);
		}
		state.setTurnState(JanKenPonState.STATE_CHOOSE_TYPE.toString());
	}
}
