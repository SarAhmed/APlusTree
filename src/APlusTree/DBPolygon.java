package APlusTree;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class DBPolygon  implements Comparable<DBPolygon>{
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
	StringTokenizer st= new StringTokenizer(s, "(),");
	
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
public String toString() {
	return ""+curID;
}


}
