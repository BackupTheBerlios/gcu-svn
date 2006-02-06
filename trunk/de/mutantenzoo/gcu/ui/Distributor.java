package de.mutantenzoo.gcu.ui;

import java.util.SortedSet;

/**
 * Helper class to implement an algorithm
 * to distribute labels on an axis so that
 * the do not overlap but at the same time
 * are as close as possible to the item they
 * are a label for.
 * @author mklemm
 *
 */
public class Distributor {
	
	private Item[] slots;
	private int minStep;
	private int currentIndex = 0;
	
	/**
	 * Initializes the Distributor
	 * @param minStep Minimum spacing between labels
	 * @param values The values that are the items that should be labeled
	 */
	public Distributor(int minStep, SortedSet<Item> values) {
		this.minStep = minStep;
		slots = new Item[values.size()];
		for(Item value : values) {
			add(value);
		}
	}
	
	/**
	 * Add an item in order of appearance
	 * @param item
	 */
	private void add(Item item) {
		slots[currentIndex] = item;
		makeSpace(currentIndex++);
	}
	
	/**
	 * If necessary, move the current item
	 * to the right so that it doesn't overlap
	 * with the item to the left of it.
	 * @param slot The index of the currently added item.
	 */
	private void makeSpace(int slot) {
		if(slot > 0 && slots[slot].exactValue - slots[slot-1].exactValue < minStep) {
			slots[slot].exactValue = slots[slot-1].exactValue + minStep;
		} 
	}
	
	/**
	 * Gets the Item at the specified index
	 * @param index The index of the item to get
	 * @return The item at the specifeid index.
	 */
	public Item get(int index) {
		return slots[index];
	}
	
	/**
	 * Prints the current object state
	 * (for debugging purposes
	 */
	public void print() {
		for(int n=0; n<slots.length; n++) {
			System.out.print(slots[n]+",");
		}
	}
	
	/**
	 * Represents an Item that can be added
	 * to this Distributor sequence.
	 * @author mklemm
	 *
	 */
	public static class Item implements Comparable<Item> {
		public int exactValue;
		public String label;
		
		public Item(String label, int exactValue) {
			this.label = label;
			this.exactValue = exactValue;
		}

		public int compareTo(Item arg0) {
			if(exactValue > arg0.exactValue) {
				return 1;
			} else if(exactValue < arg0.exactValue) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Gets the number of items in this Distributor
	 * @return The number of items in this Distributor
	 */
	public int size() {
		return currentIndex;
	}
}
