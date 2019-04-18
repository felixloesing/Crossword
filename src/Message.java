import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String text;
	boolean question;
	public boolean endGame;

	public Message(String m, boolean b) {
		this.text = m;
		this.question = b;
		this.endGame = false;
	}

}
