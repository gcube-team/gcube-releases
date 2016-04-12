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
@Algorithm(statusSupported=false, title="ABSENCE_CELLS_FROM_AQUAMAPS", abstrakt="An algorithm producing cells and features (HCAF) for a species containing absense points taken by an Aquamaps Distribution", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.ABSENCE_CELLS_FROM_AQUAMAPS", version = "1.1.0")
public class ABSENCE_CELLS_FROM_AQUAMAPS extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Table_Label. the name of the Filtered Hcaf", defaultValue="AbsenceCells_", title="the name of the Filtered Hcaf", identifier = "Table_Label", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setTable_Label(String data) {inputs.put("Table_Label",data);}
@ComplexDataInput(abstrakt="Name of the parameter: Aquamaps_HSPEC. an Aquamaps table from which to produce the absence points [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", title="an Aquamaps table from which to produce the absence points [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", maxOccurs=1, minOccurs=1, identifier = "Aquamaps_HSPEC", binding = GenericFileDataBinding.class)	public void setAquamaps_HSPEC(GenericFileData file) {inputs.put("Aquamaps_HSPEC",file);}
@LiteralDataInput(abstrakt="Name of the parameter: Take_Randomly. a flag for taking points randomly (true) or close together (false)", defaultValue="true", allowedValues= {"true","false"}, title="a flag for taking points randomly (true) or close together (false)", identifier = "Take_Randomly", maxOccurs=1, minOccurs=1,binding = LiteralBooleanBinding.class) public void setTake_Randomly(Boolean data) {inputs.put("Take_Randomly",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Number_of_Points. number of points to take", defaultValue="20", title="number of points to take", identifier = "Number_of_Points", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setNumber_of_Points(Integer data) {inputs.put("Number_of_Points",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: Species_Code. the species code according to the Fish-Base conventions", defaultValue="Fis-30189", title="the species code according to the Fish-Base conventions", identifier = "Species_Code", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSpecies_Code(String data) {inputs.put("Species_Code",data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. a HCAF table containing Absence Points cells [a http link to a table in UTF-8 ecoding following this template: (HCAF) http://goo.gl/SZG9uM]", title="a HCAF table containing Absence Points cells [a http link to a table in UTF-8 ecoding following this template: (HCAF) http://goo.gl/SZG9uM]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }