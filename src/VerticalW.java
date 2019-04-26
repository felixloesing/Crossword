
class VerticalW {
	public boolean hasPair;
	public HorizontalW hword;
	public int num;
	public int x;		
	public int y;
	public int len;
	public String word;
	public String question;
	
	
	public VerticalW(int num, int len, String word, String question){
		this.word = word;
		this.num = num;
		this.len = len;
		hasPair = false;
		this.question = question;
	}
	
	public void setPair(HorizontalW hw){
		
		this.hword = hw;
		hasPair = true;
	}
}
