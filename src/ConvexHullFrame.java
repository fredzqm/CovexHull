import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * GUI frame to display convex problem
 * @author FredZhang
 *
 */
public class ConvexHullFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int Width = 1000;
	static final int Height = 800;
	double point_d;
	double ratio;
	private List<Point> points;
	private int[] convexX;
	private int[] convexY;
	private Canvas canvas;

	/**
	 * construct a convex hull frame and display it.
	 * @param points
	 */
	public ConvexHullFrame(List<Point> points) {
		this.points = points;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		int maxX = points.get(0).x;
		int maxY = points.get(0).y;
		// find an appropriate frame size
		for (int i = 1; i < points.size(); i++) {
			Point p = points.get(i);
			if (p.y > maxY)
				maxY = p.y;
			if (p.x > maxX)
				maxX = p.x;
		}
		// set a shrinking ratio, to adjust the graph to an appropriate size
		ratio = Math.min(Width/(maxX+50.0), Height/(maxY+50.0));
		point_d = 8 * ratio;
		setSize(Width, Height);
		canvas = new Canvas();
		canvas.setSize(Width, Height);
		add(canvas);
		setVisible(true);
	}

	/**
	 * specify verteces in the convex hull, and display it.
	 * @param convex
	 */
	public void drawConvex(List<Point> convex) {
		convexX = new int[convex.size()];
		convexY = new int[convex.size()];
		for (int i = 0; i < convex.size(); i++) {
			Point p = convex.get(i);
			convexX[i] = (int) (p.x * ratio);
			convexY[i] = (int) (p.y * ratio);
		}
		repaint();
	}
	
	/**
	 * the JCompoennt that does all the painting
	 * @author FredZhang
	 */
	private class Canvas extends JComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			for (Point i : points) {
				// all points
				g2.fill(new Arc2D.Double((i.x *ratio - point_d / 2), i.y * ratio - point_d / 2, point_d,
						point_d, 0, 360, 0));
			}
			g2.setColor(Color.RED);
			if (convexX != null) {
				// the convex hull
				g2.drawPolygon(convexX, convexY, convexX.length);
				for(int i = 0; i < convexX.length ; i ++){
					// the vertice on the convex hull
					g2.fill(new Arc2D.Double( convexX[i] - point_d / 2, convexY[i] - point_d / 2, point_d,
							point_d, 0, 360,0));
				}
			}
		}

	}

}
