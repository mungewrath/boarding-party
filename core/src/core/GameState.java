package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import presenter.IPlayerNotifier;
import presenter.IPlayerNotifier.IPlayerNotifierListener;
import rules.IGameRules;
import rules.RulesException;

public class GameState implements IPlayerNotifierListener {
	// TODO: Change lists in favor of associative containers
	private List<Slot> slots = new ArrayList<Slot>();
	private Map<Resource.ResourceID, Resource> resources = new HashMap<Resource.ResourceID, Resource>();
	private List<Player> players;
	private List<Effect> globalEffects = new ArrayList<Effect>();
	private Map<IPlayerNotifier,Player> playerPresenters;
	private String turnState;
	private IGameRules rules;
	
	public GameState(IGameRules rules, List<Player> players) throws GameStateException {
		this.rules = rules;
		this.players = players;
		playerPresenters = new HashMap<IPlayerNotifier,Player>();
		for(Player p : players) {
			playerPresenters.put(p.getNotifier(), p);
			p.getNotifier().subscribeListener(this);
		}
		turnState = rules.getStartState();
	}
	
	public void setTurnState(String newState) {
		turnState = newState;
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.turnStateChanged(newState);
		}
	}
	
	public void addSlot(Slot slot) {
		slots.add(slot);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.slotAdded(slot);
		}
	}
	
	public void addGlobalEffect(Effect e) {
		globalEffects.add(e);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.globalEffectAdded(e);
		}
	}
	
	// Resources
	public void addResource(Resource r) {
		resources.put(r.getID(), r);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.resourceAdded(r);
		}
	}
	
	// Accessors
	public int getPlayerCount() {
		return players.size();
	}
	
	public Player getPlayer(int num) {
		return players.get(num);
	}
	
	public List<Effect> getAllGlobalEffects() {
		return globalEffects;
	}
	
	public Resource getResourceForPlayer(Player p, String id) {
		Resource.ResourceID key = new Resource.ResourceID(p,id);
		return resources.get(key);
	}

	@Override
	public boolean playerWantsToUseCardEffect(IPlayerNotifier player, Effect e) {
		return rules.useCardEffect(playerPresenters.get(player), e);
	}

	@Override
	public boolean playerWantsToMoveCardToSlot(IPlayerNotifier player) {
		return rules.moveCard(playerPresenters.get(player));
	}

	@Override
	public boolean playerWantsToAttachCardToCard(IPlayerNotifier player,
			Card host, Card attachee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerWantsToChangeGameState(IPlayerNotifier player,
			String newState) {
		// TODO Auto-generated method stub
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
