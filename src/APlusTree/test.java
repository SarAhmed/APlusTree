package APlusTree;

import java.util.Vector;

public class test {

	public static void main(String[] args) {
		int a[] = {1,2,3,5,6,6,6,7,10};
//		System.out.println(greatestlesser(0, a.length, 21, a));
		System.out.println(BS(a,10,0,a.length));
		System.out.println(a.length);
	}
	
	static int BS(int[] arr , int val, int lo, int hi) {
		 lo=0;
		 hi=arr.length;
		while(hi-lo>0) {
			int mid =hi+lo>>1;
			
			
			if(arr[mid]>=val) {
				hi=mid;
			}else {
				lo=mid+1;
			}
		}
		//if(arr[hi]<val && hi==arr.length-1)hi++;
		return hi;
	}
	 
	static int BSVector(Vector<Record> arr ,Comparable val,int clusteredIdx,String type) {
		 int lo=0;
		int hi=arr.size();
		while(hi-lo>0) {
			int mid =hi+lo>>1;
			Record currRecord = arr.get(mid);
			Comparable currKey = getComparable(currRecord.get(clusteredIdx), type);
			
			
			if(currKey.compareTo(val)>=0) {
				hi=mid;
			}else {
				lo=mid+1;
			}
		}
		return hi;
	}
	
}
