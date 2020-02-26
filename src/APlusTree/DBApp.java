package APlusTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;


public class DBApp {

	private static String mainDirectory = "";
	private String dbDirectory;
	private File metadata;
	private Properties dbProps;
	private String dataTypes[] = { "java.lang.Integer", "java.lang.String", "java.lang.Double", "java.lang.Boolean",
			"java.util.Date", "java.awt.Polygon" };

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException, IOException {
		File dir = new File(dbDirectory +"data/"+ strTableName+".class");
		// TO DO
		 if(dir.exists())
		 throw new DBAppException("There exists a table with this name.");

		boolean validColType = checkColumnTypes(htblColNameType);
		if (!validColType)
			throw new DBAppException("Invalid column type.");
		try {
			addToMetaData(strTableName, htblColNameType, strClusteringKeyColumn);
		} catch (IOException e) {
			throw new DBAppException("An error happened while creating the meta file!");
		}
		htblColNameType.put("TouchDate", "java.util.Date");
		int maxTuplesPerPage = Integer.parseInt(dbProps.getProperty("MaximumRowsCountinPage"));
		int nodeSize = Integer.parseInt(dbProps.getProperty("NodeSize"));
		new Table(dbDirectory, strTableName, htblColNameType, strClusteringKeyColumn, maxTuplesPerPage,nodeSize);
		System.out.println("Table is created successfully: " + strTableName);

	}

	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {
		Table t = getTable(strTableName);
		t.insertIntoTable(htblColNameValue);

	}

	public void updateTable(String strTableName, String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException, FileNotFoundException, ClassNotFoundException, IOException {
		Table t = getTable(strTableName);
		t.updateTable(strClusteringKey, htblColNameValue);

	}

	public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {
		Table t=getTable(strTableName);
		t.deleteFromTable(htblColNameValue);
		
	}

	private Table getTable(String strTableName) throws FileNotFoundException, IOException, ClassNotFoundException {
		File tableFile = new File(dbDirectory  + "data/" + strTableName + ".class");
		if (!tableFile.exists())
			return null;
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tableFile));
		Table table = (Table) ois.readObject();
		ois.close();
		return table;
	}

	public void init() throws IOException {

		File dbDir = new File(dbDirectory = mainDirectory);
		dbDir.mkdirs();

		String s="";
		
		new File(s=(dbDirectory + "config")).mkdirs();
		new File(s=(dbDirectory + "classes/APlusTree")).mkdirs();

		
		dbProps = new java.util.Properties();
		FileInputStream fis= new FileInputStream("config/DBApp.config");
		dbProps.load(fis);
		
		
		// initialize metadata file
		new File(dbDirectory + "data").mkdirs();
		this.metadata = new File(dbDirectory + "data/" + "/metadata.csv");
		if (this.metadata.createNewFile()) {
			PrintWriter out = new PrintWriter(this.metadata);
			out.println("Table Name, Column Name, Column Type, ClusteringKey, Indexed");
			out.flush();
			out.close();
		}

	}

	private boolean checkColumnTypes(Hashtable<String, String> htblColNameType) {
		for (Entry<String, String> entry : htblColNameType.entrySet()) {
			boolean flag = false;
			for (int i = 0; i < dataTypes.length; i++) {
				if (dataTypes[i].equals(entry.getValue()))
					flag = true;
			}
			if (!flag)
				return false;
		}
		return true;
	}

	private void addToMetaData(String strTableName, Hashtable<String, String> htblColNameType,
			String strClusteringKeyColumn) throws IOException {
		PrintWriter pr = new PrintWriter(new FileWriter(metadata, true));
		for (Entry<String, String> entry : htblColNameType.entrySet()) {
			String colName = entry.getKey();
			String colType = entry.getValue();
			boolean key = (colName == strClusteringKeyColumn);
			pr.append(strTableName + ", " + colName + ", " + colType + ", " + (key ? "True" : "False") + ", " + "False"
					+ "\n");
		}
		pr.flush();
		pr.close();
	}

	public static void main(String[] args) throws Exception {

		DBApp dbapp = new DBApp();
		dbapp.init();

		String strTableName = "Student";
		Hashtable htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Double");
		dbapp.createTable(strTableName, "id", htblColNameType);

		Hashtable htblColNameValue = new Hashtable();
		htblColNameValue.put("id", new Integer(2343432));
		htblColNameValue.put("name", new String("Ahmed Noor"));
		htblColNameValue.put("gpa", new Double(0.95));
		dbapp.insertIntoTable(strTableName, htblColNameValue);

		htblColNameValue.clear();
		htblColNameValue.put("id", new Integer(1));
		htblColNameValue.put("name", new String("Ahmed Noor"));
		htblColNameValue.put("gpa", new Double(0.95));
		dbapp.insertIntoTable(strTableName, htblColNameValue);

		htblColNameValue.clear();
		htblColNameValue.put("id", new Integer(1));
		htblColNameValue.put("name", new String("Dalia Noor"));
		htblColNameValue.put("gpa", new Double(1.25));
		dbapp.insertIntoTable(strTableName, htblColNameValue);

		htblColNameValue.clear();
		htblColNameValue.put("id", new Integer(1));
		htblColNameValue.put("name", new String("John Noor"));
		htblColNameValue.put("gpa", new Double(1.5));
		dbapp.insertIntoTable(strTableName, htblColNameValue);

		htblColNameValue.clear();
		htblColNameValue.put("id", new Integer(78452));
		htblColNameValue.put("name", new String("Zaky Noor"));
		htblColNameValue.put("gpa", new Double(0.88));
		dbapp.insertIntoTable(strTableName, htblColNameValue);
		htblColNameValue.clear();
		htblColNameValue.put("id", new Integer("1"));
		dbapp.deleteFromTable(strTableName, htblColNameValue);
		
//		try {
			// Reading the object from a file
//			FileInputStream file = new FileInputStream(
//					"C:\\Users\\SU\\Documents\\GitHub\\A-Tree\\databases\\AplusTree\\Student\\Student.class");
//			ObjectInputStream in = new ObjectInputStream(file);

			// Method for deserialization of object
		Table t = dbapp.getTable(strTableName);
		
			System.out.println(t.toString());

			System.out.println("Object has been deserialized ");
//		}

//		catch (IOException ex) {
//			System.out.println("IOException is caught");
//			ex.printStackTrace();
//		}

	}
}
