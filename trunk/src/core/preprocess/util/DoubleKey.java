package core.preprocess.util;

public class DoubleKey extends KeyType{
	private double x;

	public DoubleKey(double x){
		this.x = x;
	}
	@Override
	public boolean Bigger(KeyType x, KeyType y) {
		// TODO Auto-generated method stub
		DoubleKey a = (DoubleKey)x;
		DoubleKey b = (DoubleKey)y;
		return a.x>b.x;
	}

	@Override
	public void Output() {
		// TODO Auto-generated method stub
		System.out.print(this.x);
	}
}
