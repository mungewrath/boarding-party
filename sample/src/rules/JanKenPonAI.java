package rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.Effect;
import core.Resource;
import core.Slot;
import presenter.IPlayerNotifier;

public class JanKenPonAI implements IPlayerNotifier {
	List<Effect> possibleMoves = new ArrayList<Effect>();
	IPlayerNotifierListener listener;

	@Override
	public void resourceAdded(Resource arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resourceAmountChanged(Resource arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void slotAdded(Slot arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnStateChanged(String newState) {
		if(newState.equals(JanKenPonGameRules.JanKenPonState.STATE_CHOOSE_TYPE.toString())) {
			Random r = new Random();
			int choice = r.nextInt(possibleMoves.size());
			System.out.println("Al plays " + possibleMoves.get(choice).getEffectName());
			listener.playerWantsToUseCardEffect(this, possibleMoves.get(choice));
		} else if(newState.equals(JanKenPonGameRules.JanKenPonState.STATE_PLAYER_1_WON.toString())) {
			System.out.println("Al: Gah! You just got lucky, human. I'll get you next time!");
		} else {
			System.out.println("Al: Hah! Bask in my superior glory, puny mortal!");
		}
	}

	@Override
	public void globalEffectAdded(Effect e) {
		possibleMoves.add(e);
	}

	@Override
	public void subscribeListener(IPlayerNotifierListener listener) {
		// TODO Auto-generated method stub
		this.listener = listener;
	}

}
