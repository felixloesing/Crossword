import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread {
	Socket s = null;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Lock lock = new ReentrantLock();

	public Client(String hostname, int port) {
		try {
			System.out.println("Trying to connect to " + hostname + ":" + port);
			try {
				s = new Socket(hostname, port);
			} catch (Exception uhe) {
				//uhe.printStackTrace();
				while(s == null) {
					System.out.println("Connection failed.");
					System.out.print("Enter the server hostname: ");
					hostname = Scan.scan.nextLine();
					System.out.print("Enter the server port: ");
					port = Integer.valueOf(Scan.scan.nextLine());
					try {
						s = new Socket(hostname, port);
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}
			
			
			System.out.println("Connected to " + hostname + ":" + port);
			
			ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			try {
				if (s != null) {
					s.close();
				}
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			*/
		}
	}

	public static void main(String[] args) {
		System.out.println("Welcome to 201 Crossword!");
		System.out.print("Enter the server hostname: ");
		String host = Scan.scan.nextLine();
		System.out.print("Enter the server port: ");
		int port = Integer.valueOf(Scan.scan.nextLine());
		
		//@SuppressWarnings("unused")
		Client client = new Client(host, port);
		client.start();

	}
	
	public void run() {
		while (true) {
        	lock.lock();
        	String m;
        	Message message;
			try {
				message = (Message)ois.readObject();
				if (message.endGame == true) {
					return;
				}
				m = message.text;
				System.out.println(m);
				if (message.question == true) {
					String line = Scan.scan.nextLine();
            		oos.writeObject(line);
            		oos.flush();
				}
				
			} catch (EOFException eof) {
				// TODO: handle exception
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
        	
        }
	}

}
