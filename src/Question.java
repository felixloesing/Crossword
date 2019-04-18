
public class Question {
	int number;
	String question;
	String answer;
	public boolean answered;

	public Question(int num, String q, String a) {
		this.number = num;
		this.question = q;
		this.answer = a;
		this.answered = false;
	}

}
