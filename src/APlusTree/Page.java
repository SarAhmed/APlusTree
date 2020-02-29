package APlusTree;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Page implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int maxSize;
	private Vector <Record> records;
	private String path;
	

	
	public Vector<Record> getRecords() {
		return records;
	}

	
	public Page(int maxSize, String path) throws IOException
	{
		this.path = path;
		this.maxSize = maxSize;
		this.records = new Vector<>();
		this.save();
	}
	
	public boolean isFull()
	{
		return records.size() == maxSize;
	}
	
	public boolean addRecord(Record record,int index) throws IOException
	{
		if(isFull())
			return false;
		records.insertElementAt(record,index);
		save();
		return true;
	}
	
	
	public Record removeRecord(int index) throws IOException
	{
		Record r=records.remove(index) ;
		save();
		return r;
	}
	
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
	
	public int size()
	{
		return records.size();
	}
	
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
