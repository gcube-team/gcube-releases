package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.vremanagement.resourcebroker.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcebroker.utils.performance.PerformanceMonitor;

/**
 * The configuration for handling command line requests.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
class StandaloneISQueryTestConf {
	/** The file to load. [REQUIRED] */
	protected String queryFile = null;
	/** The token to use for comments */
	protected final String commentToken = "#";
	/** Where to print the output (optional). */
	protected PrintStream output = System.out;
	/** The scope in which the query should be executed (optional). */
	// the same given in Configuration
	GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");
	/** The current version. */
	public final String CURRENT_VERSION = "1.0.0";
}

/**
 * General purpose test class introduced to directly test the custom queries
 * to IS.
 * A list of queries can be given in a file e.g. /tmp/myfile.query and
 * all the groups of lines are supposed to be single queries to execute to the IS.
 *
 * <pre>
 * <b>File sample:</b>
 *
 * 	# First Query
 * 	# That's a comment
 *	for $Profile in
 *	collection("/db/Profiles")//Document/Data/child::*[local-name()='Profile']/Resource
 *	return $Profile/ID
 *
 *	# Leave a <i>BLANK</i> line among multiple queries
 *	# Use this for comments, blank lines will be ignored.
 * 	for $result in collection("/db/Profiles/GHN")
 * 	return $result
 * </pre>
 *<p>
 * The only required input is the file containing the queries to execute.
 *</p>
 *<pre>
 * <b>How to execute:</b>
 * 	<i>java {@link org.gcube.vremanagement.resourcebroker.local.testsuite.StandaloneISQueryTest} file.query</i>
 *</pre>
 *<p><b>Note:</b> <i>It is possible to express multi-line queries as shown in the previous example. The blank line
 * (or the comment token #) will delimit the end of a multi-line query expression.</i>
 * </p>
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class StandaloneISQueryTest extends GCUBEPortType {
	private PerformanceMonitor timer = new PerformanceMonitor();
	private GCUBEClientLog logger = null;
	private StandaloneISQueryTestConf conf = new StandaloneISQueryTestConf();


	public StandaloneISQueryTest() {
		this.logger = new GCUBEClientLog(StandaloneISQueryTest.class);
	}

	/**
	 * Used by {@link StandaloneISQueryTest} to initialize the menu.
	 */
	protected final Options buildMenu() {
		Option help = new Option("h", "help", false, "print this message.");
		Option about = new Option("about", "give information about this product.");
		Option version = new Option("version", "print the version information.");
		Option scope = new Option("s", "scope", true,
				"specify scope to use for quering the IS. The default value is set to: "
				+ this.conf.scope.getName());
		scope.setArgName("scope");
		Option output = new Option("o", "output", true,
		"the file where the output will be stored. By default it will be printed on the standard output.");
		Options options = new Options();
		options.addOption(help);
		options.addOption(about);
		options.addOption(version);
		options.addOption(scope);
		options.addOption(output);
		return options;
	}

	/**
	 * How the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerServiceTest} handles the input from the command line.
	 * @param args
	 */
	private int handleInput(final String[] args) {
		Options options = this.buildMenu();
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// ABOUT
			if (line.hasOption("about")) {
				System.out.println("****************************************************************************" + "\n" +
						"*  This software is part of the gCube Project." + "\n" +
						"*  Site: http://www.gcube-system.org/" + "\n" +
						"****************************************************************************" + "\n" +
						"* The gCube/gCore software is licensed as Free Open Source software" + "\n" +
						"* conveying to the EUPL (http://ec.europa.eu/idabc/eupl)." + "\n" +
						"* The software and documentation is provided by its authors/distributors" + "\n" +
						"* \"as is\" and no expressed or" + "\n" +
						"* implied warranty is given for its use, quality or fitness for a" + "\n" +
						"* particular case." + "\n" +
						"****************************************************************************" + "\n" +
						"* Daniele Strollo (ISTI-CNR)" + "\n" +
						"****************************************************************************" + "\n");
				return -1;
			}
			// VERSION
			if (line.hasOption("version")) {
				System.out.println(StandaloneISQueryTest.class.getSimpleName() + " version " + this.conf.CURRENT_VERSION);
				return -1;
			}

			// SCOPE PARAM
			if (line.hasOption("s")) {
				System.out.println("Setting GCUBEScope to... " + line.getOptionValue("scope"));
				this.conf.scope = GCUBEScope.getScope(line.getOptionValue("scope"));
			}

			// OUTPUT PARAM
			if (line.hasOption("o")) {
				System.out.println("Setting output file to... " + line.getOptionValue("output"));
				this.conf.output = new PrintStream(new File(line.getOptionValue("output")));
			}

			// HANDLE THE MENU
			if (line.hasOption("help") || line.getArgs().length < 1) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(
						StandaloneISQueryTest.class.getSimpleName() + " [OPTION]... <QUERY_FILE>",
						// header
						"Check in the tests folder for some samples."	,
						options,
						// footer
				"");
				return -1;
			}

			// SET the input file
			this.conf.queryFile = line.getArgs()[0];

		} catch (Exception exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			return -1;
		}

		return 0;
	}

	/**
	 * Internally used.
	 * Executes the single query passed in input and prints the
	 * result on the output stream specified in
	 * {@link StandaloneISQueryTestConf#output}.
	 *
	 * @param query the single query to submit to the IS
	 */
	public final void doJob(final String query) {
		timer.start();
		logger.debug("*** ISClientRequester accessing the IS");
		List<XMLResult> results = null;
		try {
			logger.info("\nExecuting query: \n" + query);
			ISClient client = GHNContext.getImplementation(ISClient.class);

			GCUBEGenericQuery isQuery = null;
			isQuery = client.getQuery(GCUBEGenericQuery.class);
			isQuery.setExpression(query);
			logger.debug("*** Applying query to SCOPE [" + this.conf.scope.toString() + "]");
			results = client.execute(isQuery, this.conf.scope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.conf.output.println("*********************************************************************************");
		this.conf.output.println("QUERY: \n" + query);
		this.conf.output.println("SCOPE: " + this.conf.scope);
		this.conf.output.println("*********************************************************************************");
		if (results == null || results.isEmpty()) {
			this.conf.output.println("NO RESULTS!!! \n\n");
		} else {
			this.conf.output.println(results + "\n\n");
		}
		timer.stop();
	}

	public static void main(final String[] args) throws Exception {
		StandaloneISQueryTest tester = new StandaloneISQueryTest();
		// PARSES the input parameters
		int result = tester.handleInput(args);
		if (result == -1) {
			return;
		}

		BufferedReader in = new BufferedReader(new FileReader(tester.conf.queryFile));
		StringBuilder query = null;
		String currLine = null;

		while ((currLine = in.readLine()) != null) {
			// a comment
			if (currLine.trim().length() > 0 && currLine.trim().startsWith(tester.conf.commentToken)) { continue; }
			if (currLine.trim().length() == 0) { continue; }
			query = new StringBuilder(currLine);
			while ((currLine = in.readLine()) != null
					&& currLine.trim().length() != 0
					&& !currLine.trim().startsWith(tester.conf.commentToken)) {
				query.append("\n" + currLine);
			}
			tester.doJob(query.toString());
		}
	}

	@Override
	protected final GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
