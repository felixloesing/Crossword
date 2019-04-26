
class VerticalW {
	
	public int x;		
	public int y;
	public boolean isPair;
	public HorizontalW hword;
	public int num;
	public int len;
	public String word;
	public String question;
	
	
	public VerticalW(int num, int len, String word, String question){
		this.word = word;
		this.num = num;
		this.len = len;
		this.question = question;
		
		isPair = false;
	}
	
	public void setPair(HorizontalW hw){
		this.hword = hw;
		isPair = true;
	}
}
