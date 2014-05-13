/* Cell.java
 * 
 * Extends JPanel - represents a single cell in the "world", which may be empty,
 * may contain a plant, or may contain an evolver facing some direction.
 */
package GeneticAlgorithm;

import javax.swing.*;
import java.awt.*;

public class Cell extends JPanel {
	Color color = Color.black;
	// evolverDirection = -1 represents "no evolver."
	int evolverDirection = -1;

	public void paintComponent (Graphics g) {
		g.setColor(color);
		g.fillRect(0,0,getWidth(),getHeight());

		if (evolverDirection >= 0) {
			g.setColor(Color.black);
			int[] xVals = new int[3];
			int[] yVals = new int[3];
			switch(evolverDirection) {
			case 0:
				xVals[0] = getWidth();
				yVals[0] = getHeight()/2;
				
				xVals[1] = 0;
				yVals[1] = getHeight()/8;
				
				xVals[2] = 0;
				yVals[2] = 7*getHeight()/8;
				
				break;
				
			case 1:
				xVals[0] = getWidth()/2;
				yVals[0] = 0;
				
				xVals[1] = getWidth()/8;
				yVals[1] = getHeight();
				
				xVals[2] = 7*getWidth()/8;
				yVals[2] = getHeight();
				
				break;
			
			case 2:
				xVals[0] = 0;
				yVals[0] = getHeight()/2;
				
				xVals[1] = getWidth();
				yVals[1] = 7*getHeight()/8;
				
				xVals[2] = getWidth();
				yVals[2] = getHeight()/8;
				
				break;
				
			case 3:
				xVals[0] = getWidth()/2;
				yVals[0] = getHeight();
				
				xVals[1] = 7*getWidth()/8;
				yVals[1] = 0;
				
				xVals[2] = getWidth()/8;
				yVals[2] = 0;
				
				break;
			}
			g.fillPolygon(xVals,yVals,3);
		}
	}
	
	public void update(int contents) {
		Color newColor = Color.black;

        if (contents == -1) {
			newColor = Color.black;
		} else if (contents == 0) {
			newColor = new Color(160, 82, 45);
		} else if (contents == 1) {
			newColor = Color.green;
		}

        if (newColor != this.color) {
            this.color = newColor;
            repaint();
        }
	}
	
	public void addEvolver(int direction) {
		this.evolverDirection = direction;
		
		repaint();
	}
	
	public void removeEvolver() {
		this.evolverDirection = -1;
		
		repaint();
	}
}
