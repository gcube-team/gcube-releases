package org.gcube.datatransfer.agent.library.test;

import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.datatransfer.common.agent.Types.AnyHolder;
import org.gcube.datatransfer.common.agent.Types.InputPattern;
import org.gcube.datatransfer.common.agent.Types.InputURIs;
import org.gcube.datatransfer.common.agent.Types.SourceData;
import org.gcube.datatransfer.common.agent.Types.StartTransferMessage;
import org.w3c.dom.Element;

public class JAXBTest {

	public static void main(String[] args) throws Exception {

		JAXBContext c = JAXBContext.newInstance(StartTransferMessage.class);
		StartTransferMessage req = new StartTransferMessage();
		req.syncOp=true;
		InputPattern pattern = new InputPattern();
		pattern.sourceId = "id";
		pattern.pattern = new AnyHolder();
		pattern.pattern.element = new Element[]{DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("boh")};
		SourceData data = new SourceData();
		data.inputSource = pattern;
		req.source = data;
		c.createMarshaller().marshal(req,System.out);
		
		req.source.inputURIs =  Arrays.asList("one","two");	
		System.out.println("/n");
		c.createMarshaller().marshal(req,System.out);
		}
}
