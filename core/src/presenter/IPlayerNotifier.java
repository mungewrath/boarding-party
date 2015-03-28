package presenter;

public interface IPlayerNotifier {
	public void turnStateChanged(String newState);

	public interface IPlayerNotifierListener {
		boolean playerWantsToUseCardEffect(IPlayerNotifier player);
		boolean playerWantsToMoveCardToSlot(IPlayerNotifier player);
		boolean playerWantsToAttachCardToCard(IPlayerNotifier player);
		boolean playerWantsToChangeGameState(IPlayerNotifier player, String newState);
		
		// Out-of-game events
		void playerQuitGame(IPlayerNotifier player);
		void playerSentMessage(IPlayerNotifier player, String message);
	}
}
