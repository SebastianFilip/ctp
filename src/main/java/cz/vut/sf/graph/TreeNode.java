package cz.vut.sf.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>>{
	private T data;
	private final TreeNode<T> parent;
	private List<TreeNode<T>> children;
	public TreeNode(TreeNode<T> p){
		this.parent = p;
		this.children = new ArrayList<TreeNode<T>>();
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
	
	public List<TreeNode<T>> getChildren(){
		return children;
	}
	public TreeNode<T> getParent(){
		return parent;
	}
	
	public void setChildren(List<TreeNode<T>> children){
		this.children = children;
	}
	
	public void addChild(TreeNode<T> children){
		this.children.add(children);
	}
	
	public boolean isLeafNode(){
		return this.children.isEmpty();
	}
	
	public void cleanSubTree(){
		// Lost all references of children of node who called it
		setChildren(new ArrayList<TreeNode<T>>());
	}

	public Iterator<TreeNode<T>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	} 
}
