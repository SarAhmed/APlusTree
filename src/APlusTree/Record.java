package APlusTree;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class Record implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private Vector<Object> vals;
		
	public Record(){
		vals = new Vector<Object>();
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vals == null) ? 0 : vals.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
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
		} else if (!vals.equals(other.vals))
			return false;
		return true;
	}

	
}
