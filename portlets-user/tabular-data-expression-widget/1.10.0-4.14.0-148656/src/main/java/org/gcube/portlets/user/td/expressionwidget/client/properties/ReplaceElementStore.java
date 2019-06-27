package org.gcube.portlets.user.td.expressionwidget.client.properties;

import java.util.ArrayList;

import org.gcube.portlets.user.td.expressionwidget.shared.replace.ReplaceType;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ReplaceElementStore {

	public ArrayList<ReplaceElement> replaceElementsOperations = new ArrayList<ReplaceElement>() {

		private static final long serialVersionUID = 1690916203781730778L;

		{
			add(new ReplaceElement(1, ReplaceType.Value));
			add(new ReplaceElement(2, ReplaceType.ColumnValue));
			add(new ReplaceElement(3, ReplaceType.SubstringByRegex));
			add(new ReplaceElement(4, ReplaceType.SubstringByIndex));
			add(new ReplaceElement(5, ReplaceType.SubstringByCharSeq));
			add(new ReplaceElement(6, ReplaceType.TextReplaceMatchingRegex));
			add(new ReplaceElement(7, ReplaceType.Concat));
			add(new ReplaceElement(8, ReplaceType.Addition));
			add(new ReplaceElement(9, ReplaceType.Subtraction));
			add(new ReplaceElement(10, ReplaceType.Modulus));
			add(new ReplaceElement(11, ReplaceType.Multiplication));
			add(new ReplaceElement(12, ReplaceType.Division));
			add(new ReplaceElement(13, ReplaceType.Upper));
			add(new ReplaceElement(14, ReplaceType.Lower));
			add(new ReplaceElement(15, ReplaceType.Trim));
			add(new ReplaceElement(16, ReplaceType.MD5));
		}
	};
	
	public ArrayList<ReplaceElement> replaceElementsOperationsNoArithmetic = new ArrayList<ReplaceElement>() {

		private static final long serialVersionUID = 1690916203781730778L;

		{
			add(new ReplaceElement(1, ReplaceType.Value));
			add(new ReplaceElement(2, ReplaceType.ColumnValue));
			add(new ReplaceElement(3, ReplaceType.SubstringByRegex));
			add(new ReplaceElement(4, ReplaceType.SubstringByIndex));
			add(new ReplaceElement(5, ReplaceType.SubstringByCharSeq));
			add(new ReplaceElement(6, ReplaceType.TextReplaceMatchingRegex));
			add(new ReplaceElement(7, ReplaceType.Concat));
			add(new ReplaceElement(8, ReplaceType.Upper));
			add(new ReplaceElement(9, ReplaceType.Lower));
			add(new ReplaceElement(10, ReplaceType.Trim));
			add(new ReplaceElement(11, ReplaceType.MD5));
		}
	};
	
	

	public ArrayList<ReplaceElement> replaceElements = new ArrayList<ReplaceElement>() {

		private static final long serialVersionUID = 1690916203781730778L;

		{
			add(new ReplaceElement(1, ReplaceType.Value));
			add(new ReplaceElement(2, ReplaceType.ColumnValue));
			add(new ReplaceElement(3, ReplaceType.SubstringByRegex));
			add(new ReplaceElement(4, ReplaceType.SubstringByIndex));
			add(new ReplaceElement(5, ReplaceType.SubstringByCharSeq));
			add(new ReplaceElement(6, ReplaceType.TextReplaceMatchingRegex));
			add(new ReplaceElement(7, ReplaceType.Upper));
			add(new ReplaceElement(8, ReplaceType.Lower));
			add(new ReplaceElement(9, ReplaceType.Trim));
			add(new ReplaceElement(10, ReplaceType.MD5));

		}
	};
	
	public ArrayList<ReplaceElement> replaceElementsArithmetic = new ArrayList<ReplaceElement>() {

		private static final long serialVersionUID = 1690916203781730778L;

		{
			add(new ReplaceElement(1, ReplaceType.ColumnValue));

		}
	};
	

}
