import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * import file from the root dictory and solve the convex hull problem with two
 * difference methods
 * 
 * @author FredZhang
 */
public class ConvexHull {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		// import the list of points
		System.out.println("Input file name?");
		Scanner in = new Scanner(System.in);
		String name = in.nextLine();
		List<Point> points = readPoints("input/" + name + ".txt");
		System.out.println("successfully import " + points.size() + " points from " + name + ".txt");

		// solve the problem and record the time elapsed.
		long time = System.nanoTime();
		System.out.print("Quick hull method");
		List<Point> convex = QuickHull.run(points);
		long t = System.nanoTime() - time;
		System.out.println(" produces " + convex.size() + " vertice convex hull in " + t / 1000000.0 + " milliseconds");

		// display the result in frame.
		ConvexHullFrame display = new ConvexHullFrame(points);
		display.drawConvex(convex);
	}

	@SuppressWarnings("resource")
	private static List<Point> readPoints(String name) throws FileNotFoundException {
		List<Point> points = new ArrayList<>();
		Scanner file = new Scanner(new File(name));
		while (file.hasNextLine()) {
			String line = file.nextLine();
			String[] s = line.split(", ");
			for (String i : s) {
				int a = i.indexOf("(");
				int b = i.indexOf(",");
				int c = i.indexOf(")");
				if (a < 0 || b < 0 || c < 0)
					continue;
				int x = Integer.parseInt(i.substring(a + 1, b));
				int y = Integer.parseInt(i.substring(b + 1, c));
				points.add(new Point(x, y));
			}
		}
		return points;
	}

	static class QuickHull {
		/**
		 * Use quick hull method to solve this problem. Divide-and-conquer.
		 * Divide set into two parts by an exteme line and run the same
		 * algorithm on two sets separtely.
		 * 
		 * @param points
		 * @return
		 */
		private static List<Point> run(List<Point> points) {
			// get the left-most and right-most points in the set, the first two
			// vertex in convex.
			int min = points.get(0).x;
			Point minP = points.get(0);
			int max = points.get(0).x;
			Point maxP = points.get(0);
			for (int i = 1; i < points.size(); i++) {
				Point p = points.get(i);
				if (p.x < min) {
					min = p.x;
					minP = p;
				} else if (p.x == min) {
					if (p.y < minP.y)
						minP = p;
				}
				if (p.x > max) {
					max = p.x;
					maxP = p;
				} else if (p.x == max) {
					if (p.y < maxP.y)
						maxP = p;
				}
			}
			Point p1 = minP;
			Point p2 = maxP;

			// recursivley call helper method, and concanate all vertece
			// together in order.
			List<Point> conv = new ArrayList<Point>();
			conv.add(p1);
			conv.addAll(VerticeOnRight(points, p1, p2));
			conv.add(p2);
			conv.addAll(VerticeOnRight(points, p2, p1));
			return conv;
		}

		/**
		 * 
		 * @param points
		 * @param p1
		 * @param p2
		 * @return all vertece on the right side of P1P2
		 */
		private static List<Point> VerticeOnRight(List<Point> points, Point p1, Point p2) {
			List<Point> m = new ArrayList<Point>();
			int a = p2.y - p1.y;
			int b = p1.x - p2.x;
			int c = p1.x * p2.y - p1.y * p2.x;
			int max = 0;
			Point maxP = null;
			for (Point p : points) {
				int s = a * p.x + b * p.y - c;
				if (s > 0) {
					m.add(p);
					if (s > max) {
						max = s;
						maxP = p;
					} else if (s == max) {
						if (distsqr(p1, p) < distsqr(p1, maxP)) {
							max = s;
							maxP = p;
						}
					}
				}
			}
			List<Point> conv = new ArrayList<Point>();
			// basis case, no more points on the right side
			if (maxP == null)
				return conv;
			conv.addAll(VerticeOnRight(m, p1, maxP));
			conv.add(maxP);
			conv.addAll(VerticeOnRight(m, maxP, p2));
			return conv;
		}
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return the square of distance between a and b
	 */
	private static int distsqr(Point a, Point b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

}
