package presenter;

import core.Card;
import core.Effect;
import core.Resource;
import core.Slot;

public interface IPlayerNotifier {
	public void turnStateChanged(String newState);
	public void slotAdded(Slot s);
	
	// Resources
	public void resourceAdded(Resource r);
	public void resourceAmountChanged(Resource r, int oldAmount);
	
	// Global effects
	public void globalEffectAdded(Effect e);
	
	// Subscriber methods
	public void subscribeListener(IPlayerNotifierListener listener);

	public interface IPlayerNotifierListener {
		boolean playerWantsToUseCardEffect(IPlayerNotifier player, Effect e);
		boolean playerWantsToMoveCardToSlot(IPlayerNotifier player);
		boolean playerWantsToAttachCardToCard(IPlayerNotifier player, Card host, Card attachee);
		boolean playerWantsToChangeGameState(IPlayerNotifier player, String newState);
		
		// Out-of-game events
		void playerQuitGame(IPlayerNotifier player);
		void playerSentMessage(IPlayerNotifier player, String message);
	}
}
