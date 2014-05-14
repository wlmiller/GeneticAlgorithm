/* World.java
 * 
 * Represents the 2D "world" in which the evolvers live.  Contains plants
 * and evolvers.  This class also handles movement of the evolvers and
 * evolution.
 */
package GeneticAlgorithm;

import java.util.ArrayList;

public class World {
	private int rows, cols;
	private ArrayList<Evolver> evolvers = new ArrayList<Evolver>();
	private ArrayList<int[]> directions = new ArrayList<int[]>();
	private Cell[][] cells;
	private double mutationRate = 0.01;
	private int numPlants;
	private int clumps;
	//private Boolean display = false;
	
	class plantSource {
		public int x;
		public int y;
		
		public plantSource(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public World() {
		this(20,60);
	}
	
	public World(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		
		this.directions.add(new int[]{1,0});
		this.directions.add(new int[]{0,-1});
		this.directions.add(new int[]{-1,0});
		this.directions.add(new int[]{0,1});

		this.cells = new Cell[rows][cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cells[i][j] = new Cell();
			}
		}
	}
	
	public Cell[][] getCells() {
		return this.cells;
	}
	
	public ArrayList<Evolver> getEvolvers() {
		return evolvers;
	}
	
	public void refreshWorld() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.update(Cell.empty);
            }
        }
		growPlants(this.numPlants, this.clumps);		
	}
	
	public void growPlants(int numPlants, int clumps) {
		this.numPlants = numPlants;
		this.clumps = clumps;
		
		if (numPlants > this.rows*this.cols) {
			numPlants = this.rows*this.cols;
			System.err.println("More plants thant cells -- using " + String.valueOf(numPlants) + ".");
		}
		if (clumps > this.rows*this.cols) clumps = numPlants;
		
		ArrayList<plantSource> plantSources = new ArrayList<plantSource>();
		
		// A number of "sources" are used to localize the plants into clumps.
		for (int i = 0; i < clumps; i++) {
			int x, y;
			x = (int)(Math.random()*this.cols);
			y = (int)(Math.random()*this.rows);
			
			plantSources.add(new plantSource(x,y));
		}

		for (int i = 0; i < numPlants; i++) {
			int source = (int)(Math.random()*clumps);
			plantSource sourcePos = plantSources.get(source);
			int x = sourcePos.x;
			int y = sourcePos.y;

			// Do a random walk from the selected source until an empty cell is found.
			while (cells[y][x].getContents() == Cell.plant) {
				int moveDir = (int)(Math.random()*4);
				switch (moveDir) {
				case 0:
					x += 1;
					break;
				case 1:
					y += 1;
					break;
				case 2:
					x -= 1;
					break;
				case 3:
					y -= 1;
					break;
				}
				if (x < 0) x += this.cols;
				if (x >= this.cols) x -= this.cols;
				if (y < 0) y += this.rows;
				if (y >= this.rows) y -= this.rows;
			}

			cells[y][x].update(Cell.plant);
		}
	}
	
	public void placeEvolvers(int numEvolvers) {
		placeEvolvers(numEvolvers, new ArrayList<int[]>());
	}
	
	public void placeEvolvers(int numEvolvers, ArrayList<int[]> genomeList) {
		int emptyCount = 0;
		
		this.evolvers = new ArrayList<Evolver>();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++ ) {
				if (cells[i][j].getContents() == Cell.empty) emptyCount++;
			}
		}
		
		if (emptyCount < numEvolvers) {
			System.err.println("There is only room for " + String.valueOf(emptyCount) + " evolvers!");
			numEvolvers = emptyCount;
		}
		
		for (int i = 0; i < numEvolvers; i++) {
			int direction = (int)(Math.random()*4);
			int x, y;
			do {
				x = (int)(Math.random()*this.cols);
				y = (int)(Math.random()*this.rows);
			} while (cells[y][x].getContents() != Cell.empty);
		
			evolvers.add(new Evolver(x, y, direction));
			if (genomeList.size() > i) {
				evolvers.get(i).setGenome(genomeList.get(i));
			}

			cells[y][x].addEvolver(direction);
		}
	}
	
	public void removeEvolvers() {
		for (Evolver e : this.evolvers) {
			int[] coords = e.getCoords();
			cells[coords[1]][coords[0]].removeEvolver();
		}
	}
	
	public void step() {
		// Update "obstMap".  This contains the map information as well as
		// evolver locations.
		for (Evolver e : this.evolvers) {
			moveEvolver(e);
		}
	}

	private void moveEvolver(Evolver e) {
		int direction = e.getDirection();
		int[] coords = e.getCoords();
		int[] facingCoords = normalize(addCoords(coords,this.directions.get(direction)));
		
		// The value of the cell the evolver is facing
		int facingCell = cells[facingCoords[1]][facingCoords[0]].getContents();
		
		// Get the action from the evolver.
		int action = e.getAction(facingCell);
		
		int newDirection;
		switch (action) {
		case 0:
			// Move forward if the cell isn't blocked.
			if (!cells[facingCoords[1]][facingCoords[0]].isBlocked()) {
				e.setCoords(facingCoords[0], facingCoords[1]);
				consume(e, facingCoords);
				cells[coords[1]][coords[0]].removeEvolver();
				cells[facingCoords[1]][facingCoords[0]].addEvolver(e.getDirection());
			}
			break;
		case 1:
			// Turn left.
			newDirection = direction + 1;
			if (newDirection >= 4) newDirection -= 4;
			e.setDirection(newDirection);
            cells[coords[1]][coords[0]].addEvolver(newDirection);
			break;
		case 2:
			// Turn right.
			newDirection = direction - 1;
			if (newDirection < 0) newDirection += 4;
			e.setDirection(newDirection);
            cells[coords[1]][coords[0]].addEvolver(newDirection);
			break;
		case 3:
			// Move backward if the cell isn't blocked.
			int backDirection = direction + 2;
			if (backDirection >= 4) backDirection -= 4;
			
			int[] newCoords = normalize(addCoords(coords,this.directions.get(backDirection)));
			
			if (!cells[newCoords[1]][newCoords[0]].isBlocked()) {
				e.setCoords(newCoords[0],newCoords[1]);
				consume(e, newCoords);

                cells[coords[1]][coords[0]].removeEvolver();
				cells[newCoords[1]][newCoords[0]].addEvolver(e.getDirection());
			}
			break;
		}
	}
	
	public void consume(Evolver e, int[] coords) {
        // Empty a cell an evolver moves onto.
		if (cells[coords[1]][coords[0]].getContents() == Cell.plant) {
			e.eat();
			cells[coords[1]][coords[0]].update(Cell.empty);
		}
	}
	
	public int[] addCoords(int[] xs, int[] ys) {
		return new int[]{xs[0]+ys[0], xs[1] + ys[1]};
	}
		
	public int[] normalize(int[] coords) {
		// Keep coords within the box.  If an evolver moves out of the box,
		// it finds itself on the opposite end of the box.
		int x = coords[0], y = coords[1];
		if (x >= this.cols) x -= this.cols;
		if (x < 0) x += this.cols;
		if (y >= this.rows) y -= this.rows;
		if (y < 0) y += this.rows;
		
		return new int[]{x,y};
		
	}
	
	public ArrayList<int[]> evolve() {
		ArrayList<int[]> genomeList = new ArrayList<int[]>();
		
		for (int i = 0; i < evolvers.size(); i+=2) {
			// Choose two (different) random evolvers.
			Evolver e1 = randomChoice(evolvers);
			Evolver e2 = e1;
		
			while (e2 == e1) {
				e2 = randomChoice(evolvers);
			}
			
			int[] genome1 = e1.getGenome();
			int[] genome2 = e2.getGenome();
			
			// Recombine and mutate the genomes.
			int[][] newGenomes = recombine(genome1, genome2);
			
			genomeList.add(newGenomes[0].clone());
			genomeList.add(newGenomes[1].clone());
		}
		
		return genomeList;
	}

	private Evolver randomChoice(ArrayList<Evolver> evolvers) {
		// Select a random option from a weighted distribution.
		int sum = 0;
		
		for (Evolver e : evolvers) {
			sum += e.getScore();
		}
		
		int selection = (int)(Math.random()*sum);
		
		int runningSum = 0;
		for (Evolver e : evolvers) {
			runningSum += e.getScore();
			if (runningSum > selection) return e;
		}
		return evolvers.get(evolvers.size()-1);
	}
	
	private int[][] recombine(int[] g1, int[] g2) {
		int[] newG1 = new int[g1.length];
		int[] newG2 = new int[g2.length];
		
		// Choose the crossover position.
		int position = (int)(Math.random()*g1.length);
		
		// The new genomes contain one of the originals before the crossover,
		// and the other after.
		for (int i = 0; i < position; i++) {
			newG1[i] = g1[i];
			newG2[i] = g2[i];
		}
		for (int i = position; i < g1.length; i++) {
			newG1[i] = g2[i];
			newG2[i] = g1[i];
		}
		
		// Randomly mutate the new genomes.
		for (int i = 0; i < g1.length; i++) {
			if (Math.random() < mutationRate) {
				if (i % 2 == 0) {
					newG1[i] = (int)(Math.random()*4);
				} else {
					newG1[i] = (int)(Math.random()*16);
				}
			}
			if (Math.random() < mutationRate) {
				if (i % 2 == 0) {
					newG2[i] = (int)(Math.random()*4);
				} else {
					newG2[i] = (int)(Math.random()*16);
				}
			}
		}
		return new int[][]{newG1, newG2};
	}
	
	public double averageScore() {
		int sum = 0;
		for (Evolver e : this.evolvers) {
			sum += e.getScore();
		}
		return (double)(sum)/this.evolvers.size();
	}
	
	public int maxScore() {
		int max = 0;
		for (Evolver e : this.evolvers) {
			int score = e.getScore();
			if (score > max) max = score;
		}
		return max;
	}
	
	public void display(Boolean val) {
		for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.display(val);
            }
        }
	}
}
