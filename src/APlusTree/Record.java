package APlusTree;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class Record implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private Vector<Object> values;
		
	public Record()
	{
		values = new Vector<Object>();
	}
	
	public void updateValue(int index , Object value)
	{
		values.setElementAt(value, index);
	}
	public void addValue(int index , Object value)
	{
		values.insertElementAt(value, index);
		
	}
	public void addValue(Object value) {
		values.add(value);
	}
	
	public Object get(int index)
	{
		return values.get(index);
	}
	
	public Vector<Object> getValues() {
		return values;
	}

	public String toString()
	{
		String ret = "";
		for(Object o: values)
			ret += o.toString() + ", ";
		return ret;
	}

	
}
