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
 * Filename: GetGHNsAlive.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.util.List;
import java.util.Vector;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.XMLResult.ISResultEvaluationException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GetGHNsAlive {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final String query =
			"let $now := current-dateTime()\n" +
			"for $GHNs in collection(\"/db/Profiles/GHN\")//Document/Data/child::*[local-name()='Profile']/Resource\n" +
			"let $RIs := collection(\"/db/Profiles/RunningInstance\")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]\n" +
			"let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)\n" +
			"let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))\n" +
			"where $GHNs/Profile/GHNDescription/Type eq 'Dynamic'\n" +
			"	and $totalminutes le 40\n" +
			"	and count($RIs) gt 0\n" +
			"return\n" +
			"<RIONGHN>\n" +
			"{$GHNs/ID}\n" +
			"	<AllocatedRI>{count($RIs)}</AllocatedRI>\n" +
			"	{$GHNs/Profile/GHNDescription/LastUpdate}\n" +
			"	<UpdateMinutesElapsed>{$totalminutes}</UpdateMinutesElapsed>\n" +
			"	<ProfileXML>\n" +
			"	    {$GHNs/Scopes}\n" +
			"	    {$GHNs/Profile/GHNDescription}\n" +
			"	    {$GHNs/Profile/Site}\n" +
			"	</ProfileXML>\n" +
			"</RIONGHN>";

		// TODO Auto-generated method stub
		GCUBEScope queryScope = GCUBEScope.getScope("/gcube/devsec");

		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery isQuery = null;
		isQuery = client.getQuery(GCUBEGenericQuery.class);
		isQuery.setExpression(query);
		System.out.println("*** Applying query to SCOPE [" + queryScope.toString() + "]");
		System.out.println("*** Evaluating: " + isQuery.getExpression());
		List<XMLResult> results = client.execute(isQuery, queryScope);
		List<GHNDescriptor> retval = new Vector<GHNDescriptor>();

		for (XMLResult elem : results) {
			try {
				List<String> riNums =  elem.evaluate("/RIONGHN/AllocatedRI/text()");
				List<String> ghnIDs = elem.evaluate("/RIONGHN/ID/text()");
				GHNDescriptor descr = new GHNDescriptor(
						// The number of allocated RI
						Integer.parseInt(riNums.get(0)),
						// The ID of ghn
						ghnIDs.get(0),
						queryScope,
						elem
					);
				retval.add(descr);
				System.out.println("*** adding descriptor: " + descr.getID() + " load[" + descr.getLoadLast1M() + ", " + descr.getLoadLast5M() + ", " + descr.getLoadLast15M() + "]");
			} catch (ISResultEvaluationException e) {
				e.printStackTrace();
			}
		}
		System.out.println(retval);
	}

}
