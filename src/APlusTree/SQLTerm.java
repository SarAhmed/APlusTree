package APlusTree;

public class SQLTerm {

	private String _strTableName ;
	private String _strColumnName;
	private String _strOperator;
	private Object _objValue;
	
	
	public SQLTerm(String tableName , String columnName , String opreator, Object val) {
		this._strTableName = tableName;
		this._strColumnName = columnName;
		this._strOperator = opreator;
		this._objValue = val ;
	}

	public String get_strTableName() {
		return _strTableName;
	}

	public String get_strColumnName() {
		return _strColumnName;
	}

	public String get_strOperator() {
		return _strOperator;
	}

	public Object get_objValue() {
		return _objValue;
	}
	
	public static void main(String[] args) {

	}


}
