import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;


public class Server {
	private Vector<Player> players;
	public static Vector<Question> acrossQuestions;
	public static Vector<Question> downQuestions;
	
	private static int downL = 0;
	private static int acrossL = 0;

	public Server(int port) {
	ServerSocket ss = null;
	
	ArrayList<HorizontalW>hwords = new ArrayList<HorizontalW>();
	ArrayList<VerticalW>vwords = new ArrayList<VerticalW>();
	char[][] board = null;
	
	
	while(true) {
		try {
			ss = new ServerSocket(port);
			System.out.println("Listening on " + port + ". ");
			System.out.println("Waiting for players...");
			Lock lock = new ReentrantLock();
			
			players = new Vector<Player>();
			
			//accept for client
			Socket s = ss.accept();
			System.out.println("Connection from " + s.getInetAddress());
			Player p = new Player(s, this, 1);
			players.add(p);
			Crossword c = new Crossword();
			
			try {
				File f = readfile();
				
				board = c.startGame(hwords, vwords, board, f);
				
			} catch (FileSystemNotFoundException fsnfe) {
				System.out.println("No valid File found. New Try on reconnect");
				sendToEveryone("No valid File found. New Try on reconnect");
				continue;
			} catch (Exception e) {
				//e.printStackTrace();
				sendToEveryone("No file found");
				System.out.println("No file found");
				continue;
			}
			
			int num = -1;
			while (num < 1 || num > 3) {
				//ask for player count
				p.sendMessage(new Message("How many players will there be? ", true));
				num = Integer.valueOf(p.receiveMessage());
			}
			
			
			System.out.println("Number of players: " + num);
			System.out.println("Reading random game file.");
			System.out.println("File read successfully.");
			
			lock.lock();
			for (int i = 1; i < num; i++) {
				int n = i+1;
				//System.out.println("Waiting for player " + n + ". ");
				sendToEveryone("Waiting for player " + n + ". ");
				s = ss.accept();
				System.out.println("Connection from " + s.getInetAddress());
				sendToEveryone("Player " + String.valueOf(i+1) + " has joined from " + s.getInetAddress());
				Player pl = new Player(s, this, n);
				pl.sendMessage(new Message("There is a game waiting for you.", false));
				int numOfPl = 1;
				for (@SuppressWarnings("unused") Player playername : players) {
					pl.sendMessage(new Message("Player " + String.valueOf(numOfPl) + " has already joined", false));
					numOfPl++;
				}
				players.add(pl);
			}
			lock.unlock();
			System.out.println("Game can now begin. ");
			sendToEveryone("The game is beginning. ");
			
			
			
			int cPlayer = 1;
			String resp = "";
			while(true) {
				if(cPlayer > num) {
					cPlayer = 1;
				}
				Player currP = players.get(cPlayer-1);
				System.out.println("Sending game board. ");
				//send out game board
				for (Player pla : players) {
					c.sendBoard(pla, board);
				}
				sendGameboard();
				//sendBoard(currP, board);
				//board = c.cleanBoard(hwords, vwords);
				//c.sendBoard(currP, board);
				System.out.println("Player " + cPlayer + "'s turn. ");
				sendToAllExcept(players.get(cPlayer-1), "Player " + cPlayer + "'s turn. ");
				currP.sendMessage(new Message("Would you like to answer a question across (a) or down (d)? ", true));
				String res = currP.receiveMessage();
				boolean isAvailable = false;
				
				if(res.equals("a")) {
					for (Question question : acrossQuestions) {
						if (question.answered == false) {
							isAvailable = true;
						}
					}
					
				} else if (res.equals("d")) {
					for (Question question : downQuestions) {
						if (question.answered == false) {
							isAvailable = true;
						}
					}
				}
				
				if (isAvailable == false) {
					while(!res.equals("a") && !res.equals("d") || isAvailable == false) {
						currP.sendMessage(new Message("That is not a valid option? ", false));
						currP.sendMessage(new Message("Would you like to answer a question across (a) or down (d)? ", true));
						res = currP.receiveMessage();
						
						if(res.equals("a")) {
							for (Question question : acrossQuestions) {
								if (question.answered == false) {
									isAvailable = true;
								}
							}
							
						} else if (res.equals("d")) {
							for (Question question : downQuestions) {
								if (question.answered == false) {
									isAvailable = true;
								}
							}
						}
					}
				}
				
				
				if(res.equals("a")) {
					resp = "across";
				} else {
					resp = "down";
				}
				
				
				currP.sendMessage(new Message("Which number? ", true));
				int number = -1;
				try {
					number = Integer.valueOf(currP.receiveMessage());
				} catch (Exception e) {
					
				}
				
				//check if question with number exists
				boolean exists = false;
				
				if (number > 0) {
					if(res.equals("a")) {
						for (Question question : acrossQuestions) {
							if (question.number == number) {
								if (question.answered == false) {
									exists = true;
								}
								
							}
						}
					} else {
						for (Question question : downQuestions) {
							if (question.number == number) {
								if (question.answered == false) {
									exists = true;
								}
							}
						}
					}
				}
				
				
				while (exists == false || number < 0) {
					currP.sendMessage(new Message("That is not a valid option. ", false));
					currP.sendMessage(new Message("Which number? ", true));
					try {
						number = Integer.valueOf(currP.receiveMessage());
					} catch (Exception e) {
						
					}
					if(res.equals("a")) {
						for (Question question : acrossQuestions) {
							if (question.number == number) {
								if (question.answered == false) {
									exists = true;
								}
							}
						}
					} else {
						for (Question question : downQuestions) {
							if (question.number == number) {
								if (question.answered == false) {
									exists = true;
								}
							}
						}
					}
				}
				
				//ask question
				String questionString = "";
				if (res.equals("a")) {
					questionString = "What is your guess for " + number + " across? ";
				} else {
					questionString = "What is your guess for " + number + " down? ";
				}
				currP.sendMessage(new Message(questionString, true));
				String answer = currP.receiveMessage();
				
				System.out.println("Player " + cPlayer + " guessed '" + answer + "' for " + number + " " + resp + ". ");
				sendToAllExcept(currP, "Player " + cPlayer + " guessed '" + answer + "' for " + number + " " + resp + ". ");
				
				if (res.equals("a")) {
					for (Question question : acrossQuestions) {
						if (question.number == number) {
							if (question.answer.toLowerCase().equals(answer.toLowerCase())) {
								//correct answer
								//add points
								System.out.println("That is correct. ");
								sendToAllExcept(currP, "That is correct. ");
								currP.sendMessage(new Message("That is correct! ", false));
								currP.score = currP.score+1;
								question.answered = true;
								//System.out.println("Looking for answer");
								HorizontalW ansW = null;
								for (HorizontalW hw : hwords) {
									//System.out.println(question.answer.toLowerCase());
									if (question.answer.toLowerCase().equals(hw.word.toLowerCase())) {
										//System.out.println("Found answer");
										
										ansW = hw;
										//System.out.println(ansW.word);
										c.placeHorizontal(ansW, board);
										hwords.remove(ansW);
										break;
									}
								}
								//System.out.println("Did not find answer");
								//c.placeAnswerHorizontal(ansW, board);
								//hwords.remove(ansW);
								//System.out.println("Sending game board.");
								//for (Player pla : players) {
									//c.sendBoard(pla, board);
								//}
							} else {
								//wrong answer
								//next player
								System.out.println("That is incorrect. ");
								sendToAllExcept(currP, "That is incorrect. ");
								currP.sendMessage(new Message("That is incorrect! ", false));
								cPlayer = cPlayer + 1;
							}
						}
					}
				} else {
					for (Question question : downQuestions) {
						if (question.number == number) {
							if (question.answer.toLowerCase().equals(answer.toLowerCase())) {
								//correct answer
								//add points
								System.out.println("That is correct. ");
								sendToAllExcept(currP, "That is correct. ");
								currP.sendMessage(new Message("That is correct! ", false));
								currP.score = currP.score+1;
								question.answered = true;
								VerticalW ansW = null;
								for (VerticalW vw : vwords) {
									if (question.answer.toLowerCase().equals(vw.word.toLowerCase())) {
										ansW = vw;
										c.placeVertical(ansW, board);
										vwords.remove(ansW);
										break;
									}
								}
								//c.placeAnswerVertical(ansW, board);
								//vwords.remove(ansW);
								//System.out.println("Sending game board.");
								//for (Player pla : players) {
									//c.sendBoard(pla, board);
								//}
							} else {
								//wrong answer
								//next player
								System.out.println("That is incorrect. ");
								sendToAllExcept(currP, "That is incorrect. ");
								currP.sendMessage(new Message("That is incorrect! ", false));
								cPlayer = cPlayer + 1;
							}
						}
					}
				}
				
						
				if(allQsAnswered() == true) {
					for (Player pla : players) {
						c.sendBoard(pla, board);
					}
					sendGameboard();
					players.sort(new Comparator<Player>() {
						@Override
				        public int compare(Player o1, Player o2) {
				            return Integer.compare(o1.score, o2.score);
				        }
					});
					Collections.reverse(players);
					
					System.out.println("The game has concluded.");
					System.out.println("Sending scores.");
					sendToEveryone("Final Score");
					for (Player player : players) {
						sendToEveryone("Player " + player.playerNumber + " - " + player.score + " correct answers. ");
					}
					sendToEveryone("");
					if (players.size() > 1) {
						if (players.get(0).score == players.get(1).score) {
							sendToEveryone("Tie between " + players.firstElement().playerNumber + " and " + players.get(1).playerNumber);
						} else {
							sendToEveryone("Player " + players.firstElement().playerNumber + " is the winner. ");
						}
					} else {
						sendToEveryone("Player " + players.firstElement().playerNumber + " is the winner. ");
					}
					
					Message endMessage = new Message("", false);
					endMessage.endGame = true;
					for (Player player : players) {
						player.sendMessage(endMessage);
					}
					for (Player pl : players) {
						pl.socket.close();
					}
					for (Question question : acrossQuestions) {
						question.answered = false;
					}
					for (Question question : downQuestions) {
						question.answered = false;
					}
					
					break;
				}
				
			}
			
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
			return;
		} catch (Exception e) {
			
		} finally {
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}
		}
	}
		
		
	}

	public static void main(String[] args) {
		acrossQuestions = new Vector<Question>();
		downQuestions = new Vector<Question>();
		@SuppressWarnings("unused")
		Server server = new Server(3456);
	}
	
	public void sendToEveryone(String messsageString) {
		for (Player player : players) {
			player.sendMessage(new Message(messsageString, false));
		}
	}
	
	
	public void sendToAllExcept(Player p, String messsageString) {
		for (Player player : players) {
			if (p != player) {
				player.sendMessage(new Message(messsageString, false));
			}
		}
	}
	public boolean allQsAnswered() {
		for (Question question : acrossQuestions) {
			if(question.answered == false) {
				return false;
			}
		}
		for (Question question : downQuestions) {
			if(question.answered == false) {
				return false;
			}
		}
		return true;
	}
	
	public void sendGameboard() {
		//send the actual board
		//need to figure out how to create it first tho
		
		for (Player player : players) {
			for (Question question : acrossQuestions) {
				if (question.answered == false) {
					player.sendMessage(new Message("Across", false));
					break;
				}
			}
			
			for (Question aq : acrossQuestions) {
				if(aq.answered == false) {
					player.sendMessage(new Message(aq.number + " " + aq.question, false));
				}
				
			}
			
			for (Question question : downQuestions) {
				if (question.answered == false) {
					player.sendMessage(new Message("Down", false));
					break;
				}
			}
			
			for (Question dq : downQuestions) {
				if(dq.answered == false) {
					player.sendMessage(new Message(dq.number + " " + dq.question, false));
				}
			}
		}
	}
	
	public static File readfile() {
		File file = null;
		/*
		try {
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		File dir = new File("./gamedata");
		File[] files = dir.listFiles();
		
		ArrayList<File> fList = new ArrayList<File>();
		fList.addAll(Arrays.asList(files));
		
		if (fList.size() < 1) {
			throw new EmptyStackException();
		}

		Random rand = new Random();
		
		while (true) {
			
			file = fList.get(rand.nextInt(files.length));
			if (fList.size() < 1) {
				throw new FileSystemNotFoundException();
			}
			fList.remove(file);
			
			//removed choice from list of possible files.
			//if not formatted properly -> choose next until list empty
			
			BufferedReader br = null;
			int acrossCount = 0;
			int downCount = 0;
			
			try {
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line = br.readLine();
				if (line == null) {
					throw new FileNotFoundException();
				}
				while (line != null) {
					
					
					if (line != null && line.toLowerCase().equals("across")) {
						acrossCount++;
						//br.readLine();
						while (line != null && !line.toLowerCase().equals("down")) {
							//System.out.println(line);
							if (!line.toLowerCase().equals("across") && !line.toLowerCase().equals("down")) {
								String[] splitLine = line.split(Pattern.quote("|"));
								if (splitLine.length != 3) {
									System.out.println("The file is not formatted properly.");
									System.out.println("There are not enough parameters on line ‘" + line + "’.");
									throw new IOException();
									//continue;
								}
								
								if (splitLine[1].contains(" ")) {
									throw new IOException();
								}
								
								Question qAcross = new Question(Integer.valueOf(splitLine[0]), splitLine[2], splitLine[1]);
								acrossQuestions.add(qAcross);
								//System.out.println("qacross: " + qAcross.question);
							}
							line = br.readLine();
						}
						
					}
					
					if (line != null && line.toLowerCase().equals("down")) {
						downCount++;
						//br.readLine();
						while (line != null && !line.toLowerCase().equals("across")) {
							//System.out.println(line);
							if (!line.toLowerCase().equals("across") && !line.toLowerCase().equals("down")) {
								String[] splitLine = line.split(Pattern.quote("|"));
								if (splitLine.length != 3) {
									//System.out.println("The file is not formatted properly.");
									//System.out.println("There are not enough parameters on line ‘" + line + "’.");
									throw new IOException();
									//continue;
								}
								
								if (splitLine[1].contains(" ")) {
									throw new IOException();
								}
								
								Question qDown = new Question(Integer.valueOf(splitLine[0]), splitLine[2], splitLine[1]);
								downQuestions.add(qDown);
								//System.out.println("qdown: " + qDown.question);
							}
							line = br.readLine();
						}
					}
					
				}
				
				if (downCount != 1 && acrossCount != 1) {
					continue;
				}
				
				
				for (Question aQ : acrossQuestions) {
					for (Question dQ : downQuestions) {
						if (aQ.number == dQ.number) {
							if (aQ.answer.charAt(0) != dQ.answer.charAt(0)) {
								throw new IOException();
							}
						}
					}
				}
				
				return file;
				
			} catch (IOException e) {
				//e.printStackTrace();
				//System.out.println(e.getMessage());
				continue;
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void getBoardSize() {
		
		downL = 0;
		acrossL = 0;
		
		for (Question question : acrossQuestions) {
			acrossL += question.answer.length();
		}
		
		for (Question question : downQuestions) {
			downL += question.answer.length();
		}
		
		downL = downL*2;
		acrossL = acrossL*2;
		
	}
	
	

}
