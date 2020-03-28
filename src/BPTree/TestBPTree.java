package BPTree;

import java.util.Scanner;
import java.util.Vector;

public class TestBPTree {

	public static void main(String[] args) {
		BPTree<Integer> tree = new BPTree<Integer>(4);
		Scanner sc = new Scanner(System.in);
		int i = 0;
		while (true) {
			int x = sc.nextInt();
			if (x == -1)
				break;
		//	Ref ref = new Ref(1,(long) i++)
			int id=0;
			Ref ref = new Ref("dir",id++);

			tree.insert(x, ref);
			System.out.println(tree.toString());
		}
		while (true) {
			int key = sc.nextInt();
			int id =sc.nextInt();
//			int newKey =sc.nextInt();
			Ref ref = new Ref("dir",id);
			if (key == -1)
				break;
		//	Ref ref = new Ref(1, idx);

			tree.delete(key,ref);
			
		//	tree.update(key, ref, newKey);;
			System.out.println(tree.toString());
			System.out.println("-----------------------------------------");
		}
		sc.close();
	}
}