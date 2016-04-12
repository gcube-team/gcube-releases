package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers;
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
@Algorithm(statusSupported=false, title="DBSCAN", abstrakt="A clustering algorithm for real valued vectors that relies on the density-based spatial clustering of applications with noise (DBSCAN) algorithm. A maximum of 4000 points is allowed.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.DBSCAN", version = "1.1.0")
public class DBSCAN extends AbstractEcologicalEngineMapper implements IClusterer{
@ComplexDataInput(abstrakt="Name of the parameter: OccurrencePointsTable. Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", title="Occurrence Points Table. Max 4000 points [a http link to a table in UTF-8 encoding following this template: (GENERIC) A generic comma separated csv file in UTF-8 encoding]", maxOccurs=1, minOccurs=1, identifier = "OccurrencePointsTable", binding = GenericFileDataBinding.class)	public void setOccurrencePointsTable(GenericFileData file) {inputs.put("OccurrencePointsTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: FeaturesColumnNames. column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", defaultValue="", title="column Names for the features [a sequence of names of columns from OccurrencePointsTable separated by | ]", identifier = "FeaturesColumnNames", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFeaturesColumnNames(String data) {inputs.put("FeaturesColumnNames",data);}
@LiteralDataInput(abstrakt="Name of the parameter: OccurrencePointsClusterLabel. table name of the resulting distribution", defaultValue="OccCluster_", title="table name of the resulting distribution", identifier = "OccurrencePointsClusterLabel", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOccurrencePointsClusterLabel(String data) {inputs.put("OccurrencePointsClusterLabel",data);}
@LiteralDataInput(abstrakt="Name of the parameter: epsilon. DBScan epsilon parameter", defaultValue="10", title="DBScan epsilon parameter", identifier = "epsilon", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setepsilon(Integer data) {inputs.put("epsilon",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: min_points. DBScan minimum points parameter (identifies outliers)", defaultValue="1", title="DBScan minimum points parameter (identifies outliers)", identifier = "min_points", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setmin_points(Integer data) {inputs.put("min_points",""+data);}
@ComplexDataOutput(abstrakt="Name of the parameter: OutputTable. Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", title="Output cluster table [a http link to a table in UTF-8 ecoding following this template: (CLUSTER) http://goo.gl/PnKhhb]", identifier = "OutputTable", binding = CsvFileDataBinding.class)	public GenericFileData getOutputTable() {URL url=null;try {url = new URL((String) outputs.get("OutputTable")); return new GenericFileData(url.openStream(),"text/csv");} catch (Exception e) {e.printStackTrace();return null;}}
@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }