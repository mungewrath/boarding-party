package core;

public class Effect {
	public static interface EffectCode {
		void exec(Effect parent, Player creator);
	}
	
	private String effectName;
	private Card card;
	private EffectCode code;
	
	public Effect(String name, Card card, EffectCode code) {
		this.effectName = name;
		this.card = card;
		this.code = code;
	}

	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}
	
	public EffectCode getCode() {
		return code;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}
}
