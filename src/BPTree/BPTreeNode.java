package BPTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import APlusTree.Page;

public abstract class BPTreeNode<T extends Comparable<T>> implements Serializable{
	
	/**
	 * Abstract class that collects the common functionalities of the inner and leaf nodes
	 */
	private static final long serialVersionUID = 1L;
	protected Comparable<T>[] keys;
	protected int numberOfKeys;
	protected int order;
	protected int index;		//for printing the tree
	private boolean isRoot;
	private String path;
	private String directory;
	private BPTree tree;
	private static int nextIdx = 0;
	// paging
	public void saveNextIdx() throws IOException
	{
		File file = new File(path+"_NextIdx.class");
		if(!file.exists())
			file.delete();
		file.createNewFile();
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(nextIdx);
		stream.close();
	}
	public int getNextIdx() {
		int val=0;
		try {

			File f = new File(path+"_NextIdx.class");
			if(!f.exists())
				return 0;
			FileInputStream file = new FileInputStream(path+"_NextIdx.class");
			ObjectInputStream in = new ObjectInputStream(file);
			val = (int) in.readObject();

			in.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
			//ex.printStackTrace();
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
		return val;
	}
	

	
	public BPTreeNode deserializeNode(String dir) {
		//System.out.println(dir);
		BPTreeNode p = null;
		if(dir==null)return null;
		try {

//			File f = new File(dir);
//			if(!f.exists())
//				return null;
			FileInputStream file = new FileInputStream(dir);
			ObjectInputStream in = new ObjectInputStream(file);
			p = (BPTreeNode) in.readObject();

			in.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
			//ex.printStackTrace();
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
		return p;
	}
	

public void save() throws IOException
	{
		File file = new File(directory);
		if(!file.exists())
			file.delete();
		file.createNewFile();
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(this);
		stream.close();
	}

	public String getDirectory() {
		return this.directory;
	}
	public String getPath() {
		return this.path;
	}
	public BPTree getTree() {
		return tree;
	}
	//path = data/tableName_colName
	
public BPTreeNode(int order,String path,BPTree tree) throws IOException 
	{
		this.tree=tree;
		this.path=path;
	//	index=tree.getNextId();
	//	tree.setNextId(index+1);
		nextIdx = getNextIdx();
		index = nextIdx++;
		saveNextIdx();
		System.out.println(index+"!!!!!!!!!!!!!!!!!!!!!!!!!");
		this.directory=path+"_"+index+".class";
		numberOfKeys = 0;
		this.order = order;
	//	this.save();
	}


	
	/**
	 * @return a boolean indicating whether this node is the root of the B+ tree
	 */
 	public boolean isRoot()
	{
		return isRoot;
	}
	
	/**
	 * set this node to be a root or unset it if it is a root
	 * @param isRoot the setting of the node
	 * @throws IOException 
	 */
	public void setRoot(boolean isRoot) throws IOException
	{
		this.isRoot = isRoot;
		save();
	}
	
	/**
	 * find the key at the specified index
	 * @param index the index at which the key is located
	 * @return the key which is located at the specified index
	 */
	public Comparable<T> getKey(int index) 
	{
		return keys[index];
	}

	/**
	 * sets the value of the key at the specified index
	 * @param index the index of the key to be set
	 * @param key the new value for the key
	 * @throws IOException 
	 */
	
	public void setKey(int index, Comparable<T> key) throws IOException 
	{
		keys[index] = key;
		save();
	}
	
	/**
	 * @return a boolean whether this node is full or not
	 */
	
public boolean isFull() 
	{
		return numberOfKeys == order;
	}
	
	/**
	 * @return the last key in this node
	 */
	public Comparable<T> getLastKey()
	{
		return keys[numberOfKeys-1];
	}
	
	/**
	 * @return the first key in this node
	 */
	public Comparable<T> getFirstKey()
	{
		return keys[0];
	}
	
	/**
	 * @return the minimum number of keys this node can hold
	 */
	public abstract int minKeys();

	/**
	 * insert a key with the associated record reference in the B+ tree
	 * @param key the key to be inserted
	 * @param recordReference a pointer to the record on the hard disk
	 * @param parent the parent of the current node
	 * @param ptr the index of the parent pointer that points to this node
	 * @return a key and a new node in case of a node splitting and null otherwise
	 * @throws IOException 
	 */
	public abstract PushUp<T> insert(T key, Ref recordReference, BPTreeInnerNode<T> parent, int ptr) throws IOException;
	
	public abstract Vector<Ref> search(T key);
	public abstract Vector<Ref> searchGreaterThan(T key);
	public abstract Vector<Ref> searchSmallerThan(Comparable minimumKey, T key);
	public abstract Vector<Ref> searchGreaterThanOrEqual(T key);
	public abstract Vector<Ref> searchSmallerThanOrEqual(Comparable minimumKey, T key);
	public abstract Vector<Ref> notEqual(Comparable minimumKey, T key);

	public abstract boolean containsKey(T key); //sarah
	public abstract void addDuplicateKey(T key, Ref recordReference) throws IOException; //sarah


	/**
	 * delete a key from the B+ tree recursively
	 * @param key the key to be deleted from the B+ tree
	 * @param parent the parent of the current node
	 * @param ptr the index of the parent pointer that points to this node 
	 * @return true if this node was successfully deleted and false otherwise
	 * @throws IOException 
	 */
	public abstract boolean delete(T key, BPTreeInnerNode<T> parent, int ptr, Ref ref) throws IOException;
	
	/**
	 * A string represetation for the node
	 */
	public String toString()
	{		
		String s = "(" + index + ")";

		s += "[";
		for (int i = 0; i < order; i++)
		{
			String key = " ";
			if(i < numberOfKeys)
				key = keys[i].toString();
			
			s+= key;
			if(i < order - 1)
				s += "|";
		}
		s += "]";
		if(this instanceof BPTreeLeafNode) {
			BPTreeLeafNode leaf = (BPTreeLeafNode)this;
			for(int i =0;i<leaf.numberOfKeys;i++) {
				s+=keys[i].toString()+"~"+leaf.getRecord(i).toString();
			}
		}
		return s;
	}

}