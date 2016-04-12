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
 * Filename: GHNScoreTable.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.types;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GHNScoreTable implements Serializable {
	private static final long serialVersionUID = 6082885557663057230L;
	private static final int IDX_SCORE = 0;
	private static final int IDX_HITS = 1;
	private static final float DEFAULT_SCORE = -1;
	private static final float DEFAULT_HITS = -1;
	private HashMap<String, float[]> table = new HashMap<String, float[]>();

	public GHNScoreTable() {
		super();
	}

	public final void registerGHNScore(final String ghnID, final float score, final int hits) {
		this.table.put(ghnID, new float[]{score, hits});
	}

	public final float getScoreFor(final String ghnID) {
		if (!table.containsKey(ghnID)) {
			return DEFAULT_SCORE;
		}
		return table.get(ghnID)[IDX_SCORE];
	}

	public final int getHitsFor(final String ghnID) {
		if (!table.containsKey(ghnID)) {
			return Math.round(DEFAULT_HITS);
		}
		return Math.round(table.get(ghnID)[IDX_HITS]);
	}

	public final int size() {
		return this.table.size();
	}

	@Override
	public final String toString() {
		StringBuilder retval = new StringBuilder();
		for (String key : this.table.keySet()) {
			float[] vals = this.table.get(key);
			retval.append(key + "(" + vals[0] + "," + vals[1] + ")" + System.getProperty("line.separator"));
		}
		return retval.toString();
	}
}
