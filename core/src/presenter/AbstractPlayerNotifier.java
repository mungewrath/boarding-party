package presenter;

import core.Card;
import core.Effect;
import core.Player;
import core.Resource;
import core.Slot;

public abstract class AbstractPlayerNotifier {
    // Typically the game state, waiting for requests
    protected IPlayerNotifierListener listener;

    public final void turnStateChanged(String newState, String oldState) {
        Thread t = new Thread(
                () -> turnStateChanged_impl(newState, oldState)
        );
        t.start();
    }
    protected void turnStateChanged_impl(String newState, String oldState) {}

    // Cards
    public final void cardDestroyed(Card c) {
        Thread t = new Thread(
                () -> cardDestroyed_impl(c)
        );
        t.start();
    }

    protected void cardDestroyed_impl(Card c) {}

    // Effects
    public final void effectAnnounced(Effect e) {
        Thread t = new Thread(
                () -> effectAnnounced_impl(e)
        );
        t.start();
    }

    protected void effectAnnounced_impl(Effect e) {}

    public final void effectTriggered(Effect e) {
        Thread t = new Thread(
                () -> effectTriggered_impl(e)
        );
        t.start();
    }

    protected void effectTriggered_impl(Effect e) {}

    // Slots
    public final void slotAdded(Slot s) {
        Thread t = new Thread(
                () -> slotAdded_impl(s)
        );
        t.start();
    }

    protected void slotAdded_impl(Slot s) {}

    public final void cardAddedToSlot(Card c, Slot s) {
        Thread t = new Thread(
                () -> cardAddedToSlot_impl(c, s)
        );
        t.start();
    }

    protected void cardAddedToSlot_impl(Card c, Slot s) {}

    public final void cardMovedToSlot(Card c, Slot s) {
        Thread t = new Thread(
                () -> cardMovedToSlot_impl(c, s)
        );
        t.start();
    }

    protected void cardMovedToSlot_impl(Card c, Slot s) {}

    public final void cardAddedToCard(Card host, Card parasite) {
        Thread t = new Thread(
                () -> cardAddedToCard_impl(host, parasite)
        );
        t.start();
    }

    protected void cardAddedToCard_impl(Card host, Card parasite) {}

    public final void cardAttachedToCard(Card host, Card parasite) {
        Thread t = new Thread(
                () -> cardAttachedToCard_impl(host, parasite)
        );
        t.start();
    }

    protected void cardAttachedToCard_impl(Card host, Card parasite) {}

    // Resources
    public final void resourceCreated(Resource r) {
        Thread t = new Thread(
                () -> resourceCreated_impl(r)
        );
        t.start();
    }

    protected void resourceCreated_impl(Resource r) {}

    public final void resourceAmountChanged(Resource r, int oldAmount) {
        Thread t = new Thread(
                () -> resourceAmountChanged_impl(r, oldAmount)
        );
        t.start();
    }

    protected void resourceAmountChanged_impl(Resource r, int oldAmount) {}

    // Global effects
    public final void globalEffectAdded(Effect e) {
        Thread t = new Thread(
                () -> globalEffectAdded_impl(e)
        );
        t.start();
    }

    protected void globalEffectAdded_impl(Effect e) {}

    // Out-of-game events
    public final void playerQuitGame(Player player) {
        Thread t = new Thread(
                () -> playerQuitGame_impl(player)
        );
        t.start();
    }

    protected void playerQuitGame_impl(Player player) {}

    public final void playerSentMessage(Player player, String message) {
        Thread t = new Thread(
                () -> playerSentMessage_impl(player, message)
        );
        t.start();
    }

    protected void playerSentMessage_impl(Player player, String message) {}

    public final void disconnectedFromGame(String message) {
        Thread t = new Thread(
                () -> disconnectedFromGame_impl(message)
        );
        t.start();
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
