package test.preprocess.util;
import core.preprocess.util.DicTree;

public class DicTreeTest {
	public static void main(String[] args){
		String[] words = {"the","the","bahia","cocoa","zone","alleviating","the","drought","since","early","January","and","imporving","prospects"};
		DicTree dic = new DicTree();
		dic.setAttribute('\\');
		dic.setOccurrence(0);
		for(int i = 0; i != words.length; i++){
			dic.addToDicTree(words[i]);
		}
		String s = "";
		DicTree.travelDicTree(dic,s);
	}
}
