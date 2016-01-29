package core;

import core.Card.CardID;

public class Effect {
	public static interface EffectCondition {
		boolean check(Effect parent, Player creator);
	}
	
	public static interface EffectCode {
		void exec(Effect parent, Player creator);
	}
	
	private String effectName;
	private Card hostCard;
	private EffectCondition condition;
	private EffectCode code;
	
	public static class EffectID {
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
	}
	
	public Effect(String name, Card card, EffectCondition cond, EffectCode code) {
		this.effectName = name;
		this.hostCard = card;
		this.condition = cond;
		this.code = code;
	}
	
	
	public EffectID getID() {
		return new EffectID(effectName, (hostCard != null ? hostCard.getID() : null));
	}

	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}
	
	public EffectCondition getCondition() {
		return condition;
	}
	
	public EffectCode getCode() {
		return code;
	}

	public Card getCard() {
		return hostCard;
	}

	public void setCard(Card card) {
		this.hostCard = card;
	}
}
