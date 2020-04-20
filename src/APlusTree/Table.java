package APlusTree;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
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
	private Hashtable<String, BPTree> colNameIndex;
	private Vector<String> pagesDirectory;

	public void dispalyIndex(String colName) {
		System.out.println(colNameIndex.get(colName));
	}

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
		this.colNameIndex = new Hashtable<String, BPTree>();
		createDirectory();
		initializeColumnsHeader();
		save();
	}

	public String getType(String colName) {
		return columnTypes.get(colName);
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

	@SuppressWarnings("unchecked")
	private HashSet<Record> linearSearch(Comparable val, String colName, String Op) throws IOException, DBAppException {
		String type = this.columnTypes.get(colName).trim();
		HashSet<Record> ans = new HashSet<Record>();
		int colIdx = getColIdx(colName);
		for (int i = 0; i < pagesDirectory.size(); i++) {
			Page p = deserialize(pagesDirectory.get(i));
			Vector<Record> pageRecords = p.getRecords();
			for (int j = 0; j < pageRecords.size(); j++) {
				Record currRecord = pageRecords.get(j);
				Comparable currKey = getComparable(currRecord.get(colIdx), type);
				switch (Op) {
				case "=":
					// if (currKey.compareTo(val) == 0)
					// ans.add(currRecord);
					if (currKey.equals(val))
						ans.add(currRecord);
					break;
				case "!=":
					// if (currKey.compareTo(val) != 0)
					// ans.add(currRecord);
					if (!currKey.equals(val))
						ans.add(currRecord);
					break;
				case ">":
					if (currKey.compareTo(val) > 0)
						ans.add(currRecord);
					break;
				case ">=":
					if (currKey.compareTo(val) >= 0)
						ans.add(currRecord);
					break;
				case "<":
					if (currKey.compareTo(val) < 0)
						ans.add(currRecord);
					break;
				case "<=":
					if (currKey.compareTo(val) <= 0)
						ans.add(currRecord);
					break;
				default: throw new DBAppException("Invalid operator");

				}

			}
		}
		return ans;
	}

	private int[] BinarySearch(Hashtable<String, Object> htblColNameValue) throws Exception {
		if (checkValidInput(htblColNameValue)) {
			String type = this.columnTypes.get(this.getClusteringKey()).trim();

			Object value = htblColNameValue.get(this.getClusteringKey());
			String clusteringKey = getClusteringKey();

			Comparable inputKey = getComparable(value, type);
			if (inputKey == null)
				throw new DBAppException(" the type of key is not comparable");

			if (hasIndex(getClusteringKey())) {
				return searchPosUsingIdx(inputKey);
			}

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

	private int[] searchPosUsingIdx(Comparable key) throws IOException {
		BPTree tree = colNameIndex.get(this.getClusteringKey());
		Vector<Ref> ref = tree.searchGreaterThan(key);
		int pageIdx = -1, recordIdx = -1;
		if (ref == null || ref.size() == 0) {
			if (pagesDirectory.size() == 0) {
				pageIdx = 0;
				recordIdx = 0;
			} else {
				Page p = deserialize(pagesDirectory.get(pagesDirectory.size() - 1));
				if (p.isFull()) {
					pageIdx = pagesDirectory.size();
					recordIdx = 0;
				} else {
					pageIdx = pagesDirectory.size() - 1;
					recordIdx = p.size();
				}
			}
		} else {
			String minDirectory = ref.get(0).getPageDirectory();
			for (Ref r : ref) {
				if (r.getPageDirectory().compareTo(minDirectory) < 0) {
					minDirectory = r.getPageDirectory();
				}
			}
			Page p = deserialize(minDirectory);
			Vector<Record> records = p.getRecords();
			int clusteredIdx = getColIdx(getClusteringKey());
			String type = columnTypes.get(getClusteringKey());

			pageIdx = pagesDirectory.indexOf(minDirectory);
			recordIdx = BSVector(records, key, clusteredIdx, type);
		}
		int[] result = new int[2];
		result[0] = pageIdx;
		result[1] = recordIdx;
		return result;

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
		Record r = new Record(this.tableName);
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

		r.add(getDate());

		int pageDirectorySize = pagesDirectory.size();
		boolean inserted = false;
		/*
		 * insert in all available indexing data structures on that table
		 */
		boolean insertedToIndex = false;
		if (pageIdx < pagesDirectory.size()) {
			addRecordToIndex(r, new Ref(pagesDirectory.get(pageIdx), r.getId()));
			insertedToIndex = true;
		}
		int firstPageIdx = pageIdx;
		for (int i = pageIdx; i < pageDirectorySize && !inserted; i++) {
			Page p = deserialize(pagesDirectory.get(i));

			/*
			 * if i = first page then we already have inserted the corresponding keys in the
			 * indexing data structures otherwise we are just updating the ref of existing
			 * records(moves from page i to page i+1)
			 */

			if (i != firstPageIdx) {
				updateRecordRef(r, new Ref(pagesDirectory.get(i - 1), r.getId()),
						new Ref(pagesDirectory.get(i), r.getId()));
			}

			if (!(inserted = p.add(r, recordIdx))) {

				Record removedRecord = p.remove(p.size() - 1);
				// System.out.println("Inserting at index :" +recordIdx);
				// this for tracing only replace it without if (only addRecord())
				if (!p.add(r, recordIdx)) {
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
			newPage.add(r, recordIdx);
			inserted = true;
			if (!insertedToIndex) {
				addRecordToIndex(r, new Ref(pagesDirectory.get(pageIdx), r.getId()));
			} else {
				updateRecordRef(r, new Ref(pagesDirectory.get(pagesDirectory.size() - 2), r.getId()),
						new Ref(pagesDirectory.get(pagesDirectory.size() - 1), r.getId()));

			}
		}
		save();
		if (!inserted)
			throw new Exception("enta matst72sh t3lem ");
		return inserted;
	}

	private void updateRecordRef(Record r, Ref oldRef, Ref newRef) throws IOException {

		for (Entry e : colNameIndex.entrySet()) {
			String colName = (String) e.getKey();
			BPTree tree = (BPTree) e.getValue();
			int colIdx = getColIdx(colName);
			Comparable key = (Comparable) r.get(colIdx);
			tree.updateRef(key, oldRef, newRef);

		}
	}

	private void addRecordToIndex(Record r, Ref ref) throws IOException {

		for (Entry e : colNameIndex.entrySet()) {
			String colName = (String) e.getKey();
			BPTree tree = (BPTree) e.getValue();
			int colIdx = getColIdx(colName);
			Comparable key = (Comparable) r.get(colIdx);
			tree.insert(key, ref);

		}
	}

	private void deleteRecordFromIndex(Record r, Ref ref) throws IOException {

		for (Entry e : colNameIndex.entrySet()) {
			String colName = (String) e.getKey();
			BPTree tree = (BPTree) e.getValue();
			int colIdx = getColIdx(colName);
			Comparable key = (Comparable) r.get(colIdx);
			tree.delete(key, ref);

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
		// cal.set(Calendar.YEAR, year);
		// cal.set(Calendar.MONTH, month - 1);
		// cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(year, month - 1, day, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

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

	public boolean updateTableBS(String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException, IOException {
		// from here i will only make every Polygon as DBPolygon
		ArrayList<String> polygonColumns = new ArrayList();
		for (Entry<String, Object> e : htblColNameValue.entrySet()) {
			if (e.getValue() instanceof Polygon) {
				polygonColumns.add(e.getKey());
			}
		}

		if (htblColNameValue.containsKey(getClusteringKey()))
			throw new DBAppException("You can NOT update the ClusteringKey");

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
				// System.out.println(tableInfo.get(i)[3].trim()+"&&&&&&&&&&&&");
				// System.out.println("da5lt goua 2ul if");
				type = tableInfo.get(i)[2].trim();
				// System.out.println(type);
				if (type.equals("java.lang.Integer")) {
					searchKey = Integer.parseInt(strClusteringKey);
				} else if (type.equals("java.lang.String")) {
					searchKey = strClusteringKey;
				} else if (type.equals("java.lang.Double")) {
					searchKey = Double.parseDouble(strClusteringKey);
				} else if (type.equals("java.awt.Polygon")) {
					// System.out.println("I am in else");
					searchKey = new DBPolygon(strClusteringKey);
					// System.out.println("search key is null?"+searchKey==null);
				} else if (type.equals("java.util.Date")) {
					searchKey = strToDateParser(strClusteringKey);
				} else if (type.equals("java.lang.Boolean")) {
					searchKey = Boolean.parseBoolean(strClusteringKey);

				}
				break;
			}
		}
		int i = 0;
		if (hasIndex(this.getClusteringKey())) {
			BPTree tree = colNameIndex.get(getClusteringKey());
			Vector<Ref> ref = tree.search(searchKey);
			// pagesDirectory.indexOf(ref.get(0).getPageDirectory());
			if (ref == null || ref.size() == 0)
				return true;
			String minDir = ref.get(0).getPageDirectory();
			for (Ref r : ref) {
				String dir = r.getPageDirectory();
				if ((dir).compareTo(minDir) < 0)
					minDir = dir;
			}
			i = pagesDirectory.indexOf(minDir);
			System.out.println("Using index");
		}

		boolean stop = false;
		boolean binaryUsed = false;
		for (; i < pagesDirectory.size() && !stop; i++) {
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

			for (int k = updateIdx; k < p.size(); k++) {
				Record currRecord = p.get(k);

				Comparable currKey = getComparable(currRecord.get(keyIdx), type);
				if (currKey.equals(searchKey)) {
					for (Entry<String, Object> entry : htblColNameValue.entrySet()) {

						String colName = entry.getKey();
						// System.out.println(colName);
						// after adding index
						Comparable value = (Comparable) entry.getValue();
						int colIdx = getColIdx(colName);
						if (hasIndex(colName)) {
							BPTree bTree = colNameIndex.get(colName);
							Comparable oldValue = (Comparable) currRecord.get(colIdx);
							bTree.update(oldValue, new Ref(pagesDirectory.get(i), currRecord.getId()), value);
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
					System.out.println(searchKey);
					System.out.println(searchKey.compareTo(currKey));

					System.out.println("I stopped " + currKey);
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
			Vector<String> pages = null;
			for (Entry e : htblColNameValue.entrySet()) {
				String col = (String) e.getKey();
				if (hasIndex(col)) {
					BPTree tree = colNameIndex.get(col);
					String type = this.columnTypes.get(col).trim();
					Comparable searchKey = getComparable(htblColNameValue.get(col), type);
					HashSet<String> usedPages = new HashSet<String>();
					Vector<Ref> ref = tree.search(searchKey);
					if (ref == null)
						ref = new Vector<Ref>(); // changed 7-4
					if (ref.size() != 0)
						pages = new Vector<String>();
					for (Ref r : ref) {
						String p = r.getPageDirectory();
						if (!usedPages.contains(p)) {
							pages.add(p);
							usedPages.add(p);
						}
					}

					break;
				}
			}
			if (pages == null)
				pages = pagesDirectory;
			for (int i = 0; i < pages.size(); i++) {
				Page p = deserialize(pages.get(i));
				for (int k = 0; k < p.size(); k++) {
					Record currRecord = p.get(k);
					if (matchRecord(currRecord, htblColNameValue)) {
						// System.out.println("there is a match");
						deleteRecordFromIndex(currRecord, new Ref(pages.get(i), currRecord.getId()));
						p.remove(k--);
					}
				}
				if (p.size() == 0) {
					int idx = pagesDirectory.indexOf(pages.get(i));
					File f = new File(pagesDirectory.get(idx));
					if (!f.delete()) {
						throw new Exception("there is an error while deleting");
					}
					pagesDirectory.remove(idx);

				}
			}
		} else {
			int clusteredIdx = getColIdx(clusteringKey);
			String type = this.columnTypes.get(this.getClusteringKey()).trim();
			Comparable searchKey = getComparable(htblColNameValue.get(this.getClusteringKey()), type);
			String minDirectory = "";
			if (hasIndex(clusteringKey)) {
				BPTree tree = colNameIndex.get(clusteringKey);
				Vector<Ref> ref = tree.search(searchKey);
				if (ref != null) {
					minDirectory = ref.get(0).getPageDirectory();
					for (Ref r : ref) {
						if (r.getPageDirectory().compareTo(minDirectory) < 0) {
							minDirectory = r.getPageDirectory();
						}
					}

				}

			}
			boolean binaryUsed = false;
			int i = 0;
			if (!minDirectory.equals(""))
				i = pagesDirectory.indexOf(minDirectory);
			for (; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Record lastRecord = p.get(p.size() - 1);
				Comparable lastRecordKey = getComparable(lastRecord.get(clusteredIdx), type);

				Record firstRecord = p.get(0);
				Comparable firstRecordKey = getComparable(firstRecord.get(clusteredIdx), type);

				if (lastRecordKey.compareTo(searchKey) < 0)
					continue;

				if (firstRecordKey.compareTo(searchKey) > 0) {// updated 28/3
					System.out.println("break in delete big loop");
					break;
				}

				Vector<Record> pageRecords = p.getRecords();
				int deleteIdx = 0;

				if (!binaryUsed) {
					deleteIdx = BSVector(pageRecords, searchKey, clusteredIdx, type);
					binaryUsed = true;
				}

				for (int k = deleteIdx; k < p.size(); k++) {
					Record currRecord = p.get(k);
					Comparable currRecordKey = getComparable(currRecord.get(clusteredIdx), type);

					if (currRecordKey.compareTo(searchKey) > 0) {// updated 28/3
						System.out.println("break in delete small loop");

						break;
					}

					if (matchRecord(currRecord, htblColNameValue)) {
						// System.out.println("there is a match");
						deleteRecordFromIndex(currRecord, new Ref(pagesDirectory.get(i), currRecord.getId()));

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
				// Changed the .equals to comapreTO
				// if (((Comparable)
				// htblColNameValue.get(header[i].trim())).compareTo((Comparable) r.get(i)) !=
				// 0)
				// return false;
				if (!((Comparable) htblColNameValue.get(header[i].trim())).equals((Comparable) r.get(i)))
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

	public void updateMetaFile(String colName) throws IOException {
		FileReader fileReader = new FileReader(directory + "../data/" + "/metadata.csv");
		StringBuilder write = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		write.append(bufferedReader.readLine() + "\n"); // To discard first line [Table Name, Column Name, Column Type,
		// Key, Indexed]
		while ((line = bufferedReader.readLine()) != null) {
			String[] metaFile = line.split(", ");
			if (metaFile[0].equals(this.tableName) && metaFile[1].equals(colName)) {
				line = this.tableName + ", " + colName + ", " + columnTypes.get(colName) + ", "
						+ (colName.equals(this.getClusteringKey()) ? "True" : "False") + ", " + "True";
			}
			write.append(line + "\n");

		}
		File metadata = new File("" + "data/" + "/metadata.csv");

		PrintWriter pr = new PrintWriter(new FileWriter(metadata));
		pr.append(write.toString());
		pr.flush();
		pr.close();
		bufferedReader.close();

	}

	/**
	 * create index on specified column name by creating BPTree on that column and
	 * 
	 * inserting in it.
	 * 
	 * @param strColName The name of the column which index is created on
	 * @throws DBAppException         If columns, foreign keys or the primary key
	 *                                are not valid
	 * @throws FileNotFoundException  If an error occurred in the stored table file
	 * @throws IOException            If an I/O error occurred
	 * @throws ClassNotFoundException If an error occurred in the stored table pages
	 *                                format
	 */
	// TO DO update metadata

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createIndex(String strColName)
			throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException {

		if (!columnTypes.containsKey(strColName)) {
			throw new DBAppException("there is no column with this name");
		}

		updateMetaFile(strColName);
		String type = columnTypes.get(strColName);
		int colPos = this.getColIdx(strColName);
		BPTree tree = null;
		String pagingPath = directory + tableName + "_" + strColName;
		// System.out.println(type);
		if (type.equals("java.lang.Integer")) {
			// System.out.println("goua 2l if");
			tree = new BPTree<Integer>(NodeSize, pagingPath);
			// System.out.println(tree.toString());
		} else if (type.equals("java.lang.String")) {
			tree = new BPTree<String>(NodeSize, pagingPath);
		} else if (type.equals("java.lang.Double")) {
			tree = new BPTree<Double>(NodeSize, pagingPath);
		} else if (type.equals("java.awt.Polygon")) {
			tree = new BPTree<DBPolygon>(NodeSize, pagingPath);
		} else if (type.equals("java.util.Date")) {
			tree = new BPTree<Date>(NodeSize, pagingPath);
		} else if (type.equals("java.lang.Boolean")) {
			tree = new BPTree<Boolean>(NodeSize, pagingPath);

		}
		colNameIndex.put(strColName, tree);
		System.out.println("sreColName: " + strColName + "**********************");

		ObjectInputStream ois;
		for (int pageIdx = 0; pageIdx < pagesDirectory.size(); pageIdx++) {
			// File f = new File(directory + tableName + "_" + index+".class");
			// if(f.exists())
			// ois = new ObjectInputStream(new FileInputStream(f));
			Page p = deserialize(pagesDirectory.get(pageIdx));
			for (int i = 0; i < p.size(); ++i) {
				Record r = p.get(i);
				Ref recordReference = new Ref(pagesDirectory.get(pageIdx), r.getId());
				tree.insert((Comparable) r.get(colPos), recordReference);
			}

			// ois.close();
		}
		System.out.println("-----------------------------------------------");
		System.out.println(tree.toString());
		this.save();
	}

	// public static HashSet<Record> and(HashSet<Record> a, HashSet<Record> b) {
	// HashSet<Record> ans = new HashSet<Record>();
	// for (Record r : a) {
	// if (b.contains(r))
	// ans.add(r);
	// }
	//// for(Record r:b) {
	//// if(a.contains(r))ans.add(r);
	//// }
	// return ans;
	//
	// }

	public HashSet<Record> and(HashSet<Record> a, SQLTerm sqlTerm) throws DBAppException, IOException {
		HashSet<Record> ans = new HashSet<Record>();
		for (Record r : a) {
			if (matchTerm(r, sqlTerm))
				ans.add(r);
		}

		return ans;

	}

	private static HashSet<Record> or(HashSet<Record> a, HashSet<Record> b) {
		for (Record r : b) {
			a.add(r);
			// if(!a.contains(r))
		}
		return a;

	}

	private static HashSet<Record> xor(HashSet<Record> a, HashSet<Record> b) {
		HashSet<Record> ans = new HashSet<Record>();
		for (Record r : a) {
			if (!b.contains(r)) {
				ans.add(r);
			}
		}
		for (Record r : b) {
			if (!a.contains(r)) {
				ans.add(r);
			}
		}
		return ans;

	}

	private boolean hasIndex(String colName) {
		return colNameIndex.containsKey(colName);
	}

	private HashSet<Record> getRecordsFromRef(Vector<Ref> refs) {
		HashMap<String, HashSet<Long>> pageToRecord = new HashMap();
		// updated 7-4
		if (refs == null)
			return new HashSet<Record>();
		for (Ref r : refs) {
			String currDirectory = r.getPageDirectory();
			long id = r.getRecordId();
			HashSet<Long> curSet = pageToRecord.getOrDefault(currDirectory, new HashSet<Long>());
			curSet.add(id);
			pageToRecord.put(currDirectory, curSet);
		}

		HashSet<Record> ans = new HashSet<Record>();
		for (Entry<String, HashSet<Long>> entry : pageToRecord.entrySet()) {
			String pageDirectory = (String) entry.getKey();
			HashSet<Long> recordsId = entry.getValue();
			Page p = deserialize(pageDirectory);
			for (int i = 0; i < p.size(); i++) {
				Record curRecord = p.get(i);
				if (recordsId.contains(curRecord.getId()))
					ans.add(curRecord);
			}
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectEqual(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();
		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX" + colName);
			BPTree tree = colNameIndex.get(colName);
			this.dispalyIndex(colName);
			ans = getRecordsFromRef(tree.search(val));
		} else if (colName.equals(getClusteringKey())) {
			System.out.println("USING CLUSTRING KEY");
			Hashtable<String, Object> t = new Hashtable<String, Object>();
			t.put(colName, val);
			int[] a = BinarySearch(t);
			int startPage = a[0];
			int startIdx = a[1];
			String type = this.columnTypes.get(colName).trim();
			for (int i = startPage; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Vector<Record> pageRecords = p.getRecords();
				if (i != startPage)
					startIdx = 0;
				for (int j = startIdx; j < pageRecords.size(); j++) {
					Record currRecord = pageRecords.get(j);
					Comparable currKey = getComparable(currRecord.get(getColIdx(colName)), type);

					// edited
					// if (currKey.compareTo(val) == 0)
					// ans.add(currRecord);
					if (currKey.equals(val))
						ans.add(currRecord);
					else
						break;

				}
			}
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, "=");
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectNotEqual(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();

		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX");
			BPTree tree = colNameIndex.get(colName);
			ans = getRecordsFromRef(tree.searchNotEqual(val));
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, "!=");
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectGreaterThan(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();

		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX");
			BPTree tree = colNameIndex.get(colName);
			ans = getRecordsFromRef(tree.searchGreaterThan(val));
		} else if (colName.equals(getClusteringKey())) {
			System.out.println("USING CLUSTRING KEY");
			Hashtable<String, Object> t = new Hashtable<String, Object>();
			t.put(colName, val);
			int[] a = BinarySearch(t);
			int startPage = a[0];
			int startIdx = a[1];
			String type = this.columnTypes.get(colName).trim();
			for (int i = startPage; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Vector<Record> pageRecords = p.getRecords();
				if (i != startPage) {
					startIdx = 0;
				}
				for (int j = startIdx; j < pageRecords.size(); j++) {
					Record currRecord = pageRecords.get(j);
					Comparable currKey = getComparable(currRecord.get(getColIdx(colName)), type);
					if (currKey.compareTo(val) > 0)
						ans.add(currRecord);

				}
			}
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, ">");
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectGreaterThanOrEqual(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();

		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX");
			BPTree tree = colNameIndex.get(colName);
			ans = getRecordsFromRef(tree.searchGreaterThanOrEqual(val));
		} else if (colName.equals(getClusteringKey())) {
			System.out.println("USING CLUSTRING KEY");
			Hashtable<String, Object> t = new Hashtable<String, Object>();
			t.put(colName, val);
			int[] a = BinarySearch(t);
			int startPage = a[0];
			int startIdx = a[1];
			String type = this.columnTypes.get(colName).trim();
			for (int i = startPage; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Vector<Record> pageRecords = p.getRecords();
				if (i != startPage) {
					startIdx = 0;
				}
				for (int j = startIdx; j < pageRecords.size(); j++) {
					Record currRecord = pageRecords.get(j);
					Comparable currKey = getComparable(currRecord.get(getColIdx(colName)), type);
					if (currKey.compareTo(val) >= 0)
						ans.add(currRecord);
					else
						System.out.println("ERROR OCCOURED!");

				}
			}
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, ">=");
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectSmallerThan(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();

		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX");
			BPTree tree = colNameIndex.get(colName);
			ans = getRecordsFromRef(tree.searchSmallerThan(val));
		} else if (colName.equals(getClusteringKey())) {
			System.out.println("USING CLUSTRING KEY");
			// Hashtable<String, Object> t = new Hashtable<String, Object>();
			// t.put(colName, val);

			String type = this.columnTypes.get(colName).trim();
			for (int i = 0; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Vector<Record> pageRecords = p.getRecords();
				for (int j = 0; j < pageRecords.size(); j++) {
					Record currRecord = pageRecords.get(j);
					Comparable currKey = getComparable(currRecord.get(getColIdx(colName)), type);
					if (currKey.compareTo(val) < 0)
						ans.add(currRecord);
					else
						break;

				}
			}
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, "<");
		}
		return ans;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public <T extends Comparable<T>> HashSet<Record> selectSmallerThanOrEqual(String colName, T val) throws Exception {
		HashSet<Record> ans = new HashSet<Record>();

		// int colIndex = getColIdx(colName);

		if (hasIndex(colName)) {
			System.out.println("USING INDEX");
			BPTree tree = colNameIndex.get(colName);
			ans = getRecordsFromRef(tree.searchSmallerThanOrEqual(val));
		} else if (colName.equals(getClusteringKey())) {
			System.out.println("USING CLUSTRING KEY");
			Hashtable<String, Object> t = new Hashtable<String, Object>();
			t.put(colName, val);

			String type = this.columnTypes.get(colName).trim();
			for (int i = 0; i < pagesDirectory.size(); i++) {
				Page p = deserialize(pagesDirectory.get(i));
				Vector<Record> pageRecords = p.getRecords();
				for (int j = 0; j < pageRecords.size(); j++) {
					Record currRecord = pageRecords.get(j);
					Comparable currKey = getComparable(currRecord.get(getColIdx(colName)), type);
					if (currKey.compareTo(val) <= 0)
						ans.add(currRecord);
					else
						break;

				}
			}
		} else {
			System.out.println("USING NOTHING");
			ans = linearSearch(val, colName, "<=");
		}
		return ans;
	}

	public boolean matchTerm(Record r, SQLTerm sqlTerm) throws DBAppException, IOException {

		String colName = sqlTerm.get_strColumnName();
		Comparable val = (Comparable) sqlTerm.get_objValue();
		if (!this.columnTypes.containsKey(colName)) {
			throw new DBAppException("Invalid col name!");
		}
		if (!checkType(val, this.columnTypes.get(colName))) {
			throw new DBAppException("Invalid col type!");
		}
		int colIdx = getColIdx(colName);
		String type = columnTypes.get(colName);
		Comparable recordVal = getComparable(r.get(colIdx), type);
		String op = sqlTerm.get_strOperator();

		switch (op) {
		case "=":
			// return recordVal.compareTo(val) == 0;
			return recordVal.equals(val);

		case ">":

			return recordVal.compareTo(val) > 0;
		case ">=":
			return recordVal.compareTo(val) >= 0;
		case "<":
			return recordVal.compareTo(val) < 0;

		case "<=":
			return recordVal.compareTo(val) <= 0;
		case "!=":
			// return recordVal.compareTo(val) != 0;
			return !recordVal.equals(val);
		default : throw new DBAppException("Invalid operator");

		}
		

	}

	// Not tested
	public boolean matchCondition(Record r, SQLTerm[] arrSQLTerms, String[] strarrOperators)
			throws IOException, DBAppException {

		if (arrSQLTerms.length == 0)
			return true;

		boolean result = matchTerm(r, arrSQLTerms[0]);

		for (int i = 1; i < arrSQLTerms.length; i++) {
			boolean currTermEval = matchTerm(r, arrSQLTerms[i]);

			switch (strarrOperators[i - 1].toUpperCase().trim()) {
			case "AND":
				result &= currTermEval;
				break;
			case "OR":
				result |= currTermEval;

				break;
			case "XOR":
				result ^= currTermEval;
				break;

			default:
				throw new DBAppException("Invalid connector!!!!");
			}

		}

		return result;

	}

	/*
	 * term1 & term2 & term3 || term4 & term5 &term6 f t t f f t
	 * 
	 * //f means doesn't have index
	 */
	// Not tested
	public boolean isLinearSearch(SQLTerm[] arrSQLTerms, String[] strarrOperators) {
		if (arrSQLTerms.length == 0)
			return true;

		// int i=1;
		// if(strarrOperators.length!=0&&strarrOperators[0].toUpperCase().trim().equals("AND"))
		// {
		// boolean
		// hasIndex=(hasIndex(arrSQLTerms[0].get_strColumnName())||arrSQLTerms[0].get_strColumnName().equals(getClusteringKey()));
		// while(i-1<strarrOperators.length&&strarrOperators[i-1].toUpperCase().trim().equals("AND"))
		// {
		// hasIndex|=hasIndex(arrSQLTerms[i].get_strColumnName())||arrSQLTerms[i].get_strColumnName().equals(getClusteringKey());
		// i++;
		// }
		// if(!hasIndex)return true;
		// }
		if (!hasIndex(arrSQLTerms[0].get_strColumnName())
				&& !arrSQLTerms[0].get_strColumnName().equals(getClusteringKey()))
			return true;

		for (int i = 1; i < arrSQLTerms.length; i++) {
			if (strarrOperators[i - 1].toUpperCase().trim().equals("AND"))
				continue;
			if (!hasIndex(arrSQLTerms[i].get_strColumnName())
					&& !arrSQLTerms[i].get_strColumnName().equals(getClusteringKey()))
				return true;
		}
		return false;
	}

	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws Exception {
		if (arrSQLTerms.length != strarrOperators.length + 1)
			throw new DBAppException("Size of Operators must match size of columns");
		if (isLinearSearch(arrSQLTerms, strarrOperators)) {
			System.out.println("selectFromTableLinear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			return selectFromTableLinear(arrSQLTerms, strarrOperators);
		} else {
			System.out.println("selectFromTableNotLinear^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

			return selectFromTableNotLinear(arrSQLTerms, strarrOperators);
		}
	}

	public Iterator selectFromTableLinear(SQLTerm[] arrSQLTerms, String[] strarrOperators)
			throws IOException, DBAppException {
		Vector<Record> result = new Vector<Record>();
		for (int i = 0; i < pagesDirectory.size(); i++) {
			Page p = deserialize(pagesDirectory.get(i));
			for (int j = 0; j < p.size(); j++) {
				Record record = p.get(j);
				if (matchCondition(record, arrSQLTerms, strarrOperators)) {
					result.add(record);
				}
			}
		}
		return result.iterator();
	}

	public Iterator selectFromTableNotLinear(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws Exception {
		HashSet<Record> set = new HashSet<Record>();
		// TO DO check if the array size equal zero
		set = selectOnOneCol(arrSQLTerms[0]);
		if (arrSQLTerms.length - 1 != strarrOperators.length)
			throw new DBAppException("Invalid syntax format!");
		for (int i = 1; i < arrSQLTerms.length; i++) {
			HashSet<Record> temp = null;
			if (!strarrOperators[i - 1].toUpperCase().trim().equals("AND"))
				temp = selectOnOneCol(arrSQLTerms[i]);

			switch (strarrOperators[i - 1].toUpperCase().trim()) {
			case "AND":
				set = and(set, arrSQLTerms[i]);
				break;
			case "OR":
				set = or(set, temp);
				break;
			case "XOR":
				set = xor(set, temp);
				break;

			default:
				throw new DBAppException("Invalid connector!!!!");
			}
		}
		return set.iterator();

	}

	private HashSet<Record> selectOnOneCol(SQLTerm sqlTerm) throws Exception {
		String colName = sqlTerm.get_strColumnName();
		Comparable val = (Comparable) sqlTerm.get_objValue();
		if (!this.columnTypes.containsKey(colName)) {
			throw new DBAppException("Invalid col name!");
		}
		if (!checkType(val, this.columnTypes.get(colName))) {
			throw new DBAppException("Invalid col type!");
		}
		String op = sqlTerm.get_strOperator();
		HashSet<Record> set = new HashSet<Record>();

		switch (op) {
		case "=":
			set = selectEqual(colName, val);
			break;
		case ">":
			set = selectGreaterThan(colName, val);
			break;
		case ">=":
			set = selectGreaterThanOrEqual(colName, val);
			break;
		case "<":
			set = selectSmallerThan(colName, val);
			break;
		case "<=":
			set = selectSmallerThanOrEqual(colName, val);
			break;
		case "!=":
			set = selectNotEqual(colName, val);
			break;
		default:
			throw new DBAppException("Error invalid operator!!!!");
		}
		return set;
	}
}
