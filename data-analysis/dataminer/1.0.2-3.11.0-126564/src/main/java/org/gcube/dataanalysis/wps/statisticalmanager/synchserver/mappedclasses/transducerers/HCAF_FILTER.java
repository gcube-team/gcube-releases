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
@Algorithm(statusSupported=false, title="HCAF_FILTER", abstrakt="An algorithm producing a HCAF table on a selected Bounding Box (default identifies Indonesia)", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.HCAF_FILTER", version = "1.1.0")
public class HCAF_FILTER extends AbstractEcologicalEngineMapper implements ITransducer{
@LiteralDataInput(abstrakt="Name of the parameter: Table_Label. the name of the Filtered Hcaf", defaultValue="hcaf_filtered", title="the name of the Filtered Hcaf", identifier = "Table_Label", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setTable_Label(String data) {inputs.put("Table_Label",data);}
@LiteralDataInput(abstrakt="Name of the parameter: B_Box_Left_Lower_Lat. the left lower latitude of the bounding box (range [-90,+90])", defaultValue="-17.098", title="the left lower latitude of the bounding box (range [-90,+90])", identifier = "B_Box_Left_Lower_Lat", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setB_Box_Left_Lower_Lat(Double data) {inputs.put("B_Box_Left_Lower_Lat",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: B_Box_Left_Lower_Long. the left lower longitude of the bounding box (range [-180,+180])", defaultValue="89.245", title="the left lower longitude of the bounding box (range [-180,+180])", identifier = "B_Box_Left_Lower_Long", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setB_Box_Left_Lower_Long(Double data) {inputs.put("B_Box_Left_Lower_Long",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: B_Box_Right_Upper_Lat. the right upper latitude of the bounding box (range [-90,+90])", defaultValue="25.086", title="the right upper latitude of the bounding box (range [-90,+90])", identifier = "B_Box_Right_Upper_Lat", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setB_Box_Right_Upper_Lat(Double data) {inputs.put("B_Box_Right_Upper_Lat",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: B_Box_Right_Upper_Long. the right upper longitude of the bounding box (range [-180,+180])", defaultValue="147.642", title="the right upper longitude of the bounding box (range [-180,+180])", identifier = "B_Box_Right_Upper_Long", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setB_Box_Right_Upper_Long(Double data) {inputs.put("B_Box_Right_Upper_Long",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. a HCAF table focusing on the selected Bounding Box [a http link to a table in UTF-8 ecoding following this template: (HCAF) http://goo.gl/SZG9uM]", title="a HCAF table focusing on the selected Bounding Box [a http link to a table in UTF-8 ecoding following this template: (HCAF) http://goo.gl/SZG9uM]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }