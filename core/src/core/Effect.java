package core;

import core.Card.CardID;

public class Effect {
	public static interface EffectCode {
		void exec(Effect parent, Player creator);
	}
	
	private String effectName;
	private Card card;
	private EffectCode code;
	
	public class EffectID {
		public String name;
		public CardID card;
		
		public EffectID(String name, CardID card) {
			super();
			this.name = name;
			this.card = card;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((card == null) ? 0 : card.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EffectID other = (EffectID) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (card == null) {
				if (other.card != null)
					return false;
			} else if (!card.equals(other.card))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private Effect getOuterType() {
			return Effect.this;
		}
	}
	
	public Effect(String name, Card card, EffectCode code) {
		this.effectName = name;
		this.card = card;
		this.code = code;
	}
	
	
	public EffectID getID() {
		return new EffectID(effectName, (card != null ? card.getID() : null));
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
