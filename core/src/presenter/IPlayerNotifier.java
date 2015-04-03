package presenter;

import core.Card;
import core.Effect;
import core.Player;
import core.Resource;
import core.Slot;

public interface IPlayerNotifier {
	public void turnStateChanged(String newState);
	
	// Cards
	public void cardDestroyed(Card c);
	
	// Effects
	public void effectTriggered(Effect e);
	
	// Slots
	public void slotAdded(Slot s);
	public void cardAddedToSlot(Card c, Slot s);
	public void cardMovedToSlot(Card c, Slot s);
	public void cardAddedToCard(Card host, Card parasite);
	public void cardAttachedToCard(Card host, Card parasite);
	
	// Resources
	public void resourceCreated(Resource r);
	public void resourceAmountChanged(Resource r, int oldAmount);
	
	// Global effects
	public void globalEffectAdded(Effect e);
	
	// Out-of-game events
	void playerQuitGame(Player player);
	void playerSentMessage(Player player, String message);
	void disconnectedFromGame(String message);
	
	// Subscriber methods
	public void subscribeListener(IPlayerNotifierListener listener);

	public interface IPlayerNotifierListener {
		boolean playerWantsToUseCardEffect(IPlayerNotifier player, Effect.EffectID e);
		boolean playerWantsToMoveCardToSlot(IPlayerNotifier player, Card.CardID card, Slot.SlotID slot);
		boolean playerWantsToAttachCardToCard(IPlayerNotifier player, Card.CardID host, Card.CardID attachee);
		boolean playerWantsToChangeGameState(IPlayerNotifier player, String newState);
		
		// Out-of-game events
		void playerQuitGame(IPlayerNotifier player);
		void playerSentMessage(IPlayerNotifier player, String message);
		void disconnectedFromGame(String message);
	}
}
