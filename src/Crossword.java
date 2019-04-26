
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class Crossword {

	public ObjectFile readInFile(ArrayList<HorizontalW>horiWords, ArrayList<VerticalW>vertWords, File file){
		String s = "";
		Scanner scanner = null;

		int height = 0;
		int width = 0;

		try {
			scanner = new Scanner(file);

			Boolean down = false;
			Boolean across = false;
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				s += line;
				s +=  "\n";

				
				if(line.equals("DOWN")){
					across = false;
					down = true;
				} else if(line.equals("ACROSS")){
					across = true;
					down = false;
				}
				else {
					String lineformat[] = line.split("\\|", 4);
					if(across){
						horiWords.add(new HorizontalW(Integer.parseInt(lineformat[0]), lineformat[1].length(), lineformat[1].toUpperCase(), lineformat[2]));
						width += lineformat[1].length();
					}
					else if(down){
						vertWords.add(new VerticalW(Integer.parseInt(lineformat[0]), lineformat[1].length(), lineformat[1].toUpperCase(), lineformat[2]));
						height += lineformat[1].length();
					}
				}			
			}	
		} catch(FileNotFoundException f){
			f.printStackTrace();
		} finally {
			scanner.close();
		}

		for(HorizontalW hword: horiWords){
			for(VerticalW vword: vertWords) {
				if(vword.num == hword.num) {
					vword.setPair(hword);
					hword.setPair(vword);
					break;
				}
			}
		}
		return new ObjectFile(2*(width+horiWords.get(0).len), 2*(height+vertWords.get(0).len), s);
	}
	
	public boolean allowedPlacementH(int start, int horz, int vert, HorizontalW hword, char[][] board){ //HORZ
		String word = hword.word;
		if(board[horz-start-1][vert] != ' '){
			return false;
		}
		if(board[horz-start+hword.len][vert] != ' '){
			return false;
		}
		for(int i=0; i<hword.len; i++){
			if(board[i+horz-start][vert+1] != ' ' || board[i+horz-start][vert-1] != ' '){
				if(horz-start+i != horz){
					return false;
				}
			}
			if(board[i+horz-start][vert] != ' ' && board[i+horz-start][vert] != word.charAt(i)){ //start represents # chars before to begin placing
				return false;
			}
		}
		return true;
	}

	public boolean allowedPlacementV(int start, int horz, int vert, VerticalW vword, char[][] board){ //VERT
		String word = vword.word;
		if(board[horz][vert-start-1] != ' '){
			return false;
		}
		if(board[horz][vert-start+vword.len] != ' '){
			return false;
		}
		for(int j=0; j<vword.len; j++){
			if(board[horz-1][j+vert-start] != ' ' || board[horz+1][j+vert-start] != ' '){
				if(vert-start+j != vert){
					return false;
				}
			}
			if(board[horz][j+vert-start] != ' ' && board[horz][j+vert-start] != word.charAt(j)){ //start represents # chars before to begin placing
				return false;
			}
		}
		return true;
	}
	
	public void placeHorizontal(HorizontalW hw, char [][] board) {
		try {
			int p = 0;
			for (int i = hw.x; i < hw.word.length()+hw.x; i++) {
				board[i][hw.y] = hw.word.charAt(p);
				p++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void placeVertical(VerticalW vw, char [][] board) {
		try {
			int p = 0;
			for (int i = vw.y; i < vw.word.length()+vw.y; i++) {
				board[vw.x][i] = vw.word.charAt(p);
				p++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public char[][] startGame(ArrayList<HorizontalW> hor, ArrayList<VerticalW> vert, char[][] boardst, File f){
		ArrayList<HorizontalW>hori = new ArrayList<HorizontalW>();
		ArrayList<VerticalW>verti = new ArrayList<VerticalW>();

		ArrayList<HorizontalW>horiWPlaced = new ArrayList<HorizontalW>();
		ArrayList<VerticalW>vertWPlaced = new ArrayList<VerticalW>();
		
		ObjectFile fileobject = readInFile(hori, verti, f);
		int width = fileobject.x;
		int height = fileobject.y;
		char[][] board = new char[width][height];
		
		ArrayList<Position>coordV = new ArrayList<Position>();
		ArrayList<Position>coordH = new ArrayList<Position>();
		
		//initialize board
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				board[i][j] = ' ';
			}
		}

		coordV = addWordH(0,(int)width/2,(int)height/2, hori.get(0), board);

		
		hori.get(0).x = (int)width/2;
		hori.get(0).y = (int)height/2;
		horiWPlaced.add(hori.get(0));

		//Determine if pair
		if (hori.get(0).isPair) {
			coordH = addWordV(0,(int)width/2,(int)height/2, hori.get(0).vertWord, board);
			hori.get(0).vertWord.y = (int)height/2;
			hori.get(0).vertWord.x = (int)width/2;
			vertWPlaced.add(hori.get(0).vertWord);
			verti.remove(hori.get(0).vertWord);
		}

		hori.remove(0);
		
		solve(hori, coordH, verti, coordV, board);
		vertWPlaced.addAll(verti);
		horiWPlaced.addAll(hori);
		
		char[][] resizedBoard = resizeBoard(horiWPlaced, vertWPlaced);
		
		hor.addAll(horiWPlaced);
		vert.addAll(vertWPlaced);
		boardst = resizedBoard;
		return resizedBoard;
	}

	public ArrayList<Position> addWordH(int start, int horz, int vert, HorizontalW hword, char[][] board) {
		ArrayList<Position> coords = new ArrayList<Position>();
		
		for(int i=0; i<hword.len; i++){
			if(board[i+horz-start][vert] == ' '){
				int x = i+horz-start;
				int y = vert;
				char c = hword.word.charAt(i);
				
				coords.add(new Position(x,y));
				board[x][y] = c;
			}
		} 
		return coords;
	}

	public ArrayList<Position> addWordV(int start, int horz, int vert, VerticalW vword, char[][] board) {

		ArrayList<Position> coords = new ArrayList<Position>();

		for(int j=0; j<vword.len; j++) {

			if(board[horz][j+vert-start] == ' '){
				int y = j+vert-start;
				char c = vword.word.charAt(j);
				int x = horz;

				
				coords.add(new Position(x,y));
				board[x][y] = c;
			}
		} 
		return coords;
	}


	public void unplaceWord(char[][] board, ArrayList<Position>placedCoords){
		for (Position coord : placedCoords) {
			board[coord.x][coord.y] = ' ';
		}
	}

	public boolean solve(ArrayList<HorizontalW>hwords, ArrayList<Position>hcoords, ArrayList<VerticalW>vwords, ArrayList<Position>vcoords, char[][] board){
		if (hwords.size() == 0 && vwords.size() == 0){
			//printBoard(board);
			return true; //SOLUTION
		}
		//try all potential indexes 
		//PLACE HWORD
		boolean kill = false;
		for(Position index : hcoords){
			for(HorizontalW hword: hwords){
				for(int i=0; i<hword.len; i++){
					ArrayList<Position>placedIndices = new ArrayList<Position>();
					ArrayList<Position> placedIndices2 = new ArrayList<Position>();
					if(allowedPlacementH(i, index.x, index.y, hword, board)){
						//hword.setXY(index.x-i,index.y);

						hword.x = index.x-i;
						hword.y = index.y;
						//get slice of hwords, all but current
						int hindex = hwords.indexOf(hword);
						ArrayList<HorizontalW> nexthwords = new ArrayList<HorizontalW>(hwords.subList(0, hindex));
						if(hindex+1<hwords.size()){
							nexthwords.addAll(hwords.subList(hindex+1, hwords.size()));
						}
						//update vcoords
						ArrayList<Position>nextvcoords = new ArrayList<Position>();
						placedIndices = addWordH(i, index.x, index.y, hword, board); //place chars on board
						nextvcoords.addAll(vcoords);
						nextvcoords.addAll(placedIndices);
						if(!hword.isPair){ //if word does not have pair
							kill = solve(nexthwords, hcoords, vwords, nextvcoords, board);		//next step of recursion
							if(kill == true) return true;
							unplaceWord(board, placedIndices);						//remove chars from board
						}		
						else if(allowedPlacementV(0, index.x-i, index.y,hword.vertWord, board)){//PLACE CORRESPONDING VWORD as well
							VerticalW vword = hword.vertWord;
							ArrayList<Position>nexthcoords = new ArrayList<Position>();
							//place vword pair at(x-i,y) starting w/ first index of vert word
							placedIndices2 = addWordV(0, index.x-i, index.y, vword, board); //place chars on board
							//update hcoords with new vword placement
							nexthcoords.addAll(hcoords);
							nexthcoords.addAll(placedIndices2);
							int vindex = vwords.indexOf(vword);
							ArrayList<VerticalW> nextvwords = new ArrayList<VerticalW>(vwords.subList(0, vindex));
							if(vindex+1<vwords.size()){
								nextvwords.addAll(vwords.subList(vindex+1, vwords.size()));
							}
							kill = solve(nexthwords, nexthcoords, nextvwords, nextvcoords,board);
							if(kill == true) return true;
							unplaceWord(board, placedIndices);						//remove chars from board
							unplaceWord(board, placedIndices2);						//remove chars from board
						}
					}
				}
			}
		}
		//PLACE VWORD
		for(Position index : vcoords){
			for(VerticalW vword: vwords){
				for(int i=0; i<vword.len; i++){
					ArrayList<Position>placedIndices = new ArrayList<Position>();
					ArrayList<Position> placedIndices2 = new ArrayList<Position>();
					if(allowedPlacementV(i, index.x, index.y, vword, board)){
						//vword.setXY(index.x, index.y-i);
						vword.x = index.x;
						vword.y = index.y-i;
						//get slice of hwords, all but current
						int vindex = vwords.indexOf(vword);
						ArrayList<VerticalW> nextvwords = new ArrayList<VerticalW>(vwords.subList(0, vindex));
						if(vindex+1<vwords.size()){
							nextvwords.addAll(vwords.subList(vindex+1, vwords.size()));
						}
						ArrayList<Position>nexthcoords = new ArrayList<Position>();
						placedIndices = addWordV(i, index.x, index.y, vword, board); //place chars on board
						//update hcoords with new vword placement
						nexthcoords.addAll(hcoords);
						nexthcoords.addAll(placedIndices);    
						if(!vword.isPair){
							kill = solve(hwords, nexthcoords, nextvwords, vcoords, board);		//next step of recursion
							if(kill == true) return true;
							unplaceWord(board, placedIndices);						//remove chars from board
						}
						else if(allowedPlacementH(0, index.x, index.y-i,vword.hword, board)){//place corresponding hword
							HorizontalW hword = vword.hword;
							ArrayList<Position>nextvcoords = new ArrayList<Position>();
							//place hword pair at(x-i,y) starting w/ first index of hor word
							placedIndices2 = addWordH(0, index.x, index.y-i, hword, board); //place chars on board
							//update hcoords with new vword placement
							nextvcoords.addAll(vcoords);
							nextvcoords.addAll(placedIndices2);
							int hindex = hwords.indexOf(hword);
							ArrayList<HorizontalW> nexthwords = new ArrayList<HorizontalW>(hwords.subList(0, hindex));
							if(hindex+1<hwords.size()){
								nexthwords.addAll(hwords.subList(hindex+1, hwords.size()));
							}
							kill = solve(nexthwords, nexthcoords, nextvwords, nextvcoords, board);		//next step of recursion
							if(kill == true) return true;
							unplaceWord(board, placedIndices);						//remove chars from board
							unplaceWord(board, placedIndices2);						//remove chars from board
						}
					}
				}
			}
		}
		return false;
	}

	public void printBoard(char [][] board){
		for(int j=0; j<board[0].length; j++){
			for(int i=0; i<board.length; i++){
				if(board[i][j] == '^'){
					System.out.print("  ");
				}
				else if((int)board[i][j]>0 && (int)board[i][j] < 65){
					System.out.print(" " + (int) board[i][j]);
				}
				else if((int)board[i][j]>9 && (int)board[i][j] < 65){
					System.out.print((int) board[i][j]);
				}
				else{
					System.out.print(board[i][j] + " ");
				}
			}
			System.out.println();
		}
	}

	public void sendBoard(Player p, char [][] board){
		try {
			for(int j=0; j<board[0].length; j++){
				String line = "";
				for(int i=0; i<board.length; i++){
					if(board[i][j] == '^'){
						line += "  ";
					} else if ((int)board[i][j] < 65 && (int)board[i][j]>0){
						line += " " + (int) board[i][j];
					} else if ((int)board[i][j] < 65 && (int)board[i][j]>9){
							line += (int) board[i][j];
					} else {
						line += board[i][j] + " ";
					}
				}
				p.sendMessage(new Message(line, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error sending board");
		}
	}

	public char[][] resizeBoard(ArrayList<HorizontalW>HwordFinal, ArrayList<VerticalW>VwordFinal){
		int lenh = 0;
		int shiftl = 100;
		int shiftup = 100;
		int lenv = 0;
		
		for(VerticalW vw : VwordFinal){
			if(vw.y < shiftup){
				shiftup = vw.y;
			}
		}

		for(HorizontalW hw : HwordFinal){
			if(hw.x < shiftl){
				shiftl = hw.x;
			}
		}
		
		for(VerticalW vw : VwordFinal){
			vw.x = vw.x-shiftl+1;
			vw.y = vw.y-shiftup+1;
			if(vw.y + vw.len > lenv){
				lenv = vw.y + vw.len;
			}
		}
		
		for(HorizontalW hw : HwordFinal){
			hw.x = hw.x-shiftl+1; 
			hw.y = hw.y-shiftup+1;
			if(hw.x + hw.len > lenh){
				lenh = hw.x +hw.len;
			}
		}
		
		char[][] newBoard = new char[lenh][lenv];
		
		for(int i=0; i<lenh; i++){
			for(int j=0; j<lenv; j++){
				newBoard[i][j] = '^';
			}
		}
		
		for(VerticalW v : VwordFinal){
			if(!v.isPair){

				if(newBoard[v.x-1][v.y] == '^'){
					newBoard[v.x-1][v.y] = (char) v.num;
				}
				else if(newBoard[v.x][v.y-1] == '^'){
					newBoard[v.x][v.y-1] = (char) v.num;
				}

			}

			for(int i=0; i<v.len; i++){
				newBoard[v.x][v.y+i] = '_';
			}
		}
		
		for(HorizontalW hw : HwordFinal){
			if(newBoard[hw.x-1][hw.y] == '^'){
				newBoard[hw.x-1][hw.y] = (char)hw.num;
			}
			for(int i=0; i<hw.len; i++){
				newBoard[hw.x+i][hw.y] = '_';
			}
		}
		
		return newBoard;
	}

}

