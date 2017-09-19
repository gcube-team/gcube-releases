package org.gcube.dataaccess.databases.resources.processing;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.resources.DBResource;

/**
 * Class that, taking the url as input, performs a parsing process of the field
 * Url through a tree decision
 */
public class Decider {

	// Method that implements the tree decision to parse and build the field
	// Url.
	public static void decide(DBResource obj, int index) throws Exception{

		AnalysisLogger.getLogger().debug(
				"In class Decider->starting the parsing process");

		String EntireUrl = "";

		String[] SplitOne = null;

		boolean varone = false;
		boolean var = false;

		if (obj.getAccessPoints().get(index).address().contains("//")) { // the
																			// url
																			// contains
																			// the
																			// character
																			// "//"

			AnalysisLogger.getLogger().debug(
					"In class Decider->the url contains the character '//' ");

			varone = true;

			SplitOne = obj.getAccessPoints().get(index).address().split("//"); // Split
																				// on
																				// the
																				// node
																				// of
																				// the
																				// tree

			AnalysisLogger.getLogger().debug(
					"In class Decider->split operation on '//' ");

			AnalysisLogger.getLogger().debug(
					"In class Decider->SplitOne's lenght: " + SplitOne.length);

			// Test Print
			for (int i = 0; i < SplitOne.length; i++) {

				AnalysisLogger.getLogger().debug(
						"In class Decider->Split_one: " + SplitOne[i]);

			}

			if (SplitOne.length > 1) { // with the split operation there is two
										// parts on the left of "//" and on the
										// right of "//".

				// try to build the first part of the string url

				// recover the url in the left part of the url

				if (SplitOne[0].length() == 0) { // There is not information on
													// the left of "//".
					// Left Node LevelTree=2

					EntireUrl = EntireUrl + "jdbc" + ":";

					if (obj.getPlatformName().toLowerCase().contains("mysql")) {

						EntireUrl = EntireUrl + "mysql" + ":";

					} else {

						EntireUrl = EntireUrl + "postgresql" + ":";

					}

					AnalysisLogger.getLogger().debug(
							"In class Decider->result: " + EntireUrl);

				}

				// Split operation on the Left Node LevelTree=2

				else { // there is information on the left of "//"

					// if (SplitOne[0].contains(":")){
					String[] SplitTwo = SplitOne[0].split(":");

					// System.out.println("split ':' one");

					AnalysisLogger.getLogger().debug(
							"In class Decider->split operation on '/'");

					AnalysisLogger.getLogger().debug(
							"In class Decider->Split_two's lenght: "
									+ SplitTwo.length);

					// Test Print
					for (int i = 0; i < SplitTwo.length; i++) {

						AnalysisLogger.getLogger().debug(
								"In class Decider->Split_two: " + SplitTwo[i]);

					}

					// check on the lenght

					if (SplitTwo.length == 2) { // the two strings related to
												// "jdbc" and the driver's name
												// are presented.

						if ((obj.getPlatformName().toLowerCase()
								.contains("postgres"))) {

							AnalysisLogger
									.getLogger()
									.debug("In class Decider->setting the url using the driver");

							EntireUrl = SplitTwo[0] + ":" + "postgresql" + ":";
						}
						if (obj.getPlatformName().toLowerCase()
								.contains("mysql")) {

							EntireUrl = SplitTwo[0] + ":" + "mysql" + ":";

						}

					} else { // there is one string: or the "jdbc" or the
								// driver's name.

						if (SplitTwo[0].toLowerCase().equals("jdbc")) {

							EntireUrl = "jdbc" + ":";

							// if
							// ((this.getPlatformName().toLowerCase().contains("postgres"))||(this.getPlatformName().toLowerCase().contains("postgis"))){

							if ((obj.getPlatformName().toLowerCase()
									.contains("postgres"))) {
								EntireUrl = EntireUrl + "postgresql" + ":";

							}

							if (obj.getPlatformName().toLowerCase()
									.contains("mysql")) {

								EntireUrl = EntireUrl + "mysql" + ":";

							}

						} else { // there is the driver's name. I check the
									// variable db, set by the method guessDB,
									// to set the url properly.

							if (obj.getPlatformName().toLowerCase()
									.contains("postgres")) {

								AnalysisLogger
										.getLogger()
										.debug("In class Decider->setting the url using the driver postgres");

								EntireUrl = "jdbc" + ":" + "postgresql" + ":";

							}

							else if (obj.getPlatformName().toLowerCase()
									.contains("mysql")) {

								AnalysisLogger
										.getLogger()
										.debug("In class Decider->setting the url using the driver mysql");

								EntireUrl = "jdbc" + ":" + "mysql" + ":";

							}

						}

					}
					// }

				}

			} else { // with the split operation there is one part on the left
						// of "//".

				EntireUrl = obj.getAccessPoints().get(index).address();

				if ((obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("postgres"))
						|| (obj.getAccessPoints().get(index).address()
								.toLowerCase().contains("postgis"))) {

					EntireUrl = "jdbc:postgresql://";

				}
				if (obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("mysql")) {

					EntireUrl = "jdbc:mysql://";

				}

				// the url is built using the available information.
				EntireUrl = EntireUrl + obj.getHostedOn() + ":" + obj.getPort()
						+ "/"
						+ obj.getAccessPoints().get(index).getDatabaseName();

				AnalysisLogger.getLogger().debug(
						"In class Decider->result: " + EntireUrl);

				var = true;
			}

		}

		if ((obj.getAccessPoints().get(index).address().contains("/"))
				&& (var == false)) { // it manages several cases. It is selected
										// if: 1) there is only the part on the
										// right of "//", 2) there is the entire
										// string, 3) there is the part on the
										// right of "/"

			// System.out.println("manage '/'");

			AnalysisLogger.getLogger().debug(
					"In class Decider->the url contains characther '/'");

			if (varone == true) { // There is the hostname, the port number or
									// both. A split on "/" is performed to
									// retrieve the database's name

				// Split operation on the Right Node LevelTree=2

				String[] SplitThree = SplitOne[1].split("/");

				AnalysisLogger.getLogger().debug(
						"In class Decider->split operation on '/'");

				AnalysisLogger.getLogger().debug(
						"In class Decider->Split_three's lenght: "
								+ SplitThree.length);

				for (int i = 0; i < SplitThree.length; i++) {

					AnalysisLogger.getLogger().debug(
							"In class Decider->Split_three: " + SplitThree[i]);

				}

				if (SplitThree[0].length() == 0) { // There are not the hostname
													// and the port number.
					// Left Node LevelTree= 3

					EntireUrl = EntireUrl + "//" + obj.getHostedOn() + ":"
							+ obj.getPort();

				} else { // Recovery host and the port number

					String[] SplitFour = SplitThree[0].split(":");

					AnalysisLogger.getLogger().debug(
							"In class Decider->split operation on ':'");

					AnalysisLogger.getLogger().debug(
							"In class Decider->Split_four's lenght: "
									+ SplitFour.length);

					for (int i = 0; i < SplitFour.length; i++) {

						AnalysisLogger.getLogger()
								.debug("In class Decider->Split_four: "
										+ SplitFour[i]);

					}

					if (SplitFour[0].length() == 0) { // there is not the
														// hostname.
						// Left Node LevelTree=4

						EntireUrl = EntireUrl + "//" + obj.getHostedOn();
					} else { // there is the hostname
						obj.setHostedOn(SplitFour[0]);

						EntireUrl = EntireUrl + "//" + SplitFour[0];

					}

					if (SplitFour.length > 1) { // the url contains the port
												// number too.

						// the url contains the port number

						obj.setPort(SplitFour[1]);

						EntireUrl = EntireUrl + ":" + SplitFour[1];

					} else {
						// the url does not contain the port number

						EntireUrl = EntireUrl + ":" + obj.getPort();

					}

				}

				if (SplitThree.length > 1) { // Right Node LevelTree= 3
					// the url contains the database's name

					obj.getAccessPoints().get(index)
							.setDatabaseName(SplitThree[1]);

					EntireUrl = EntireUrl + "/" + SplitThree[1];

				}

				else {

					// The url does not contain the database's name

					EntireUrl = EntireUrl
							+ "/"
							+ obj.getAccessPoints().get(index)
									.getDatabaseName();

				}

			}

			else { // there is only the database's name

				String[] SplitThree = obj.getAccessPoints().get(index)
						.address().split("/");

				AnalysisLogger.getLogger().debug(
						"In class Decider->split operation on '/'");

				obj.getAccessPoints().get(index).setDatabaseName(SplitThree[1]);

				if (SplitThree[0].length() == 0) { // only the database's name
													// is retrieved

					// if
					// ((this.getPlatformName().equals("postgres"))||(this.getPlatformName().equals("postgis"))){
					if ((obj.getPlatformName().equals("postgres"))) {

						EntireUrl = "jdbc:" + "postgresql" + ":" + "//"
								+ obj.getHostedOn() + ":" + obj.getPort() + "/"
								+ SplitThree[1];

					}

					if (obj.getPlatformName().toLowerCase().contains("mysql")) {

						EntireUrl = "jdbc:" + "mysql" + ":" + "//"
								+ obj.getHostedOn() + ":" + obj.getPort() + "/"
								+ SplitThree[1];

					}
				}

				if (SplitThree[0].length() != 0) { // on the left of "/" there
													// are other information

					String[] SplitTwo = SplitThree[0].split(":");

					AnalysisLogger.getLogger().debug(
							"In class Decider->split operation on ':'");

					AnalysisLogger.getLogger().debug(
							"In class Decider->Split_two's lenght");

					for (int i = 0; i < SplitTwo.length; i++) {

						AnalysisLogger.getLogger().debug(
								"In class Decider->Split_two: " + SplitTwo[i]);

					}

					// check on the lenght

					if (SplitTwo.length == 2) { // The two strings "jdbc" and
												// driver's name are presented.

						// sono presenti stringa "jdbc" e "nome driver"

						// *this.getAccessPoints().get(index).SetDriver(SplitTwo[1]);

						if ((SplitTwo[1].toLowerCase().contains("postgres"))
								|| (SplitTwo[1].toLowerCase()
										.contains("postgis"))) {

							EntireUrl = SplitTwo[0] + ":" + "postgresql" + ":";
						}
						if (SplitTwo[1].toLowerCase().contains("mysql")) {

							EntireUrl = SplitTwo[0] + ":" + "mysql" + ":";

						}

						// EntireUrl=SplitTwo[0]+":"+SplitTwo[1]+":";

						// if
						// ((this.getPlatformName().toLowerCase().contains("postgres"))||(this.getPlatformName().toLowerCase().contains("postgis"))){
						if ((obj.getPlatformName().toLowerCase()
								.contains("postgres"))) {

							EntireUrl = EntireUrl + "//" + obj.getHostedOn()
									+ ":" + obj.getPort() + "/" + SplitThree[1];

						}

						if (obj.getPlatformName().toLowerCase()
								.contains("mysql")) {

							EntireUrl = EntireUrl + "//" + obj.getHostedOn()
									+ ":" + obj.getPort() + "/" + SplitThree[1];

						}

					} else { // only one string between "jdbc" or driver's name
								// is presented

						if (SplitTwo[0].toLowerCase().equals("jdbc")) { // the
																		// string
																		// "jdbc"
																		// is
																		// presented

							EntireUrl = "jdbc" + ":";

							if ((obj.getPlatformName().toLowerCase()
									.contains("postgres"))
									|| (obj.getPlatformName().toLowerCase()
											.contains("postgis"))) {

								EntireUrl = EntireUrl + "postgresql" + ":"
										+ "//" + obj.getHostedOn() + ":"
										+ obj.getPort() + "/" + SplitThree[1];

							}

							if (obj.getPlatformName().toLowerCase()
									.contains("mysql")) {

								EntireUrl = EntireUrl + "mysql" + ":" + "//"
										+ obj.getHostedOn() + ":"
										+ obj.getPort() + "/" + SplitThree[1];

							}

						} else { // the string related to the driver's name is
									// presented

							// EntireUrl="jdbc"+":"+SplitTwo[0]+":"+"//"+this.getHostedOn()+":"+this.getPort()+"/"+SplitThree[1];;
							// *this.getAccessPoints().get(index).SetDriver(SplitTwo[0]);

							if ((obj.getDBguessed().contains("postgis"))
									|| (obj.getDBguessed().contains("postgres"))) {
								// EntireUrl="jdbc"+":"+SplitTwo[0]+":"+"//"+this.getHostedOn()+":"+this.getPort()+"/"+SplitThree[1];}

								// *this.getAccessPoints().get(index).SetDriver(SplitTwo[0]);

								EntireUrl = "jdbc" + ":" + "postgresql" + ":"
										+ "//" + obj.getHostedOn() + ":"
										+ obj.getPort() + "/" + SplitThree[1];
							}
							if (obj.getDBguessed().contains("mysql")) {

								EntireUrl = "jdbc" + ":" + "mysql" + ":" + "//"
										+ obj.getHostedOn() + ":"
										+ obj.getPort() + "/" + SplitThree[1];

							}

						}

					}

				}

			}

		}

		// if(this.getAccessPoints().get(index).address().contains(":")){}

		obj.getAccessPoints().get(index).setUrl(EntireUrl);

		AnalysisLogger.getLogger().debug(
				"In class Decider->Url normalized: "
						+ obj.getAccessPoints().get(index).address());

	}

}
