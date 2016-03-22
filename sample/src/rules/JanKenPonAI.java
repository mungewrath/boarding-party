package rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.Effect;
import presenter.AbstractPlayerNotifier;

public class JanKenPonAI extends AbstractPlayerNotifier {
	List<Effect> possibleMoves = new ArrayList<Effect>();

	@Override
	protected void turnStateChanged_impl(String newState, String oldState) {
		System.out.println("Starting AI sleep");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newState.equals(JanKenPonGameRules.JanKenPonState.STATE_CHOOSE_TYPE.toString())) {
			Random r = new Random();
			int choice = r.nextInt(possibleMoves.size());
			System.out.println("Al plays " + possibleMoves.get(choice).getEffectName());
			listener.playerWantsToUseCardEffect(this, possibleMoves.get(choice).getID());
		} else if(newState.equals(JanKenPonGameRules.JanKenPonState.STATE_PLAYER_1_WON.toString())) {
			System.out.println("Al: Gah! You just got lucky, human. I'll get you next time!");
		} else {
			System.out.println("Al: Hah! Bask in my superior glory, puny mortal!");
		}
	}

	@Override
	protected void globalEffectAdded_impl(Effect e) {
		possibleMoves.add(e);
	}


}
