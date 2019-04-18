import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public int score = 0;
	public int playerNumber = -1;
	
	@SuppressWarnings("unused")
	private Server server;
	public Socket socket;
	
	public Player(Socket s, Server ser, int num) {
		this.server = ser;
		this.socket = s;
		this.playerNumber = num;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void sendMessage(Message message) {
		
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public String receiveMessage() {
		String line = null;
		try {
				try {
					line = (String)ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			//System.out.println(line);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return line;
	}

}
