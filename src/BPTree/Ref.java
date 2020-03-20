package BPTree;

import java.io.Serializable;

public class Ref implements Serializable {

	/**
	 * This class represents a pointer to the record. It is used at the leaves of
	 * the B+ tree
	 */
	private static final long serialVersionUID = 1L;
	private String pageDirectory;
	private long recordId;
	// indexInPage;

	public Ref(String pageDirectory, long id) {
		this.pageDirectory = pageDirectory;
		this.recordId = id;
		// this.indexInPage = indexInPage;
	}
	public long getRecordId() {
		return this.recordId;
	}

	/**
	 * @return the page at which the record is saved on the hard disk
	 */
	public String getPageDirectory() {
		return pageDirectory;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageDirectory == null) ? 0 : pageDirectory.hashCode());
		result = prime * result + (int) (recordId ^ (recordId >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ref other = (Ref) obj;
		if (pageDirectory == null) {
			if (other.pageDirectory != null)
				return false;
		} else if (!pageDirectory.equals(other.pageDirectory))
			return false;
		if (recordId != other.recordId)
			return false;
		return true;
	}

	/**
	 * @return the index at which the record is saved in the page
	 */
//	public int getIndexInPage()
//	{
//		return indexInPage;
//	}
}