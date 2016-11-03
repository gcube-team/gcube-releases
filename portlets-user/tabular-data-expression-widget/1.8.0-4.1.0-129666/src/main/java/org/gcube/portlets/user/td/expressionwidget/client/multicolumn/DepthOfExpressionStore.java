package org.gcube.portlets.user.td.expressionwidget.client.multicolumn;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DepthOfExpressionStore implements Serializable {

	private static final long serialVersionUID = -1908324094430432681L;

	private static ArrayList<DepthOfExpressionElement> store;

	public static DepthOfExpressionElement bottomFirstElement = new DepthOfExpressionElement(
			0, DepthOfExpressionType.BOTTOM,
			DepthOfExpressionType.BOTTOM.getLabel());
	private static DepthOfExpressionElement startAndFirstElement = new DepthOfExpressionElement(
			1, DepthOfExpressionType.STARTAND,
			DepthOfExpressionType.STARTAND.getLabel());
	private static DepthOfExpressionElement startOrFirstElement = new DepthOfExpressionElement(
			2, DepthOfExpressionType.STARTOR,
			DepthOfExpressionType.STARTOR.getLabel());

	private static DepthOfExpressionElement commaOtherElement = new DepthOfExpressionElement(
			0, DepthOfExpressionType.COMMA,
			DepthOfExpressionType.COMMA.getLabel());
	private static DepthOfExpressionElement startAndOtherElement = new DepthOfExpressionElement(
			1, DepthOfExpressionType.STARTAND, ", And[");
	private static DepthOfExpressionElement startOrOtherElement = new DepthOfExpressionElement(
			2, DepthOfExpressionType.STARTOR, ", Or(");
	private static DepthOfExpressionElement endAndOtherElement = new DepthOfExpressionElement(
			3, DepthOfExpressionType.ENDAND,
			DepthOfExpressionType.ENDAND.getLabel());
	private static DepthOfExpressionElement endOrOtherElement = new DepthOfExpressionElement(
			4, DepthOfExpressionType.ENDOR,
			DepthOfExpressionType.ENDOR.getLabel());

	public static ArrayList<DepthOfExpressionElement> getDepthFirstRow() {
		store = new ArrayList<DepthOfExpressionElement>();
		store.add(bottomFirstElement);
		store.add(startAndFirstElement);
		store.add(startOrFirstElement);
		return store;
	}

	public static ArrayList<DepthOfExpressionElement> getDepthOtherRows() {
		store = new ArrayList<DepthOfExpressionElement>();
		store.add(commaOtherElement);
		store.add(startAndOtherElement);
		store.add(endAndOtherElement);
		store.add(startOrOtherElement);
		store.add(endOrOtherElement);
		return store;
	}

	public static ArrayList<DepthOfExpressionElement> getDepthSecondArgRow() {
		store = new ArrayList<DepthOfExpressionElement>();
		store.add(commaOtherElement);
		store.add(startAndOtherElement);
		store.add(endAndOtherElement);
		return store;
	}

}
