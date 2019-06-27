package org.gcube.vremanagement.softwaregateway.impl.packages;

import java.util.List;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;

public class MavenPackage extends Package {

	public MavenPackage(Coordinates coordinates){
		setCoordinates(coordinates);
	}
	
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSALocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubePackage> getPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GCubePackage> getPlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String register() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub

	}

	private void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public Coordinates getCoordinates(){
		return this.coordinates;
	}
	
}
