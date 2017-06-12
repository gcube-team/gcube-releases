/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GraphData.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.types;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.timeseries.charts.support.assertions.Assertion;
import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;


/**
 * All the data needed to represent a single graph.
 *
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GraphData implements Serializable {
	private static final long serialVersionUID = 930654337632116093L;
	private List<Point<? extends Number, ? extends Number>> data = null;
	private Number minY = 0;
	private Number maxY = 50000;

	@SuppressWarnings("unused")
	public GraphData() {
		// for serialization issues only
	}

	public GraphData(
			final List<Point<? extends Number, ? extends Number>> data,
			final boolean invertAxis) throws InvalidParameterException {
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(data != null && data.size() > 0,
				new InvalidParameterException(
				"Invalid data. Null and empty not allowed"));
		if (invertAxis) {
			this.data = this.invertAxis(data);
		} else {
			this.data = data;
		}
	}

	public final List<Point<? extends Number, ? extends Number>> getData() {
		return this.data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Point<? extends Number, ? extends Number>> invertAxis(
			final List<Point<? extends Number, ? extends Number>> points) {
		List<Point<? extends Number, ? extends Number>> retval = new Vector<Point<? extends Number, ? extends Number>>();
		int linesNum = points.get(0).getEntries().size();

		// Prepare lines - labels of groups
		for (int i = 0; i < linesNum; i++) {
			try {
				retval.add(new Point<Double, Double>(points.get(0).getEntries()
						.get(i).getLabel(), Double.valueOf(points.get(0)
								.getEntries().get(i).getValue().toString())));
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
		}

		// Insert values inside groups
		for (Point<? extends Number, ? extends Number> xPoint : points) {
			for (int i = 0; i < linesNum; i++) {
				try {
					retval.get(i).addEntry(
							new ValueEntry(xPoint.getLabel(), Double
									.valueOf(xPoint.getEntries().get(i)
											.getValue().toString())));
				} catch (InvalidParameterException e) {
					e.printStackTrace();
				}
			}
		}

		return retval;
	}

	public final void setMaxY(final Number maxY) {
		this.maxY = maxY;
	}

	public final void setMinY(final Number minY) {
		this.minY = minY;
	}

	public final Number getMinY() {
		return minY;
	}

	public final Number getMaxY() {
		return maxY;
	}

}
