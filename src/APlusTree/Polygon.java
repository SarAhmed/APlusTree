package APlusTree;

public class Polygon  implements Comparable<Polygon>{
java.awt.Polygon polygon;
public Polygon(java.awt.Polygon p) {
	this.polygon=p;
}
@Override
public int compareTo(Polygon o) {
	double curArea=this.polygon.getBounds().getSize().getWidth()*this.polygon.getBounds().getSize().getHeight();

	double OArea=o.polygon.getBounds().getSize().getWidth()*o.polygon.getBounds().getSize().getHeight();
	return  (Math.abs(curArea-OArea))<1e-6?0:(curArea>OArea)?1:-1;
}


}
