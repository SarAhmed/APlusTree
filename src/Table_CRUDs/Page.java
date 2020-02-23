package Table_CRUDs;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Page implements Serializable{
	
	/**
	 * A page is part of a table holding its records.
	 * Tables are stored in many pages in a binary 
	 * file format (.class files)
	 */
	private static final long serialVersionUID = 1L;
	private int maxSize;
	private Vector <Record> records;
	private String path;
	

	
	public Vector<Record> getRecords() {
		return records;
	}

	
	/**
	 * Create a new page specifying the maximum number of records it can hold
	 * and the path at which the page will be stored relative to the executable files
	 * 
	 * @param maxSize the maximum number of records that fit in one page
	 * @param path the path at which the page is stored relative to the executable files
	 * @throws IOException If an I/O error occurred
	 */
	public Page(int maxSize, String path) throws IOException
	{
		this.path = path;
		this.maxSize = maxSize;
		this.records = new Vector<>();
		this.save();
	}
	
	/**
	 * Check whether a page has records with the maximum number of records or not
	 * @return whether a page is full or not
	 */
	public boolean isFull()
	{
		return records.size() == maxSize;
	}
	
	/**
	 * Insert a new record at the end of the page
	 * @param record the record to be inserted
	 * @return a boolean to indicate a successful/failed insertion 
	 * @throws IOException If an I/O error occurred
	 */
	public boolean addRecord(Record record,int index) throws IOException
	{
		if(isFull())
			return false;
		records.insertElementAt(record,index);
		save();
		return true;
	}
	
	/**
	 * Delete a record from the page at specified index
	 * @param index the index of the record in the page to be deleted
	 * @throws IOException If an I/O error occurred
	 */
	
	public Record removeRecord(int index) throws IOException
	{
		Record r=records.remove(index) ;
		save();
		return r;
	}
	
	/**
	 * save the page permanently on a secondary storage
	 * @throws IOException If an I/O error occurred
	 */
	public void save() throws IOException
	{
		File f = new File(path);
		if(!f.exists())
			f.delete();
		f.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(this);
		oos.close();
	}
	
	/**
	 * Returns the current number of records in the page
	 * @return the number of records in the page
	 */
	public int size()
	{
		return records.size();
	}
	
	/**
	 * Get a record with its position in the page
	 * @param index the position of the record in the page
	 * @return the required record
	 */
	public Record get(int index)
	{
		if(index >= 0 && index < this.size())
			return records.get(index);
		throw new IndexOutOfBoundsException(""+index);
	}
	
	public String toString() {
		String r="";
		for(Record rec: records) {
			r+=rec.toString()+"\n";
		}
		return r;
	}
}
