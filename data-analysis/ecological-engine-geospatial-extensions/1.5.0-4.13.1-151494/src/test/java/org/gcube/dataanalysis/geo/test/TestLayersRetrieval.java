package org.gcube.dataanalysis.geo.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.Keywords;
import org.opengis.util.InternationalString;

public class TestLayersRetrieval {

	static String cfg = "./cfg/";
	//TODO: filter WoA names and attach them to the title
	public static void main(String[] args) throws Exception{
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		GeoNetworkInspector featurer = new GeoNetworkInspector();
		featurer.setScope(null);
		List<Metadata> metae = featurer.getAllGNInfobyText("thredds", "1");
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		String d = "#";
		System.out.println("Parameter Name"+d+"Time Range"+d+"Dimensions"+d+"Unit of Measure"+d+"Resolution (decimal degrees)"+d+"Details");
		List<String> table = new ArrayList<String>();
		for (Metadata meta:metae){
			Identification id = meta.getIdentificationInfo().iterator().next();
			String title = id.getCitation().getTitle().toString();
			String abstractF = id.getAbstract().toString();
			
			DefaultDataIdentification did = (DefaultDataIdentification) id;
			double resolution = MathFunctions.roundDecimal(did.getSpatialResolutions().iterator().next().getDistance(),3);
			Collection<? extends Keywords> keys = id.getDescriptiveKeywords();
			String unit = "";
			for (Keywords key:keys){
				for(InternationalString string:key.getKeywords()) {
					String ss = string.toString();
					if (ss.startsWith("unit:"))
						unit = ss.substring(ss.indexOf(":")+1);
				}
			}
			String[] elements =null;
			if (title.contains("Bio-Oracle"))
			{
				elements = new String[4];
				elements[0]=title;
				elements[1]=abstractF.substring(abstractF.indexOf("Aggregated"),abstractF.lastIndexOf("]")+1);
				elements[2]="2D";
				unit = abstractF.substring(abstractF.indexOf("(")+1,abstractF.lastIndexOf(")"));
				elements[3]=abstractF;
			}
			else if (title.contains("GEBCO"))
			{
				elements = new String[4];
				elements[0]=title;
				elements[1]="2008";
				elements[2]="3D";
				unit = "m";
				elements[3]=abstractF;
			}
			else if (title.contains("WorldClimBio")||title.contains("Etna")||title.contains("OpenModeller"))
			{
				continue;
			}
			else
				elements = parseTitle(title);
			String entry = elements[0]+d+elements[1]+d+elements[2]+d+unit+d+resolution+d+elements[3];
			if (!table.contains(entry)){
				table.add(entry);
//				System.out.println(elements[0]+d+elements[1]+d+elements[2]+d+resolution+d+elements[3]);
			}
		}
		Collections.sort(table);
		for (String element:table){
			System.out.println(element);
		}
//		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
	}
	
	
	public static void main1(String[] args) throws Exception{
//		String example = "Standard Deviation from Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String example = "Salinity from [12-15-99 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-grids}";
		String example = "Salinity from [12-15-99 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-grids}";
		parseTitle(example);
	}
	
	public static String[] parseTitle(String title){
//		System.out.println("Parsing Title:"+title);
		String timerange = title.substring(title.indexOf("["),title.lastIndexOf("]")+1);
//		timerange = timerange.replace("] to [", " ; ");
//		System.out.println(timerange);
		String realtitle = title.substring(0,title.indexOf("[")).trim();
		realtitle = realtitle.substring(0,realtitle.lastIndexOf(" ")).trim();
//		System.out.println(realtitle);
		String dimensions = title.substring(title.indexOf("] (")+3);
		dimensions = dimensions.substring(0,dimensions.indexOf(")")).trim();
//		System.out.println(dimensions);
		String notes = title.substring(title.indexOf("{")+1,title.lastIndexOf("}"));
		String woa = "World Ocean Atlas 09:";
		String prefixnote = "";
		if (notes.startsWith(woa)){
			prefixnote = notes.substring(woa.length()+1);
			prefixnote = prefixnote.substring(0,prefixnote.indexOf(":")).trim()+": "; 
		}
//		System.out.println(notes);
		String[] elements = new String[]{prefixnote+realtitle, timerange, dimensions,notes};
		return elements;
	}
	
}
