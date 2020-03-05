package APlusTree;

import java.awt.Polygon;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

public class DBAppTest {

	
public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		testInsert(db);
	
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
		htblColNameType.put("Bola", "java.lang.Boolean");
		htblColNameType.put("poly", "java.awt.Polygon");
		
		db.createTable(tableName,"Bola",htblColNameType);
		Random rnd= new Random();
		
		for(int i = 0 ; i < 52 ; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id= rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", new String("Student"+(id)));
			htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
			boolean xa=i%2==0;
			htblColNameValue.put("Bola", new Boolean(xa));

			
			int n=3+rnd.nextInt(5);
			int[]x =new int[n];
			int[] y=new int[n];
			for(int j=0;j<n;j++) {
				x[j]=rnd.nextInt(10);
				y[j]=rnd.nextInt(10);
			}
			htblColNameValue.put("poly",new Polygon(x,y,n));
			db.insertIntoTable("Student", htblColNameValue);
			
		}
			
		
		
	}
	
	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Student";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		int[]x= {8,8,9};
		int[]y= {2,1,0};
		
		htblColNameValue.put("poly", new Polygon(x,y,3));
db.deleteFromTable(tableName, htblColNameValue);		
		
		
		
	}
	

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Student";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		htblColNameValue.put("name", "momo");
db.updateTable(tableName, "(8 ,2 ) (8 ,1 ) (9 ,0)", htblColNameValue);
		
		
		
	}
}
