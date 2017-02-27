import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
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
		List<Point> points = new ArrayList<Point>();
		System.out.println("Input file name?");
		Scanner in = new Scanner(System.in);
		String name = in.nextLine();
		Scanner file = new Scanner(new File("input/" + name + ".txt"));
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
		System.out.println("successfully import " + points.size() + " points from " + name + ".txt");

		// choose the method
		int choice = 0;
		while (true) {
			System.out.println(
					"Please choose the method for finding convexHull (1.brutal force; 2.improved brutal force; 3.quickhull alogrithm)");
			try {
				choice = in.nextInt();
			} catch (InputMismatchException e) {
				continue;
			}
			if (choice == 1 || choice == 2 || choice == 3)
				break;
		}

		// solve the problem and record the time elapsed.
		List<Point> convex = null;
		long time = System.nanoTime();
		if (choice == 1) {
			System.out.print("Brutal force method");
			convex = BrutalForce.run(points);
		} else if (choice == 2) {
			System.out.print("Improved brutal force method");
			convex = ImprovedBrutalForce.run(points);
		} else {
			System.out.print("Quick hull method");
			convex = QuickHull.run(points);
		}
		long t = System.nanoTime() - time;
		System.out.println(" produces " + convex.size() + " vertice convex hull in " + t / 1000000.0 + " milliseconds");

		// display the result in frame.
		ConvexHullFrame display = new ConvexHullFrame(points);
		display.drawConvex(convex);
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

	static class ImprovedBrutalForce {
		/**
		 * Find the first vertex of convex hull with brutal force, which is
		 * called seed. Then start from the seed and find the next vertix
		 * connected to it until the convex is completed.
		 * 
		 * @param points
		 * @return the list of vertece in the convex hull in order
		 */
		private static List<Point> run(List<Point> points) {
			List<Point> conv = new ArrayList<Point>();
			// first find a seed with brutal force search
			n: for (Point p : points) {
				for (Point q : points) {
					Point d = nextVertix(points, p, q);
					if (d != null) {
						conv.add(d);
						break n;
					}
				}
			}
			// start with the seed and go clockwisely to find the rest
			while (true) {
				Point cur = conv.get(conv.size() - 1);
				for (Point p : points) {
					Point d = nextVertix(points, cur, p);
					if (d != null) {
						if (d == conv.get(0))
							return conv;// the loop is completed
						conv.add(d);
						break;
					}
				}
			}
		}

		/**
		 * check to see whether cur--guess can be an edge in vertex using brutal
		 * force method. It checks to see whether all points are on the left
		 * side of cur-guess. If this test passes and there are no other points
		 * on this line, guess is returned as a valid vertex. However, if there
		 * are other points along this line, guess might be at the middle of an
		 * edge, so the furthest point from cur is returned.
		 * 
		 * Note that cur does not have to be a valid vertex of convex hull, it
		 * can be at the middle of an edge, but the returned value is guaranteed
		 * to be a vertex of edge.
		 * 
		 * @param points
		 * @param cur
		 *            the point to assist the searching of a vertex, cur has to
		 *            be on an edge for the method to return a point.
		 * @param guess
		 *            the point to be check, but other points except it can also
		 *            be returned.
		 * @return a valid vertex of the covex hull found in this search, null
		 *         if none if found.
		 */
		private static Point nextVertix(List<Point> points, Point cur, Point guess) {
			if (cur.equals(guess))
				return null;
			int a = cur.y - guess.y;
			int b = guess.x - cur.x;
			int c = guess.x * cur.y - guess.y * cur.x;
			List<Point> onLine = new ArrayList<Point>();
			for (Point k : points) {
				int d = a * k.x + b * k.y - c;
				if (d < 0) {
					return null;
				} else if (d == 0) {
					if (!k.equals(cur) && !k.equals(guess))
						onLine.add(k);
				}
			}
			int maxdistsqr = distsqr(cur, guess);
			Point nextVertix = guess;
			for (Point i : onLine) {
				if (distsqr(cur, i) > maxdistsqr) {
					maxdistsqr = distsqr(cur, i);
					nextVertix = i;
				}
			}
			return nextVertix;
		}
	}

	static class BrutalForce {

		/**
		 * Brutal forcely searching through the pairs of points in the list and
		 * store those possible to be vertice of convex hull. Then process those
		 * stored data, eliminating those point at the middle of an edge, and
		 * return the result convex vertix list.
		 * 
		 * @param points
		 * @return vertice list of convex hull
		 */
		private static List<Point> run(List<Point> points) {
			// construct a function from all points on the convex hull to any
			// other points on the same line clockwisely.
			HashMap<Point, HashSet<Point>> edges = new HashMap<Point, HashSet<Point>>();
			for (int i = 0; i < points.size(); i++) {
				Point p = points.get(i);
				for (int j = i + 1; j < points.size(); j++) {
					Point q = points.get(j);
					// avoid repeated points
					if (p.equals(q)) {
						continue;
					}
					int a = p.y - q.y;
					int b = q.x - p.x;
					int c = q.x * p.y - q.y * p.x;
					boolean hasLeft = false;
					boolean hasRight = false;
					for (Point k : points) {
						int d = a * k.x + b * k.y - c;
						if (d < 0) {
							hasLeft = true;
							if (hasRight) {
								break;
							}
						} else if (d > 0) {
							hasRight = true;
							if (hasLeft) {
								break;
							}
						}
					}
					// store possible pairs of points in a certain order
					if (!hasLeft) {
						if (!edges.containsKey(p)) {
							HashSet<Point> l = new HashSet<Point>();
							l.add(q);
							edges.put(p, l);
						} else
							edges.get(p).add(q);
					} else if (!hasRight) {
						if (!edges.containsKey(q)) {
							HashSet<Point> l = new HashSet<Point>();
							l.add(p);
							edges.put(q, l);
						} else
							edges.get(q).add(p);
					}
				}
			}
			// generate the convex vertice list clockwisely.
			List<Point> conv = new ArrayList<Point>();
			Point cur = edges.keySet().iterator().next();
			while (true) {
				HashSet<Point> possibles = edges.get(cur);
				if (possibles.size() == 1) {
					cur = possibles.iterator().next();
				} else {
					// occur when multiple points are on one edge.
					Point p = possibles.iterator().next();
					p: do {
						for (Point q : edges.get(p)) {
							if (possibles.contains(q)) {
								p = q;
								continue p;
							}
						}
					} while (false);
					cur = p;
				}
				if (conv.size() > 1 && cur.equals(conv.get(0)))
					return conv;
				conv.add(cur);
			}
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
