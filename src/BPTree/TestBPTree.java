package BPTree;

import java.util.Scanner;

public class TestBPTree {

	public static void main(String[] args) 
	{
		BPTree<Integer> tree = new BPTree<Integer>(4);
		Scanner sc = new Scanner(System.in);
		int i =0;
		while(true) 
		{
			int x = sc.nextInt();
			if(x == -1)
				break;
			Ref ref = new Ref(1, i++);
			tree.insert(x, ref);
			System.out.println(tree.toString());
		}
		while(true) 
		{
			int x = sc.nextInt();
			int idx =sc.nextInt();
			if(x == -1)
				break;
			Ref ref = new Ref(1, idx);

			tree.delete(x,ref);
			System.out.println(tree.toString());
		}
		sc.close();
	}	
}