package Table_CRUDs;

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

import DB_Exceptions.DBAppException;

public class DBApp {

	private static String mainDirectory = "databases/";
	private String dbDirectory;
	private File metadata;
	private Properties dbProps;
	private String dataTypes[] = { "java.lang.Integer", "java.lang.String", "java.lang.Double", "java.lang.Boolean",
			"java.util.Date", "java.awt.Polygon" };

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException {
		File dir = new File(dbDirectory + strTableName);
		// TO DO
		//    	if(dir.exists())
		//    		throw new DBAppException("There exists a table with this name.");

		boolean validColType = checkColumnTypes(htblColNameType);
		if (!validColType)
			throw new DBAppException();
		try {
			addToMetaData(strTableName, htblColNameType, strClusteringKeyColumn);
		} catch (IOException e) {
			System.out.println("An error happened while creating the meta file!");
		}
		htblColNameType.put("TouchDate", "java.util.Date");
		int maxTuplesPerPage = Integer.parseInt(dbProps.getProperty("MaximumRowsCountinPage"));
		new Table(dbDirectory, strTableName, htblColNameType,  strClusteringKeyColumn, maxTuplesPerPage, indexOrder);
		System.out.println("Table is created successfully: " + strTableName);

	}

	
	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		

	}

	private Table getTable(String strTableName) throws FileNotFoundException, IOException, ClassNotFoundException
    {
    	File tableFile = new File(dbDirectory+strTableName+"/"+strTableName+".class");
    	if(!tableFile.exists())
    		return null;
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tableFile));
    	Table table = (Table) ois.readObject();
    	ois.close();
    	return table;
    }
	public void init(Integer maximumRowsCountinPage, Integer indexOrder) throws IOException {

		File dbDir = new File(dbDirectory = mainDirectory + "AplusTree/");
		dbDir.mkdirs();

		// initialize config file
		// TO DO
		dbProps = new Properties();
		dbProps.put("MaximumRowsCountinPage", indexOrder.toString());
		dbProps.put("IndexOrder", maximumRowsCountinPage.toString());
		new File(dbDirectory + "/config").mkdirs();
		File config = new File(dbDirectory + "/config/DBApp.config");
		config.createNewFile();
		FileOutputStream fos = new FileOutputStream(config);
		dbProps.store(fos, "DB Properties");
		fos.close();

		// initialize metadata file
		new File(dbDirectory + "data").mkdirs();
		this.metadata = new File(dbDirectory + "data/" + "/metadata.csv");
		if (this.metadata.createNewFile()) {
			PrintWriter out = new PrintWriter(this.metadata);
			out.println("Table Name, Column Name, Column Type, Key, Indexed");
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

}
