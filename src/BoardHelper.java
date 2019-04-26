
public class BoardHelper {
	
	public static void placeWord(int x, int y, String s, boolean across, char[][] board) {
		if (across) {
			int p = 0;
			for (int i = x; i < s.length()+x; i++) {
				board[i][y] = s.charAt(p);
				p++;
			}
		} else {
			int p = 0;
			for (int i = y; i < s.length()+y; i++) {
				board[x][i] = s.charAt(p);
				p++;
			}
		}
	}
	
	public static boolean legal(int x, int y, String s, boolean across, char[][] board) {
		if (across) {
			for (int i = x; i < s.length()+x; i++) {
				if (!(board[i][y] == ' ')) {
					return false;
				}
			}
		} else {
			for (int i = y; i < s.length()+y; i++) {
				if (!(board[x][i] == ' ')) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void printBoard(char[][] board) {
		for (int j = 0; j < board[0].length; j++) {
			String str = "";
			for (int i = 0; i < board.length; i++) {
				if (board[i][j] == ' ') {
					str += '_' + " ";
				} else {
					str += board[i][j] + " ";
				}
			}
			System.out.println(str);
		}
	}
	
	
}
