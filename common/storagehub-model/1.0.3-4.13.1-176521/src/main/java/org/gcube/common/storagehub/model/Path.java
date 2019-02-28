package org.gcube.common.storagehub.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class Path {

	protected List<String> paths = null;
	
	private Path(){}
	
	protected Path(String path){
		if (!(path==null || path.isEmpty())) 
			paths = Arrays.asList(path.split("/")).stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}
	
	public String toPath(){
		if (paths ==null || paths.isEmpty()) return "/";
		else return "/"+paths.stream().collect(Collectors.joining("/"))+"/";
	}
	
	
	public String getLastDirName(){
		return paths.get(paths.size()-1);
	}
	
	protected Path append(Path anotherPath){
		Path path = new Path();
		path.paths = new LinkedList<String>(this.paths);
		if (anotherPath.paths!=null)
			path.paths.addAll(anotherPath.paths);
		return path;
	}
	
	public void appendParent(String parent){
		this.paths = new LinkedList<String>(this.paths);
		this.paths.add(0, parent);
		
	}
	
	protected Path append(String anotherPath){
		return this.append(new Path(anotherPath));
	}
	
	protected Path remove(Path anotherPath){
		Path path = new Path();
		path.paths = new LinkedList<String>(this.paths);
		path.paths.removeAll(anotherPath.paths);
		return path;
	}
}
