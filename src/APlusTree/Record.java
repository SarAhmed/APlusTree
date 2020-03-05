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

	
}
