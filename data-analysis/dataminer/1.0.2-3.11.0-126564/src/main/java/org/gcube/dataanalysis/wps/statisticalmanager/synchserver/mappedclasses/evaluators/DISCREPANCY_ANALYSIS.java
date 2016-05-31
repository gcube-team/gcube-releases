package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators;
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
@Algorithm(statusSupported=false, title="DISCREPANCY_ANALYSIS", abstrakt="An evaluator algorithm that compares two tables containing real valued vectors. It drives the comparison by relying on a geographical distance threshold and a threshold for K-Statistic.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.DISCREPANCY_ANALYSIS", version = "1.1.0")
public class DISCREPANCY_ANALYSIS extends AbstractEcologicalEngineMapper implements IEvaluator{
@ComplexDataInput(abstrakt="Name of the parameter: FirstTable. First Table [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", title="First Table [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", maxOccurs=1, minOccurs=1, identifier = "FirstTable", binding = GenericFileDataBinding.class)	public void setFirstTable(GenericFileData file) {inputs.put("FirstTable",file);}
@ComplexDataInput(abstrakt="Name of the parameter: SecondTable. Second Table [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", title="Second Table [a http link to a table in UTF-8 encoding following this template: (HSPEC) http://goo.gl/OvKa1h]", maxOccurs=1, minOccurs=1, identifier = "SecondTable", binding = GenericFileDataBinding.class)	public void setSecondTable(GenericFileData file) {inputs.put("SecondTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: FirstTableCsquareColumn. the csquares column name in the first table [the name of a column from FirstTable]", defaultValue="csquarecode", title="the csquares column name in the first table [the name of a column from FirstTable]", identifier = "FirstTableCsquareColumn", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFirstTableCsquareColumn(String data) {inputs.put("FirstTableCsquareColumn",data);}
@LiteralDataInput(abstrakt="Name of the parameter: SecondTableCsquareColumn. the csquares column name in the second table [the name of a column from SecondTable]", defaultValue="csquarecode", title="the csquares column name in the second table [the name of a column from SecondTable]", identifier = "SecondTableCsquareColumn", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSecondTableCsquareColumn(String data) {inputs.put("SecondTableCsquareColumn",data);}
@LiteralDataInput(abstrakt="Name of the parameter: FirstTableProbabilityColumn. the probability column in the first table [the name of a column from FirstTable]", defaultValue="probability", title="the probability column in the first table [the name of a column from FirstTable]", identifier = "FirstTableProbabilityColumn", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFirstTableProbabilityColumn(String data) {inputs.put("FirstTableProbabilityColumn",data);}
@LiteralDataInput(abstrakt="Name of the parameter: SecondTableProbabilityColumn. the probability column in the second table [the name of a column from SecondTable]", defaultValue="probability", title="the probability column in the second table [the name of a column from SecondTable]", identifier = "SecondTableProbabilityColumn", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setSecondTableProbabilityColumn(String data) {inputs.put("SecondTableProbabilityColumn",data);}
@LiteralDataInput(abstrakt="Name of the parameter: ComparisonThreshold. the comparison threshold", defaultValue="0.1", title="the comparison threshold", identifier = "ComparisonThreshold", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setComparisonThreshold(Double data) {inputs.put("ComparisonThreshold",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: MaxSamples. the comparison threshold", defaultValue="10000", title="the comparison threshold", identifier = "MaxSamples", maxOccurs=1, minOccurs=1, binding = LiteralIntBinding.class) public void setMaxSamples(Integer data) {inputs.put("MaxSamples",""+data);}
@LiteralDataInput(abstrakt="Name of the parameter: KThreshold. Threshold for K-Statistic: over this threshold values will be considered 1 for agreement calculation. Default is 0.5", defaultValue="0.5", title="Threshold for K-Statistic: over this threshold values will be considered 1 for agreement calculation. Default is 0.5", identifier = "KThreshold", maxOccurs=1, minOccurs=1, binding = LiteralDoubleBinding.class) public void setKThreshold(Double data) {inputs.put("KThreshold",""+data);}

@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }