package org.gcube.portlets.docxgenerator.transformer;

import java.awt.Image;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.relationships.Namespaces;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.docxgenerator.content.*;
import org.gcube.portlets.docxgenerator.content.RContent.RContentVertAlign;
import org.gcube.portlets.docxgenerator.utils.HTML2XML;
import org.gcube.portlets.docxgenerator.utils.IDGenerator;
import org.gcube.portlets.docxgenerator.utils.PixelToTwipConverter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Transforms a text source InputComponent into a Content object.
 * 
 * TODO: Future work - Some classes should be implemented, which should contain the methods like
 *   createDom, traverseNodes, transformHtmlImage, createHyperlink, parseAttributes, extractCssAttributeValue, 
 *   setParagraphStyles, setHeadingStyle, setTocEntry.
 * 
 * @author Luca Santocono
 * 
 */
public class TextTransformer implements Transformer {

	private static final Log log = LogFactory.getLog(TextTransformer.class);
	private final String hostName;
	
	public TextTransformer(String hostName) {
		this.hostName = hostName;
	}

	private Node createDom(final String toParse)
	throws ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = null;
		Node body = null;
		try {
			dom = db.parse(new InputSource(new StringReader(toParse)));
			NodeList nodelist = dom.getElementsByTagName("body");
			body = nodelist.item(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return body;
	}

	private void traverseNodes(Node parentNode, TextAreaContent textArea, PContent p, RContent r, final WordprocessingMLPackage wmlPack) {
		
		for (Node node = parentNode.getFirstChild(); node != null; node = node
		.getNextSibling()) {			
			switch (node.getNodeType()) {
			case Node.TEXT_NODE: {
				
				String string = node.getNodeValue();
				String subStrings[] = string.split("\n");
				
				if(!subStrings[0].isEmpty()) {
					r.addText(subStrings[0]);
					log.debug("PARSING..........................................string: "+ subStrings[0]);
					//r.preserveSpace();
					p.addRun(r);
					r = new RContent();
				}
				break;
			}
			case Node.ELEMENT_NODE: {
				log.debug("Element Node    " + node.getNodeName());
				if (node.getNodeName().equals("div")) {
					
					PContent newp = new PContent();
					setParagraphStyles(node.getAttributes(),newp);
					textArea.addPContent(newp);
					
					r = new RContent();
					traverseNodes(node, textArea, newp, r, wmlPack);
					
					if(node.getNextSibling() != null ){
						Node parent = node.getParentNode();
						p = new PContent();
						r = new RContent();
						setParagraphStyles(parent.getAttributes(),p);
						textArea.addPContent(p);
					}
				} else if (node.getNodeName().equals("br")) {
					if (node.getNextSibling() == null || node.getNextSibling().getNextSibling() == null || node.getNextSibling().getNextSibling().getNodeName().equals("div"))
						break;
					r.addNewLine();
					p.addRun(r);
					r = new RContent();
				} else if (node.getNodeName().equals("a")) {
					HyperlinkContent link = createHyperlink(node);
					p.addHyperlink(link);
					createHyperLinkRelationship(wmlPack, link);
					//traverseNodes(node, textArea, p, r,
						//	wmlPack);
				} else if (node.getNodeName().equals("b")) {
					r.setBold();
					traverseNodes(node, textArea, p, r, wmlPack);
					r = new RContent();
				} else if (node.getNodeName().equals("i")) {
					r.setItalic();
					traverseNodes(node, textArea, p, r, wmlPack);
					r = new RContent();
				} else if (node.getNodeName().equals("u")) {
					r.setUnderlined();
					traverseNodes(node, textArea, p, r, wmlPack);
					r = new RContent();
				}  else if (node.getNodeName().equals("sub")) {
					r.setVertAlign(RContentVertAlign.SUBSCRIPT);
					traverseNodes(node, textArea, p, r, wmlPack);
					r = new RContent();
				} else if (node.getNodeName().equals("sup")) {
					r.setVertAlign(RContentVertAlign.SUPERSCRIPT);
					traverseNodes(node, textArea, p, r, wmlPack);
					r = new RContent();
				} else if (node.getNodeName().equals("span")) {
					RContent newr = new RContent();
					parseAttributes(node.getAttributes(), newr, false);
					traverseSpanNodes(node, newr, p, wmlPack);
				} else if (node.getNodeName().equals("font")) {
					RContent newr = new RContent();
					parseAttributes(node.getAttributes(), newr, false);
					traverseFontNodes(node, newr, p, wmlPack);
				} else if (node.getNodeName().equals("img")) {
					RContent imagecontent = (RContent) transformHtmlImage(node
							.getAttributes(), wmlPack);
					p.addContent(imagecontent);
					r = new RContent();
				}
				break;
			}
			}
		}// end for
		
	}
	
	private void traverseSpanNodes(Node parentNode, RContent r, PContent p, final WordprocessingMLPackage wmlPack) {
		
		for (Node node = parentNode.getFirstChild(); node != null; node = node
		.getNextSibling()) {
			
			switch (node.getNodeType()) {
			case Node.TEXT_NODE: {
				
				String string = node.getNodeValue();
				String subStrings[] = string.split("\n");
				r.addText(subStrings[0]);
				log.debug("PARSING..........................................SPAN string: "+ subStrings[0]);
				
				p.addRun(r);
				r = new RContent();
				parseAttributesOfAllParents(parentNode, r);
				break;
			}
			case Node.ELEMENT_NODE: {
				log.debug("Element Node in SPAN    " + node.getNodeName());
				if (node.getNodeName().equals("span")){
					
					RContent newr = new RContent();
					parseAttributesOfAllParents(node,newr);
					traverseSpanNodes(node, newr, p, wmlPack);
					
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
					
				} else if (node.getNodeName().equals("br")) {
					r.addNewLine();
					p.addRun(r);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("b")) {
					r.setBold();
					traverseSpanNodes(node, r, p, wmlPack);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("i")) {
					r.setItalic();
					traverseSpanNodes(node, r, p, wmlPack);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("u")) {
					r.setUnderlined();
					traverseSpanNodes(node, r, p, wmlPack);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("sub")) {
					r.setVertAlign(RContentVertAlign.SUBSCRIPT);
					traverseSpanNodes(node, r, p, wmlPack);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("sup")) {
					r.setVertAlign(RContentVertAlign.SUPERSCRIPT);
					traverseSpanNodes(node, r, p, wmlPack);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				} else if (node.getNodeName().equals("font")) {
					
					Node sibiling = node.getPreviousSibling();
					if((sibiling != null)) {
						RContent newr = new RContent();
						parseAttributesOfAllParents(node, newr);
						traverseFontNodes(node,newr,p, wmlPack);
					} else { 
						parseAttributes(node.getAttributes(), r, false);
						traverseFontNodes(node, r, p, wmlPack);
					}
					
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);	
				} else if (node.getNodeName().equals("img")) {
					RContent imagecontent = (RContent) transformHtmlImage(node
							.getAttributes(), wmlPack);
					p.addContent(imagecontent);
					r = new RContent();
					parseAttributesOfAllParents(parentNode, r);
				}
				break;
				
			}
			}
		}
	}
	private void traverseFontNodes(Node parentNode, RContent r, PContent p, final WordprocessingMLPackage wmlPack) {
		
		for (Node node = parentNode.getFirstChild(); node != null; node = node
				.getNextSibling()) {
					
					switch (node.getNodeType()) {
					case Node.TEXT_NODE: {
						String string = node.getNodeValue();
						String subStrings[] = string.split("\n");
						r.addText(subStrings[0]);
						log.debug("PARSING..........................................FONT string: "+ subStrings[0]);
						p.addRun(r);
						r = new RContent();
						parseAttributesOfAllParents(parentNode, r);
						break;
					}
					case Node.ELEMENT_NODE: {
						if (node.getNodeName().equals("span")){
							
							Node sibiling = node.getPreviousSibling();
							if((sibiling != null)) {
								RContent newr = new RContent();
								parseAttributesOfAllParents(node, newr);
								traverseSpanNodes(node,newr,p, wmlPack);
							} else { 
								parseAttributes(node.getAttributes(), r, false);
								traverseSpanNodes(node, r, p, wmlPack);
							}
							
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
							
						}else if (node.getNodeName().equals("br")) {
							r.addNewLine();
							p.addRun(r);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						} else if (node.getNodeName().equals("b")) {
							r.setBold();
							traverseFontNodes(node, r, p, wmlPack);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						} else if (node.getNodeName().equals("i")) {
							r.setItalic();
							traverseFontNodes(node, r, p, wmlPack);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						} else if (node.getNodeName().equals("u")) {
							r.setUnderlined();
							traverseFontNodes(node, r, p, wmlPack);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						}else if (node.getNodeName().equals("sub")) {
							r.setVertAlign(RContentVertAlign.SUBSCRIPT);
							traverseFontNodes(node, r, p, wmlPack);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						} else if (node.getNodeName().equals("sup")) {
							r.setVertAlign(RContentVertAlign.SUPERSCRIPT);
							traverseFontNodes(node, r, p, wmlPack);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						} else if (node.getNodeName().equals("font")) {
							
							RContent newr = new RContent();
							parseAttributesOfAllParents(node,newr);
							traverseFontNodes(node, newr, p, wmlPack);
							
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
							
							
						} else if (node.getNodeName().equals("img")) {
							RContent imagecontent = (RContent) transformHtmlImage(node
									.getAttributes(), wmlPack);
							p.addContent(imagecontent);
							r = new RContent();
							parseAttributesOfAllParents(parentNode, r);
						}
						break;
						
					}
					}
				}
	}

	private boolean parseAttributes(NamedNodeMap nodemap, RContent rcontent, boolean checkSizeFont) {
		if(nodemap == null)
			return checkSizeFont;
		if (nodemap.getNamedItem("style") != null) {
			String style = nodemap.getNamedItem("style").getNodeValue();

			log.debug("the style is.........." + style);

			if (style.contains("bold"))
				rcontent.setBold();
			if (style.contains("italic"))
				rcontent.setItalic();
			if (style.contains("underline"))
				rcontent.setUnderlined();
			if (style.contains("color")) {
				String color = extractCssAttributeValue(style);
				rcontent.setColor(color);
				
			}
			if (style.contains("line-through"))
				rcontent.setStrike();
		}
		
		if (nodemap.getNamedItem("size") != null && !checkSizeFont) {
			checkSizeFont = true;
			String value = nodemap.getNamedItem("size").getNodeValue();
			rcontent.setFontSize(PixelToTwipConverter.convertToTwip(value));
		}
		return checkSizeFont;
	}
	
	private void parseAttributesOfAllParents(Node node, RContent rcontent) {
		Boolean checkSizeFont = false;
		while(node!=null) {// && (node.getNodeName().equals("span") || node.getNodeName().equals("font"))) {
			
			checkSizeFont = parseAttributes(node.getAttributes(), rcontent, checkSizeFont);	
			
			node = node.getParentNode();
		}
	}

	private String extractCssAttributeValue(final String style) {
		String[] tosplit = style.split(":");
		tosplit = tosplit[1].split(";");
		String value = tosplit[0].trim();
		if (value.endsWith("px"))
			value = value.replaceAll("px", "");
		return value;
	}

	private void setParagraphStyles(NamedNodeMap nodemap, PContent pcontent) {
		if (nodemap.getNamedItem("style") != null) {
			String style = nodemap.getNamedItem("style").getNodeValue();
			if (style.contains("center"))
				pcontent.setCentered();
			else if (style.contains("left"))
				pcontent.setLeft();
			else if (style.contains("right"))
				pcontent.setRight();

		}
	}
	
	private Content transformHtmlImage(final NamedNodeMap nodemap, final WordprocessingMLPackage wmlPack) {

		RContent runcontent;
		String src = nodemap.getNamedItem("src").getNodeValue();
		try {
			runcontent = new RContent();
			// <img alt="D4Science Home"
			// src="http://newportal.d4science.research-infrastructures.eu/vre/images/d4science_logo.gif"
			// style="width: 385px; height: 157px;" _moz_resizing="true"/>
			ImageTransformer transformer = new ImageTransformer(hostName);
			BinaryPartAbstractImage imagePart;
			Inline inline;
			Image image = transformer.createImageFromUrl(src);
			byte[] imgBytes1 = transformer.transformImageToBytes(image);
			imagePart = BinaryPartAbstractImage.createImagePart(wmlPack,
					imgBytes1);

			if (nodemap.getNamedItem("style") != null
					&& nodemap.getNamedItem("style").getNodeValue().contains(
					"width")) {
				int width = Integer.parseInt(extractCssAttributeValue(nodemap
						.getNamedItem("style").getNodeValue()));
				inline = imagePart.createImageInline(null, null, IDGenerator
						.imageIdGenerator(), IDGenerator.imageIdGenerator(),
						width * 15, false);

			} else {
				inline = imagePart.createImageInline(null, null, IDGenerator
						.imageIdGenerator(), IDGenerator.imageIdGenerator(), false);

			}

			runcontent.insertImage(inline);
			return runcontent;
		} catch (Exception e) {
			log.warn("Cannot fetch image at " + src + " " + e);
			return null;
		}
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private HyperlinkContent createHyperlink(Node node) {

		HyperlinkContent hyperlinkcontent = null;
		try {
			NamedNodeMap nodemap = node.getAttributes();
			hyperlinkcontent = new HyperlinkContent();
			String href = nodemap.getNamedItem("href").getNodeValue();
			hyperlinkcontent.setUrl(href);
			hyperlinkcontent.addText(node.getFirstChild().getNodeValue());
			hyperlinkcontent.setHistory(true);
		}
		catch (NullPointerException e) {
			hyperlinkcontent.setUrl("#");
			hyperlinkcontent.addText("");			
		}
		return hyperlinkcontent;
	}
	
	/**
	 * Creates a HyperLink relationship, which is written in the relationship
	 * file of the docx package.
	 * 
	 * @param wmlPack The current WordprocessingMLPackage.
	 * @param content Needed to extract the HyperLinks contained in it.
	 */
	public void createHyperLinkRelationship(
			final WordprocessingMLPackage wmlPack, final HyperlinkContent link) {

		org.docx4j.relationships.ObjectFactory factory = new org.docx4j.relationships.ObjectFactory();
		org.docx4j.relationships.Relationship rel = factory
		.createRelationship();
		rel.setType(Namespaces.HYPERLINK);
		rel.setTarget(link.getUrl());
		rel.setTargetMode("External");
		wmlPack.getMainDocumentPart().getRelationshipsPart()
		.addRelationship(rel);
		link.setId(rel.getId());
		
	}

	/**
	 * @see it.cnr.isti.docxgenerator.transformer.Transformer#transform(Component,
	 *      org.docx4j.openpackaging.packages.WordprocessingMLPackage)
	 * 
	 * @param component
	 *            Source InputComponent that is going to be transformed.
	 * @param wmlPack
	 *            WordprocessingMLPackage object, which represents a docx
	 *            archive. Passed to insert intermediate data if needed during
	 *            the transformation.
	 * @return A Content object that can be inserted in the docx archive.
	 * 
	 */
	@Override
	public ArrayList<Content> transform(final BasicComponent component,
			final WordprocessingMLPackage wmlPack) {
		
		String content = (String)component.getPossibleContent();
		
		
		
		//toParse.replaceAll("&nbsp;", "");
		Node body = null;
		try {
			String toParse = HTML2XML.convert(content);
		//	String toParse = String.format("<body>%s</body>", content);
			//toParse = toParse.replaceAll("<br>", "<br />");
			
		
			
			//toParse = toParse.replaceAll("\">","\" />");
			log.debug("------------TO PARSE..........................TO PARSE------:" + toParse);
			body = createDom(toParse);
		} catch (ParserConfigurationException e) {
			log.warn("Wrong parser configuration " + e);
		} catch (SAXException e) {
			log.warn("SAX parsing error " + e);
		} catch (IOException e) {
		}
		
		TextAreaContent textArea = new TextAreaContent();
		
		if (body != null) {
			PContent p = new PContent();
			RContent r = new RContent();
			textArea.addPContent(p);
			traverseNodes(body, textArea, p, r, wmlPack);
		}
		ArrayList<Content> list = new ArrayList<Content>();
		
		for(PContent p: (Vector<PContent>)textArea.getContent())	
			//wmlPack.getMainDocumentPart().addObject(p.getContent());
			list.add(p);
		
		return list;	
	}
}
