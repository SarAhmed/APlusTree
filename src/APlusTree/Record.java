package APlusTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static java.util.Objects.hash;

import java.awt.Polygon;

public class Record implements Serializable {

	private static final long serialVersionUID = 1L;
	private Vector<Object> vals;
	private static long nextId = 0l;
	private long id;
	String tableName;

	public void saveNextId() throws IOException {
		File file = new File("data/" + tableName+"_RecordNextID.class");
		if (!file.exists())
			file.delete();
		file.createNewFile();
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(nextId);
		stream.close();
	}

	public long getNextId() {
		long val = 0;
		try {

			File f = new File("data/" + tableName+"_RecordNextID.class");
			if (!f.exists())
				return 0;
			FileInputStream file = new FileInputStream("data/" + tableName+"_RecordNextID.class");
			ObjectInputStream in = new ObjectInputStream(file);
			val = (long) in.readObject();

			in.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
			// ex.printStackTrace();
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
		return val;
	}

	public Record(String tableName) throws IOException {
		this.tableName = tableName;
		vals = new Vector<Object>();
		nextId = getNextId();
		this.id = nextId++;
		saveNextId();
	}

	public boolean hasId(long id) {
		return this.id == id;
	}

	public long getId() {
		return this.id;
	}

	public Object get(int idx) {
		return vals.get(idx);
	}

	public void add(int idx, Object val) {
		vals.insertElementAt(val, idx);

	}

	public void update(int idx, Object val) {
		vals.setElementAt(val, idx);
	}

	public Vector<Object> getValues() {
		return vals;
	}

	public void add(Object val) {
		vals.add(val);
	}

	public String toString() {
		String s = "";
		for (Object o : vals)
			s += o.toString() + ", ";

		s += " record id " + id;
		return s;
	}

	@Override
	public int hashCode() {
		// System.out.println("I am using hascode");
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vals == null) ? 0 : vals.hashCode()) + hash(getId());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Record))
			return false;
		Record record = (Record) o;
		// System.out.println(getId() == record.getId());
		return getId() == record.getId() && vals.equals(record.vals);
	}

	/*
	 * @Override public boolean equals(Object obj) { //
	 * System.out.println("I am using equal");
	 * 
	 * if (this == obj) return true; if (obj == null) return false; if (getClass()
	 * != obj.getClass()) return false; Record other = (Record) obj; if (vals ==
	 * null) { if (other.vals != null) return false; } else if
	 * (!vals.equals(other.vals)) { //System.out.println("Val no equal"); return
	 * false; } return true; }
	 */

	public static void main(String[] args) throws IOException {
//		String s1 = new String("sarah");
//		String s2 = new String("sarah");
//		// System.out.println(s1==s2);
//		Record r1 = new Record();
//		r1.vals.add(new Integer(1));
//		Record r2 = new Record();
//		r2.vals.add(new Integer(1));
//		HashSet<Record> a = new HashSet<>();
//		a.add(r1);
//		// System.out.println(r1.hashCode() == r2.hashCode());
//
//		// System.out.println(hash(r1.hashCode()) == hash(r2.hashCode()));
//		// HashMap<Integer ,Record> map = new HashMap<>();
//		// map.put(hash(r1),r1);
//		// System.out.println(a.contains(r2));
//		Vector<Object> d1 = new Vector<>();
//		d1.add(new Integer(1));
//		Vector<Object> d2 = new Vector<>();
//		d2.add(new Integer(1));
//		System.out.println(d1.equals(d2));
		Record r = new Record("Ahmed");
		r.add("aman");r.add(5);r.add(true);r.add(new Date());r.add(new DBPolygon(new Polygon()));
		for(Object p : r.vals) System.out.println(p.getClass());
   }
}
