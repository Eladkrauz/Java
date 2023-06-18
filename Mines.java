package mines;

import java.util.HashSet;
import java.util.Random;

public class Mines {
	// an inner class for location on the board
	private class Location {
		private int x, y;

		public Location(int x, int y) { // Constructor for Location class
			this.x = x;
			this.y = y;
		}

		public HashSet<Location> neighboors() { // returns a set of the neighboors
			HashSet<Location> set = new HashSet<>();
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (i != x || j != y) { // not this location
						if (i >= 0 && i < height && j >= 0 && j < width) { // if in the bounds
							set.add(new Location(i, j)); // adding to the set
						}
					}
				}
			}
			return set;
		}
	}

	// an inner class for a spot on the board
	class Spot {
		private Location location;
		private boolean open, flag, mine;
		private int around;

		public Spot(Location location, boolean open, boolean flag, boolean mine) { // Constructor for Spot class
			this.location = location;
			this.open = open;
			this.flag = flag;
			this.mine = mine;
		}

		// sets the around number of a spot
		public void setAround(int around) {
			this.around = around;
		}

		// returns if there is a flag on this spot
		public boolean hasFlag() {
			return flag;
		}

		// returns if the spot is open
		public boolean isOpen() {
			return open;
		}

		// returns a string representation of a spot
		@Override
		public String toString() {
			if (mine) {
				return "X";
			} else {
				return around == 0 ? " " : around + "";
			}
		}
	}

	private int height, width;
	private Spot[][] board;
	public static Random random = new Random();
	private boolean showAll = false;

	public Mines(int height, int width, int numMines) { // Constructor
		this.height = height;
		this.width = width;
		// creating the board
		int i, j;
		board = new Spot[height][width];
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				board[i][j] = new Spot(new Location(i, j), false, false, false);
			}
		}

		// adding the mines randomly
		for (i = 0; i < numMines; i++) {
			if (!addMine(random.nextInt(height), random.nextInt(width))) {
				i--; // if the random location is already a mine, will have another try
			}
		}
	}

	// this method checks if the spot is already a mine, if not - sets as a mine
	public boolean addMine(int i, int j) {
		if (board[i][j].mine == true) { // already a mine
			return false;
		} else {
			board[i][j].mine = true; // set this spot as a mine
			updateAroundSpots();
			return true;
		}
	}

	// sets mines 'around' number for each spot on the board
	private void updateAroundSpots() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int around = 0;
				HashSet<Location> set = board[i][j].location.neighboors(); // returns a set of neighboors
				for (Location loc : set) { // running on the set
					if (board[loc.x][loc.y].mine == true) {
						around++;
					}
				}
				board[i][j].setAround(around);
			}
		}
	}

	// this method opens a spot. if it is a mine - returns false
	// otherwise - returns true and opens all the neighboors
	public boolean open(int i, int j) {
		if (i < 0 || j < 0 || i >= height || j >= width || board[i][j].open || board[i][j].mine)
			return false;
		else {
			board[i][j].open = true;
			if (board[i][j].around == 0) { // if it has no mines around it
				openNeighboors(i, j); // also opens all its neighboors
			}
			return true;
		}
	}

	// a recursive help method for open. Opens the given spot and all its
	// neighboors only if they are not mines and have not been opened yet.
	private void openNeighboors(int i, int j) {
		HashSet<Location> set = board[i][j].location.neighboors();
		for (Location loc : set) {
			if (!board[loc.x][loc.y].open && !board[loc.x][loc.y].mine) { // not open and not a mine
				board[loc.x][loc.y].open = true; // open the spot
				if (board[loc.x][loc.y].around == 0) {// if there are no mines next to the place
					openNeighboors(loc.x, loc.y);
				}
			}
		}
	}

	// this method sets/unsets a flag in a specific location
	public void toggleFlag(int x, int y) {
		board[x][y].flag = board[x][y].flag == false ? true : false;
	}

	// returns true if all the non-mine spots are open, otherwise false
	public boolean isDone() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (!board[i][j].mine && !board[i][j].open) // not a mine and not open
					return false;
			}
		}
		return true;
	}

	// returns a spot
	public Spot getSpot(int i, int j) {
		return board[i][j];
	}

	// returns a string representation of a spot
	public String get(int i, int j) {
		// return cell string if it is in bounds of board, otherwise return empty string
		if (i >= 0 && i < height && j >= 0 && j < width) {
			Spot curr = board[i][j];
			if (showAll) { // if showAll is true >> show all the values
				return curr.toString();
			} else {
				if (curr.open) {
					return curr.toString();
				} else if (curr.flag) {
					return "F";
				} else {
					return ".";
				}
			}
		} else
			return "";
	}

	// sets the showAll field
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	// returns a string representation of a board
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		int i, j;
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				ret.append(get(i, j));
			}
			ret.append("\n");
		}
		return ret.toString();
	}
}