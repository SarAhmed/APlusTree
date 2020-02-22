package Table_CRUDs;
import java.io.Serializable;
import java.util.Vector;

public class Record implements Serializable{

	/**
	 * A record represents a row in a table or
	 * a tuple in a relation. It consists of 
	 * an array of values.
	 */
	
	private static final long serialVersionUID = 1L;
	private Vector<Object> values;
		
	/**
	 * Creates a new record
	 * @param size number of columns of the table holding the record
	 */
	public Record()
	{
		values = new Vector<Object>();
	}
	
	/**
	 * Update the value for a given column in the record 
	 * @param index the index of the column to be updated
	 * @param value the new value
	 */
	public void addValue(int index , Object value)
	{
		values.setElementAt(value, index);
	}
	
	/**
	 * Get the value of a given column of this record
	 * @param index index of the required column
	 * @return the value of that column
	 */
	public Object get(int index)
	{
		return values.get(index);
	}
	
	/**
	 * Display the record values 
	 */
	public String toString()
	{
		String ret = "";
		for(Object o: values)
			ret += o.toString() + ", ";
		return ret;
	}
}
