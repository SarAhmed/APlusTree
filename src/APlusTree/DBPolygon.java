package APlusTree;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

public class DBPolygon  implements Comparable<DBPolygon>{
private Polygon polygon;
static int id;

public DBPolygon(java.awt.Polygon p) {
	id++;
	this.polygon=p;
}
@Override
public int compareTo(DBPolygon o) {
	double curArea=this.polygon.getBounds().getSize().getWidth()*this.polygon.getBounds().getSize().getHeight();

	double OArea=o.polygon.getBounds().getSize().getWidth()*o.polygon.getBounds().getSize().getHeight();
	return  (Math.abs(curArea-OArea))<1e-6?0:(curArea>OArea)?1:-1;
}

public String toString() {
	return ""+id;
}


}
