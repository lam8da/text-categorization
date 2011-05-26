package test.preprocess.util;
import core.preprocess.util.*;
public class BTreeTest {
	public static void main(String[] args){
		try {
			BTree<DoubleKey> tree = new BTree<DoubleKey>(3);
			for(int i = 0; i != 10; i++){
				DoubleKey x = new DoubleKey(i);
				tree.BTreeInsert(x);
			}
			tree.BTreeDepthFristTravel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
