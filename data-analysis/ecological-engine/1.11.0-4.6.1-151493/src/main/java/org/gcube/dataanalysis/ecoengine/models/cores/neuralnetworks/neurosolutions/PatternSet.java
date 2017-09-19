package org.gcube.dataanalysis.ecoengine.models.cores.neuralnetworks.neurosolutions;


// represents all patterns which will be used during training
class PatternSet {

	Pattern[] patterns; // whole pattern set including all patterns
	Pattern[] trainingpatterns; // patterns to be used during training
	Pattern[] crossvalpatterns; // patterns to be used during cross validation
	Pattern[] testpatterns; // patterns to be used using testing
	double[] crossvaldeviations;
	double[] testdeviations;
	private Randomizer randomizer;

	// constructor.
	// parameters should sum to 1
	public PatternSet (String sourceFile, int noofinputs, int nooftargets, double ratiotraining, double ratiocrossval, double ratiotest, Randomizer randomizer) {
		
		// load patterns from the file
		
		// 1st determine how many patterns there are
		LineReader linereader = new LineReader(sourceFile);
		int counter = 0;
		double temp_double;
		while (linereader.NextLineSplitted()){
			try {
				// count numeric data only
				for ( int i = 0; i < (noofinputs + nooftargets); i++) {
					temp_double = Double.parseDouble(linereader.column[i]);
				}
				counter++;
			}
			catch (NumberFormatException e) {}
		}
		linereader = null;
		patterns = new Pattern[counter];

		// then read each pattern and place it into the array
		double[] temp_in = new double[noofinputs];
		double[] temp_tar = new double[nooftargets];
		linereader = new LineReader(sourceFile);
		counter = 0;
		while (linereader.NextLineSplitted()){
			try { // count numeric data only
				for (int i = 0; i < noofinputs; i++) {
					temp_in[i] = Double.parseDouble(linereader.column[i]);
				}
				for (int i = noofinputs; i < noofinputs+nooftargets; i++) {
					temp_tar[i-noofinputs] = Double.parseDouble(linereader.column[i]);
				}
				patterns[counter++] = new Pattern(temp_in, temp_tar);
			}
			catch (NumberFormatException e) {}
		}
		linereader = null;
		
		// now determine the no. of training, cross val. and test patterns
		trainingpatterns = new Pattern[(int)(Math.round(patterns.length * ratiotraining))];
		crossvalpatterns = new Pattern[(int)(Math.round(patterns.length * ratiocrossval))];
		testpatterns = new Pattern[patterns.length - trainingpatterns.length - crossvalpatterns.length];
		
		int patterntoselect;
		int patternsnotselected = patterns.length;
		
		// first create training patterns
		for ( int i = 0; i < trainingpatterns.length; i++ ) {
			patterntoselect = randomizer.random.nextInt(patternsnotselected);
			counter = 0;
			for ( int j = 0; j < patterns.length; j++ ) {
				if ( !patterns[j].selected ) {
					if ( counter == patterntoselect ) {
						trainingpatterns[i] = patterns[j];
						patterns[j].selected = true;
						patternsnotselected--;
						break;
					}
					counter++;
				}
			}
		}
		
		// then create cross validation patterns
		for ( int i = 0; i < crossvalpatterns.length; i++ ) {
			patterntoselect = randomizer.random.nextInt(patternsnotselected);
			counter = 0;
			for ( int j = 0; j < patterns.length; j++ ) {
				if ( !patterns[j].selected ) {
					if ( counter == patterntoselect ) {
						crossvalpatterns[i] = patterns[j];
						patterns[j].selected = true;
						patternsnotselected--;
						break;
					}
					counter++;
				}
			}
		}
		
		// and then create test patterns
		for ( int i = 0; i < testpatterns.length; i++ ) {
			patterntoselect = randomizer.random.nextInt(patternsnotselected);
			counter = 0;
			for ( int j = 0; j < patterns.length; j++ ) {
				if ( !patterns[j].selected ) {
					if ( counter == patterntoselect ) {
						testpatterns[i] = patterns[j];
						patterns[j].selected = true;
						patternsnotselected--;
						break;
					}
					counter++;
				}
			}
		}
		
		// and now switch all patterns as !selected, so that they are ready for training
		for ( int i = 0; i < patterns.length; i++ ) {
			patterns[i].selected = false;
		}
		
		// calculate average deviations for cross val data as well as test data
		double[] averages = new double[nooftargets];
		crossvaldeviations = new double[nooftargets];
		testdeviations = new double[nooftargets];
		for (int i = 0; i < nooftargets; i++) {
			// first calculate crossval deviations
			averages[i] = 0;
			for (int j = 0; j < crossvalpatterns.length; j++) {
				averages[i] += crossvalpatterns[j].target[i];
			}
			averages[i] /= crossvalpatterns.length;
			// now calculate deviations
			crossvaldeviations[i] = 0;
			for (int j = 0; j < crossvalpatterns.length; j++) {
				crossvaldeviations[i] += Math.abs(crossvalpatterns[j].target[i] - averages[i]);
			}
			crossvaldeviations[i] = crossvaldeviations[i] * 2 / crossvalpatterns.length;
			
			// then calculate test deviations
			averages[i] = 0;
			for (int j = 0; j < testpatterns.length; j++) {
				averages[i] += testpatterns[j].target[i];
			}
			averages[i] /= testpatterns.length;
			// now calculate deviations
			testdeviations[i] = 0;
			for (int j = 0; j < testpatterns.length; j++) {
				testdeviations[i] += Math.abs(testpatterns[j].target[i] - averages[i]);
			}
			testdeviations[i] = testdeviations[i] * 2 / testpatterns.length;
		}	
	}
		
}