package network;

import java.io.BufferedReader;
import java.io.IOException;

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
	
	public NetworkPlayerNotifier(INetworkAdapter adapter, String tag) throws PlayerNotifierException {
		try {
			adapter.subscribeListener(this, tag);
		} catch(NetworkException ex) {
			throw new PlayerNotifierException("Unable to bind to network adapter with tag "+tag);
		}
	}
	
	protected enum NetworkMessageCode {
		CODE_TURN_CHANGED,
		CODE_CARD_DESTROYED,
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
	public void messageReceived(BufferedReader in, String userCode) throws NetworkException {
		String codeLine;
		try {
			codeLine = in.readLine();
		
			NetworkMessageCode messageCode = NetworkMessageCode.valueOf(codeLine); 
		
			switch(messageCode) {
				case CODE_TURN_CHANGED:
					String newTurnState = in.readLine();
					listener.playerWantsToChangeGameState(this, newTurnState);
					break;
				case CODE_CARD_DESTROYED:
					break;
				case CODE_EFFECT_TRIGGERED:
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
					break;
				case CODE_GLOBAL_EFFECT_ADDED:
					break;
				case CODE_PLAYER_QUIT_GAME:
					listener.playerQuitGame(this);
					break;
				case CODE_PLAYER_SENT_MESSAGE:
					String playerMessage = in.readLine();
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

	@Override
	public void turnStateChanged(String newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardDestroyed(Card c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void effectTriggered(Effect e) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerSentMessage(Player player, String message) {
		// TODO Auto-generated method stub
		
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
