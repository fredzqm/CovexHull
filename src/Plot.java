import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * for plotting the experiment data and approximation formula
 * 
 * @author FredZhang
 *
 */
public class Plot extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static double[][] data = {
			{ 25, 250, 1250, 2500, 5000, 10000, 25000, 100000, 200000, 500000 },
			{ 2.8388, 20.3193, 136.7920, 249.6994, 954.1295, 3535.0032,
					17571.6688, 301319.2527, 1201449.0769, 7086523.9356 },
			{ 2.3891, 7.9902, 13.1577, 29.8031, 32.6702, 32.2079, 91.9480,
					182.0825, 6531.9116, 36360.1730 },
			{ 1.1884, 1.9280, 4.3388, 6.3452, 9.3166, 15.7280, 23.7685,
					59.5806, 89.6678, 146.1920 } };
	private static final int Offset = 50;
	private static final int NumOfpoints = 1000;
	private static final int Width = 1000;
	private static final int Height = 800;
	private static final int X_max = 500000;

	ArrayList<Double> approximate;
	Canvas canvas;
	int gap = X_max / NumOfpoints;
	double ratioX;
	double ratioY;
	int choice;

	public Plot(int n) {
		choice = n;
		ratioX = (Width - 2 * Offset) / (X_max + 0.0);
		ratioY = -(Height - 2 * Offset) / data[choice][9];
		System.out.println("" + ratioX + " " + ratioY + " " + data[choice][9]);
		approximate = new ArrayList<Double>();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(Width, Height);
		canvas = new Canvas();
		canvas.setSize(Width, Height);
		add(canvas);

		// a is the approximation function
		Function a = null;
		switch (n) {
		case 1:
			a = new Function() {
				@Override
				public double f(int n) {
					return 2.7407e-5 * n * n + 0.479317 * n - 3571.4;
				}
			};
			break;
		case 2:
			a = new Function() {
				@Override
				public double f(int n) {
					return 1.4643e-7 * n * n - 2.332e-4 * n - 56.10;
				}
			};
			break;
		case 3:
			a = new Function() {
				@Override
				public double f(int n) {
					return Math.sqrt(n) / 5;

				}
			};
			break;
		}

		for (int m = 0; m <= X_max; m += gap) {
			approximate.add(a.f(m));
		}

		setVisible(true);
	}

	interface Function {
		public double f(int n);
	}

	class Canvas extends JComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.translate(Offset, Height - Offset);
			for (int i = 0; i < data[0].length - 1; i++) {
				Point2D.Double p = new Point2D.Double(data[0][i] * ratioX,
						data[choice][i] * ratioY);
				Point2D.Double q = new Point2D.Double(data[0][i + 1] * ratioX,
						data[choice][i + 1] * ratioY);
				g2.draw(new Line2D.Double(p, q));
			}
			g2.drawString("Time for 500000: "+data[choice][9]+" ms", (int)(data[0][9] * ratioX)-300,
					(int)(data[choice][9] * ratioY));

			g2.setColor(Color.RED);
			for (int i = 0; i < NumOfpoints - 1; i++) {
				Point2D.Double p = new Point2D.Double(i * gap * ratioX,
						approximate.get(i) * ratioY);
				Point2D.Double q = new Point2D.Double((i + 1) * gap * ratioX,
						approximate.get(i + 1) * ratioY);
				g2.draw(new Line2D.Double(p, q));
			}
		}
	}

	public static void main(String[] args) {
		new Plot(1);
		new Plot(2);
		new Plot(3);
	}
}
