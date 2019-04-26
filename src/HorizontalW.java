
class HorizontalW {
	
	public int x;
	public int y;
	public VerticalW vertWord;
	public boolean isPair;
	public int num;
	public int len;
	public String word;
	public String question;
	
	
	public HorizontalW(int num, int len, String word, String question){
		this.num = num;
		this.len = len;
		this.word = word;
		vertWord = null;
		this.question = question;
		isPair = false;
	}
	
	public void setPair(VerticalW vword){
		isPair = true;
		this.vertWord = vword;
	}
}
