package core;

public class Effect {
	private String effectName;
	private Card card;
	
	interface EffectCode {
		void exec(Effect parent, Player creator);
	}
}
