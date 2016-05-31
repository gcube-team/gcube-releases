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
@Algorithm(statusSupported=false, title="HRS", abstrakt="An evaluator algorithm that calculates the Habitat Representativeness Score, i.e. an indicator of the assessment of whether a specific survey coverage or another environmental features dataset, contains data that are representative of all available habitat variable combinations in an area.", identifier="org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.evaluators.HRS", version = "1.1.0")
public class HRS extends AbstractEcologicalEngineMapper implements IEvaluator{
@ComplexDataInput(abstrakt="Name of the parameter: ProjectingAreaTable. A Table containing projecting area information [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", title="A Table containing projecting area information [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", maxOccurs=1, minOccurs=1, identifier = "ProjectingAreaTable", binding = GenericFileDataBinding.class)	public void setProjectingAreaTable(GenericFileData file) {inputs.put("ProjectingAreaTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: OptionalCondition. optional filter for taking area rows", defaultValue="where oceanarea>0", title="optional filter for taking area rows", identifier = "OptionalCondition", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setOptionalCondition(String data) {inputs.put("OptionalCondition",data);}
@ComplexDataInput(abstrakt="Name of the parameter: PositiveCasesTable. A Table containing positive cases [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", title="A Table containing positive cases [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", maxOccurs=1, minOccurs=1, identifier = "PositiveCasesTable", binding = GenericFileDataBinding.class)	public void setPositiveCasesTable(GenericFileData file) {inputs.put("PositiveCasesTable",file);}
@ComplexDataInput(abstrakt="Name of the parameter: NegativeCasesTable. A Table containing negative cases [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", title="A Table containing negative cases [a http link to a table in UTF-8 encoding following this template: (HCAF) http://goo.gl/SZG9uM]", maxOccurs=1, minOccurs=1, identifier = "NegativeCasesTable", binding = GenericFileDataBinding.class)	public void setNegativeCasesTable(GenericFileData file) {inputs.put("NegativeCasesTable",file);}
@LiteralDataInput(abstrakt="Name of the parameter: FeaturesColumns. Features columns [a sequence of names of columns from PositiveCasesTable separated by | ]", defaultValue="", title="Features columns [a sequence of names of columns from PositiveCasesTable separated by | ]", identifier = "FeaturesColumns", maxOccurs=1, minOccurs=1, binding = LiteralStringBinding.class) public void setFeaturesColumns(String data) {inputs.put("FeaturesColumns",data);}

@ComplexDataOutput(abstrakt="Output that is not predetermined", title="NonDeterministicOutput", identifier = "non_deterministic_output", binding = GenericXMLDataBinding.class)
 public XmlObject getNon_deterministic_output() {return (XmlObject) outputs.get("non_deterministic_output");}
@Execute	public void run() throws Exception {		super.run();	} }