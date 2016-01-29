package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Card.CardID;
import core.Effect.EffectID;
import core.Resource.ResourceID;
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
	private final boolean isMaster;
	
	public GameState(IGameRules rules, List<Player> players, boolean isMaster) throws GameStateException {
		this.rules = rules;
		this.players = players;
		this.isMaster = isMaster;
		playerPresenters = new HashMap<IPlayerNotifier,Player>();
		for(Player p : players) {
			playerPresenters.put(p.getNotifier(), p);
			p.getNotifier().subscribeListener(this);
		}
		turnState = rules.getStartState();
	}
	
	protected enum NotifierMessageCode {
		CODE_TURN_CHANGED,
		CODE_CARD_DESTROYED,
		CODE_EFFECT_ANNOUNCED,
		CODE_EFFECT_TRIGGERED,
		CODE_SLOT_ADDED,
		CODE_CARD_ADDED_TO_SLOT,
		CODE_CARD_MOVED_TO_SLOT,
		CODE_CARD_ADDED_TO_CARD,
		CODE_CARD_ATTACHED_TO_CARD,
		CODE_RESOURCE_CREATED,
		CODE_RESOURCE_AMOUNT_CHANGED,
		CODE_GLOBAL_EFFECT_ADDED,
		CODE_PLAYER_QUIT_GAME,
		CODE_PLAYER_SENT_MESSAGE,
		CODE_DISCONNECTED
	}
	
	protected class NotifierThread implements Runnable {
		private Thread t;
		private NotifierMessageCode code;
		private Object args[];
		private IPlayerNotifier listener;
		
		public NotifierThread(NotifierMessageCode code, IPlayerNotifier listener, Object args[]) {
			this.code = code;
			this.args = args;
			this.listener = listener;
			t = new Thread(this, code.name()); 
			t.start();
		}

		@Override
		public void run() {
			System.out.println("Started thread for: "+code.name());
			switch(code) {
			case CODE_TURN_CHANGED:
				listener.turnStateChanged((String)args[0]);
				break;
			case CODE_CARD_DESTROYED:
				listener.cardDestroyed((Card)args[0]);
				break;
			case CODE_EFFECT_ANNOUNCED:
				listener.effectAnnounced((Effect)args[0]);
				break;
			case CODE_EFFECT_TRIGGERED:
				listener.effectTriggered((Effect)args[0]);
				break;
			case CODE_SLOT_ADDED:
				listener.slotAdded((Slot)args[0]);
				break;
			case CODE_CARD_ADDED_TO_SLOT:
				listener.cardAddedToSlot((Card)args[0], (Slot)args[1]);
				break;
			case CODE_CARD_MOVED_TO_SLOT:
				listener.cardMovedToSlot((Card)args[0], (Slot)args[1]);
				break;
			case CODE_CARD_ADDED_TO_CARD:
				listener.cardAddedToCard((Card)args[0], (Card)args[1]);
				break;
			case CODE_CARD_ATTACHED_TO_CARD:
				listener.cardAttachedToCard((Card)args[0], (Card)args[1]);
				break;
			case CODE_RESOURCE_CREATED:
				listener.resourceCreated((Resource)args[0]);
				break;
			case CODE_RESOURCE_AMOUNT_CHANGED:
				listener.resourceAmountChanged((Resource)args[0], (Integer)args[1]);
				break;
			case CODE_GLOBAL_EFFECT_ADDED:
				listener.globalEffectAdded((Effect)args[0]);
				break;
			case CODE_PLAYER_QUIT_GAME:
				listener.playerQuitGame((Player)args[0]);
				break;
			case CODE_PLAYER_SENT_MESSAGE:
				listener.playerSentMessage((Player)args[0], (String)args[1]);
				break;
			case CODE_DISCONNECTED:
				listener.disconnectedFromGame((String)args[0]);
				break;
			}
		}
	}
	
	// Whether this instance is running the game, or is a client connected to a host.
	// Clients should have restricted abilities to execute effects, and instead request they be sent to the server.
	public boolean getIsMaster() {
		return isMaster;
	}
	
	public void setTurnState(String newState) {
		turnState = newState;
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.turnStateChanged(newState);
			//new NotifierThread(NotifierMessageCode.CODE_TURN_CHANGED, presenter,
					//new Object[] { newState });
		}
	}
	
	public String getTurnState() {
		return turnState;
	}
	
	public void addSlot(Slot slot) {
		slots.put(slot.getID(), slot);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.slotAdded(slot);
			//new NotifierThread(NotifierMessageCode.CODE_SLOT_ADDED, presenter,
				//	new Object[] { slot });
		}
	}
	
	public void addGlobalEffect(Effect e) {
		globalEffects.add(e);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.globalEffectAdded(e);
			//new NotifierThread(NotifierMessageCode.CODE_GLOBAL_EFFECT_ADDED, presenter,
				//	new Object[] { e });
		}
	}
	
	// Resources
	public void addResource(Resource r) {
		resources.put(r.getID(), r);
		for(IPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.resourceCreated(r);
			//new NotifierThread(NotifierMessageCode.CODE_RESOURCE_CREATED, presenter,
				//	new Object[] { r });
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
		synchronized(this) {
			if(rules.canUseCardEffect(playerPresenters.get(player), getEffectForID(e))) {
				for(IPlayerNotifier presenter : playerPresenters.keySet()) {
					// We don't want to send this to the player that just requested it
					if(presenter != player) {
						presenter.effectAnnounced(getEffectForID(e));
					}
				}
				if(isMaster) {
					rules.useCardEffect(playerPresenters.get(player), getEffectForID(e));
					for(IPlayerNotifier presenter : playerPresenters.keySet()) {
						presenter.effectTriggered(getEffectForID(e));
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean playerTriggeredCardEffect(IPlayerNotifier player, EffectID e) {
		synchronized(this) {
			// TODO Auto-generated method stub
			System.out.println(playerPresenters.get(player).getName() + " used effect " + e.name);
		}
		return true;
	}

	@Override
	public boolean playerWantsToMoveCardToSlot(IPlayerNotifier player, Card.CardID card, Slot.SlotID slot) {
		synchronized(this) {
			return rules.moveCard(playerPresenters.get(player));
		}
	}

	@Override
	public boolean playerWantsToAttachCardToCard(IPlayerNotifier player,
			Card.CardID host, Card.CardID attachee) {
		synchronized(this) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public boolean playerWantsToChangeGameState(IPlayerNotifier player,
			String newState) {
		synchronized(this) {
			//if(rules.changeTurnState(playerPresenters.get(player), newState)) {
//				this.setTurnState(newState);
				//return true;
			//} else {
				return false;
			//}
		}
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

	@Override
	public void resourceAmountChanged(ResourceID resource, int amount) {
		synchronized(this) {
			// TODO Auto-generated method stub
			
		}
	}
}
