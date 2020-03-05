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
	private String directory;
	

	
	
	public Page(int maxSize, String directory) throws IOException
	{
		this.maxSize = maxSize;
		this.directory = directory;
		this.records = new Vector<>();
		this.save();
	}
	
	
	public boolean add(Record record,int idx) throws IOException{
		if(isFull())
			return false;
		records.insertElementAt(record,idx);
		save();
		return true;
	}
	
	public boolean isFull(){
		return records.size() == maxSize;
	}
	
	public Record remove(int idx) throws IOException{
		Record r=records.remove(idx) ;
		save();
		return r;
	}
	// copy rights @ Ahmad Al-Sagheer
	public Record get(int idx){
		if(idx >= 0 && idx < this.size())
			return records.get(idx);
		throw new IndexOutOfBoundsException(""+idx);
	}
	
	public int size(){
		return records.size();
	}
	
	
	public String toString() {
		String r="";
		for(Record rec: records) {
			r+=rec.toString()+"\n";
		}
		return r;
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

	public Vector<Record> getRecords() {
		return records;
	}

}
