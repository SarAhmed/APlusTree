package Table_CRUDs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import DB_Exceptions.DBAppException;
import DB_Exceptions.DBEngineException;

public class Table implements Serializable {
	private int maxTuplesPerPage, indexOrder, curPageIndex, numOfColumns;
	private String path, tableName, tableHeader, primaryKey;
	private Hashtable<String, String> colTypes;
	private Vector<String> pagesDirectory;

	public Table(String path, String strTableName, Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName, int maxTuplesPerPage, int indexOrder)
			throws IOException, ClassNotFoundException, DBEngineException {

		this.path = path + strTableName + "/";
		this.tableName = strTableName;
		this.primaryKey = strKeyColName;
		this.colTypes = htblColNameType;
		this.maxTuplesPerPage = maxTuplesPerPage;
		this.curPageIndex = -1;
		this.numOfColumns = 0;
		this.indexOrder = indexOrder;
		this.pagesDirectory = new Vector<String>();
		createDirectory();
		createPage();
		saveTable();
	}

	private void createDirectory() {
		File tableDir = new File(path);
		tableDir.mkdir();
	}

	private Page createPage() throws IOException {
		curPageIndex++;
		Page p = new Page(maxTuplesPerPage, path + tableName + "_" + curPageIndex + ".class");
		pagesDirectory.add(path + tableName + "_" + curPageIndex + ".class");
		saveTable();
		return p;

	}

	// this is for splitting page
	@SuppressWarnings("unused")
	private Page createSplittedPage(int directoryPosition) throws IOException {
		curPageIndex++;
		Page p = new Page(maxTuplesPerPage, path + tableName + "_" + curPageIndex + ".class");
		pagesDirectory.insertElementAt(path + tableName + "_" + curPageIndex + ".class", directoryPosition);
		saveTable();
		return p;

	}

	private void saveTable() throws IOException {
		File f = new File(path + tableName + ".class");
		if (f.exists())
			f.delete();
		f.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(this);
		oos.close();
	}



}
