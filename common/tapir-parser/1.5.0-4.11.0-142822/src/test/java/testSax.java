import java.util.Iterator;

import org.gcube.data.spd.parser.DarwinSimpleRecord;
import org.gcube.data.spd.parser.RecordsIterator;
import org.xml.sax.SAXException;


public class testSax {
	static int start = 0;
	static String filter = "http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22Jacarand%22&orderBy=http://rs.tdwg.org/dwc/dwcore/ScientificName&orderBy=http://rs.tdwg.org/dwc/dwcore/InstitutionCode";

	public static final String baseurl = "http://tapir.cria.org.br/tapirlink/tapir.php/specieslink";
	public static final String model = "http://rs.tdwg.org/tapir/cs/dwc/1.4/model/dw_core_geo_cur.xml";
	public static final int limit = 100;
	
	public static void main(String[] args) throws SAXException {
		
		RecordsIterator set = new RecordsIterator(baseurl,filter, model, limit, false);
		Iterator<DarwinSimpleRecord> it = set.iterator();
		DarwinSimpleRecord element;

		while (it.hasNext()) {
//			System.out.println("it.next()");
			element = it.next();
			System.out.println(element.globalUniqueIdentifier + " - " + element.scientificName);
//			System.out.println(element.scientificName);
//			System.out.println((element.dateLastModified).getTime());
//			System.out.println(element.authorYearOfScientificName);
//			System.out.println(element.family);
//			System.out.println(element.verbatimLongitude);
//			System.out.println(element.decimalLatitude);
//			System.out.println(element.decimalLongitude);

		}
}

}
