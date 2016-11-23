package org.gcube.dataanalysis.ecoengine.utils;

import java.awt.Point;
import java.util.Vector;

/**
 * A class that calculates the nearest neighbor according to a set of points in the space having integer coordinates
 */
class Nearest {
	// Projection axes defines
	public static final int PROJX = 1;
	public static final int PROJY = 2;

	int n; // This is the number of point expected to be tested
	int projaxe; // Current projection axe
	int compare; // Number of compare made to find the nearest
	int maxradius; // Maximum radius of search in the array
	int ndata; // Number of data in the arrays
	Point data[]; // Point array
	Point xindex[]; // Index for coordinates X (x is the index, y is the value)
	Point yindex[]; // Index for coordinates Y (x is the index, y is the value)
	boolean flags[]; // Array of flags that indicate the validity of point (true or false)

	Point cindex[]; // Pointer to current index
	int cvalue, cind; // Current values for the search
	Point cp; // Current point being tested

	// Constructor of NNFinder
	public Nearest(Vector points) {
		n = 10; // We expect to test 10 points... (this is arbitrary)
		// Copy the point vector into an array
		ndata = points.size();
		// We create the arrays
		data = new Point[ndata];
		xindex = new Point[ndata];
		yindex = new Point[ndata];
		flags = new boolean[ndata];
		// We initialise the data
		for (int i = 0; i < ndata; i++) {
			data[i] = (Point) points.elementAt(i);
			// Create a new point which will have
			// field x : the index of the original point in the array (data)
			// field y : the value of the projected point on the axe
			xindex[i] = new Point(i, data[i].x);
			yindex[i] = new Point(i, data[i].y);
			flags[i] = true; // The point is valid
		}
		// Sort the index for axe X
		cindex = xindex;
		BubbleSort();
		// Sort the index for axe Y
		cindex = yindex;
		BubbleSort();
		compare = 0;
	}

	// Simple bubble sort. Uses the index
	// array to determine the values of the
	// element of the array
	public void BubbleSort() {
		Point ptmp;
		for (int i = ndata - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (cindex[j].y > cindex[j + 1].y) { // Swap the points
					ptmp = cindex[j];
					cindex[j] = cindex[j + 1];
					cindex[j + 1] = ptmp;
				}
			}
		}
	}

	// Returns the number of compare done for
	// finding the nearest point
	public int getCompare() {
		return compare;
	}

	// Returns the maximum radius of search
	// in the arrays of points.
	public int getMaxRadius() {
		return maxradius;
	}

	// Returns the projection axe used to find
	// the nearest neighbor.
	public int getProjectionAxe() {
		return projaxe;
	}

	// Dichotomic search in an ordered array using
	// index
	private int DichoSearchIndex(int value) {
		int inf, sup, centre;
		inf = 0;
		sup = ndata - 1;

		// Check for obious case
		if (value <= cindex[inf].y)
			return inf;
		else if (value >= cindex[sup].y)
			return sup;

		// Search until we have at least two elements
		while (sup > inf) {
			centre = (inf + sup) / 2;
			if (cindex[centre].y == value)
				return centre;
			else if (cindex[centre].y < value)
				inf = centre + 1;
			else
				sup = centre - 1;
		}
		return inf;
	}

	// This function will reset the all the flags to true
	private void ResetFlags() {
		for (int i = 0; i < ndata; i++)
			flags[i] = true;
	}

	public Point FindFirstNN(Point p) {
		int index1, index2, i, j;
		int sparse1, sparse2;
		int xdim, ydim;
		float s1, s2;
		// Compute the dimension of the pointset
		xdim = xindex[ndata - 1].y - xindex[0].y;
		ydim = yindex[ndata - 1].y - yindex[0].y;

		// Remember the point being tested
		cp = p;

		// Reset the flags
		ResetFlags();

		// Find the point on the projected axes
		cindex = xindex;
		index1 = DichoSearchIndex(p.x);
		cindex = yindex;
		index2 = DichoSearchIndex(p.y);

		// Mesure the sparsity of axe X
		i = index1 - n / 2;
		if (i < 0)
			i = 0;
		j = index1 + n / 2;
		if (j >= ndata)
			j = ndata - 1;
		sparse1 = xindex[j].y - xindex[i].y;
		// Normalize sparsity
		s1 = (float) sparse1 / (float) xdim;

		// Mesure the sparsity of axe Y
		i = index2 - n / 2;
		if (i < 0)
			i = 0;
		j = index2 + n / 2;
		if (j >= ndata)
			j = ndata - 1;
		sparse2 = yindex[j].y - yindex[i].y;
		// Normalise sparsity
		s2 = (float) sparse2 / (float) ydim;

		if (s1 > s2) { // We take the x axe
			cindex = xindex;
			cvalue = p.x;
			cind = index1;
			projaxe = PROJX;
		} else { // We take the y axe
			cindex = yindex;
			cvalue = p.y;
			cind = index2;
			projaxe = PROJY;
		}

		// Init the number of compare
		compare = 0;
		maxradius = 0;
		return FindNextNN();
	}

	public Point FindNextNN() {
		float mindist, dist;
		int mini;
		int i, il, ir;

		// Find the radius of the circle
		// cind is already at the left of the test point
		il = cind;
		// Find the closest next valid point
		while (flags[cindex[il].x] == false && il > 0)
			il--;
		if (flags[cindex[il].x] == true) {
			// Compute the distance
			mindist = ComputeDistance(cp, data[cindex[il].x]);
			compare++;
		} else
			// Put something big no chance of having a distance bigger than that
			mindist = 500 * 500;

		// Here, we must verify if it's not the end already
		if (cind < ndata - 1) {
			ir = cind + 1;
			while (flags[cindex[ir].x] == false && ir < ndata - 1)
				ir++;
			if (flags[cindex[ir].x] == true) {
				dist = ComputeDistance(cp, data[cindex[ir].x]);
				compare++;
			} else
				dist = 500 * 500;

			if (mindist < dist) {
				maxradius = (int) Math.sqrt((double) mindist);
				mini = il;
			} else {
				mini = ir;
				maxradius = (int) Math.sqrt((double) dist);
				mindist = dist;
			}
		} else {
			mini = il;
			ir = ndata - 1;
			maxradius = (int) Math.sqrt((double) mindist);
		}

		// Search to the left of cind
		i = il - 1;
		while (i > 0 && maxradius > Math.abs(cvalue - cindex[i].y)) {
			if (flags[cindex[i].x] == true) {
				dist = ComputeDistance(cp, data[cindex[i].x]);
				compare++;
				if (dist < mindist) {
					mindist = dist;
					mini = i;
				}
			}
			// Go to the left
			i--;
		}

		// Search to the right of cind
		i = ir + 1;
		while (i < ndata && maxradius > Math.abs(cindex[i].y - cvalue)) {
			if (flags[cindex[i].x] == true) {
				dist = ComputeDistance(cp, data[cindex[i].x]);
				compare++;
				if (dist < mindist) {
					mindist = dist;
					mini = i;
				}
			}
			i++; // Go to the right
		}

		// Set the flag of the point found to false so that
		// we don't find it again for next search
		flags[cindex[mini].x] = false;

		// return the closest point
		return data[cindex[mini].x];
	}

	// A crude way to find the nearest neighbor
	public Point FindNearestNeighborCrude(Point p) {
		float mindist, dist;
		int mini = 0;
		// Init compare
		compare = 0;
		mindist = ComputeDistance(p, data[mini]);
		for (int i = 0; i < ndata; i++) {
			dist = ComputeDistance(p, data[i]);
			compare++;
			if (dist < mindist) {
				mini = i;
				mindist = dist;
			}
		}
		return data[mini];
	}

	// Compute the SQUARED distance between to points
	public float ComputeDistance(Point p1, Point p2) {
		float dx, dy;
		dx = p2.x - p1.x;
		dy = p2.y - p1.y;
		return (dx * dx + dy * dy);
	}
	
}
