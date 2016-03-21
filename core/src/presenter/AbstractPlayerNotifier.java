package presenter;

import core.Card;
import core.Effect;
import core.Player;
import core.Resource;
import core.Slot;

public abstract class AbstractPlayerNotifier {
    // Typically the game state, waiting for requests
    protected IPlayerNotifierListener listener;
    
	public final void turnStateChanged(String newState) {
        synchronized (this) {
            turnStateChanged_impl(newState);
        }
    }
    protected void turnStateChanged_impl(String newState) {}
	
	// Cards
	public final void cardDestroyed(Card c) {
        synchronized (this) {
            cardDestroyed_impl(c);
        }
    }
    protected void cardDestroyed_impl(Card c) {}

    // Effects
	public final void effectAnnounced(Effect e) {
        synchronized (this) {
            effectAnnounced_impl(e);
        }
    }
    protected void effectAnnounced_impl(Effect e) {}
	public final void effectTriggered(Effect e) {
        synchronized (this) {
            effectTriggered_impl(e);
        }
    }
    protected void effectTriggered_impl(Effect e) {}
	
	// Slots
	public final void slotAdded(Slot s) {
        synchronized (this) {
            slotAdded_impl(s);
        }
    }
    protected void slotAdded_impl(Slot s) {}
	public final void cardAddedToSlot(Card c, Slot s) {
        synchronized (this) {
            cardAddedToSlot_impl(c, s);
        }
    }
    protected void cardAddedToSlot_impl(Card c, Slot s) {}
	public final void cardMovedToSlot(Card c, Slot s) {
        synchronized (this) {
            cardMovedToSlot_impl(c, s);
        }
    }
    protected void cardMovedToSlot_impl(Card c, Slot s) {}
	public final void cardAddedToCard(Card host, Card parasite) {
        synchronized (this) {
            cardAddedToCard_impl(host, parasite);
        }
    }
    protected void cardAddedToCard_impl(Card host, Card parasite) {}
	public final void cardAttachedToCard(Card host, Card parasite) {
        synchronized (this) {
            cardAttachedToCard_impl(host, parasite);
        }
    }
    protected void cardAttachedToCard_impl(Card host, Card parasite) {}
	
	// Resources
	public final void resourceCreated(Resource r) {
        synchronized (this) {
            resourceCreated_impl(r);
        }
    }
    protected void resourceCreated_impl(Resource r) {}
	public final void resourceAmountChanged(Resource r, int oldAmount) {
        synchronized (this) {
            resourceAmountChanged_impl(r, oldAmount);
        }
    }
    protected void resourceAmountChanged_impl(Resource r, int oldAmount) {}
	
	// Global effects
	public final void globalEffectAdded(Effect e) {
        synchronized (this) {
            globalEffectAdded_impl(e);
        }
    }
    protected void globalEffectAdded_impl(Effect e) {}
	
	// Out-of-game events
	public final void playerQuitGame(Player player) {
        synchronized (this) {
            playerQuitGame_impl(player);
        }
    }
    protected void playerQuitGame_impl(Player player) {}
	public final void playerSentMessage(Player player, String message) {
        synchronized (this) {
            playerSentMessage_impl(player, message);
        }
    }
    protected void playerSentMessage_impl(Player player, String message) {}
	public final void disconnectedFromGame(String message) {
        synchronized (this) {
            disconnectedFromGame_impl(message);
        }
    }
    protected void disconnectedFromGame_impl(String message) {}
	
	// Subscriber methods
	public void subscribeListener(IPlayerNotifierListener listener) {
        this.listener = listener;
    }

	public interface IPlayerNotifierListener {
		// Effects
		boolean playerWantsToUseCardEffect(AbstractPlayerNotifier player, Effect.EffectID e);
		boolean playerTriggeredCardEffect(AbstractPlayerNotifier player, Effect.EffectID e);
		boolean playerWantsToMoveCardToSlot(AbstractPlayerNotifier player, Card.CardID card, Slot.SlotID slot);
		boolean playerWantsToAttachCardToCard(AbstractPlayerNotifier player, Card.CardID host, Card.CardID attachee);
		boolean playerWantsToChangeGameState(AbstractPlayerNotifier player, String newState);
		
		
		// Server master mandates
		// These are commands passed from the host of the game and must be carried out
		// by the client
		void resourceAmountChanged(Resource.ResourceID resource, int amount);
		
		// Out-of-game events
		void playerQuitGame(AbstractPlayerNotifier player);
		void playerSentMessage(AbstractPlayerNotifier player, String message);
		void disconnectedFromGame(String message);
	}
}
