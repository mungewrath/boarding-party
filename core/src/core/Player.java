package core;

import presenter.IPlayerNotifier;


public class Player {
	private String name;
	private IPlayerNotifier notifier;

	public Player(String name, IPlayerNotifier notifier) {
		this.name = name;
		this.notifier = notifier;
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
