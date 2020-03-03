package APlusTree;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;


public class Table implements Serializable {
	private static final long serialVersionUID = 1L;

	private int maxTuplesPerPage, curPageIndex, numOfColumns,NodeSize;
	private String path, tableName, tableHeader, clusteringKey;
	private Hashtable<String, String> colTypes;
	private Vector<String> pagesDirectory;

	public Table(String path, String strTableName, Hashtable<String, String> htblColNameType, String strKeyColName,
			int maxTuplesPerPage,int nodeSize) throws IOException {
		this.NodeSize=nodeSize;
		this.path = path  + "data/";
		this.tableName = strTableName;
		this.clusteringKey = strKeyColName;
		this.colTypes = htblColNameType;
		this.maxTuplesPerPage = maxTuplesPerPage;
		this.curPageIndex = -1;
		this.numOfColumns = 0;
		this.pagesDirectory = new Vector<String>();
		createDirectory();
		// createPage();
		initializeColumnsHeader();
		saveTable();
	}

	private void initializeColumnsHeader() throws IOException {
		tableHeader = "";
		ArrayList<String[]> colInfo=getColInfo();
		for(int i=0;i<colInfo.size();i++) {
			tableHeader+=colInfo.get(i)[1]+", ";
		}

		tableHeader+="Touch Date";
		
//		for (Entry<String, String> entry : colTypes.entrySet()) {
//			numOfColumns++;
//			tableHeader += entry.getKey() + ", ";
//		}
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


	private void saveTable() throws IOException {
		File f = new File(path + tableName + ".class");
		if (f.exists())
			f.delete();
		f.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(this);
		oos.close();
	}

	private int[] search(Hashtable<String, Object> htblColNameValue) throws Exception {
		if (checkValidInput(htblColNameValue)) {
			String type = this.colTypes.get(this.getClusteringKey()).trim();

			Object value = htblColNameValue.get(this.getClusteringKey());
			String clusteringKey = getClusteringKey();

			Comparable inputKey = getComparable(value, type);
			// TO DO use DBException not Exception
			if (inputKey == null)
				throw new DBAppException(" the type of key is not comparable");

			int pageIdx = -1;
			int recordIdx = -1;
			int clusteredIdx = getColIdx(clusteringKey);
			all: for (int i = 0; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Record lastRecord = p.getRecords().get(p.size() - 1);

				Comparable recordKey = getComparable(lastRecord.get(clusteredIdx), type);

				if (recordKey.compareTo(inputKey) < 0) {
					// 2 cases

					// A. you don't have next page

					if (i == pagesDirectory.size() - 1) {

						pageIdx = i;
						recordIdx = p.size();
						// if the last page is full i will create a new one
						if (recordIdx == this.maxTuplesPerPage) {
							pageIdx++;
							recordIdx = 0;
						}

						break all;

					}
					// B. next page start element is greater than me
					else {
						Page nxtPage = deserialize(pagesDirectory.get(i + 1));

						Record firstnxtPageRecord = nxtPage.getRecords().get(0);
						Comparable firstnxtPageRecordKey = getComparable(firstnxtPageRecord.get(clusteredIdx), type);

						if (firstnxtPageRecordKey.compareTo(inputKey) >= 0) {
							pageIdx = i;
							recordIdx = p.size();

							// if the page is full i will add it to the next one
							if (recordIdx == this.maxTuplesPerPage) {
								pageIdx++;
								recordIdx = 0;
							}

							break all;
						}

					}
				} else {
					// if the last record is greater than me so i will iterate the whole page to
					// find the location
					Vector<Record> pageRecords = p.getRecords();
					for (int k = 0; k < p.size(); k++) {
						Record currRecord = pageRecords.get(k);
						Comparable currKey = getComparable(currRecord.get(clusteredIdx), type);
						if (currKey.compareTo(inputKey) >= 0) {
							pageIdx = i;
							recordIdx = k;

							break all;
						}

					}
				}
			}

			if (pageIdx == -1) {
				pageIdx = 0;
				recordIdx = 0;
			}
			int[] result = new int[2];
			result[0] = pageIdx;
			result[1] = recordIdx;
			return result;
		}
		// TO DO
		 throw new DBAppException("invalid input format");
	}
	
	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	    Date date = new Date();  
	    return formatter.format(date);
	}

	public boolean insertIntoTable(Hashtable<String, Object> htblColNameValue) throws Exception {
		
		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns=new ArrayList();
		for(Entry<String,Object> e:htblColNameValue.entrySet()) {
			if(e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}
		
		for( String s:polygonColumns) {
			Polygon p=(Polygon)htblColNameValue.get(s);
			htblColNameValue.put(s, new DBPolygon(p));
		}
		
		// this is the end of making every polygon as DBPolygon
		int[] location = search(htblColNameValue);
		if (location == null)
			throw new DBAppException("Invalid record format");
		int pageIdx = location[0];
		int recordIdx = location[1];
		Record r = new Record();
		ArrayList<String[]> metaInfo = this.getColInfo();
		for (int i = 0; i < metaInfo.size(); i++) {
			Object value = htblColNameValue.getOrDefault(metaInfo.get(i)[1], null);
			//updated
			if(value!=null && value instanceof Polygon ) {
				value=new DBPolygon((Polygon)value);
			}
			r.addValue(value);
		}
		// TO DO replace this with htblColName.add("touch date",getDate()) at the beginning if it's inserted in metadata
			r.addValue(getDate());
		
		
		int pageDirectorySize = pagesDirectory.size();
		boolean inserted = false;

		for (int i = pageIdx; i < pageDirectorySize && !inserted; i++) {
			Page p = deserialize(pagesDirectory.get(i));

			if (!(inserted = p.addRecord(r, recordIdx))) {

				Record removedRecord = p.removeRecord(p.size() - 1);

				// this for tracing only replace it without if (only addRecord())
				if (!p.addRecord(r, recordIdx)) {
					// TO DO remove this case
					throw new Exception("feh haga 3'lt");
				}

				r = removedRecord;
				recordIdx = 0;
				pageIdx++;

			}
		}

		if (!inserted) {
			Page newPage = createPage();
			newPage.addRecord(r, recordIdx);
			inserted = true;
		}
		saveTable();
		if (!inserted)
			throw new Exception("enta matst72sh t3lem ");
		return inserted;
	}

	private static Comparable getComparable(Object value, String type) {
		Comparable c = null;
		if (type.equals("java.lang.Integer")) {
			c = (Integer) value;
		} else if (type.equals("java.lang.String")) {
			c = (String) value;
		} else if (type.equals("java.lang.Double")) {
			c = (Double) value;
		} else if (type.equals("java.awt.Polygon")) {
			c=(DBPolygon)value;
		
		}else if(type.equals("java.util.Date")) {
			c=(java.util.Date)value;
		}
		return c;

	}

	public int getColIdx(String colName) throws IOException {
		ArrayList<String[]> colInfo = this.getColInfo();
		for (int i = 0; i < colInfo.size(); i++) {
			if (colInfo.get(i)[1].equals(colName))
				return i;
		}
		return -1;
	}
	private Date strToDateParser(String s) {
		StringTokenizer st= new StringTokenizer(s,"- ");
		int year=Integer.parseInt(st.nextToken());
		int month=Integer.parseInt(st.nextToken());
		int day=Integer.parseInt(st.nextToken());
		return new java.util.Date(year,month, day);
	}

	public ArrayList<String[]> getColInfo() throws IOException {

		/* Double dots to get back a dir to be able to acces meta data file */
		FileReader fileReader = new FileReader(path + "../data/" + "/metadata.csv");

		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		ArrayList<String[]> col = new ArrayList<>();
		bufferedReader.readLine(); // To discard first line [Table Name, Column Name, Column Type, Key, Indexed]
		while ((line = bufferedReader.readLine()) != null) {
			String[] metaFile = line.split(", ");
			if (metaFile[0].equals(this.tableName)) {
				col.add(metaFile);
			}

		}
		bufferedReader.close();
		return col;

	}

	public boolean updateTable(String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException, IOException {
		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns=new ArrayList();
		for(Entry<String,Object> e:htblColNameValue.entrySet()) {
			if(e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}
		
		for( String s:polygonColumns) {
			Polygon p=(Polygon)htblColNameValue.get(s);
			htblColNameValue.put(s, new DBPolygon(p));
		}
		
		// this is the end of making every polygon as DBPolygon
	
		
		if (!checkValidInput(htblColNameValue)) {
			throw new DBAppException("Invalid Input Format");
		}

		int keyIdx = this.getColIdx(getClusteringKey());
		ArrayList<String[]> tableInfo = this.getColInfo();
		Comparable searchKey = null;
		String type = "";
		for (int i = 0; i < tableInfo.size(); i++) {
			if (tableInfo.get(i)[3].trim().equals("True")) {
				type = tableInfo.get(i)[2].trim();
				if (type.equals("java.lang.Integer")) {
					searchKey = Integer.parseInt(strClusteringKey);
				} else if (type.equals("java.lang.String")) {
					searchKey = strClusteringKey;
				} else if (type.equals("java.lang.Double")) {
					searchKey = Double.parseDouble(strClusteringKey);
				}else if(type.equals("java.awt.Polygon")) {
					searchKey=new DBPolygon(strClusteringKey);
				}else if (type.equals("java.util.Date")) {
					searchKey=strToDateParser(strClusteringKey);
				}
				break;
			}
		}

		boolean stop = false;
		for (int i = 0; i < pagesDirectory.size() && !stop; i++) {
			Page p = deserialize(pagesDirectory.get(i));
			boolean updated = false;
			for (int k = 0; k < p.size(); k++) {
				Record currRecord = p.get(k);

				Comparable currKey = getComparable(currRecord.get(keyIdx), type);
				if (currKey.compareTo(searchKey) == 0) {
					for (Entry<String, Object> entry : htblColNameValue.entrySet()) {
						String colName = entry.getKey();
						Object value = entry.getValue();
						int colIdx = getColIdx(colName);

						currRecord.updateValue(colIdx, value);
						updated = true;
					}
					//TO DO if touchDate is in MetaData => add to htblColNames.add(String touchDate,getDate) at the begning of the method
					if(updated) {
						currRecord.updateValue(currRecord.getValues().size()-1, getDate());
					}
					
				} else if (currKey.compareTo(searchKey) > 0) {
					stop = true;
					break;
				}
			}
			if (updated) {
				p.save();
			}
		}

		return true;
	}

	public void deleteFromTable(Hashtable<String, Object> htblColNameValue) throws Exception {
		getColInfo();
		
		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns=new ArrayList();
		for(Entry<String,Object> e:htblColNameValue.entrySet()) {
			if(e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}
		
		for( String s:polygonColumns) {
			Polygon p=(Polygon)htblColNameValue.get(s);
			htblColNameValue.put(s, new DBPolygon(p));
		}
		
		// this is the end of making every polygon as DBPolygon
	
		
		if(!checkValidInput(htblColNameValue)) {
			throw new Exception("invalid data types");
		}
		
		for(int i=0;i<pagesDirectory.size();i++) {
			Page p =deserialize(pagesDirectory.get(i));
			for(int k=0;k<p.size();k++) {
				Record currRecord=p.get(k);
				if(matchRecord(currRecord, htblColNameValue)) {
					//System.out.println("there is a match");
					p.removeRecord(k--);
				}
			}
			if(p.size()==0) {
				File f= new File(pagesDirectory.get(i));
				if(!f.delete()) {
					throw new Exception("there is an error while deleting");
				}
				pagesDirectory.remove(i--);
				
			}
		}
		
		this.saveTable();
	}

	public boolean matchRecord(Record record, Hashtable<String, Object> htblColNameValue) {
		String[] header = tableHeader.split(", ");
		for (int i = 0; i < header.length-1; i++) {
			if (htblColNameValue.containsKey(header[i].trim())) {
				Vector<Object> r = record.getValues();
				// put in mind whether it compares using refrence or value
//				System.out.println("header : "+header[i].trim());
//				System.out.println("header value : "+htblColNameValue.get(header[i].trim()));
//				System.out.println("record value : "+r.get(i));
//				if(i==0) {
//					System.out.println("i m here");
//					
//				}
//				System.out.println(htblColNameValue.get(header[i].trim()).equals(r.get(i)));
				if (!htblColNameValue.get(header[i].trim()).equals(r.get(i)))
					return false;
			}
		}
		return true;

	}

	public Page deserialize(String dir) {
		Page p = null;
		try {
			FileInputStream file = new FileInputStream(dir);
			ObjectInputStream in = new ObjectInputStream(file);

			p = (Page) in.readObject();

			in.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
			ex.printStackTrace();
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
		return p;
	}

	public boolean checkValidInput(Hashtable<String, Object> htblColNameValue) {
		for (Entry<String, Object> entry : htblColNameValue.entrySet()) {
			String colName = entry.getKey();
			Object value = entry.getValue();
			/* Check that a column with this name already exists */
			if (!this.colTypes.containsKey(colName))
				{
			//	System.out.println("the column name doesn't exist");
				return false;
				}
			/* Check for valid column type */
			if (!checkType(value, this.colTypes.get(colName)))
				{
				//System.out.println("the col type is invalid");
				return false;
				}

		}
		return true;
	}

	private boolean checkType(Object value, String type) {
		String actualType = (value.getClass().toString().split(" "))[1].trim();
		//System.out.println("actual type is "+actualType);
		
		if(actualType.equals("APlusTree.DBPolygon")&&type.equals("java.awt.Polygon")) {
			return true;
		}
		return actualType.equals(type);

	}

	public String getClusteringKey() {
		return this.clusteringKey;
	}

	public String toString() {
		String r = "";
		r += tableHeader;
		for (int i = 0; i < pagesDirectory.size(); i++) {
			String tmpS=pagesDirectory.get(i);
			Page tmp =deserialize(tmpS);
			r += " page number " + i + "\n " + tmp.toString();

		}
		return r;
	}
}
