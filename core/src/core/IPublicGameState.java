package core;

import java.util.List;

/**
 * Created by matthew.unrath on 3/21/2016.
 */
public interface IPublicGameState {
    List<Effect> getAllGlobalEffects();
    int getPlayerCount();
    Player getPlayer(int num);
    Resource getResourceForPlayer(Player p, String id);
    boolean isMaster();
}
