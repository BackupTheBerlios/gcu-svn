package de.mutantenzoo.gcu.ui;

import java.util.SortedSet;


public class Distributor {
	
	private Item[] slots;
	private int minStep;
	private int currentIndex = 0;
	
	public Distributor(int minStep, SortedSet<Item> values) {
		this.minStep = minStep;
		slots = new Item[values.size()];
		for(Item value : values) {
			add(value);
			//System.out.println(value.label+": "+value.exactValue);
		}
	}
/*	
	public void add(int exactValue, String label) {
		int slot = exactValue / minStep;
		if(slots[slot] != null) {
			shl(slot);
			slots[slot] = new Item(label, exactValue, true);
		} else {
			slots[slot] = new Item(label, exactValue, false);
		}
	}
*/
	
	private void add(Item item) {
		slots[currentIndex] = item;
		makeSpace(currentIndex++);
	}
	
	
	private void makeSpace(int slot) {
		if(slot > 0 && slots[slot].exactValue - slots[slot-1].exactValue < minStep) {
			slots[slot].exactValue = slots[slot-1].exactValue + minStep;
		} 
	}
	
	public Item get(int index) {
		return slots[index];
	}
	
	public void print() {
		for(int n=0; n<slots.length; n++) {
			System.out.print(slots[n]+",");
		}
	}
	
	public static interface Mapper {
		int map(double value);
	}
	
	public static class Item implements Comparable<Item> {
		public int exactValue;
		public String label;
		public boolean shifted = false;
		
		public Item(String label, int exactValue, boolean shifted) {
			this.label = label;
			this.exactValue = exactValue;
			this.shifted = shifted;
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

	public int size() {
		return currentIndex;
	}
}
