package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Effect.EffectID;
import core.Resource.ResourceID;
import presenter.AbstractPlayerNotifier;
import presenter.AbstractPlayerNotifier.IPlayerNotifierListener;
import rules.IGameRules;

public class GameState implements IPlayerNotifierListener, IPublicGameState {
	// TODO: Change lists in favor of associative containers
	private Map<Slot.SlotID, Slot> slots = new HashMap<Slot.SlotID, Slot>();
	private Map<Resource.ResourceID, Resource> resources = new HashMap<Resource.ResourceID, Resource>();
	private List<Player> players;
	private List<Effect> globalEffects = new ArrayList<Effect>();
	private Map<AbstractPlayerNotifier,Player> playerPresenters;
	private String turnState;
	private IGameRules rules;
	private final boolean isMaster;
	
	public GameState(IGameRules rules, List<Player> players, boolean isMaster) throws GameStateException {
		this.rules = rules;
		this.players = players;
		this.isMaster = isMaster;
		playerPresenters = new HashMap<AbstractPlayerNotifier,Player>();
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
		private AbstractPlayerNotifier playerNotifier;
		
		public NotifierThread(NotifierMessageCode code, AbstractPlayerNotifier playerNotifier, Object args[]) {
			this.code = code;
			this.args = args;
			this.playerNotifier = playerNotifier;
			t = new Thread(this, code.name()); 
			t.start();
		}

		@Override
		public void run() {
			System.out.println("Started thread for: "+code.name());
			switch(code) {
			case CODE_TURN_CHANGED:
				playerNotifier.turnStateChanged((String) args[0]);
				break;
			case CODE_CARD_DESTROYED:
				playerNotifier.cardDestroyed((Card) args[0]);
				break;
			case CODE_EFFECT_ANNOUNCED:
				playerNotifier.effectAnnounced((Effect) args[0]);
				break;
			case CODE_EFFECT_TRIGGERED:
				playerNotifier.effectTriggered((Effect) args[0]);
				break;
			case CODE_SLOT_ADDED:
				playerNotifier.slotAdded((Slot) args[0]);
				break;
			case CODE_CARD_ADDED_TO_SLOT:
				playerNotifier.cardAddedToSlot((Card) args[0], (Slot) args[1]);
				break;
			case CODE_CARD_MOVED_TO_SLOT:
				playerNotifier.cardMovedToSlot((Card) args[0], (Slot) args[1]);
				break;
			case CODE_CARD_ADDED_TO_CARD:
				playerNotifier.cardAddedToCard((Card) args[0], (Card) args[1]);
				break;
			case CODE_CARD_ATTACHED_TO_CARD:
				playerNotifier.cardAttachedToCard((Card) args[0], (Card) args[1]);
				break;
			case CODE_RESOURCE_CREATED:
				playerNotifier.resourceCreated((Resource) args[0]);
				break;
			case CODE_RESOURCE_AMOUNT_CHANGED:
				playerNotifier.resourceAmountChanged((Resource) args[0], (Integer) args[1]);
				break;
			case CODE_GLOBAL_EFFECT_ADDED:
				playerNotifier.globalEffectAdded((Effect) args[0]);
				break;
			case CODE_PLAYER_QUIT_GAME:
				playerNotifier.playerQuitGame((Player) args[0]);
				break;
			case CODE_PLAYER_SENT_MESSAGE:
				playerNotifier.playerSentMessage((Player) args[0], (String) args[1]);
				break;
			case CODE_DISCONNECTED:
				playerNotifier.disconnectedFromGame((String) args[0]);
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
        for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
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
		for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.slotAdded(slot);
			//new NotifierThread(NotifierMessageCode.CODE_SLOT_ADDED, presenter,
				//	new Object[] { slot });
		}
	}
	
	public void addGlobalEffect(Effect e) {
		globalEffects.add(e);
		for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.globalEffectAdded(e);
			//new NotifierThread(NotifierMessageCode.CODE_GLOBAL_EFFECT_ADDED, presenter,
				//	new Object[] { e });
		}
	}
	
	// Resources
	public void addResource(Resource r) {
		resources.put(r.getID(), r);
		for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
			presenter.resourceCreated(r);
			//new NotifierThread(NotifierMessageCode.CODE_RESOURCE_CREATED, presenter,
				//	new Object[] { r });
		}
	}
	
	// Accessors
    @Override
	public int getPlayerCount() {
		return players.size();
	}

    @Override
	public Player getPlayer(int num) {
		return players.get(num);
	}

    @Override
	public List<Effect> getAllGlobalEffects() {
		return globalEffects;
	}

    @Override
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
	public boolean playerWantsToUseCardEffect(AbstractPlayerNotifier player, Effect.EffectID e) {
		synchronized(this) {
			if(rules.canUseCardEffect(playerPresenters.get(player), getEffectForID(e))) {
				for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
					// We don't want to send this to the player that just requested it
					if(presenter != player) {
						presenter.effectAnnounced(getEffectForID(e));
					}
				}
				if(isMaster) {
					rules.useCardEffect(playerPresenters.get(player), getEffectForID(e));
					for(AbstractPlayerNotifier presenter : playerPresenters.keySet()) {
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
	public boolean playerTriggeredCardEffect(AbstractPlayerNotifier player, EffectID e) {
		synchronized(this) {
			// TODO Auto-generated method stub
			System.out.println(playerPresenters.get(player).getName() + " used effect " + e.name);
		}
		return true;
	}

	@Override
	public boolean playerWantsToMoveCardToSlot(AbstractPlayerNotifier player, Card.CardID card, Slot.SlotID slot) {
		synchronized(this) {
			return rules.moveCard(playerPresenters.get(player));
		}
	}

	@Override
	public boolean playerWantsToAttachCardToCard(AbstractPlayerNotifier player,
			Card.CardID host, Card.CardID attachee) {
		synchronized(this) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public boolean playerWantsToChangeGameState(AbstractPlayerNotifier player,
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
	public void playerQuitGame(AbstractPlayerNotifier player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerSentMessage(AbstractPlayerNotifier player, String message) {
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
