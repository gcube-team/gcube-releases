package org.gcube.dataaccess.databases.resources.processing;

import java.io.IOException;
import java.net.UnknownHostException;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataaccess.databases.resources.processing.Guesser;
import org.gcube.dataaccess.databases.resources.processing.Normalizer;


/** Class that performs the normalization process using the available information specified from the user */

public class Normalizer {

	/* It performs the normalization process considering as input an access point field of a DBResource resource */
	public static void normalize(DBResource obj, int index) throws Exception {

		AnalysisLogger.getLogger().debug(
				"In class Normalizer->starting to guess the database's type");
		
		Guesser guess=new Guesser();

		String db = guess.guessDB(obj, index);

		obj.setDBguessed(db);

		AnalysisLogger.getLogger().debug(
				"In class Normalizer->starting the normalization process");

		// the Url contains at least the character between the "//", "/" or ":"
		if ((obj.getAccessPoints().get(index).address().contains("//"))
				|| (obj.getAccessPoints().get(index).address().contains("/"))
				|| (obj.getAccessPoints().get(index).address().contains(":"))) 
		{
            
			AnalysisLogger.getLogger().debug(
					"In class Normalizer->calling the parsing process of the url");
			Decider.decide(obj, index);

		} else { // the Url does not contain none of the characters "//", "/" or
					// ":" so there is an indecision to be managed

			AnalysisLogger.getLogger().debug(
					"In class Normalizer->starting to manage an indecision");

			if ((obj.getAccessPoints().get(index).address().equals(obj
					.getHostedOn())) && (!(obj.getHostedOn().equals("")))) { /* the address is the hostname */

				obj.getAccessPoints()
						.get(index)
						.setUrl("//"
								+ obj.getAccessPoints().get(index).address());

				AnalysisLogger.getLogger().debug(
						"In class Normalizer->starting the tree decision process using the hostname");
				
				Decider.decide(obj, index);

			} else if ((!(obj.getAccessPoints().get(index).address().equals("")))
					&& (!(obj.getAccessPoints().get(index).address().equals(obj
							.getHostedOn())) && (obj.getHostedOn().equals("")))) {

				// throw new UnknownHostException("The host is unknown");

				throw new UnknownHostException(
						"the available information are not sufficient to determine the complete address: please fill the field 'Hosted On'");

			} else if ((!(obj.getAccessPoints().get(index).address()
					.equals("jdbc")))
					&& (!(obj.getAccessPoints().get(index).address()
							.toLowerCase().toLowerCase().contains("mysql")))

					&& (!(obj.getAccessPoints().get(index).address()
							.toLowerCase().contains("postgres")))
					&& (!(obj.getAccessPoints().get(index).address()
							.toLowerCase().contains("postgis")))
					&& (!(obj.getAccessPoints().get(index).address()
							.toLowerCase().contains(obj.getPort())))
					&& (!(obj.getAccessPoints().get(index).address().equals("")))) { /* the address is the database's name */

				obj.getAccessPoints()
						.get(index)
						.setUrl("/"
								+ obj.getAccessPoints().get(index).address());

				AnalysisLogger.getLogger().debug(
						"In class Normalizer->starting the tree decision process using the database's name");
				
				Decider.decide(obj, index);

			} else if ((obj.getAccessPoints().get(index).address()
					.toLowerCase().contains("postgres"))
					|| (obj.getAccessPoints().get(index).address()
							.toLowerCase().contains("postgis"))
					|| (obj.getAccessPoints().get(index).address()
							.toLowerCase().contains("mysql"))) {  /* the address is the driver's name */

				obj.getAccessPoints()
						.get(index)
						.setUrl("jdbc:"
								+ obj.getAccessPoints().get(index).address()
								+ "://");

				AnalysisLogger.getLogger().debug(
						"In class Normalizer->starting the tree decision process using the driver's name");

				Decider.decide(obj, index);

			}

			// if ((obj.getAccessPoints().get(index).address().equals(""))){
			if ((obj.getAccessPoints().get(index).address().equals(""))) { /* the address is empty so several available information are used to build the Url */

				AnalysisLogger.getLogger().debug(
						"In class Normalizer->managing the address null");

				// Empty address management

				if (!(obj.getHostedOn().equals(""))
						&& (obj.getHostedOn() != null)) { /* the hostname is used if it is not null. */

//					AnalysisLogger.getLogger().debug(
//							"In class Normalizer->using the hostname"
//									+ obj.getHostedOn());
					
					AnalysisLogger.getLogger().debug(
							"In class Normalizer->managing the address null using the hostname");

					obj.getAccessPoints().get(index).setUrl(obj.getHostedOn());
					
					AnalysisLogger.getLogger().debug(
							"In class Normalizer->recalling the 'normalize' method");

					normalize(obj, index);

				}

			}

		}

	}

}
