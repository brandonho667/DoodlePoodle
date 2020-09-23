import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Line {
	int x1 = 0, y1 = 0, x2 = 0, y2 = 0, thick = 10;
	Color color;

	/**
	 * Paints a Line on the canvas
	 * 
	 * @param g
	 */
	public void paint(Graphics g) {
		g.setColor(color);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(thick));
		g2.drawLine(x1, y1, x2, y2);
		g2.setStroke(new BasicStroke(1));
	}

	/**
	 * Creates a line with start/end positions, thickness, and color.
	 * 
	 * @param x1
	 *            starting x position of Line
	 * @param y1
	 *            starting y position of Line
	 * @param x2
	 *            ending x position of Line
	 * @param y2
	 *            ending y position of line
	 * @param thicc
	 *            thickness of line
	 * @param r
	 *            the red color component of line
	 * @param g
	 *            the green color component of line
	 * @param b
	 *            the blue color component of line
	 */
	public Line(int x1, int y1, int x2, int y2, int thiccc, int r, int g, int b) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		thick = thiccc;
		color = new Color(r, g, b);
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public int getThick() {
		return thick;
	}

	public Color getColor() {
		return color;
	}
}