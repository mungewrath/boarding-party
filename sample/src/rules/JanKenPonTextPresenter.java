package rules;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import core.Effect;
import core.IPublicGameState;
import presenter.AbstractPlayerNotifier;

public class JanKenPonTextPresenter extends AbstractPlayerNotifier {
	public IPublicGameState state;
	private static Scanner input = new Scanner(System.in);

	@Override
	protected void turnStateChanged_impl(String newState, String oldState) {
		System.out.println("Intercepted state:" + newState);
		if(newState.equals(JanKenPonGameRules.JanKenPonState.STATE_CHOOSE_TYPE.toString())) {
			if(!newState.equals(oldState)) {
				try {
					chooseMove();
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		} else if(newState.startsWith(JanKenPonGameRules.JanKenPonState.STATE_PLAYER_1_WON.toString())) {
			System.out.println("You won!");
			listener.playerQuitGame(this);
		} else {
			System.out.println("Oh no, the AI won!");
			listener.playerQuitGame(this);
		}
	}
	
	private void chooseMove() throws Exception {
        System.out.println("Choose your move:");
        List<Effect> moves = state.getAllGlobalEffects();
        for(int i = 0; i < moves.size(); i++) {
            System.out.println(i + ") " + moves.get(i).getEffectName());
        }
        int choice = -1;
        do {
            try {
                choice = input.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Invalid choice.");
            }
        }
        while(choice > moves.size() || choice < 0);
        if(!listener.playerWantsToUseCardEffect(JanKenPonTextPresenter.this, moves.get(choice).getID())) {
            System.out.println("!!!! Presenter couldn't use card!");
        }
	}

}
