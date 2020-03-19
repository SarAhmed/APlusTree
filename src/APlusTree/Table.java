package APlusTree;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;

import BPTree.BPTree;
import BPTree.Ref;

public class Table implements Serializable {
	private static final long serialVersionUID = 1L;

	private int MaximumRowsCountinPage, curIdx, NodeSize;
	private String tableName, directory, tableHeader, clusteringKey;
	private Hashtable<String, String> columnTypes;
	private Hashtable<String,BPTree> colNameIndex;

	private Vector<String> pagesDirectory;

	public Table(String path, String strTableName, Hashtable<String, String> htblColNameType, String strKeyColName,

			int MaximumRowsCountinPage, int nodeSize) throws IOException {
		this.directory = path + "data/";
		this.curIdx = -1;
		this.columnTypes = htblColNameType;
		this.NodeSize = nodeSize;
		this.tableName = strTableName;
		this.clusteringKey = strKeyColName;
		this.pagesDirectory = new Vector<String>();
		this.MaximumRowsCountinPage = MaximumRowsCountinPage;
		this.colNameIndex = new Hashtable<String,BPTree>();
		createDirectory();
		initializeColumnsHeader();
		save();
	}

	private void initializeColumnsHeader() throws IOException {
		tableHeader = "";
		ArrayList<String[]> colInfo = getColInfo();
		for (int i = 0; i < colInfo.size(); i++) {
			tableHeader += colInfo.get(i)[1] + ", ";
		}

		tableHeader += "Touch Date";

	}

	private void createDirectory() {
		File tableDir = new File(directory);
		tableDir.mkdir();
	}

	private Page createPage() throws IOException {
		curIdx++;
		Page page = new Page(MaximumRowsCountinPage, directory + tableName + "_" + curIdx + ".class");
		pagesDirectory.add(directory + tableName + "_" + curIdx + ".class");
		save();
		return page;

	}

	private void save() throws IOException {
		File file = new File(directory + tableName + ".class");
		if (file.exists())
			file.delete();
		file.createNewFile();
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(this);
		stream.close();
	}

	private int[] search(Hashtable<String, Object> htblColNameValue) throws Exception {
		if (checkValidInput(htblColNameValue)) {
			String type = this.columnTypes.get(this.getClusteringKey()).trim();

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
						if (recordIdx == this.MaximumRowsCountinPage) {
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
							if (recordIdx == this.MaximumRowsCountinPage) {
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

	private int[] BinarySearch(Hashtable<String, Object> htblColNameValue) throws Exception {
		if (checkValidInput(htblColNameValue)) {
			String type = this.columnTypes.get(this.getClusteringKey()).trim();

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
						if (recordIdx == this.MaximumRowsCountinPage) {
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
							if (recordIdx == this.MaximumRowsCountinPage) {
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
					pageIdx = i;
					recordIdx = BSVector(pageRecords, inputKey, clusteredIdx, type);

					break all;

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

	static int BSVector(Vector<Record> arr, Comparable val, int clusteredIdx, String type) {
		int lo = 0;
		int hi = arr.size();
		while (hi - lo > 0) {
			int mid = hi + lo >> 1;
			Record currRecord = arr.get(mid);
			Comparable currKey = getComparable(currRecord.get(clusteredIdx), type);

			if (currKey.compareTo(val) >= 0) {
				hi = mid;
			} else {
				lo = mid + 1;
			}
		}
		return hi;
	}

	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

	public boolean insertIntoTable(Hashtable<String, Object> htblColNameValue) throws Exception {

		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns = new ArrayList();
		for (Entry<String, Object> e : htblColNameValue.entrySet()) {
			if (e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}

		for (String s : polygonColumns) {
			Polygon p = (Polygon) htblColNameValue.get(s);
			htblColNameValue.put(s, new DBPolygon(p));
		}

		// this is the end of making every polygon as DBPolygon
		int[] location = BinarySearch(htblColNameValue);
		if (location == null)
			throw new DBAppException("Invalid record format");
		int pageIdx = location[0];
		int recordIdx = location[1];
		Record r = new Record();
		ArrayList<String[]> metaInfo = this.getColInfo();
		for (int i = 0; i < metaInfo.size(); i++) {
			if (!htblColNameValue.containsKey(metaInfo.get(i)[1])) {
				throw new DBAppException("There is a missing Column or you entered a null value.");
			}
			Object value = htblColNameValue.get(metaInfo.get(i)[1]);
			// updated
			if (value != null && value instanceof Polygon) {
				value = new DBPolygon((Polygon) value);
			}
			r.add(value);
		}
		// TO DO replace this with htblColName.add("touch date",getDate()) at the
		// beginning if it's inserted in metadata
		r.add(getDate());

		int pageDirectorySize = pagesDirectory.size();
		boolean inserted = false;
		// add the record to all indices with reference to pageIdx directory
		boolean insertedToIndex=false;
		if(pageIdx<pagesDirectory.size()) {
		addRecordToIndex(r, new Ref(pagesDirectory.get(pageIdx)));
		insertedToIndex=true;
		}
		int firstPageIdx=pageIdx;
		for (int i = pageIdx; i < pageDirectorySize && !inserted; i++) {
			Page p = deserialize(pagesDirectory.get(i));
			
			if (!(inserted = p.add(r, recordIdx))) {
					
				Record removedRecord = p.remove(p.size() - 1);
				
				// this for tracing only replace it without if (only addRecord())
				if (!p.add(r, recordIdx)) {
					// TO DO remove this case
					throw new Exception("feh haga 3'lt");
				}
				if(i!=firstPageIdx) {
					updateRecordRef(r, new Ref(pagesDirectory.get(i-1)), new Ref(pagesDirectory.get(i)));
				}
				r = removedRecord;
				recordIdx = 0;
				pageIdx++;

			}
		}

		if (!inserted) {
			Page newPage = createPage();
			newPage.add(r, recordIdx);
			inserted = true;
			if(!insertedToIndex) {
				addRecordToIndex(r, new Ref(pagesDirectory.get(pageIdx)));
			}else {
				updateRecordRef(r, new Ref(pagesDirectory.get(pagesDirectory.size()-2)), new Ref(pagesDirectory.get(pagesDirectory.size()-1)));
				
			}
		}
		save();
		if (!inserted)
			throw new Exception("enta matst72sh t3lem ");
		return inserted;
	}

	private void updateRecordRef(Record r,Ref oldRef,Ref newRef) {
		
		for(Entry e:colNameIndex.entrySet()) {
			String colName = (String)e.getKey();
			BPTree tree=(BPTree<Comparable<T>>)e.getValue();
			int colIdx=getColIdx(colName);
			Comparable key= (Comparable)r.get(colIdx);
			tree.updateRef(key, oldRef, newRef);
			
		}
	}
	
	private void addRecordToIndex(Record r,Ref ref) {
		
		for(Entry e:colNameIndex.entrySet()) {
			String colName = (String)e.getKey();
			BPTree tree=(BPTree<Comparable<T>>)e.getValue();
			int colIdx=getColIdx(colName);
			Comparable key= (Comparable)r.get(colIdx);
			tree.insert(key,ref );
			
		}
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
			c = (DBPolygon) value;

		} else if (type.equals("java.util.Date")) {
			c = (java.util.Date) value;
		} else if (type.equals("java.lang.Boolean")) {
			c = (java.lang.Boolean) value;

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

	@SuppressWarnings("deprecation")
	private Date strToDateParser(String s) {
		StringTokenizer st = new StringTokenizer(s, "- ");
		int year = Integer.parseInt(st.nextToken());
		int month = Integer.parseInt(st.nextToken());
		int day = Integer.parseInt(st.nextToken());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		Date dateRepresentation = cal.getTime();

		return dateRepresentation;
	}

	public ArrayList<String[]> getColInfo() throws IOException {

		/* Double dots to get back a dir to be able to acces meta data file */
		FileReader fileReader = new FileReader(directory + "../data/" + "/metadata.csv");

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

//	public boolean updateTable(String strClusteringKey, Hashtable<String, Object> htblColNameValue)
//			throws DBAppException, IOException {
//		// from here i will only make every Polygon as DBPolygon
//		ArrayList<String> polygonColumns = new ArrayList();
//		for (Entry<String, Object> e : htblColNameValue.entrySet()) {
//			if (e.getValue() instanceof Polygon) {
//				polygonColumns.add(e.getKey());
//			}
//		}
//
//		for (String s : polygonColumns) {
//			Polygon p = (Polygon) htblColNameValue.get(s);
//			htblColNameValue.put(s, new DBPolygon(p));
//		}
//
//		// this is the end of making every polygon as DBPolygon
//
//		if (!checkValidInput(htblColNameValue)) {
//			throw new DBAppException("Invalid Input Format");
//		}
//
//		int keyIdx = this.getColIdx(getClusteringKey());
//		ArrayList<String[]> tableInfo = this.getColInfo();
//		Comparable searchKey = null;
//		String type = "";
//		for (int i = 0; i < tableInfo.size(); i++) {
//			if (tableInfo.get(i)[3].trim().equals("True")) {
//				type = tableInfo.get(i)[2].trim();
//				if (type.equals("java.lang.Integer")) {
//					searchKey = Integer.parseInt(strClusteringKey);
//				} else if (type.equals("java.lang.String")) {
//					searchKey = strClusteringKey;
//				} else if (type.equals("java.lang.Double")) {
//					searchKey = Double.parseDouble(strClusteringKey);
//				} else if (type.equals("java.awt.Polygon")) {
//					searchKey = new DBPolygon(strClusteringKey);
//				} else if (type.equals("java.util.Date")) {
//					searchKey = strToDateParser(strClusteringKey);
//				} else if (type.equals("java.lang.Boolean")) {
//					searchKey = Boolean.parseBoolean(strClusteringKey);
//
//				}
//				break;
//			}
//		}
//
//		boolean stop = false;
//		for (int i = 0; i < pagesDirectory.size() && !stop; i++) {
//			Page p = deserialize(pagesDirectory.get(i));
//			boolean updated = false;
//			for (int k = 0; k < p.size(); k++) {
//				Record currRecord = p.get(k);
//
//				Comparable currKey = getComparable(currRecord.get(keyIdx), type);
//				if (currKey.compareTo(searchKey) == 0) {
//					for (Entry<String, Object> entry : htblColNameValue.entrySet()) {
//						String colName = entry.getKey();
//						Object value = entry.getValue();
//						int colIdx = getColIdx(colName);
//
//						currRecord.update(colIdx, value);
//						updated = true;
//					}
//					// TO DO if touchDate is in MetaData => add to htblColNames.add(String
//					// touchDate,getDate) at the begning of the method
//					if (updated) {
//						currRecord.update(currRecord.getValues().size() - 1, getDate());
//					}
//
//				} else if (currKey.compareTo(searchKey) > 0) {
//					stop = true;
//					break;
//				}
//			}
//			if (updated) {
//				p.save();
//			}
//		}
//
//		return true;
//	}

	
public boolean updateTableBS(String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException, IOException {
		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns = new ArrayList();
		for (Entry<String, Object> e : htblColNameValue.entrySet()) {
			if (e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}

		for (String s : polygonColumns) {
			Polygon p = (Polygon) htblColNameValue.get(s);
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
				} else if (type.equals("java.awt.Polygon")) {
					searchKey = new DBPolygon(strClusteringKey);
				} else if (type.equals("java.util.Date")) {
					searchKey = strToDateParser(strClusteringKey);
				} else if (type.equals("java.lang.Boolean")) {
					searchKey = Boolean.parseBoolean(strClusteringKey);

				}
				break;
			}
		}

		boolean stop = false;
		boolean binaryUsed = false;
		for (int i = 0; i < pagesDirectory.size() && !stop; i++) {
			Page p = deserialize(pagesDirectory.get(i));
			Record lastRecord = p.get(p.size() - 1);
			Comparable lastRecordKey = getComparable(lastRecord.get(keyIdx), type);

			if (lastRecordKey.compareTo(searchKey) < 0)
				continue;

			Vector<Record> pageRecords = p.getRecords();
			int updateIdx = 0;
			if (!binaryUsed) {
				updateIdx = BSVector(pageRecords, searchKey, keyIdx, type);
				binaryUsed = true;
			}

			boolean updated = false;

			// let pageidx 2li
			// let BSidx
			for (int k = updateIdx; k < p.size(); k++) {
				Record currRecord = p.get(k);

				Comparable currKey = getComparable(currRecord.get(keyIdx), type);
				if (currKey.compareTo(searchKey) == 0) {

					for (Entry<String, Object> entry : htblColNameValue.entrySet()) {
						String colName = entry.getKey();
						// after adding index
						Comparable value =(Comparable) entry.getValue();
						int colIdx = getColIdx(colName);
						if(hasIndex(colName)) {
							BPTree bTree=colNameIndex.get(colName);
							Comparable oldValue=(Comparable)currRecord.get(colIdx);
							bTree.update(oldValue, new Ref(pagesDirectory.get(i)), value);
						}

						currRecord.update(colIdx, value);
						updated = true;
					}
					// TO DO if touchDate is in MetaData => add to htblColNames.add(String
					// touchDate , getDate ) at the begning of the method
					if (updated) {
						currRecord.update(currRecord.getValues().size() - 1, getDate());
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
		
		ArrayList<String> polygonColumns = new ArrayList();
		for (Entry<String, Object> e : htblColNameValue.entrySet()) {
			if (e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}

		for (String s : polygonColumns) {
			Polygon p = (Polygon) htblColNameValue.get(s);
			htblColNameValue.put(s, new DBPolygon(p));
		}

		String clusteringKey = getClusteringKey();
		
		if (!checkValidInput(htblColNameValue)) {
			throw new Exception("invalid data types");
		}

		if (!htblColNameValue.containsKey(clusteringKey)) {
			// from here i will only make every Polygon as DBPolygon
						// this is the end of making every polygon as DBPolygon

		
			for (int i = 0; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				for (int k = 0; k < p.size(); k++) {
					Record currRecord = p.get(k);
					if (matchRecord(currRecord, htblColNameValue)) {
						// System.out.println("there is a match");
						p.remove(k--);
					}
				}
				if (p.size() == 0) {
					File f = new File(pagesDirectory.get(i));
					if (!f.delete()) {
						throw new Exception("there is an error while deleting");
					}
					pagesDirectory.remove(i--);

				}
			}
		}else {
			System.out.println("******************Da5alt 2l else part***********************");
			int clusteredIdx=getColIdx(clusteringKey);
			String type = this.columnTypes.get(this.getClusteringKey()).trim(); 
			Comparable searchKey=getComparable(htblColNameValue.get(this.getClusteringKey()), type);
			boolean binaryUsed=false;
			for(int i=0;i<pagesDirectory.size();i++) {
				Page p=deserialize(pagesDirectory.get(i));
				Record lastRecord=p.get(p.size()-1);
				Comparable lastRecordKey=getComparable(lastRecord.get(clusteredIdx), type);
				
				if (lastRecordKey.compareTo(searchKey) < 0)
					continue;
				
				Vector<Record> pageRecords= p.getRecords();
				int deleteIdx = 0;
				
				if (!binaryUsed) {
					deleteIdx = BSVector(pageRecords, searchKey, clusteredIdx, type);
					binaryUsed = true;
				}
				
				for (int k = deleteIdx; k < p.size(); k++) {
					Record currRecord = p.get(k);
					if (matchRecord(currRecord, htblColNameValue)) {
						// System.out.println("there is a match");
						p.remove(k--);
					}
				}
				if (p.size() == 0) {
					File f = new File(pagesDirectory.get(i));
					if (!f.delete()) {
						throw new Exception("there is an error while deleting");
					}
					pagesDirectory.remove(i--);

				}

				
				
				
				
			}
			
		}

		this.save();
	}

	public boolean matchRecord(Record record, Hashtable<String, Object> htblColNameValue) {
		String[] header = tableHeader.split(", ");
		for (int i = 0; i < header.length - 1; i++) {
			if (htblColNameValue.containsKey(header[i].trim())) {
				Vector<Object> r = record.getValues();

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
			if (!this.columnTypes.containsKey(colName)) {
				// System.out.println("the column name doesn't exist");
				return false;
			}
			/* Check for valid column type */
			if (!checkType(value, this.columnTypes.get(colName))) {
				// System.out.println("the col type is invalid");
				return false;
			}

		}
		return true;
	}

	private boolean checkType(Object value, String type) {
		String actualType = (value.getClass().toString().split(" "))[1].trim();

		if (actualType.equals("APlusTree.DBPolygon") && type.equals("java.awt.Polygon")) {
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
			String tmpS = pagesDirectory.get(i);
			Page tmp = deserialize(tmpS);
			r += " page number " + i + "\n " + tmp.toString();

		}
		return r;
	}
	/**
	 * create index on specified column name by creating BPTree on that column and inserting in it.
	 * @param strColName The name of the column which index is created on
	 * @throws DBEngineException If columns, foreign keys or the primary key are not valid
	 * @throws FileNotFoundException If an error occurred in the stored table file
	 * @throws IOException If an I/O error occurred
	 * @throws ClassNotFoundException If an error occurred in the stored table pages format
	 */
	// TO DO update metadata
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createIndex(String strColName)  throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException {
		 	String type = columnTypes.get(strColName);
		 	int colPos = this.getColIdx(strColName);
		 	BPTree tree = null;
		 System.out.println(type);
		 	if (type.equals("java.lang.Integer")) {
		 		System.out.println("goua 2l if");
		 		tree = new BPTree<Integer>(NodeSize);
		 		System.out.println(tree.toString());
			} else if (type.equals("java.lang.String")) {
		 		tree = new BPTree<String>(NodeSize);
			} else if (type.equals("java.lang.Double")) {
		 		tree = new BPTree<Double>(NodeSize);
			} else if (type.equals("java.awt.Polygon")) {
		 		tree = new BPTree<DBPolygon>(NodeSize);
			} else if (type.equals("java.util.Date")) {
		 		tree = new BPTree<Date>(NodeSize);
			} else if (type.equals("java.lang.Boolean")) {
		 		tree = new BPTree<Boolean>(NodeSize);

			}
		 	colNameIndex.put(strColName, tree);	 	
		 	
			ObjectInputStream ois;
		 	for (int pageIdx = 0; pageIdx < pagesDirectory.size(); pageIdx++) {
//				File f = new File(directory + tableName + "_" + index+".class");
//				if(f.exists())
//		    	ois = new ObjectInputStream(new FileInputStream(f));
		    	Page p = deserialize(pagesDirectory.get(pageIdx));
				for(int i = 0; i < p.size(); ++i)
				{
					Record r = p.get(i);
					Ref recordReference = new Ref(pagesDirectory.get(pageIdx));
					tree.insert((Comparable) r.get(colPos), recordReference);
				}
				
				//	ois.close();
		 	}
		 	System.out.println("-----------------------------------------------");
			System.out.println(tree.toString());
		 	this.save();
	  }

	private static HashSet<Record> and(HashSet<Record> a,HashSet<Record> b){
		HashSet<Record> ans=new HashSet<Record>();
		for(Record r:a) {
			if(b.contains(r))ans.add(r);
		}
		for(Record r:b) {
			if(a.contains(r))ans.add(r);
		}
		return ans;
		
	}

	private static HashSet<Record> or(HashSet<Record> a,HashSet<Record> b){
		
		for(Record r:b) {
			if(!a.contains(r))a.add(r);
		}
		return a;
		
	}private static HashSet<Record> xor(HashSet<Record> a,HashSet<Record> b){
		HashSet<Record> ans=new HashSet<Record>();
		for(Record r:a) {
			if(!b.contains(r))ans.add(r);
		}
		for(Record r:b) {
			if(!a.contains(r))ans.add(r);
		}
		return ans;
		
	}
	
	private boolean hasIndex(String colName) {
		return colNameIndex.contains(colName);
	}
	
	private HashSet<Record> getRecordsFromRef(Vector<Ref> refs){
		HashSet<Record> ans=new HashSet<Record>();
		for(Ref r:refs) {
		String pageDirectoryString=r.getPageDirectory();
		Page p= deserialize(pageDirectoryString);
		p.get
		}
		
	}
	public <T extends Comparable<T>>HashSet<Record> selectEqual(String colName,T val) throws IOException{
		HashSet<Record> ans=new HashSet<Record>();
		int colIndex=getColIdx(colName);
		if(hasIndex(colName)) {
			BPTree tree=colNameIndex.get(colName);
			tree.search(val);
		}else {
			
		}
		
	}
}
