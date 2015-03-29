package core;

import java.util.Set;

/**
 * Represents a generic countable resource, such coins.
 * @author matthew.unrath
 *
 */
public class Resource {
	private Player owner;
	private int quantity;
	private Set<String> types;
	private String name;
	
	public static class ResourceID {
		public Player owner;
		public String name;
		
		public ResourceID(Player owner, String name) {
			this.owner = owner;
			this.name = name;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other.getClass() == ResourceID.class) {
				ResourceID o = (ResourceID)other;
				return (o.owner.equals(owner) && o.name.equals(name));
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return 5*owner.getName().length() + 23*name.length();
		}
	}
	
	public Resource(String name, Player owner) {
		this.name = name;
		this.owner = owner;
		quantity = 0;
	}
	
	public ResourceID getID() {
		return new ResourceID(owner, name);
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity; 
	}
	
	public int getQuantity() {
		return quantity;
	}
}
