package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Card.CardID;
import presenter.IPlayerNotifier;
import presenter.IPlayerNotifier.IPlayerNotifierListener;
import rules.IGameRules;
import rules.RulesException;

public class GameState implements IPlayerNotifierListener {
	// TODO: Change lists in favor of associative containers
	private Map<Slot.SlotID, Slot> slots = new HashMap<Slot.SlotID, Slot>();
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
		slots.put(slot.getID(), slot);
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
			presenter.resourceCreated(r);
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
		Resource.ResourceID key = new Resource.ResourceID(p.getID(),id);
		return resources.get(key);
	}
	
	// Locators
	public Effect getEffectForID(Effect.EffectID eid) {
		if(eid.card != null) {
			return getCardForID(eid.card).getEffect(eid.name);
		} else {
			for(Effect e : globalEffects) {
				if(e.getEffectName().equals(eid.name)) {
					return e;
				}
			}
			return null;
		}
	}
	
	public Card getCardForID(Card.CardID cid) {
		if(cid.parentCard != null) {
			return getCardForID(cid.parentCard).getChild(cid.cardIndex);
		} else if(cid.slot != null) {
			return getSlotForID(cid.slot).getCardAtIndex(cid.slotNum);
		} else {
			return null;
		}
	}
	
	public Slot getSlotForID(Slot.SlotID sid) {
		return slots.get(sid);
	}

	@Override
	public boolean playerWantsToUseCardEffect(IPlayerNotifier player, Effect.EffectID e) {
		return rules.useCardEffect(playerPresenters.get(player), getEffectForID(e));
	}

	@Override
	public boolean playerWantsToMoveCardToSlot(IPlayerNotifier player, Card.CardID card, Slot.SlotID slot) {
		return rules.moveCard(playerPresenters.get(player));
	}

	@Override
	public boolean playerWantsToAttachCardToCard(IPlayerNotifier player,
			Card.CardID host, Card.CardID attachee) {
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

	@Override
	public void disconnectedFromGame(String message) {
		// TODO Auto-generated method stub
		
	}
}
