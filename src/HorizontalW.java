
class HorizontalW {
	
	public VerticalW vertWord;
	public boolean hasPair;
	public int num;
	public int x;
	public int y;
	public int len;
	public String word;
	public String question;
	
	
	public HorizontalW(int num, int len, String word, String question){
		this.num = num;
		this.len = len;
		this.word = word;
		hasPair = false;
		vertWord = null;
		this.question = question;
	}
	public void setPair(VerticalW vword){
		hasPair = true;
		this.vertWord = vword;
	}
	/*
	public void setXY(int x, int y){
		this.x = x;
		this.y = y;
	}
	*/
}
