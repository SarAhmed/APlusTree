package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

public class DBAppTest {

	
public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		testDelete(db);
		System.out.println(db.displayTable("Ahmed"));
	}


	public static void testInsert(DBApp db) throws Exception {
		db.init();
		
		//1.Student Table
		String tableName = "Ahmed";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Integer");
		htblColNameType.put("Bola", "java.lang.Double");
		htblColNameType.put("poly", "java.awt.Polygon");
		htblColNameType.put("EntryDate", "java.util.Date");

		
		db.createTable(tableName,"poly",htblColNameType);
		Random rnd= new Random();
		
		for(int i = 0 ; i < 52 ; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id= rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", new String("Ali"+(id)));
			htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
			boolean xa=i%2==0;
			htblColNameValue.put("Bola", rnd.nextDouble());
			int n=3+rnd.nextInt(5);
			int[]x =new int[n];
			int[] y=new int[n];
			for(int j=0;j<n;j++) {
				x[j]=rnd.nextInt(10);
				y[j]=rnd.nextInt(10);
			}
			int d= rnd.nextInt(10)+1;
			int m= rnd.nextInt(10)+1;
			htblColNameValue.put("EntryDate",new Date("2000/"+m+"/"+d));
			htblColNameValue.put("poly",new Polygon(x,y,n));
			db.insertIntoTable("Ahmed", htblColNameValue);
			
		}
			
		
		
	}
	
	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Ahmed";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		int[]x= {8,8,9};
		int[]y= {2,1,0};
		
		//htblColNameValue.put("Bola", new Double(0.7054163647592768));
		htblColNameValue.put("name", "Ali0");
db.deleteFromTable(tableName, htblColNameValue);		
		
		
		
	}
	

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Ahmed";
	
		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		htblColNameValue.put("gpa", new Integer(2000000009));
db.updateTable(tableName, "(1,4),(1,4),(9,4)", htblColNameValue);
		
		
		
	}
}
