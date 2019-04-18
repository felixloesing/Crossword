
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
	
	public static boolean checkWord(int x, int y, String s, boolean across, char[][] board) {
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
}
