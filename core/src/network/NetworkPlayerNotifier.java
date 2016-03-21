package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import core.Card;
import core.Effect;
import core.Player;
import core.Resource;
import core.Slot;
import presenter.IPlayerNotifier;
import presenter.PlayerNotifierException;
import network.INetworkAdapter.INetworkAdapterListener;

public class NetworkPlayerNotifier implements IPlayerNotifier, INetworkAdapterListener {
	private IPlayerNotifierListener listener;
	private INetworkAdapter adapter;
	private String playerTag;
	
	public NetworkPlayerNotifier(INetworkAdapter adapter, String tag) throws PlayerNotifierException {
		playerTag = tag;
		try {
			adapter.subscribeListener(this, tag);
			this.adapter = adapter;
		} catch(NetworkException ex) {
			throw new PlayerNotifierException("Unable to bind to network adapter with tag "+tag);
		}
	}
	
	protected enum NetworkMessageCode {
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

	@Override
	public void messageReceived(List<String> contents, String userCode) throws NetworkException {
		String codeLine;
		try {
			Iterator<String> iter = contents.iterator();
			codeLine = iter.next();
			
			System.out.println("<<<Received message code "+codeLine);
		
			NetworkMessageCode messageCode = NetworkMessageCode.valueOf(codeLine); 
			
			Card.CardID cid;
			Slot.SlotID sid;
			Effect.EffectID eid;
			Resource.ResourceID rid;
			Player.PlayerID pid;
		
			switch(messageCode) {
				case CODE_TURN_CHANGED:
					String newTurnState = iter.next();
					listener.playerWantsToChangeGameState(this, newTurnState);
					break;
				case CODE_CARD_DESTROYED:
					break;
				case CODE_EFFECT_ANNOUNCED:
					eid = readEffectID(iter);
					listener.playerWantsToUseCardEffect(this, eid);
					break;
				case CODE_EFFECT_TRIGGERED:
					eid = readEffectID(iter);
					listener.playerTriggeredCardEffect(this, eid);
					break;
				case CODE_SLOT_ADDED:
					break;
				case CODE_CARD_ADDED_TO_SLOT:
					break;
				case CODE_CARD_MOVED_TO_SLOT:
					break;
				case CODE_CARD_ADDED_TO_CARD:
					break;
				case CODE_CARD_ATTACHED_TO_CARD:
					break;
				case CODE_RESOURCE_CREATED:
					break;
				case CODE_RESOURCE_AMOUNT_CHANGED:
					rid = readResourceID(iter);
					int amount = Integer.valueOf(iter.next());
					listener.resourceAmountChanged(rid, amount);
					break;
				case CODE_GLOBAL_EFFECT_ADDED:
					break;
				case CODE_PLAYER_QUIT_GAME:
					listener.playerQuitGame(this);
					break;
				case CODE_PLAYER_SENT_MESSAGE:
					String playerMessage = iter.next();
					listener.playerSentMessage(this, playerMessage);
					break;
				case CODE_DISCONNECTED:
					break;
				default:
					throw new NetworkException("Unknown message code", NetworkException.NetworkErrorCode.NERR_RECEIVER_REJECTED_MESSAGE);
			}
		} catch(IOException ex) {
			throw new NetworkException("Error reading message: "+ex.getMessage(), NetworkException.NetworkErrorCode.NERR_NETWORK_ERROR);
		}
	}
	
	private void writePlayerID(Player.PlayerID pid, StringBuilder out) {
		if(pid != null) {
			out.append(pid.name + "\n");
		} else {
			out.append("\n");
		}
	}
	private Player.PlayerID readPlayerID(Iterator<String> iter) throws IOException {
		String name = iter.next();
		if(!name.equals("")) {
			return new Player.PlayerID(name);
		} else {
			return null;
		}
	}
	
	private void writeSlotID(Slot.SlotID sid, StringBuilder out) {
		if(sid != null) {
			out.append(sid.name + "\n");
			writePlayerID(sid.playerID, out);
		} else {
			out.append("\n");
			writePlayerID(null, out);
		}
	}
	private Slot.SlotID readSlotID(Iterator<String> iter) throws IOException {
		String name = iter.next();
		if(!name.equals("")) {
			Player.PlayerID pid = readPlayerID(iter);
			return new Slot.SlotID(name, pid);
		} else {
			return null;
		}
	}
	
	private void writeCardID(Card.CardID cid, StringBuilder out) {
		if(cid != null) {
			out.append(cid.name + "\n");
			out.append(cid.slotNum + "\n");
			writeSlotID(cid.slot, out);
			out.append(cid.cardIndex + "\n");
			writeCardID(cid.parentCard, out);
		} else {
			out.append("\n");
		}
	}
	private Card.CardID readCardID(Iterator<String> iter) throws IOException {
		String name = iter.next();
		if(!name.equals("")) {
			int slotNum = Integer.valueOf(iter.next());
			Slot.SlotID sid = readSlotID(iter);
			int cardIndex = Integer.valueOf(iter.next());
			Card.CardID cid = readCardID(iter);
			return new Card.CardID(name, sid, slotNum, cid, cardIndex);
		} else {
			return null;
		}
	}
	
	private void writeEffectID(Effect.EffectID eid, StringBuilder out) {
		out.append(eid.name + "\n");
		writeCardID(eid.card, out);
	}
	private Effect.EffectID readEffectID(Iterator<String> iter) throws IOException {
		String name = iter.next();
		Card.CardID cid = readCardID(iter);
		return new Effect.EffectID(name, cid);
	}
	
	private void writeResourceID(Resource.ResourceID rid, StringBuilder out) {
		out.append(rid.name + "\n");
		writePlayerID(rid.owner, out);
	}
	private Resource.ResourceID readResourceID(Iterator<String> iter) throws IOException {
		String name = iter.next();
		Player.PlayerID pid = readPlayerID(iter);
		return new Resource.ResourceID(pid, name);
	}
	
	private StringBuilder beginMessage(NetworkMessageCode code) {
		return new StringBuilder(code.name() + "\n");
	}

	@Override
	public void turnStateChanged(String newState) {
		StringBuilder message = beginMessage(NetworkMessageCode.CODE_TURN_CHANGED);
		message.append(newState + "\n");
		adapter.sendMessage(message.toString(), false);
	}

	@Override
	public void cardDestroyed(Card c) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void effectAnnounced(Effect e) {
		StringBuilder message = beginMessage(NetworkMessageCode.CODE_EFFECT_ANNOUNCED);
		writeEffectID(e.getID(), message);
		adapter.sendMessage(message.toString(), false);
	}

	@Override
	public void effectTriggered(Effect e) {
		StringBuilder message = beginMessage(NetworkMessageCode.CODE_EFFECT_TRIGGERED);
		writeEffectID(e.getID(), message);
		adapter.sendMessage(message.toString(), false);
	}

	@Override
	public void slotAdded(Slot s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardAddedToSlot(Card c, Slot s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardMovedToSlot(Card c, Slot s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardAddedToCard(Card host, Card parasite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardAttachedToCard(Card host, Card parasite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resourceCreated(Resource r) {
		StringBuilder message = beginMessage(NetworkMessageCode.CODE_RESOURCE_CREATED);
		writeResourceID(r.getID(), message);
		adapter.sendMessage(message.toString(), false);
	}

	@Override
	public void resourceAmountChanged(Resource r, int oldAmount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void globalEffectAdded(Effect e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerQuitGame(Player player) {
		StringBuilder message = beginMessage(NetworkMessageCode.CODE_PLAYER_QUIT_GAME);
		adapter.sendMessage(message.toString(), false);
	}

	@Override
	public void playerSentMessage(Player player, String message) {
		StringBuilder messageBody = beginMessage(NetworkMessageCode.CODE_PLAYER_SENT_MESSAGE);
		messageBody.append(messageBody + "\n");
		adapter.sendMessage(messageBody.toString(), false);
	}

	@Override
	public void disconnectedFromGame(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribeListener(IPlayerNotifierListener listener) {
		this.listener = listener; 
	}
}
