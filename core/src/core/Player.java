package core;

import presenter.IPlayerNotifier;


public class Player {
	private String name;
	private IPlayerNotifier notifier;
	
	public static class PlayerID {
		public String name;
		
		public PlayerID(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
			PlayerID other = (PlayerID) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}

	public Player(String name, IPlayerNotifier notifier) {
		this.name = name;
		this.notifier = notifier;
	}
	
	public PlayerID getID() {
		return new PlayerID(name);
	}
	
	public String getName() {
		return name;
	}
	public IPlayerNotifier getNotifier() {
		return notifier;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other.getClass() == Player.class) {
			Player o = (Player)other;
			return o.getName().equals(name);
		} else {
			return false;
		}
	}
}
