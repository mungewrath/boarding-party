package core;

import java.util.List;

import core.Player.PlayerID;

public class Slot {
	private List<Card> cards;
	private Player owner;
	private String name;
	
	public static class SlotID {
		public String name;
		public PlayerID playerID;
		
		public SlotID(String name, PlayerID pid) {
			this.name = name;
			this.playerID = pid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((playerID == null) ? 0 : playerID.hashCode());
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
			SlotID other = (SlotID) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (playerID == null) {
				if (other.playerID != null)
					return false;
			} else if (!playerID.equals(other.playerID))
				return false;
			return true;
		}
	}
	
	public SlotID getID() {
		return new SlotID(name, owner.getID());
	}
	
	public int getCardIndex(Card c) {
		return cards.indexOf(c);
	}

	public Card getCardAtIndex(int index) {
		return cards.get(index);
	}
}
