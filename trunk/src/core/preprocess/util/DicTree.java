package core.preprocess.util;

public class DicTree {
	
	private int occurrence;
	private char val;
	private DicTree Child;
	private DicTree Brother;
	
	/*
	 * Constructor without parameters
	 * */
	public DicTree(){
		this.val = 0;
		this.occurrence = 0;
		this.Child = null;
		this.Brother = null;
	}
	
	/*
	 * Constructor with occurrence
	 * */
	public DicTree(char val, int occurrence,DicTree Child, DicTree Brother){
		this.val = val;
		this.occurrence = occurrence;
		this.Child = Child;
		this.Brother = Brother;
	}
	
	/*
	 * 
	 * */
	public void setAttribute(char val){
		this.val = val;
	}
	
	/*
	 * */
	public void setOccurrence(int occurrence){
		this.occurrence = occurrence;
	}
	
	/*
	 * */
	public void setFirstChild(DicTree Child){
		this.Child = Child;
	}
	/*
	 * */
	public void setFirstBrother(DicTree Brother){
		this.Brother = Brother;
	}
	
	/*
	 * */
	public char getAttribute(){
		return this.val;
	}
	
	/*
	 * Get Occurrence of the current node
	 * */
	public int getOccurrence(){
		return this.occurrence;
	}
	
	/*
	 * Get The Firt Child
	 * */
	public DicTree getFirstChild(){
		return this.Child;
	}
	
	/*
	 * Get the first brother
	 * */
	public DicTree getFirstBrother(){
		return this.Brother;
	}
	
	/*
	 * Add Word To The DicTree
	 * */
	public int addToDicTree(String word){
		DicTree tmp = this;
		int flag;
		int i;
		for(i = 0; i != word.length(); i++){
			if(tmp.Child == null){
				tmp.Child = new DicTree(word.charAt(i),0,null,null);
				tmp = tmp.Child;
			}
			else{
				if(tmp.Child.getAttribute() > word.charAt(i)){/*Add To The Head Of The List*/
					DicTree t = new DicTree(word.charAt(i),0,null,tmp.Child);
					tmp.Child = t;
					tmp = t;
				}
				else if(word.charAt(i) == tmp.Child.getAttribute()){
					tmp = tmp.Child;
				}
				else{
					tmp = tmp.Child;
					flag = 1;
					while(tmp.Brother != null){
						if(word.charAt(i) == tmp.Brother.getAttribute()){
							tmp = tmp.Brother;
							flag = 0;
							break;
						}
						else if(word.charAt(i) < tmp.Brother.getAttribute()){
							DicTree t = new DicTree(word.charAt(i),0,null,tmp.Brother);
							tmp.Brother = t;
							tmp = t;
							flag = 0;
							break;
						}
						tmp = tmp.Brother;
					}
					if(flag == 1){
						DicTree t = new DicTree(word.charAt(i),0,null,null);
						tmp.Brother = t;
						tmp = t;
					}
				}
			}
		}
		tmp.setOccurrence(tmp.getOccurrence()+1);
		return 0;
	}
	
	/*
	 * Write DicTree
	 * */
	public int writeDicTree(){
		return 0;
	}
	
	/*
	 * */
	public static void travelDicTree(DicTree root, String s){
		if(root.Child == null){
			return ;
		}
		else{
			s = s+root.Child.getAttribute();
			travelDicTree(root.Child,s);
			if(root.Child.getOccurrence() != 0)
				System.out.println(root.Child.getOccurrence()+" "+s);
			if(s.length() == 1){
				s = "";
			}
			else{
				s = s.substring(0, s.length()-2);
			}
			if(root.Brother != null){
				s = s+root.Brother.getAttribute();
				travelDicTree(root.Brother,s);
			}
		}
	}
}
