package core;

import java.util.List;

import core.Slot.SlotID;

public class Card {
	private String name;
	private List<Effect> effects;
	private Player owner;
	private Player controller;
	private Card parentCard;
	private List<Card> attachedCards;
	private Slot slot;
	
	// Is card face-down or face-up?
	private boolean hidden;
	
	public static class CardID {
		public String name;
		public SlotID slot;
		public int slotNum;
		public CardID parentCard;
		public int cardIndex;
		
		public CardID(String name, SlotID slot, int slotNum, CardID parentCard, int cardIdx) {
			this.slot = slot;
			this.slotNum = slotNum;
			this.parentCard = parentCard;
			this.cardIndex = cardIdx;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cardIndex;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((parentCard == null) ? 0 : parentCard.hashCode());
			result = prime * result + ((slot == null) ? 0 : slot.hashCode());
			result = prime * result + slotNum;
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
			CardID other = (CardID) obj;
			if (cardIndex != other.cardIndex)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (parentCard == null) {
				if (other.parentCard != null)
					return false;
			} else if (!parentCard.equals(other.parentCard))
				return false;
			if (slot == null) {
				if (other.slot != null)
					return false;
			} else if (!slot.equals(other.slot))
				return false;
			if (slotNum != other.slotNum)
				return false;
			return true;
		}
	}
	
	public CardID getID() {
		return new CardID(name,
				(slot != null ? slot.getID() : null), (slot != null ? slot.getCardIndex(this) : 0), 
				(parentCard != null ? parentCard.getID() : null), (parentCard != null ? parentCard.getCardIndex(this) : 0));
	}
	
	public String getName() {
		return name;
	}
	
	public int getCardIndex(Card c) {
		return attachedCards.indexOf(c);
	}
	
	public Card getChild(int index) {
		return attachedCards.get(index);
	}
	
	public Effect getEffect(String name) {
		for(Effect e : effects) {
			if(e.getEffectName().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
