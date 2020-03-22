package APlusTree;

import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.*;

import static APlusTree.Table.and;

public class DBAppTest {

	public static void main(String[] args) throws Exception {
		DBApp db = new DBApp();
		//testInsert(db);
		System.out.println(db.displayTable("Ahmed"));

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
//		Hashtable<String, Object> htblColNameValue = new Hashtable();s
//		db.createBTreeIndex("Ahmed", "kkkkk");
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
		System.out.println("----------------------------------------------------------------test");
		Table a = db.getTable("Ahmed");
		//a.selectEqual("id",9);
		//System.out.println(a.selectEqual("id",9).size());
		HashSet<Record> a1 = a.selectEqual("id",9);
		//System.out.println(a.selectEqual("name","Ali9").size());
		HashSet<Record> b1 = a.selectEqual("name","Ali9");
	//	System.out.println(and(a1,b1).size());
		Record r1 = null , r2 =null;
		/*for (Record r: a1) {
			if(b1.contains(r)){System.out.println("here*****22******");}
		//	System.out.println(r.toString()+ "*********"+r.getId());
		//	if (r.getId() == 77) r1 = r ;
		}*/
	//	System.out.println("***************************************************");
		System.out.println();
		for (Record r: b1) {
		//	System.out.print(r.hashCode()+"****");
		//	System.out.println(r.toString()+"********"+r.getId());
			if (r.getId() == 77) r2 = r ;
		}
		//System.out.println(r1.getValues().equals(r2.getValues()));
	//	System.out.println(r2.getValues().toString());
		/*Vector<Object> v1 = r1.getValues() , v2 = r2.getValues();
		Iterator<Object> t1 = v1.iterator() , t2 = v2.iterator();
		while (t1.hasNext() && t2.hasNext()){
			Object w1 = t1.next() , w2 = t2.next();
			System.out.println(w1+"   "+w2+"  "+w1.equals(w2));
		}*/
		/*Double d1 = new Double(2.0);
		Double d2 = new Double(2.0);
		System.out.println(d1.hashCode() == d2.hashCode());*/
		//select(db);

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
		Object val = 9;
		SQLTerm arr[] = new SQLTerm[2];
		String operator []= {"OR"};

		arr[0] = new SQLTerm(tableName, columnName, opreator, val);
		
		columnName = "name";
		opreator = "=";
		val = "Ali9";
		arr[1] = new SQLTerm(tableName, columnName, opreator, val);
		 Iterator itr = db.selectFromTable(arr, operator);
		 
		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}
		

		

	}
}
