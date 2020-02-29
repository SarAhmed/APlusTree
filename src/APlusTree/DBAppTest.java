package APlusTree;

import java.util.Hashtable;
import java.util.Random;

public class DBAppTest {

	
public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		//testInsert(db);
		testUpdate(db);
		System.out.println(db.displayTable("Student"));
	}


	public static void testInsert(DBApp db) throws Exception {
		db.init();
		
		//1.Student Table
		String tableName = "Student";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Integer");
		db.createTable(tableName,"id",htblColNameType);
		Random rnd= new Random();
		
		for(int i = 0 ; i < 52 ; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id= rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", new String("Student"+(id)));
			htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
			
			db.insertIntoTable("Student", htblColNameValue);
		}
			
		
		
	}
	
	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Student";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		htblColNameValue.put("id", 1);
db.deleteFromTable(tableName, htblColNameValue);		
		
		
		
	}
	
	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Student";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		htblColNameValue.put("name", "moh");
db.updateTable(tableName, ""+6, htblColNameValue);
		
		
		
	}
}
