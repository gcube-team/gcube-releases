package org.gcube.portlets.user.td.widgetcommonevent.shared;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 *         Operations that can be launched from the grid menu
 */
public enum GridHeaderOperationId {
	COLUMNPOSITION("3"), COLUMNLABEL("4"), COLUMNADD("5"), COLUMNDELETE("6"), COLUMNSPLIT(
			"7"), COLUMNMERGE("8"), COLUMNTYPE("9"), COLUMNFILTER("10"), COLUMNBATCHREPLACE(
			"11"), ANNOTATIONADD("12"), DUPLICATEDETECTION("13"), COLUMNREPLACEBYEXPRESSION(
			"14"), COLUMNREPLACEBYEXTERNAL("15"), DOWNSCALECSQUARE("16");

	/**
	 * @param text
	 */
	private GridHeaderOperationId(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

}
