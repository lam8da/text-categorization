package test.preprocess;
import core.preprocess.DicTree;

public class DicTreeTest {
	public static void main(String[] args){
		String[] words = {"the","the","bahia","cocoa","zone","alleviating","the","drought","since","early","January","and","imporving","prospects"};
		DicTree dic = new DicTree();
		dic.SetAttribute('\\');
		dic.SetOccurrence(0);
		for(int i = 0; i != words.length; i++){
			dic.AddToDicTree(words[i]);
		}
		String s = "";
		DicTree.TravelDicTree(dic,s);
	}
}
