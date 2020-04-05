package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.*;

//import static APlusTree.Table.and;

public class DBAppTest {

	public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		// testInsert(db);
		select(db);
		System.out.println("***********************************************");
		System.out.println(db.displayTable("Ahmed"));

	}

	public static void testInsert(DBApp db) throws Exception {
		db.init();

		// 1.Student Table
		String tableName = "Ahmed";
		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		db.createTable(tableName, "id", htblColNameType);
		db.createBTreeIndex(tableName, "id");

		Random rnd = new Random();

		for (int i = 0; i < 100; i++) {
			Hashtable<String, Object> htblColNameValue = new Hashtable();
			int id = rnd.nextInt(10);
			htblColNameValue.put("id", new Integer(id));
			htblColNameValue.put("name", "aman" + id);
			db.insertIntoTable("Ahmed", htblColNameValue);

		}

	}

	public static void testDelete(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("Bola", new Double(0.7054163647592768));

		htblColNameValue.put("name", "Ali8");
//		int [] x = {3,8,9,8} , y = {5,4,6,0};
//		htblColNameValue.put("poly", new Poygon(x,y,4));
		htblColNameValue.put("gpa", 16);

		db.deleteFromTable(tableName, htblColNameValue);

	}

	public static void testUpdate(DBApp db) throws Exception {
		String tableName = "Ahmed";

		Hashtable<String, Object> htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		// htblColNameValue.put("gpa", new Integer(8));
		htblColNameValue.put("name", "Saraaaaaaaaaaaaaaaaah");

		db.updateTable(tableName, "(9 ,9 ) (0 ,9 ) (6 ,0 ) (2 ,3 ) (3 ,6 ) (4 ,7 )", htblColNameValue);

	}

	public static void select(DBApp db) throws Exception {
		String tableName = "Ahmed";
		String columnName = "name";
		String opreator = "=";
		String val = "aman1";
		SQLTerm arr[] = new SQLTerm[3];
		String operator[] = { "XOR", "OR" };

		arr[0] = new SQLTerm(tableName, columnName, opreator, val);

		String columnName1 = "name";
		String opreator1 = "=";
		String val1 = "aman5";
		arr[1] = new SQLTerm(tableName, columnName1, opreator1, val1);

		String columnName2 = "id";
		String opreator2 = "=";
		int val2 = 9;
		arr[2] = new SQLTerm(tableName, columnName2, opreator2, val2);
		Iterator itr = db.selectFromTable(arr, operator);

		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}

	}
}
