package core.preprocess.util;

import java.util.ArrayList;

class BTNode<T extends KeyType>
{
	public int M;
	public ArrayList<BTNode<T>> children;
	public ArrayList<T> key;
	public int keynum;
	public boolean leaf;
	public BTNode(int M)throws Exception
	{
		if(M <= 1){
			throw new Exception("M should larger than 1");
		}
		int i;
		this.M = M;
		this.children = new ArrayList<BTNode<T>>(2*this.M);
		for(i = 0; i != 2*this.M; i++){
			this.children.add(null);
		}
		this.key = new ArrayList<T>(2*this.M-1);
		for(i = 0; i != 2*this.M-1; i++){
			this.key.add(null);
		}
		this.keynum = 0;
		this.leaf = true;
	}
}

public  class BTree<T extends KeyType> {
	int M;
	BTNode<T> root = null;
	public BTree(int M) throws Exception
	{
		if(M <= 1){
			throw new Exception("M should larger than 1");
		}
		this.M = M;
		root = new BTNode<T>(M);
	}
	
	private void BTreeSplitChild(BTNode<T> x, int i, BTNode<T> y) throws Exception
	{
		int j;
		BTNode<T> z = new BTNode<T>(this.M);
		z.leaf = y.leaf;
		for(j = 0; j != M-1; j++){
			z.key.set(j,y.key.get(j+M));
		}
		z.keynum = this.M-1;
		
		if(!y.leaf){
			for(j = 0; j != M; j++){
				z.children.set(j,y.children.get(j+M));
			}
		}
		y.keynum = M-1;
		
		for(j = x.keynum; j > i; j--){
			x.children.set(j+1, x.children.get(j));
		}
		x.children.set(i+1,z);
		
		for(j = x.keynum-1; j >= i; j--){
			x.key.set(j+1, x.key.get(j));
		}
		x.key.set(i, y.key.get(this.M-1));
		x.keynum++;
	}
	
	private void BTreeInsertNonFull(BTNode<T> x, T k) throws Exception
	{
		int i = x.keynum;
		if(x.leaf){
			while(i > 0 && k.Bigger(x.key.get(i-1), k)){
				x.key.set(i, x.key.get(i-1));
				i--;
			}
			x.key.set(i,k);
			x.keynum++;
		}
		else{
			while( i > 0 && k.Bigger(x.key.get(i-1), k))i--;
			
			if(x.children.get(i).keynum == 2*this.M-1){
				BTreeSplitChild(x,i,x.children.get(i));
				if(k.Bigger(k, x.key.get(i)))i++;
			}
			BTreeInsertNonFull(x.children.get(i),k);
		}
	}
	
	public void BTreeInsert(T k) throws Exception{
		BTNode<T> r = this.root;
		if(r.keynum == 2*this.M-1){
			BTNode<T> s = new BTNode<T>(this.M);
			s.leaf = false;
			s.children.set(0,r);
			this.root = s;
			BTreeSplitChild(s,0,r);
			BTreeInsertNonFull(s,k);
		}
		else{
			BTreeInsertNonFull(r,k);
		}
	}
	
	public void BTreeDepthFristTravel(){
		this.BTreeDepthFirstTravel(this.root);
	}
	
	private void BTreeDepthFirstTravel(BTNode<T> root){
		int i;
		if(root.leaf){
			for(i = 0; i != root.keynum; i++){
				(root.key.get(i)).Output();
				System.out.print('\t');
			}
		}
		else{
			for(i = 0; i != root.keynum; i++){
				BTreeDepthFirstTravel(root.children.get(i));
				(root.key.get(i)).Output();
				System.out.print('\t');
			}
			BTreeDepthFirstTravel(root.children.get(i));
		}
	}
}
