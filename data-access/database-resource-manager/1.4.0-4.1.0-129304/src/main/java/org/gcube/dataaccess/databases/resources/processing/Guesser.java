package org.gcube.dataaccess.databases.resources.processing;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.resources.DBResource;

/**
 * Class that uses as inputs the available information (platform, driver and
 * dialect) to determine the database's type
 */

public class Guesser {

	private String db = ""; // the database's type

	// Method that determines the database's type using the available
	// information as platform, driver and dialect and set these parameters to
	// the correct values if they are not specified according to a well-formed
	// mode.
	public String guessDB(DBResource obj, int index) throws Exception{

		AnalysisLogger.getLogger().debug(
				"In class Guesser->starting the guess process");

//		String platform = "";

		if ((obj.getPlatformName().trim().equals(""))
				&& (obj.getAccessPoints().get(index).getDriver().equals(""))
				&& (obj.getAccessPoints().get(index).getDialect().equals(""))
				&& (obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("mysql"))) { // it is used the 'mysql'
												// driver's name

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the 'mysql' driver's name");

			// System.out.println("Set condition default");

			db = "mysql";

			obj.setPort("3306");

			// return db;

		}

		else if ((obj.getPlatformName().trim().equals(""))
				&& (obj.getAccessPoints().get(index).getDriver().equals(""))
				&& (obj.getAccessPoints().get(index).getDialect().equals(""))
				&& (obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("postgis"))) { // it is used the 'postgis'
													// dialect's name

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the address information: "
							+ obj.getAccessPoints().get(index).address()
									.toLowerCase());

			db = "postgis";

			obj.setPort("5432");

		}

		if ((obj.getPlatformName().trim().equals(""))
				&& (obj.getAccessPoints().get(index).getDriver().equals(""))
				&& (obj.getAccessPoints().get(index).getDialect().equals(""))
				&& (!(obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("mysql")))
				&& (!(obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("postgres")))) { // in this case there are not
													// enough information so the
													// database's name and the
													// port number are set to
													// the default values.

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number are set to the default values");

			db = "postgres";

			obj.setPort("5432");

		}

		if ((obj.getPlatformName().trim().equals(""))
				&& (obj.getAccessPoints().get(index).getDriver().equals(""))
				&& (obj.getAccessPoints().get(index).getDialect().equals(""))
				&& (obj.getAccessPoints().get(index).address().toLowerCase()
						.contains("postgres"))) { // it is used the 'postgres'
													// driver's name

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the address information: "
							+ obj.getAccessPoints().get(index).address()
									.toLowerCase());

			db = "postgres";

			obj.setPort("5432");

		}

		if (((obj.getPlatformName() != ""))
				&& (obj.getPlatformName().toLowerCase().contains("mysql"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the platform's name: "
							+ obj.getPlatformName().toLowerCase());

			db = "mysql";
			obj.setPort("3306");

		} else if ((obj.getAccessPoints().get(index).getDriver() != "")
				&& (obj.getAccessPoints().get(index).getDriver().toLowerCase()
						.contains("mysql"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the driver's name: "
							+ obj.getAccessPoints().get(index).getDriver()
									.toLowerCase());

			db = "mysql";
			obj.setPort("3306");
		} else if ((obj.getAccessPoints().get(index).getDialect() != "")
				&& (obj.getAccessPoints().get(index).getDialect().toLowerCase()
						.contains("mysql"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the dialect's name: "
							+ obj.getAccessPoints().get(index).getDialect()
									.toLowerCase());

			db = "mysql";
			obj.setPort("3306");

		}

		if ((obj.getPlatformName() != "")
				&& (obj.getPlatformName().toLowerCase().contains("postgres"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the platform's name: "
							+ obj.getPlatformName().toLowerCase());

			db = "postgres";
			obj.setPort("5432");

		} else if ((obj.getAccessPoints().get(index).getDriver() != "")
				&& (obj.getAccessPoints().get(index).getDriver().toLowerCase()
						.contains("postgres"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the driver's name: "
							+ obj.getAccessPoints().get(index).getDriver()
									.toLowerCase());

			db = "postgres";
			obj.setPort("5432");
		} else if ((obj.getAccessPoints().get(index).getDialect() != "")
				&& (obj.getAccessPoints().get(index).getDialect().toLowerCase()
						.contains("postgres"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the dialect's name: "
							+ obj.getAccessPoints().get(index).getDialect()
									.toLowerCase());

			db = "postgres";
			obj.setPort("5432");

		}

		if ((obj.getPlatformName() != "")
				&& (obj.getPlatformName().toLowerCase().contains("postgis"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the platform's name: "
							+ obj.getPlatformName().toLowerCase());

			db = "postgis";
			obj.setPort("5432");

			// }else if
			// ((this.getAccessPoints().get(index).Driver.toLowerCase().contains("postgis"))&&(!(this.getAccessPoints().get(index).Driver.equals("")))){
		} else if ((obj.getAccessPoints().get(index).getDriver() != "")
				&& (obj.getAccessPoints().get(index).getDriver().toLowerCase()
						.contains("postgis"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the driver's name: "
							+ obj.getAccessPoints().get(index).getDriver()
									.toLowerCase());

			db = "postgis";
			obj.setPort("5432");

			// }else if
			// ((this.getAccessPoints().get(index).getDialect()!="")&&(this.getAccessPoints().get(index).getDialect().toLowerCase().contains("postgis")))
			// {
		} else if ((obj.getAccessPoints().get(index).getDialect() != "")
				&& (obj.getAccessPoints().get(index).getDialect().toLowerCase()
						.contains("postgis"))) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the dialect's name: "
							+ obj.getAccessPoints().get(index).getDialect()
									.toLowerCase());

			db = "postgis";
			obj.setPort("5432");

		} else if (obj.getAccessPoints().get(index).address().toLowerCase()
				.contains("postgis")) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number determined using the address information: "
							+ obj.getAccessPoints().get(index).address()
									.toLowerCase());

			db = "postgis";
			obj.setPort("5432");

		}

		if (db.equals("")) {

			AnalysisLogger
					.getLogger()
					.debug("In class Guesser->database's name and port number are set to default values because the database's type is not determined : "
							+ obj.getAccessPoints().get(index).address()
									.toLowerCase());

			db = "postgres";
			obj.setPort("5432");

		}

		// 'Set' process of the platform, driver and dialect parameters

		// Set Platform's name Operation

		if ((db.equals("mysql")) || (db.equals("postgres"))) {

			obj.setPlatformName(db);

			AnalysisLogger.getLogger().debug(
					"In class Guesser->setting platform's name: "
							+ obj.getPlatformName());

		} else if (db.equals("postgis")) {

			obj.setPlatformName("postgres");

			AnalysisLogger.getLogger().debug(
					"In class Guesser->setting platform's name: "
							+ obj.getPlatformName());

		}

		// Set Driver's name Operation

		if ((obj.getAccessPoints().get(index).getDriver() == "")
				|| (!(obj.getAccessPoints().get(index).getDriver()
						.contains(".")))) {

			if (db.contains("postgres")) {

				obj.getAccessPoints().get(index)
						.SetDriver("org.postgresql.Driver");

				AnalysisLogger.getLogger().debug(
						"In class Guesser->setting driver's name: "
								+ obj.getAccessPoints().get(index).getDriver());

			} else if (db.contains("postgis")) {

				obj.getAccessPoints().get(index)
						.SetDriver("org.postgresql.Driver");

				AnalysisLogger.getLogger().debug(
						"In class Guesser->setting driver's name: "
								+ obj.getAccessPoints().get(index).getDriver());

			} else if (db.contains("mysql")) {

				obj.getAccessPoints().get(index)
						.SetDriver("com.mysql.jdbc.Driver");

				AnalysisLogger.getLogger().debug(
						"In class Guesser->setting driver's name: "
								+ obj.getAccessPoints().get(index).getDriver());
			}

		}

		// Set Dialect's name operation
		if ((obj.getAccessPoints().get(index).getDialect() == "")
				|| (!(obj.getAccessPoints().get(index).getDialect()
						.contains(".")))) {

			if (db.contains("postgres")) {

				obj.getAccessPoints().get(index)
						.SetDialect("org.hibernate.dialect.PostgreSQLDialect");

				AnalysisLogger
						.getLogger()
						.debug("In class Guesser->setting dialect's name: "
								+ obj.getAccessPoints().get(index).getDialect());

			} else if (db.contains("postgis")) {

				obj.getAccessPoints()
						.get(index)
						.SetDialect(
								"org.hibernatespatial.postgis.PostgisDialect");

				AnalysisLogger
						.getLogger()
						.debug("In class Guesser->setting dialect's name: "
								+ obj.getAccessPoints().get(index).getDialect());

			} else if (db.contains("mysql")) {

				obj.getAccessPoints().get(index)
						.SetDialect("org.hibernate.dialect.MySQLDialect");

				AnalysisLogger
						.getLogger()
						.debug("In class Guesser->setting dialect's name: "
								+ obj.getAccessPoints().get(index).getDialect());
			}

		}

		return db;

	}

	// it returns the db field of the object Guesser
	public String getDB() {

		return db;

	}

}
