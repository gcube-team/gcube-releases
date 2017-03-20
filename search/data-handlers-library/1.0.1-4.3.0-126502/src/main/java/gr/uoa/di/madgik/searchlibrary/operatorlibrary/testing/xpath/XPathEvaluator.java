package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing.xpath;
//package xpath;
//
//import java.util.List;
//import javax.xml.xpath.*;
//import org.w3c.dom.*;
//
//public class XPathEvaluator implements XPathFunction {
//
//  // This class could easily be implemented as a Singleton.
//    
//  public Object evaluate(List args) throws XPathFunctionException {
//
//    if (args.size() != 1) {
//      throw new XPathFunctionException("Wrong number of arguments to valid-isbn()");
//    }
//
//    String isbn;
//    Object o = args.get(0);
//
//    // perform conversions
//    if (o instanceof String) isbn = (String) args.get(0);
//    else if (o instanceof Boolean) isbn = o.toString();
//    else if (o instanceof Double) isbn = o.toString();
//    else if (o instanceof NodeList) {
//        NodeList list = (NodeList) o;
//        Node node = list.item(0);
//        // getTextContent is available in Java 5 and DOM 3.
//        // In Java 1.4 and DOM 2, you'd need to recursively 
//        // accumulate the content.
//        isbn= node.getTextContent();
//    }
//    else {
//        throw new XPathFunctionException("Could not convert argument type");
//    }
//	return o;
//  }
//
//}