package BPTree;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

public class BPTreeLeafNode<T extends Comparable<T>> extends BPTreeNode<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Ref>[] records;
	private String next;
	

	@SuppressWarnings("unchecked")
	public BPTreeLeafNode(int n,String path,BPTree tree) throws IOException {
		super(n, path,tree);
		keys = new Comparable[n];
		records = new Vector[n];
		for (int i = 0; i < n; i++) {
			records[i] = new Vector<Ref>();
		}
		save();
	}

	/**
	 * @return the next leaf node
	 */
	public BPTreeLeafNode<T> getNext() {
	
		return(next==null)?null:(BPTreeLeafNode)deserializeNode(next);
	}

	/**
	 * sets the next leaf node
	 * 
	 * @param node the next leaf node
	 * @throws IOException 
	 */
	public void setNext(BPTreeLeafNode<T> node) throws IOException {
		if(node==null) {
			System.out.println("you are having next as null");
			this.next=null;
		}else {
			
			this.next = node.getDirectory();
		}
		save();
	}

	/**
	 * @param index the index to find its record
	 * @return the reference of the queried index
	 */
	public Vector<Ref> getRecord(int index) {

		return records[index];
	}

	/**
	 * sets the record at the given index with the passed reference
	 * 
	 * @param index           the index to set the value at
	 * @param recordReference the reference to the record
	 * @throws IOException 
	 */
	public void setRecord(int index, Vector<Ref> recordReference) throws IOException {
		records[index] = recordReference;
		save();
	}

	/**
	 * @return the reference of the last record
	 */
	public Vector<Ref> getFirstRecord() {
		return records[0];
	}

	/**
	 * @return the reference of the last record
	 */
	public Vector<Ref> getLastRecord() {
		return records[numberOfKeys - 1];
	}

	/**
	 * finds the minimum number of keys the current node must hold
	 */
	public int minKeys() {
		if (this.isRoot())
			return 1;
		return (order + 1) / 2;
	}

	/**
	 * insert the specified key associated with a given record refernce in the B+
	 * tree
	 * @throws IOException 
	 */
	public PushUp<T> insert(T key, Ref recordReference, BPTreeInnerNode<T> parent, int ptr) throws IOException {
		if (this.isFull()) {
			BPTreeNode<T> newNode = this.split(key, recordReference);
			Comparable<T> newKey = newNode.getFirstKey();
			return new PushUp<T>(newNode, newKey);
		} else {
			int index = 0;
			while (index < numberOfKeys && getKey(index).compareTo(key) <= 0)
				++index;
			Vector<Ref> newVec = new Vector<Ref>();
			newVec.add(recordReference);
			this.insertAt(index, key, newVec);
			return null;
		}
	}

	/**
	 * inserts the passed key associated with its record reference in the specified
	 * index
	 * 
	 * @param index           the index at which the key will be inserted
	 * @param key             the key to be inserted
	 * @param recordReference the pointer to the record associated with the key
	 * @throws IOException 
	 */
	private void insertAt(int index, Comparable<T> key, Vector<Ref> recordReference) throws IOException {
		for (int i = numberOfKeys - 1; i >= index; --i) {
			this.setKey(i + 1, getKey(i));
			this.setRecord(i + 1, getRecord(i));
		}

		this.setKey(index, key);

		this.setRecord(index, recordReference);
		++numberOfKeys;
		save();
	}

	/**
	 * splits the current node
	 * 
	 * @param key             the new key that caused the split
	 * @param recordReference the reference of the new key
	 * @return the new node that results from the split
	 * @throws IOException 
	 */
	public BPTreeNode<T> split(T key, Ref recordReference) throws IOException {
		int keyIndex = this.findIndex(key);
		int midIndex = numberOfKeys / 2;
		if ((numberOfKeys & 1) == 1 && keyIndex > midIndex) // split nodes evenly
			++midIndex;

		int totalKeys = numberOfKeys + 1;
		// move keys to a new node
		BPTreeLeafNode<T> newNode = new BPTreeLeafNode<T>(order,this.getPath(),this.getTree());
		for (int i = midIndex; i < totalKeys - 1; ++i) {
			newNode.insertAt(i - midIndex, this.getKey(i), this.getRecord(i));
			numberOfKeys--;
		}

		// insert the new key
		Vector<Ref> newVec = new Vector<Ref>();
		newVec.add(recordReference);
		if (keyIndex < totalKeys / 2) {

			this.insertAt(keyIndex, key, newVec);
		} else {
			newNode.insertAt(keyIndex - midIndex, key, newVec);
		}
		// set next pointers
		newNode.setNext(this.getNext());
		this.setNext(newNode);
		save();
		return newNode;
	}

	/**
	 * finds the index at which the passed key must be located 
	 * 
	 * @param key the key to be checked for its location
	 * @return the expected index of the key
	 */
	public int findIndex(T key) {

		for (int i = 0; i < numberOfKeys; ++i) {
			int cmp = getKey(i).compareTo(key);
			if (cmp > 0)
				return i;
		}
		return numberOfKeys;
	}

	/**
	 * returns the record reference with the passed key and null if does not exist
	 */
	@Override
	public Vector<Ref> search(T key) {

		
		for (int i = 0; i < numberOfKeys; ++i)
//			if (this.getKey(i).compareTo(key) == 0)
//				return this.getRecord(i);
			if (this.getKey(i).equals(key))
				return this.getRecord(i);
		return null;
	}

	public Vector<Ref> searchGreaterThan(T key) {

		Vector<Ref> result = new Vector<Ref>();

		for (int i = 0; i < numberOfKeys; ++i) {
			if (this.getKey(i).compareTo(key) > 0)
				result.addAll(this.getRecord(i));
		}
		if(this.next!=null) {
			result.addAll(getNext().searchGreaterThan(key));
		}
		return result;
	}
	public Vector<Ref> searchGreaterThanOrEqual(T key) {
		Vector<Ref> result = new Vector<Ref>();

		for (int i = 0; i < numberOfKeys; ++i) {
			if (this.getKey(i).compareTo(key) >= 0)
				result.addAll(this.getRecord(i));
		}
		if(this.next!=null) {
			result.addAll(getNext().searchGreaterThanOrEqual(key));
		}
		return result;
	}
	public Vector<Ref> searchSmallerThan(Comparable minKey,T key) {
		Vector<Ref> result = new Vector<Ref>();

		for (int i = 0; i < numberOfKeys; ++i) {
			if (this.getKey(i).compareTo(key) >= 0)
				return result;
			else {
				result.addAll(this.getRecord(i));
			}
		}
		if(this.next!=null) {
			result.addAll(getNext().searchSmallerThan(minKey, key));
		}
		return result;
	}
	public Vector<Ref> searchSmallerThanOrEqual(Comparable minKey,T key) {
		Vector<Ref> result = new Vector<Ref>();

		for (int i = 0; i < numberOfKeys; ++i) {
			if (this.getKey(i).compareTo(key) > 0)
				return result;
			else {
				result.addAll(this.getRecord(i));
			}
		}
		if(this.next!=null) {
			result.addAll(getNext().searchSmallerThanOrEqual(minKey, key));
		}
		return result;
	}
	public Vector<Ref> notEqual(Comparable minKey,T key) {

		Vector<Ref> result = new Vector<Ref>();

		for (int i = 0; i < numberOfKeys; ++i) {
//			if (this.getKey(i).compareTo(key) != 0) {
			if (!this.getKey(i).equals(key) ) {

				result.addAll(this.getRecord(i));
			}
		}
		if(this.next!=null) {
			result.addAll(getNext().notEqual(minKey, key));
		}
		return result;
	}




	public boolean containsKey(T key) {
		for (int i = 0; i < numberOfKeys; ++i)
			if (this.getKey(i).compareTo(key) == 0) {
				return true;

			}
		return false;
	}

	public void addDuplicateKey(T key, Ref ref) throws IOException {
		for (int i = 0; i < numberOfKeys; ++i)
			if (this.getKey(i).compareTo(key) == 0) {
				records[i].add(ref);
				save();
				return;
			}
	}

	/**
	 * 
	 * the passed key from the B+ tree
	 * @throws IOException 
	 */
	
	public boolean delete(T key, BPTreeInnerNode<T> parent, int ptr, Ref ref) throws IOException {

		for (int i = 0; i < numberOfKeys; ++i)
//			if (keys[i].compareTo(key) == 0) {
			if (keys[i].equals(key) ) {

				this.deleteAt(i, ref);
				if (i == 0 && ptr > 0) {
					// update key at parent
					parent.setKey(ptr - 1, this.getFirstKey());
				}
				// check that node has enough keys
				if (!this.isRoot() && numberOfKeys < this.minKeys()) {
					// 1.try to borrow
					boolean f = false;
					if (f = borrow(parent, ptr))
						return true;
					// 2.merge
					System.out.println(f);
					merge(parent, ptr);
				}
				return true;
			}
		return false;
	}

	/**
	 * delete a key at the specified index of the node
	 * 
	 * @param index the index of the key to be deleted
	 * @throws IOException 
	 */
	public void deleteAt(int index, Ref ref) throws IOException {
//		System.out.println("index: " + index + " ref: " + ref);
//		System.out.println("record size: " + records[index].size());
		for (int i = 0; i < records[index].size(); i++) {
		//	String pageDirectory = records[index].get(i).getPageDirectory();
			Ref recordRef=records[index].get(i);
			//int recordIdx = records[index].get(i).getIndexInPage();
			//System.out.println(pageDirectory + " " );
			if (recordRef.equals(ref) ) {
				records[index].remove(i);
				break;
			}
		}
		
		if (records[index].size() == 0) {
			for (int i = index; i < numberOfKeys - 1; ++i) {
				keys[i] = keys[i + 1];
				records[i] = records[i + 1];
			}
			numberOfKeys--;
		}
		save();
	}
	

	/**
	 * tries to borrow a key from the left or right sibling
	 * 
	 * @param parent the parent of the current node
	 * @param ptr    the index of the parent pointer that points to this node
	 * @return true if borrow is done successfully and false otherwise
	 * @throws IOException 
	 */
	public void deleteVectorAt(int index) throws IOException {
		for (int i = index; i < numberOfKeys - 1; ++i) {
			keys[i] = keys[i + 1];
			records[i] = records[i + 1];
		}
		numberOfKeys--;
		save();
	}

	public boolean borrow(BPTreeInnerNode<T> parent, int ptr) throws IOException {
		// check left sibling
		if (ptr > 0) {
			BPTreeLeafNode<T> leftSibling = (BPTreeLeafNode<T>) (parent.getChild(ptr - 1));
			if (leftSibling.numberOfKeys > leftSibling.minKeys()) {
				this.insertAt(0, leftSibling.getLastKey(), leftSibling.getLastRecord());
				leftSibling.deleteVectorAt(leftSibling.numberOfKeys - 1);
				parent.setKey(ptr - 1, keys[0]);
				System.out.println(parent.getDirectory()+"laf");

				return true;
			}
		}

		// check right sibling
		if (ptr < parent.numberOfKeys) {
			BPTreeLeafNode<T> rightSibling = (BPTreeLeafNode<T>) parent.getChild(ptr + 1);
			if (rightSibling.numberOfKeys > rightSibling.minKeys()) {
				this.insertAt(numberOfKeys, rightSibling.getFirstKey(), rightSibling.getFirstRecord());
				rightSibling.deleteVectorAt(0);
				parent.setKey(ptr, rightSibling.getFirstKey());
				System.out.println(parent.getDirectory()+"Tttdasdasdttttt");

				return true;
			}
		}
		System.out.println(parent.getDirectory()+"Go to hell");

		return false;
	}

	/**
	 * merges the current node with its left or right sibling
	 * 
	 * @param parent the parent of the current node
	 * @param ptr    the index of the parent pointer that points to this node
	 * @throws IOException 
	 */
	public void merge(BPTreeInnerNode<T> parent, int ptr) throws IOException {
		if (ptr > 0) {
			// merge with left
			BPTreeLeafNode<T> leftSibling = (BPTreeLeafNode<T>) parent.getChild(ptr - 1);
			leftSibling.merge(this);
			parent.deleteAt(ptr - 1);
			
		} else {
			// merge with right
			BPTreeLeafNode<T> rightSibling = (BPTreeLeafNode<T>) parent.getChild(ptr + 1);
			this.merge(rightSibling);
			parent.deleteAt(ptr);
			
		}
	}

	/**
	 * merge the current node with the specified node. The foreign node will be
	 * deleted
	 * 
	 * @param foreignNode the node to be merged with the current node
	 * @throws IOException 
	 */
	public void merge(BPTreeLeafNode<T> foreignNode) throws IOException {
		for (int i = 0; i < foreignNode.numberOfKeys; ++i)
			this.insertAt(numberOfKeys, foreignNode.getKey(i), foreignNode.getRecord(i));

		this.setNext(foreignNode.getNext());
		System.out.println("Leave Node   "+foreignNode.getDirectory());
		File file = new File(foreignNode.getDirectory());
		if(file.exists())
			file.delete();
		//Added this line to delete from disk 
	}

	

	
	
}