package core;

import java.util.List;
import java.util.Map;

import presenter.IPlayerNotifier;
import presenter.IPlayerNotifier.IPlayerNotifierListener;
import rules.IGameRules;

public class GameState implements IPlayerNotifierListener {
	private List<Slot> slots;
	private List<Player> players;
	private Map<IPlayerNotifier,Player> playerPresenters;
	private String turnState;
	private IGameRules rules;
	
	public GameState(IGameRules rules) {
		this.rules = rules;
		turnState = rules.getStartState();
		rules.initialize();
	}
	
	public void setTurnState(String newState) {
		turnState = newState;
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.turnStateChanged(newState);
		}
	}

	@Override
	public boolean playerWantsToUseCardEffect(IPlayerNotifier player) {
		return rules.useCardEffect(playerPresenters.get(player));
	}

	@Override
	public boolean playerWantsToMoveCardToSlot(IPlayerNotifier player) {
		return rules.moveCard(playerPresenters.get(player));
	}

	@Override
	public boolean playerWantsToAttachCardToCard(IPlayerNotifier player) {
		return false;
	}

	@Override
	public boolean playerWantsToChangeGameState(IPlayerNotifier player, String newState) {
		return rules.changeTurnState(playerPresenters.get(player), newState);
	}

	@Override
	public void playerQuitGame(IPlayerNotifier player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerSentMessage(IPlayerNotifier player, String message) {
		// TODO Auto-generated method stub
		
	}
}
