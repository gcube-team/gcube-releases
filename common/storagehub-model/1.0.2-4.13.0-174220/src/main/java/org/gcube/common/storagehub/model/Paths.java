package org.gcube.common.storagehub.model;

public class Paths {

	public static Path getPath(String path){
		return new Path(path);
	}
	
	public static Path append(Path path, Path anotherPath){
		return path.append(anotherPath);
	}
	
	public static Path append(Path path, String anotherPath){
		return path.append(anotherPath);
	}
	
	public static Path remove(Path path, Path anotherPath){
		return path.remove(anotherPath);
	}
	
}
