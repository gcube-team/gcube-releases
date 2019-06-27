package org.gcube.portlets.user.td.expressionwidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ColumnDataTypeStore implements Serializable {

	private static final long serialVersionUID = -1908324094430432681L;

	private static ArrayList<ColumnDataTypeElement> store;

	private static ColumnDataTypeElement integerElement = new ColumnDataTypeElement(
			1, ColumnDataType.Integer);
	private static ColumnDataTypeElement numericElement = new ColumnDataTypeElement(
			2, ColumnDataType.Numeric);
	private static ColumnDataTypeElement booleanElement = new ColumnDataTypeElement(
			3, ColumnDataType.Boolean);
	private static ColumnDataTypeElement textElement = new ColumnDataTypeElement(
			4, ColumnDataType.Text);
	private static ColumnDataTypeElement dateElement = new ColumnDataTypeElement(
			5, ColumnDataType.Date);
	private static ColumnDataTypeElement geometryElement = new ColumnDataTypeElement(
			6, ColumnDataType.Geometry);

	public static ArrayList<ColumnDataTypeElement> getMeasureType() {
		store = new ArrayList<ColumnDataTypeElement>();
		store.add(integerElement);
		store.add(numericElement);
		return store;
	}

	public static int selectedMeasurePosition(String selected) {
		int position = 0;
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			position = 1;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				position = 2;
			} else {
			}
		}
		return position;
	}

	public static ColumnDataType selectedMeasure(String selected) {
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			return ColumnDataType.Integer;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				return ColumnDataType.Numeric;
			} else {
				return null;
			}
		}
	}

	public static ColumnDataTypeElement selectedMeasureElement(String selected) {
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			return integerElement;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				return numericElement;
			} else {
				return null;
			}
		}
	}
	
	public static ColumnDataTypeElement selectedMeasureElement(
			ColumnDataType columnDataType) {
		if (columnDataType == null) {
			return null;
		}

		switch (columnDataType) {
		case Integer:
			return integerElement;
		case Numeric:
			return numericElement;
		default:
			return null;

		}

	}
	
	

	public static ArrayList<ColumnDataTypeElement> getAttributeType() {
		store = new ArrayList<ColumnDataTypeElement>();
		store.add(integerElement);
		store.add(numericElement);
		store.add(booleanElement);
		store.add(textElement);
		store.add(dateElement);
		store.add(geometryElement);
		return store;
	}

	public static int selectedAttributePosition(String selected) {
		int position = 0;
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			position = 1;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				position = 2;
			} else {
				if (selected.compareTo(ColumnDataType.Boolean.toString()) == 0) {
					position = 3;
				} else {
					if (selected.compareTo(ColumnDataType.Text.toString()) == 0) {
						position = 4;
					} else {
						if (selected.compareTo(ColumnDataType.Date.toString()) == 0) {
							position = 5;
						} else {
							if (selected.compareTo(ColumnDataType.Geometry
									.toString()) == 0) {
								position = 6;
							} else {

							}
						}
					}

				}
			}
		}
		return position;
	}

	public static ColumnDataType selectedAttribute(String selected) {
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			return ColumnDataType.Integer;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				return ColumnDataType.Numeric;
			} else {
				if (selected.compareTo(ColumnDataType.Boolean.toString()) == 0) {
					return ColumnDataType.Boolean;
				} else {
					if (selected.compareTo(ColumnDataType.Text.toString()) == 0) {
						return ColumnDataType.Text;
					} else {
						if (selected.compareTo(ColumnDataType.Date.toString()) == 0) {
							return ColumnDataType.Date;
						} else {
							if (selected.compareTo(ColumnDataType.Geometry
									.toString()) == 0) {
								return ColumnDataType.Geometry;
							} else {
								return null;
							}
						}
					}

				}
			}
		}
	}

	public static ColumnDataTypeElement selectedAttributeElement(String selected) {
		if (selected.compareTo(ColumnDataType.Integer.toString()) == 0) {
			return integerElement;
		} else {
			if (selected.compareTo(ColumnDataType.Numeric.toString()) == 0) {
				return numericElement;
			} else {
				if (selected.compareTo(ColumnDataType.Boolean.toString()) == 0) {
					return booleanElement;
				} else {
					if (selected.compareTo(ColumnDataType.Text.toString()) == 0) {
						return textElement;
					} else {
						if (selected.compareTo(ColumnDataType.Date.toString()) == 0) {
							return dateElement;
						} else {
							if (selected.compareTo(ColumnDataType.Geometry
									.toString()) == 0) {
								return geometryElement;
							} else {
								return null;
							}
						}

					}

				}
			}
		}
	}

	public static ColumnDataTypeElement selectedAttributeElement(
			ColumnDataType columnDataType) {
		if (columnDataType == null) {
			return null;
		}

		switch (columnDataType) {
		case Boolean:
			return booleanElement;
		case Date:
			return dateElement;
		case Geometry:
			return geometryElement;
		case Integer:
			return integerElement;
		case Numeric:
			return numericElement;
		case Text:
			return textElement;
		default:
			return null;

		}

	}

}
