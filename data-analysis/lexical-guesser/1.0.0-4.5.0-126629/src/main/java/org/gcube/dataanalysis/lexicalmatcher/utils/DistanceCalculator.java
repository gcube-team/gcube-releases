package org.gcube.dataanalysis.lexicalmatcher.utils;

public class DistanceCalculator {

	// ****************************
	// Get minimum of three values
	// ****************************

	private int Minimum(int a, int b, int c) {
		int mi;

		mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;

	}

	// *****************************
	// Compute Levenshtein distance
	// *****************************

	public int LD(String s, String t) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost

		// Step 1

		n = s.length();
		m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];

		// Step 2

		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= n; i++) {

			s_i = s.charAt(i - 1);

			// Step 4

			for (j = 1; j <= m; j++) {

				t_j = t.charAt(j - 1);

				// Step 5

				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}

				// Step 6

				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);

			}

		}

		// Step 7

		return d[n][m];

	}

	// *****************************
	// Calculate Complete Distance
	// *****************************
	public double CD(boolean useSimpleDistance, String h, String t) {
		return CD(useSimpleDistance, h, t,false,false);
	}
	//output will be a percentage. 1 will mean a complete agreement between the inputs
	public double CD(boolean useSimpleDistance, String h, String t, boolean ignoreCase, boolean boostMatch) {
		
		
		
		double distance = 0;
		if ((h == null) && (t == null)) {
			distance = 1;
		} 
		else if ((h != null) && (t != null)) {
			
			h = treatString(h,ignoreCase);
			t = treatString(t,ignoreCase);
			int lt = t.length();
			int lh = h.length();
			double matchFactor = 1.5f;
			if (boostMatch)
				matchFactor = 2f;
			
			if (((lt==0)&&(lh!=0))||((lt!=0)&&(lh==0)))
				distance = 0;
			else if (h.equalsIgnoreCase(t)){
				distance = 1;
			}
			else if (useSimpleDistance) {
				distance = 0;
			}
			else if (t.contains(h)) {
				// calcolo la percentuale di contenimento
				String treatedT = t.replace(h, "");
				double percentage = 1 - ((double) treatedT.length() / (double) lt);
//				AnalysisLogger.getLogger().debug("Complete Distance Calculation: coverage percentage of h on t " + percentage);
//				double percentage = 0.9;
				percentage = Math.min(percentage * matchFactor,0.98);
				distance = percentage;
			} 
			else if (h.contains(t)) {
				// calcolo la percentuale di contenimento
				String treatedH = h.replace(t, "");
				double percentage = 1 - ((double) treatedH.length() / (double) lh);
//				AnalysisLogger.getLogger().debug("Complete Distance Calculation: coverage percentage of t on h " + percentage);
//				double percentage = 0.9;
				percentage = Math.min(percentage * matchFactor,0.98);
				distance = percentage;
			}
			else {
				/*
				if ((lh>lt)||((lt>lh*1.5))){
					System.out.println("UNMATCHABLE "+lt +" vs "+lh);
					distance = 0;	
				}
				else{
					*/
				//calcolo percentuale su Levenshtein distance
				int levenDist = LD(h, t);
				int maxlen = Math.max(lh, lt);
				distance = 1-((double)levenDist / (double)maxlen);
//				System.out.println("L " + levenDist+" max "+maxlen+" h "+h+" t "+t);
//				AnalysisLogger.getLogger().debug("Complete Distance Calculation: leven distance percentage of h on t " + distance);
//				}
			}
		}
		
		return distance;
	}
	
	private String treatString(String h, boolean ignoreCase){
		//tolgo la punteggiatura
		h = h.replaceAll("[!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", "");
		//riduco gli spazi multipli a spazi singoli
		h = h.replaceAll("[ ]+", " ");
		//trim
		h = h.trim();
		if (ignoreCase)
			h = h.toLowerCase();
		
		return h;
	}
	
	
	public static void main(String[] args) {

		String h = "Mediteranean";
		String t = "Mediterranean horse mackerel";
		DistanceCalculator d = new DistanceCalculator();
		double cd = d.CD(false,h, t, true , true);
		System.out.println("Distance between "+h+" and "+t+" : " + cd);

	}

}
