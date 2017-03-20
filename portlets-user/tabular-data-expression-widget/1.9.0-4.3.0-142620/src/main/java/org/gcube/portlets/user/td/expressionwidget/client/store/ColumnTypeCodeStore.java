package org.gcube.portlets.user.td.expressionwidget.client.store;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnTypeCodeStore implements Serializable {

	private static final long serialVersionUID = -1908324094430432681L;

	protected static ColumnTypeCodeElement annotation = new ColumnTypeCodeElement(
			1, ColumnTypeCode.ANNOTATION);
	protected static ColumnTypeCodeElement attribute = new ColumnTypeCodeElement(
			2, ColumnTypeCode.ATTRIBUTE);
	protected static ColumnTypeCodeElement measure = new ColumnTypeCodeElement(
			3, ColumnTypeCode.MEASURE);
	protected static ColumnTypeCodeElement code = new ColumnTypeCodeElement(4,
			ColumnTypeCode.CODE);
	protected static ColumnTypeCodeElement codeName = new ColumnTypeCodeElement(
			5, ColumnTypeCode.CODENAME);
	protected static ColumnTypeCodeElement codeDescription = new ColumnTypeCodeElement(
			6, ColumnTypeCode.CODEDESCRIPTION);
	protected static ColumnTypeCodeElement dimension = new ColumnTypeCodeElement(
			7, ColumnTypeCode.DIMENSION);
	protected static ColumnTypeCodeElement timeDimension = new ColumnTypeCodeElement(
			8, ColumnTypeCode.TIMEDIMENSION);

	protected static ArrayList<ColumnTypeCodeElement> store;

	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodes(TRId trId) {
		store = new ArrayList<ColumnTypeCodeElement>();
		if (trId == null || trId.getTableTypeName() == null) {
			Log.debug("Attention no valid table type, trId:" + trId);
			store.add(annotation);
			return store;
		}

		if (trId.getTableTypeName().compareTo("Generic") == 0) {
			store.add(annotation);
			store.add(attribute);
			store.add(measure);
			store.add(code);
			store.add(codeName);
			store.add(codeDescription);
			store.add(dimension);
			store.add(timeDimension);
		} else {
			if (trId.getTableTypeName().compareTo("Codelist") == 0) {
				store.add(annotation);
				store.add(code);
				store.add(codeName);
				store.add(codeDescription);
			} else {
				if (trId.getTableTypeName().compareTo("Dataset") == 0) {
					store.add(attribute);
					store.add(measure);
					store.add(dimension);
					store.add(timeDimension);
				} else {
					store.add(annotation);
				}

			}

		}

		return store;
	}

	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodesForAddColumn(
			TRId trId) {
		store = new ArrayList<ColumnTypeCodeElement>();
		if (trId == null || trId.getTableTypeName() == null) {
			Log.debug("Attention no valid table type, trId:" + trId);
			store.add(annotation);
			return store;
		}

		if (trId.getTableTypeName().compareTo("Generic") == 0) {
			store.add(annotation);
			store.add(attribute);
			store.add(measure);
			store.add(code);
			store.add(codeName);
			store.add(codeDescription);
		} else {
			if (trId.getTableTypeName().compareTo("Codelist") == 0) {
				store.add(annotation);
				store.add(code);
				store.add(codeName);
				store.add(codeDescription);
			} else {
				if (trId.getTableTypeName().compareTo("Dataset") == 0) {
					store.add(attribute);
					store.add(measure);
				} else {
					store.add(annotation);
				}

			}

		}

		return store;
	}

	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodesForRule() {
		store = new ArrayList<ColumnTypeCodeElement>();

		store.add(annotation);
		store.add(attribute);
		store.add(measure);
		store.add(code);
		store.add(codeName);
		store.add(codeDescription);

		return store;
	}

	/**
	 * 
	 * @return
	 */
	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodesForGeneric() {
		store = new ArrayList<ColumnTypeCodeElement>();
		store.add(annotation);
		store.add(attribute);
		store.add(measure);
		store.add(code);
		store.add(codeName);
		store.add(codeDescription);
		store.add(dimension);
		store.add(timeDimension);

		return store;
	}

	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodesForCodelist() {
		store = new ArrayList<ColumnTypeCodeElement>();
		store.add(annotation);
		store.add(code);
		store.add(codeName);
		store.add(codeDescription);

		return store;
	}

	public static ArrayList<ColumnTypeCodeElement> getColumnTypeCodesForDataset() {
		store = new ArrayList<ColumnTypeCodeElement>();
		store.add(attribute);
		store.add(measure);
		store.add(dimension);
		store.add(timeDimension);

		return store;
	}

	public static int selectedPosition(String selected) {
		int position = 0;
		if (selected.compareTo(ColumnTypeCode.ANNOTATION.toString()) == 0) {
			position = 1;
		} else {
			if (selected.compareTo(ColumnTypeCode.ATTRIBUTE.toString()) == 0) {
				position = 2;
			} else {
				if (selected.compareTo(ColumnTypeCode.MEASURE.toString()) == 0) {
					position = 3;
				} else {
					if (selected.compareTo(ColumnTypeCode.CODE.toString()) == 0) {
						position = 4;
					} else {
						if (selected.compareTo(ColumnTypeCode.CODENAME
								.toString()) == 0) {
							position = 5;
						} else {
							if (selected
									.compareTo(ColumnTypeCode.CODEDESCRIPTION
											.toString()) == 0) {
								position = 6;
							} else {
								if (selected.compareTo(ColumnTypeCode.DIMENSION
										.toString()) == 0) {
									position = 7;
								} else {
									if (selected
											.compareTo(ColumnTypeCode.TIMEDIMENSION
													.toString()) == 0) {
										position = 8;
									} else {

									}

								}

							}

						}

					}

				}

			}

		}
		return position;
	}

	public static ColumnTypeCode selected(String selected) {
		Log.debug("ColumnTypeCodeStore Selected:" + selected);
		if (selected.compareTo(ColumnTypeCode.ANNOTATION.toString()) == 0) {
			return ColumnTypeCode.ANNOTATION;
		} else {
			if (selected.compareTo(ColumnTypeCode.ATTRIBUTE.toString()) == 0) {
				return ColumnTypeCode.ATTRIBUTE;
			} else {
				if (selected.compareTo(ColumnTypeCode.MEASURE.toString()) == 0) {
					return ColumnTypeCode.MEASURE;
				} else {
					if (selected.compareTo(ColumnTypeCode.CODE.toString()) == 0) {
						return ColumnTypeCode.CODE;
					} else {
						if (selected.compareTo(ColumnTypeCode.CODENAME
								.toString()) == 0) {
							return ColumnTypeCode.CODENAME;
						} else {
							if (selected
									.compareTo(ColumnTypeCode.CODEDESCRIPTION
											.toString()) == 0) {
								return ColumnTypeCode.CODEDESCRIPTION;
							} else {
								if (selected.compareTo(ColumnTypeCode.DIMENSION
										.toString()) == 0) {
									return ColumnTypeCode.DIMENSION;
								} else {
									if (selected
											.compareTo(ColumnTypeCode.TIMEDIMENSION
													.toString()) == 0) {
										return ColumnTypeCode.TIMEDIMENSION;
									} else {
										return null;
									}

								}

							}

						}

					}

				}

			}

		}
	}

	public static ColumnTypeCodeElement selectedElement(String selected) {
		if (selected.compareTo(ColumnTypeCode.ANNOTATION.toString()) == 0) {
			return annotation;
		} else {
			if (selected.compareTo(ColumnTypeCode.ATTRIBUTE.toString()) == 0) {
				return attribute;
			} else {
				if (selected.compareTo(ColumnTypeCode.MEASURE.toString()) == 0) {
					return measure;
				} else {
					if (selected.compareTo(ColumnTypeCode.CODE.toString()) == 0) {
						return code;
					} else {
						if (selected.compareTo(ColumnTypeCode.CODENAME
								.toString()) == 0) {
							return codeName;
						} else {
							if (selected
									.compareTo(ColumnTypeCode.CODEDESCRIPTION
											.toString()) == 0) {
								return codeDescription;
							} else {
								if (selected.compareTo(ColumnTypeCode.DIMENSION
										.toString()) == 0) {
									return dimension;
								} else {
									if (selected
											.compareTo(ColumnTypeCode.TIMEDIMENSION
													.toString()) == 0) {
										return timeDimension;
									} else {
										return null;
									}

								}

							}

						}

					}

				}

			}

		}
	}

}
