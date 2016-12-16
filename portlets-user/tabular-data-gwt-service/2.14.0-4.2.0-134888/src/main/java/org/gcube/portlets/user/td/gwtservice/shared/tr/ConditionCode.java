package org.gcube.portlets.user.td.gwtservice.shared.tr;


/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum ConditionCode {
	GenericValidity(0), 
	OnlyOneCodeColumn(1),
	OnlyOneCodenameColumn(2),
	MaxOneCodenameForDataLocale(3),
	AllowedColumnType(4),
	MustHaveDataLocaleMetadataAndAtLeastOneLabel(5),
	MustContainAtLeastOneDimension(6),
	MustContainAtLeastOneMeasure(7),
	GenericTupleValidity(100),
	DuplicateTupleValidation(101),
	DuplicateValueInColumn(102),
	AmbiguousValueOnExternalReference(103),
	MissingValueOnExternalReference(104),
	CodeNamePresence(105),
	CastValidation(106),
	ValidPeriodFormat(105); 
	
	
	/**
	 * @param int
	 */
	private ConditionCode(final int id) {
		this.id = id;
	}

	private final int id;
	
	
	public int getValue() {
		return id;
	}
	
	@Override
	public String toString(){
		return String.valueOf(id);
	}

	
	

}


