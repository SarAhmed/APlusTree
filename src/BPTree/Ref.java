package BPTree;

import java.io.Serializable;

public class Ref implements Serializable{
	
	/**
	 * This class represents a pointer to the record. It is used at the leaves of the B+ tree 
	 */
	private static final long serialVersionUID = 1L;
	private String pageDirectory; 
	//indexInPage;
	
	
public Ref(String pageDirectory)
	{
		this.pageDirectory = pageDirectory;
	//	this.indexInPage = indexInPage;
	}
	
	/**
	 * @return the page at which the record is saved on the hard disk
	 */
	public String getPageDirectory()
	{
		return pageDirectory;
	}
	
	/**
	 * @return the index at which the record is saved in the page
	 */
//	public int getIndexInPage()
//	{
//		return indexInPage;
//	}
}