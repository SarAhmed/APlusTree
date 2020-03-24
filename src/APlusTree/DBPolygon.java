package APlusTree;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.util.Objects.hash;

public class DBPolygon  implements Comparable<DBPolygon>,Serializable{
	private static final long serialVersionUID = 1L;
	
private Polygon polygon;
static int id;
int curID;
public DBPolygon(java.awt.Polygon p) {
	curID=id++;
	this.polygon=p;
}
public DBPolygon(String s) {
	this.polygon=getPolygon(s);
	curID=id++;
}
@Override
public int compareTo(DBPolygon o) {
	double curArea=this.polygon.getBounds().getSize().getWidth()*this.polygon.getBounds().getSize().getHeight();

	double OArea=o.polygon.getBounds().getSize().getWidth()*o.polygon.getBounds().getSize().getHeight();
	return  (Math.abs(curArea-OArea))<1e-6?0:(curArea>OArea)?1:-1;
}
private static Polygon getPolygon(String s) {
	StringTokenizer st= new StringTokenizer(s, "(), ");
	
	int n=st.countTokens()/2;
	int[] x= new int[n];
	int[] y= new int[n];
	int i=0;
	while(st.hasMoreTokens()) {
		x[i]=Integer.parseInt(st.nextToken());
		y[i++]=Integer.parseInt(st.nextToken());
	}
	
	Polygon p = new Polygon(x, y, n);
	return p;
}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DBPolygon)) return false;
		DBPolygon dbPolygon = (DBPolygon) o;
		return  Arrays.equals(dbPolygon.polygon.ypoints,this.polygon.ypoints) &&
				Arrays.equals(dbPolygon.polygon.xpoints,this.polygon.xpoints) && dbPolygon.polygon.npoints == (this.polygon.npoints) ;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(polygon.xpoints) + Arrays.hashCode(polygon.ypoints) + hash(polygon.npoints);
		//return hash(polygon , getID());
	}

	/*@Override
        public boolean equals(Object o) {
            DBPolygon q=(DBPolygon)o;
            return this.compareTo(q)==0;
        }*/
public String toString() {
	int[] x=polygon.xpoints;
	int[] y=polygon.ypoints;
	String r="polygon "+curID;
	for(int i=0;i<x.length;i++)
		r+="("+x[i]+" ,"+y[i]+" ) ";
	r+=" with area "+(this.polygon.getBounds().getSize().getWidth()*this.polygon.getBounds().getSize().getHeight());
	
	return r;
}

	public static void main(String[] args) {
		int [] x = {1,2,3} , y = {0,2,0};
		Polygon p1 = new Polygon(x,y,3);
		DBPolygon d1 = new DBPolygon(p1);
		Polygon p2 = new Polygon(x,y,3);
		DBPolygon d2 = new DBPolygon(p1);

		int [] a = p1.xpoints;
		System.out.println(d1.hashCode() == d2.hashCode());



	}

}
