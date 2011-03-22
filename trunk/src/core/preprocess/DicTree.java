package core.preprocess;

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
	public void SetAttribute(char val){
		this.val = val;
	}
	
	/*
	 * */
	public void SetOccurrence(int occurrence){
		this.occurrence = occurrence;
	}
	
	/*
	 * */
	public void SetFirstChild(DicTree Child){
		this.Child = Child;
	}
	/*
	 * */
	public void SetFirstBrother(DicTree Brother){
		this.Brother = Brother;
	}
	
	/*
	 * */
	public char GetAttribute(){
		return this.val;
	}
	
	/*
	 * Get Occurrence of the current node
	 * */
	public int GetOccurrence(){
		return this.occurrence;
	}
	
	/*
	 * Get The Firt Child
	 * */
	public DicTree GetFirstChild(){
		return this.Child;
	}
	
	/*
	 * Get the first brother
	 * */
	public DicTree GetFirstBrother(){
		return this.Brother;
	}
	
	/*
	 * Add Word To The DicTree
	 * */
	public int AddToDicTree(String word){
		DicTree tmp = this;
		int flag;
		int i;
		for(i = 0; i != word.length(); i++){
			if(tmp.Child == null){
				tmp.Child = new DicTree(word.charAt(i),0,null,null);
				tmp = tmp.Child;
			}
			else{
				if(tmp.Child.GetAttribute() > word.charAt(i)){/*Add To The Head Of The List*/
					DicTree t = new DicTree(word.charAt(i),0,null,tmp.Child);
					tmp.Child = t;
					tmp = t;
				}
				else if(word.charAt(i) == tmp.Child.GetAttribute()){
					tmp = tmp.Child;
				}
				else{
					tmp = tmp.Child;
					flag = 1;
					while(tmp.Brother != null){
						if(word.charAt(i) == tmp.Brother.GetAttribute()){
							tmp = tmp.Brother;
							flag = 0;
							break;
						}
						else if(word.charAt(i) < tmp.Brother.GetAttribute()){
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
		tmp.SetOccurrence(tmp.GetOccurrence()+1);
		return 0;
	}
	
	/*
	 * Write DicTree
	 * */
	public int WriteDicTree(){
		return 0;
	}
	
	/*
	 * */
	public static void TravelDicTree(DicTree root, String s){
		if(root.Child == null){
			return ;
		}
		else{
			s = s+root.Child.GetAttribute();
			TravelDicTree(root.Child,s);
			if(root.Child.GetOccurrence() != 0)
				System.out.println(root.Child.GetOccurrence()+" "+s);
			if(s.length() == 1){
				s = "";
			}
			else{
				s = s.substring(0, s.length()-2);
			}
			if(root.Brother != null){
				s = s+root.Brother.GetAttribute();
				TravelDicTree(root.Brother,s);
			}
		}
	}
}
