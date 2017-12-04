package org.gcube.spatial.data.sdi.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapAdapter extends XmlAdapter<Element, Map<String, String>> {

    private DocumentBuilder documentBuilder;

    public MapAdapter() throws Exception {
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    @Override
    public Element marshal(Map<String, String> map) throws Exception {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("data");
        document.appendChild(rootElement);
        for(Entry<String,String> entry : map.entrySet()) {
            Element childElement = document.createElement(entry.getKey());
            childElement.setTextContent(entry.getValue());
            rootElement.appendChild(childElement);
        }
        return rootElement;
    }

    @Override
    public Map<String, String> unmarshal(Element rootElement) throws Exception {
        NodeList nodeList = rootElement.getChildNodes();
        Map<String,String> map = new HashMap<String, String>(nodeList.getLength());
        for(int x=0; x<nodeList.getLength(); x++) {
            Node node = nodeList.item(x);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                map.put(node.getNodeName(), node.getTextContent());
            }
        }
        return map;
    }

}