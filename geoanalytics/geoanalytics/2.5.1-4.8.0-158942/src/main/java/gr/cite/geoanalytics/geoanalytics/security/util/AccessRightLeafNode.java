package gr.cite.geoanalytics.geoanalytics.security.util;

public class AccessRightLeafNode extends AccessRightNode {

	public enum AccessRightType {
		View,
		Edit,
	}
	
	public enum AccessRightClass {
		Normal,
		Encompassing
	}
	
	private AccessRightType rightType = null;
	private AccessRightClass rightClass = null;
	
	public AccessRightClass getRightClass() {
		return rightClass;
	}

	public void setRightClass(AccessRightClass rightClass) {
		if(rightClass == null) throw new IllegalArgumentException("Access right class cannot be null");
		this.rightClass = rightClass;
	}
	
	public AccessRightType getRightType() {
		return rightType;
	}

	public void setRightType(AccessRightType rightType) {
		if(rightType == null) throw new IllegalArgumentException("Access right type cannot be null");
		this.rightType = rightType;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
}
