/* Evolver.java
 * 
 * Represents evolvers: a position, direction, score, and genome.
 */
package GeneticAlgorithm;

public class Evolver {
	private int x, y;
	private int direction;
	private int score;
	private int state;
	class action {
		int move;
		int newState;
	}
	private action[][] genome;
	
	private final int NUM_STATES = 16;
	private final int NUM_INPUTS = 3;
	
	public Evolver(int x, int y, int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.state = 0;
		this.score = 1;
		
		// Construct a random genome.
		this.genome = new action[NUM_STATES][NUM_INPUTS];
		for (int i = 0; i < NUM_STATES; i++) {
			for (int j = 0; j < NUM_INPUTS; j++) {
				genome[i][j] = new action();
				genome[i][j].move = (int)(Math.random()*4);
				genome[i][j].newState = (int)(Math.random()*NUM_STATES);
			}
		}
	}
	
	public void setCoords(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int[] getCoords() {
		return new int[]{x, y};
	}
	
	public int getDirection() {
		return direction;
	}
	
	public int getAction(int facing) {
		// Update the internal state and return an action.
		action theAction = genome[this.state][facing];
		
		this.state = theAction.newState;
		return theAction.move;
	}
	
	public void eat() {
		this.score++;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int[] getGenome() {
		// Translate the genome into a 1D array for evolution.
		int[] genomeArray = new int[NUM_STATES*NUM_INPUTS*2];
		
		int idx = 0;
		for (int i = 0; i < NUM_STATES; i++) {
			for (int j = 0; j < NUM_INPUTS; j++) {
				genomeArray[idx] = genome[i][j].move;
				genomeArray[idx + 1] = genome[i][j].newState;
				idx += 2;
			}
		}
		
		return genomeArray;
	}
	
	public void setGenome(int[] genomeArray) {
		// Translate the genome from a 1D array.
		for (int i = 0; i < genomeArray.length; i+=2) {
			int state = (int)(i/(NUM_INPUTS*2));
			int input = (int)((i - state*NUM_INPUTS*2)/(2));
			genome[state][input].move = genomeArray[i];
			genome[state][input].newState = genomeArray[i+1];
		}
	}
}
