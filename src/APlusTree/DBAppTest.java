package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class DBAppTest {

	public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		//testInsert(db);
		//System.out.println(db.displayTable("Ahmed"));

//		Hashtable<String, Object> htblColNameValue = new Hashtable();
//		testUpdate(db);
//		Random rnd= new Random();
//
//		int id= 9;
//		htblColNameValue.put("id", new Integer(id));
//		htblColNameValue.put("name", new String("Saraaaaaaaaaaaaah"));
//		htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
//		boolean xa=3%2==0;
//		htblColNameValue.put("Bola", rnd.nextDouble());
//		int n=3+rnd.nextInt(5);
//		int[]x =new int[n];
//		int[] y = new int[n];
//		for (int j = 0; j < n; j++) {
//			x[j] = rnd.nextInt(10);
//			y[j] = rnd.nextInt(10);
//		}
//		int d = rnd.nextInt(10) + 1;
//		int m = rnd.nextInt(10) + 1;
//		htblColNameValue.put("EntryDate", new Date("2000/" + m + "/" + d));
//		htblColNameValue.put("poly", new Polygon(x, y, n));
//		db.insertIntoTable("Ahmed", htblColNameValue);
//		testInsert(db);
		
//		System.out.println(db.displayTable("Ahmed"));
//		Hashtable<String, Object> htblColNameValue = new Hashtable();
		db.createBTreeIndex("Ahmed", "kkkkk");
//		htblColNameValue.put("id", new Integer(70));
//		htblColNameValue.put("name", new String("AliXMen"));
//		htblColNameValue.put("Bola", 12.0);
//		htblColNameValue.put("gpa", new Integer(40));
//		htblColNameValue.put("EntryDate", new Date());
//		int [] a = {12,13,14,12};
//		int [] b = {35,36,37,35};
//		int [] c= {12,23};
//		htblColNameValue.put("poly", new Polygon( a,b,2));
//		db.insertIntoTable("Ahmed", htblColNameValue);
//
//	
//		System.out.println("----------------------------------------------------------------test");
//		select(db);

	}

	public static void testInsert(DBApp db) throws Exception {
		db.init();

		// 1.Student Table
		String tableName = "Ahmed";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Integer");
		htblColNameType.put("Bola", "java.lang.Double");
		htblColNameType.put("poly", "java.awt.Polygon");
		htblColNameType.put("EntryDate", "java.util.Date");

		db.createTable(tableName, "id", htblColNameType);
		Random rnd = new Random();

		for (int i = 0; i < 100; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id = rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", new String("Ali" + (id)));
			htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
			boolean xa = i % 2 == 0;
			htblColNameValue.put("Bola", rnd.nextDouble());
			int n = 3 + rnd.nextInt(5);
			int[] x = new int[n];
			int[] y = new int[n];
			for (int j = 0; j < n; j++) {
				x[j] = rnd.nextInt(10);
				y[j] = rnd.nextInt(10);
			}
			int d = rnd.nextInt(10) + 1;
			int m = rnd.nextInt(10) + 1;
			htblColNameValue.put("EntryDate", new Date("2000/" + m + "/" + d));
			htblColNameValue.put("poly", new Polygon(x, y, n));
			db.insertIntoTable("Ahmed", htblColNameValue);

		}

	}

	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("Bola", new Double(0.7054163647592768));

		htblColNameValue.put("name", "Khaleeeeeeeeeeeeeed");
		htblColNameValue.put("id", 9);
		db.deleteFromTable(tableName, htblColNameValue);

	}

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		htblColNameValue.put("gpa", new Integer(2000000009));
		htblColNameValue.put("name", "Hotlkjasdasdasd");

		db.updateTable(tableName, "-1", htblColNameValue);

	}

	public static void select(DBApp db) throws Exception {
		String tableName = "Ahmed";
		String columnName = "id";
		String opreator = "=";
		Object val = 40;
		SQLTerm arr[] = new SQLTerm[2];
		String operator []= {"XOR"};

		arr[0] = new SQLTerm(tableName, columnName, opreator, val);
		
		columnName = "name";
		opreator = "=";
		val = "AliXMen";
		arr[1] = new SQLTerm(tableName, columnName, opreator, val);
		 Iterator itr = db.selectFromTable(arr, operator);
		 
		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}
		

		

	}
}
