package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlObject;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.*;
import org.n52.wps.algorithm.annotation.*;
import org.n52.wps.io.data.*;
import org.n52.wps.io.data.binding.complex.*;
import org.n52.wps.io.data.binding.literal.*;
import org.n52.wps.server.*;import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.AbstractEcologicalEngineMapper;import org.n52.wps.server.*;import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.*;
@Algorithm(statusSupported=false, title="OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA", abstrakt="Algorithm returning most observed species in a specific years range (data collected from OBIS database).", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA", version = "1.1.0")
public class OBIS_SPECIES_OBSERVATIONS_PER_LME_AREA extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Area_type. Choose the area name", allowedValues= {"AGULHAS CURRENT","ANTARCTICA","ARABIAN SEA","BALTIC SEA","BARENTS SEA","BAY OF BENGAL","BEAUFORT SEA","BENGUELA CURRENT","BLACK SEA","CALIFORNIA CURRENT","CANARY CURRENT","CARIBBEAN SEA","CELTIC-BISCAY SHELF","CHUKCHI SEA","EAST BERING SEA","EAST BRAZIL SHELF","EAST CENTRAL AUSTRALIAN SHELF","EAST CHINA SEA","EAST GREENLAND SHELF","EAST SIBERIAN SEA","FAROE PLATEAU","GUINEA CURRENT","GULF OF ALASKA","GULF OF CALIFORNIA","GULF OF MEXICO","GULF OF THAILAND","HUDSON BAY","HUMBOLDT CURRENT","IBERIAN COASTAL","ICELAND SHELF","INDONESIAN SEA","INSULAR PACIFIC-HAWAIIAN","KARA SEA","KUROSHIO CURRENT","LAPTEV SEA","MEDITERRANEAN SEA","NEWFOUNDLAND-LABRADOR SHELF","NEW ZEALAND SHELF","NORTH AUSTRALIAN SHELF","NORTH BRAZIL SHELF","NORTHEAST AUSTRALIAN SHELF","NORTHEAST U.S. CONTINENTAL SHELF","NORTH SEA","NORTHWEST AUSTRALIAN SHELF","NORWEGIAN SEA","OYASHIO CURRENT","PACIFIC CENTRAL-AMERICAN COASTAL","PATAGONIAN SHELF","RED SEA","SCOTIAN SHELF","SEA OF JAPAN","SEA OF OKHOTSK","SOMALI COASTAL CURRENT","SOUTH BRAZIL SHELF","SOUTH CHINA SEA","SOUTHEAST AUSTRALIAN SHELF","SOUTHEAST U.S. CONTINENTAL SHELF","SOUTHWEST AUSTRALIAN SHELF","SULU-CELEBES SEA","WEST BERING SEA","WEST CENTRAL AUSTRALIAN SHELF","WEST GREENLAND SHELF","YELLOW SEA"}, defaultValue="AGULHAS CURRENT", title="Choose the area name", identifier = "Area_type", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setArea_type(String data) {inputs.put("Area_type",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Start_year. Starting year of the analysis", defaultValue="1800", title="Starting year of the analysis", identifier = "Start_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setStart_year(String data) {inputs.put("Start_year",data);}
@LiteralDataInput(abstrakt="Name of the parameter: End_year. Ending year of the analysis", defaultValue="2020", title="Ending year of the analysis", identifier = "End_year", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setEnd_year(String data) {inputs.put("End_year",data);}
@LiteralDataInput(abstrakt="Name of the parameter: Selected species. List of the species to analyze [a sequence of values separated by | ] (format: String)", defaultValue="", title="List of the species to analyze [a sequence of values separated by | ] (format: String)", identifier = "Selected species", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSelected_species(String data) {inputs.put("Selected species",data);}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }