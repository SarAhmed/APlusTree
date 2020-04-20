package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.*;


public class DBAppTest {

	public static void main(String[] args) throws Exception {
		DBApp db= new DBApp();
		//	testUpdate(db);
		//db.createBTreeIndex("Ahmed" ,"name");
	//	testInsert2(db);
		
		//testDelete(db);
			//testInsert(db);
		// db.createBTreeIndex("Ahmed", "EntryDate");
		 select(db);
			//	testDelete(db);
				System.out.println("......................................................................");
			//	testUpdate(db);
			//select(db);
			System.out.println(db.displayTable("Ahmed"));
		//	Table t = db.getTable("Ahmed");
		//	t.dispalyIndex("poly");//t.dispalyIndex("name");

			
	}

	public static void testInsert(DBApp db) throws Exception {
		db.init();

		// 1.Student Table
		String tableName = "Ahmed";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Integer");
		//htblColNameType.put("Bola", "java.lang.Double");
		//htblColNameType.put("poly", "java.awt.Polygon");
		htblColNameType.put("EntryDate", "java.util.Date");

		db.createTable(tableName, "EntryDate", htblColNameType);
		Random rnd = new Random();

		for (int i = 0; i < 100; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id = rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", new String("Ali" + (id)));
			htblColNameValue.put("gpa", new Integer(rnd.nextInt(100)));
			boolean xa = i % 2 == 0;
		//	htblColNameValue.put("Bola", rnd.nextDouble());
			int n = 3 + rnd.nextInt(5);
			int[] x = new int[n];
			int[] y = new int[n];
			for (int j = 0; j < n; j++) {
				x[j] = rnd.nextInt(10);
				y[j] = rnd.nextInt(10);
			}
			int d = rnd.nextInt(10) + 1;
			int m = rnd.nextInt(10) + 1;
			htblColNameValue.put("EntryDate", new Date("2000/" + 4 + "/" + d));
	//		htblColNameValue.put("poly", new Polygon(x, y, n));
			db.insertIntoTable("Ahmed", htblColNameValue);

		}

	}
	public static void testInsert2(DBApp db) throws Exception {
		
		
		
		

		Random rnd = new Random();

		for (int i = 1500; i < 1509; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id = i;
			htblColNameValue.put("id", new Integer(i));
			htblColNameValue.put("name", "aman" + 1502);
			Calendar cal = Calendar.getInstance();
			//cal.set(Calendar.Month, 1)
			cal.set(2020+i, i%12+1, (i)%30 ,0, 0, 0);
			cal.set(Calendar.MILLISECOND,0);
			Date date = cal.getTime();
			htblColNameValue.put("date",date);
			db.insertIntoTable("Ahmed", htblColNameValue);

			}

	}

	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("Bola", new Double(0.7054163647592768));

		/*
		 * htblColNameValue.put("id", 7); int [] x = {1,0,8,4,7,3,4}; int [] y =
		 * {2,0,0,2,7,9,9};
		 */
		Calendar cal = Calendar.getInstance();
		//cal.set(Calendar.Month, 1)
		cal.set(2000, 3, 1 ,0, 0, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date date = cal.getTime();
		htblColNameValue.put("EntryDate",date);
	//	htblColNameValue.put("poly", new Polygon(x,y,7));
//		htblColNameValue.put("gpa", 16);

		db.deleteFromTable(tableName, htblColNameValue);

	}

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("gpa", new Integer(8)); */
		Calendar cal = Calendar.getInstance();
		//cal.set(Calendar.Month, 1)
		cal.set(2000, 3, 2 ,0, 0, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date date = cal.getTime();
		//htblColNameValue.put("EntryDate",date);
		//htblColNameValue.put("id", 99);
		htblColNameValue.put("name", "aman5555");


		db.updateTable(tableName, "2000-4-3", htblColNameValue);

	}
	//(1 ,2 ) (0 ,0 ) (8 ,0 ) (4 ,2 ) (7 ,7 ) (3 ,9 ) (4 ,9 )

	public static void select(DBApp db) throws Exception {
		//int[] x = new int[7];
//		int[] y = new int[7];
		Calendar cal = Calendar.getInstance();
		//cal.set(Calendar.Month, 1)
		cal.set(2000, 3, 2 ,0, 0, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date date = cal.getTime();
		int [] x = {1,0,8,4,7,3,4};
		int [] y = {2,0,0,2,7,9,9};
		String tableName = "Ahmed";
		String columnName = "name";
		String opreator = "<";
		String val ="Ali9";
		System.out.println(val);
		SQLTerm arr[] = new SQLTerm[2];
		String operator[] = {"OR"};

		arr[0] = new SQLTerm(tableName, columnName, opreator, val);
		/*
		 * String columnName1 = "name"; String opreator1 = "="; String val1 = "aman5";
		 * arr[1] = new SQLTerm(tableName, columnName1, opreator1, val1);
		 */
		String columnName2 = "name";
		String opreator2 = "=";
		String val2 = "aman5555";
		arr[1] = new SQLTerm(tableName, columnName2, opreator2, val2);
		
		String columnName3 = "id";
		String opreator3 = ">";
		int val3 = 0;
		//arr[0] = new SQLTerm(tableName, columnName3, opreator3, val3);
		
		Iterator itr = db.selectFromTable(arr, operator);

		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}

	}
}
