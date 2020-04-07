package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.*;


public class DBAppTest {

	public static void main(String[] args) throws Exception {
		DBApp db= new DBApp();
		//	testUpdate(db);
		//db.createBTreeIndex("Ahmed" ,"name");
		testInsert2(db);

		

		System.out.println("......................................................................");
		//testDelete(db);
			//testInsert(db);
		 //db.createBTreeIndex("Ahmed", "id");

			//select(db);
			System.out.println(db.displayTable("Ahmed"));
			Table t = db.getTable("Ahmed");
			t.dispalyIndex("id");//t.dispalyIndex("name");

			
	}

	public static void testInsert(DBApp db) throws Exception {
		db.init();

		// 1.Student Table
		String tableName = "Ahmed";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		
		  htblColNameType.put("id", "java.lang.Integer"); htblColNameType.put("name",
		  "java.lang.String"); // htblColNameType.put("date", "java.util.Date");
		  db.createTable(tableName, "id", htblColNameType);
		 

		Random rnd = new Random();

		for (int i = 0; i < 6; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id = i;
			htblColNameValue.put("id", new Integer(i));
			htblColNameValue.put("name", "aman" + id);
			//Calendar cal = Calendar.getInstance();
			//cal.set(Calendar.Month, 1)
			/*
			 * cal.set(2020+i, i%12+1, (i)%30 ,0, 0, 0); cal.set(Calendar.MILLISECOND,0);
			 * Date date = cal.getTime(); htblColNameValue.put("date",date);
			 */
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
//			Calendar cal = Calendar.getInstance();
//			//cal.set(Calendar.Month, 1)
//			cal.set(2020+i, i%12+1, (i)%30 ,0, 0, 0);
//			cal.set(Calendar.MILLISECOND,0);
//			Date date = cal.getTime();
//			htblColNameValue.put("date",date);
			db.insertIntoTable("Ahmed", htblColNameValue);

			}

	}

	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("Bola", new Double(0.7054163647592768));

		htblColNameValue.put("id", 4);
//		int [] x = {3,8,9,8} , y = {5,4,6,0};
//		htblColNameValue.put("poly", new Poygon(x,y,4));
//		htblColNameValue.put("gpa", 16);

		db.deleteFromTable(tableName, htblColNameValue);

	}

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("gpa", new Integer(8));
		htblColNameValue.put("id", 5);
		htblColNameValue.put("name", "aman5555");


		db.updateTable(tableName, "1", htblColNameValue);

	}

	public static void select(DBApp db) throws Exception {
		String tableName = "Ahmed";
		String columnName = "name";
		String opreator = "=";
		String val = "SalmaSarahAmannnnnn1502";
		SQLTerm arr[] = new SQLTerm[2];
		String operator[] = {"OR"};

		arr[0] = new SQLTerm(tableName, columnName, opreator, val);
		/*
		 * String columnName1 = "name"; String opreator1 = "="; String val1 = "aman5";
		 * arr[1] = new SQLTerm(tableName, columnName1, opreator1, val1);
		 */
		String columnName2 = "id";
		String opreator2 = "=";
		int val2 = 1503;
		arr[1] = new SQLTerm(tableName, columnName2, opreator2, val2);
		
		Iterator itr = db.selectFromTable(arr, operator);

		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}

	}
}
