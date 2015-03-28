package core;

import java.util.List;

public class Card {
	private List<Effect> effects;
	private Player owner;
	private Player controller;
	private List<Card> attachedCards;
	
	// Is card face-down or face-up?
	private boolean hidden;
}
