package APlusTree;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

public class Record implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private Vector<Object> vals;
	private static long nextId=0l;
	private long id;
	public Record(){
		vals = new Vector<Object>();
		this.id=nextId++;
	}
	public boolean hasId(long id) {
		return this.id==id;
	}
	public long getId() {
		return this.id;
	}
	public Object get(int idx){
		return vals.get(idx);
	}
	
	
	public void add(int idx , Object val){
		vals.insertElementAt(val, idx);
		
	}
	public void update(int idx , Object val){
		vals.setElementAt(val, idx);
	}
	public Vector<Object> getValues() {
		return vals;
	}
	public void add(Object val) {
		vals.add(val);
	}
	
	

	public String toString(){
		String s = "";
		for(Object o: vals)
			s += o.toString() + ", ";
		return s;
	}
	
	
	@Override
	public int hashCode() {
		//System.out.println("I am using hascode");
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vals == null) ? 0 : vals.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
	//	System.out.println("I am using equal");

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (vals == null) {
			if (other.vals != null)
				return false;
		} else if (!vals.equals(other.vals)) {
			//System.out.println("Val no equal");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String s1 =new String("sarah");
		String s2=new String("sarah");
		System.out.println(s1==s2);
	}
}
