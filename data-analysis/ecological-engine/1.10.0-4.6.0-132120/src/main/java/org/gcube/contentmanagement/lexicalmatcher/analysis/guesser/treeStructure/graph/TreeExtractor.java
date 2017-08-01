package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.SessionFactory;

public class TreeExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	TreeNode categoriesTree;
	
	//recupera l'albero delle categorie
	public TreeNode getCategoriesTree(SessionFactory DB){
			return categoriesTree;
	}

	//creo un nuovo Albero
	public TreeExtractor(){
		categoriesTree = new TreeNode(TreeNode.ROOT);
	}
	
	class TreeNode implements Iterable<TreeNode> {
		
		public static final String ROOT = "ROOT";
		
		  private Set<TreeNode> children;
		  public String name;
		  
		  public TreeNode(String Name) {
		    children = new HashSet<TreeNode>();
		    name = Name;
		  }

		  public String getName(){
			  return name;
		  } 
		  
		  public boolean addChild(TreeNode n) {
		    return children.add(n);
		  }

		  public boolean removeChild(TreeNode n) {
		    return children.remove(n);
		  }

		  public Iterator<TreeNode> iterator() {
		    return children.iterator();
		  }
		  
		  public boolean isLeaf(){
			  return ((children==null) || (children.size()==0));
		  }
		  
		  public boolean isRoot(){
			  return (name.equals(ROOT));
		  }
		  
		}

}
