package model;

import exceptions.GameAlreadyWonException;


public class Board {
	private Node start;
	private Node matrix;
	private int rows;
	private int columns;
	private int ladders;
	private int players;
	private int snakes;
	private int size;
	private GamePiece gp;
	private GamePiece winnerGP;
	private int turn;
	private SavedNumber first;
	private String boardString="";
	private String boxesInformation="";
	private Node upper;
	private String movementInformation;
	private String characters;
	private char winnerPiece;

	public Board(int rows,int columns,int snakes, int ladders,String playersString){
		start = new Node(0,0);
		gp = null;
		choosePlayers(playersString);
		winnerGP = null;
		winnerPiece = ' ';
		movementInformation = "";
		this.rows = rows;
		this.columns = columns;
		this.ladders = ladders;
		this.snakes=snakes;
		this.players = players;
		characters = "";
		turn = 1;
		size = rows*columns;
		addToSavedNumbers(size);
		startBoard();
	}

	public Board(int rows,int columns,int snakes, int ladders, int players) {
		start = new Node(0,0);
		gp=null;
		winnerGP = null;
		winnerPiece = ' ';
		movementInformation = "";
		this.rows = rows;
		this.columns = columns;
		this.ladders = ladders;
		this.snakes=snakes;
		this.players = players;
		characters = "";
		turn = 1;
		size = rows*columns;
		addToSavedNumbers(size);
		startBoard();
		putPlayers(players);
		//System.out.println("Esto es lo que detecta como guardado");
		//System.out.println(gp.getNumber());
		//System.out.println(gp.getNext().getNumber());
		//System.out.println(gp.getNext().getNext().getNumber());
		//System.out.println(gp.getNext().getNext().getNext().getNumber());
		//System.out.println("Busquemos errores");
		//System.out.println(gpAt(3).getNumber());
		
	}

	public void choosePlayers(String playersString){
		choosePlayers(playersString,0);
	}

	public void choosePlayers(String playersString,int i){
		if(i<playersString.length()){
			GamePiece newPiece = new GamePiece(playersString.charAt(i),"",i);
			GamePiece aux = gp;
			gp = newPiece;
			gp.setNext(aux);
			putPlayers(players,i+1);
		}
	}

	public void startBoard(){
		try{
			matrix=buildTheBoard(0,0,null);
			upper = upper(matrix);
			putLadders(ladders);
			putSnakes(snakes);
			start.setNext(matrix);
		}
		catch (NullPointerException e){
			System.out.println("Lo improbable sucedio");
			first=null;
			addToSavedNumbers(size);
			startBoard();
		}
	}
	
	public Node buildTheBoard(int r, int c, Node current) {
		if (r >= rows || c >= columns) {
            return null;
        }
		
		Node aux = new Node(translator(c,r),originalTranslator(c,r));
		//System.out.println(c+" "+r);
		
		aux.setPrev(current);
		aux.setDown(current);
		aux.setNext(buildTheBoard(r, c + 1, aux));
		aux.setUp(buildTheBoard(r+1, c, aux));
		return aux;
	}

	public GamePiece gpAt(int index){
		return gpAt(index,gp);
	}

	private GamePiece gpAt(int index,GamePiece aux) {
		if (aux == null) {
			//System.out.println("EN SERIO ....");
			return null;
		}
		else{
			//System.out.println("entro aqui pero no entendio ");
			//System.out.println(aux.getNumber());
			if(aux.getNumber()==index){
				//System.out.println("funciono");
				return aux;
			}
			else{
				return gpAt(index,aux.getNext());
			}
		}
	}
	public int originalTranslator(int r,int c){
		int first = columns*(rows-c)-(columns);
		//System.out.println("first es: "+first);
		return first+r+1;
	}
	public int translator(int r,int c) {
		int first = 0;
		int change = 0;
		if(c % 2 ==0) {
			first = (columns*c);
			change = 1;
		}
		else {
			first = (columns*c)+(columns+1);
			change = -1;
		}
		return first+((r+1)*change);
	}

	public void movePieces() throws  GameAlreadyWonException{
		boardString = "";
		int aux = turnToHisBase(turn-1);
		//System.out.println(turn);
		//System.out.println("Es el turno "+aux);
		int dice = gpAt(aux).rollTheDice();
		movementInformation = "Player "+gpAt(aux).getCharacter()+" has rolled the dice and scored "+dice;

		int previousBox=gpAt(aux).getPreviousBox();
		int actualBox = gpAt(aux).getActualBox();
		if(actualBox >= size){
			winnerGP = gpAt(aux);
			winnerPiece = winnerGP.getCharacter();
			winnerGP.setNext(null);
			throw new GameAlreadyWonException();
		}
		//System.out.println("Se pone en la casilla "+actualBox);
		if(previousBox!=0){
			//System.out.println("algo se borro wtf");
			getANode(previousBox,upper).deletePiece();
		}
		if(getANode(actualBox,upper).getTypeOfBox()==1){
			useALadder(actualBox,aux);
		}
		else if(getANode(actualBox,upper).getTypeOfBox()==3){
			useASnake(actualBox,aux);
		}
		else{
			getANode(actualBox,upper).addPiece(gpAt(aux));
		}

		turn++;
	}

	private void useALadder(int actualBox,int player){
		getANode(actualBox,upper).getLadder().addPiece(gpAt(player));
	}

	private void useASnake(int actualBox,int player){
		getANode(actualBox,upper).getSnake().addPiece(gpAt(player));
	}

	private int turnToHisBase(int number){
		if(number >= players){
			return turnToHisBase(number-players);
		}
		else{
			return number;
		}
	}

	public void putLadders(int ladders) throws  NullPointerException{
		putLadders(0,ladders);
	}
	
	private void putLadders(int i,int ladders) throws  NullPointerException{
		if (i < ladders) {
			int limit = 1; //antes era columns+1
			//System.out.println("El limite es "+limit);
			if (ladders > limit) {
				limit = ladders;
			}
			int random = (int) Math.floor(Math.random() * (size - limit - columns) + 1);
			//System.out.println(random+" es el numero generado");
			int ladderStart = chooseBoxes(limit - columns, 1, random, limit);
			int aux = numberToHisBase(ladderStart);
			//System.out.println(ladderStart+" es el principio");
			//System.out.println(aux+" es el numero base");
			//System.out.println("Empieza desde: "+(ladderStart+((columns+1)-aux)));
			int random2 = (int) Math.floor(Math.random() * (size - (ladderStart + ((columns + 1) - aux))) + (ladderStart + ((columns + 1) - aux)));
			//System.out.println("Originalmente era "+random2);
			int ladderEnd = chooseBoxes(ladderStart + ((columns + 1) - aux), ladderStart + ((columns + 1) - aux), random2, 1);
			//System.out.println(ladderStart);
			//System.out.println(ladderEnd);
			getANode(ladderStart, upper).setTypeOfBox(1);
			getANode(ladderStart, upper).setBoxInformation((i+1)+"");
			getANode(ladderStart, upper).setLadder(getANode(ladderEnd, upper));
			getANode(ladderEnd, upper).setTypeOfBox(2);
			getANode(ladderEnd, upper).setBoxInformation((i+1)+"");
			putLadders(i + 1, ladders);
		}
	}

	public void putPlayers(int players) throws NullPointerException{
		putPlayers(players,players-1);
	}

	private void putPlayers(int players,int i) throws  NullPointerException{
		if(i >= 0){
			char p = (char)(i+33);
			//System.out.println("se agrega el "+i);
			GamePiece newPiece = new GamePiece(p,"",i);
			GamePiece aux = gp;
			gp = newPiece;
			gp.setNext(aux);
			putPlayers(players,i-1);
		}
		//System.out.println(gp.getNumber()+" ->");
		//System.out.println(gp.getNext().getNumber());
		//System.out.println(gp.getNext().getNext().getNumber());
		//System.out.println(gp.getNext().getNext().getNext().getNumber());
	}

	private int numberToHisBase(int number){
		if(number > columns){
			return numberToHisBase(number-(columns));
		}
		else{
			return number;
		}
	}

	public void putSnakes(int snakes){
		putSnakes(0,snakes);
	}

	private void putSnakes(int i,int snakes){
		if(i<snakes){
			int limit = columns+1;

			if(snakes>limit){
				limit = snakes;
				//System.out.println("El limite son las serpientes");
			}
			int random = (int) Math.floor(Math.random()*(size-limit-2)+limit);
			//System.out.println("El limite es "+limit);
			//System.out.println(random+" El primer random");
			int snakeHead = chooseBoxesForSnakes(limit-2,limit,random);
			//System.out.println(snakeHead+" nuevo valor");
			int aux = numberToHisBase(snakeHead);
			int random2 = (int) Math.floor(Math.random()*(snakeHead-aux)+1);
			//System.out.println("El segundo random es "+random2);
			int snakeTail = chooseBoxesForSnakes(snakeHead-aux,1,random2);
			//System.out.println(snakeTail+" nuevo valor");
			char information = (char) (i+65);
			getANode(snakeHead, upper).setTypeOfBox(3);
			getANode(snakeHead, upper).setBoxInformation(information+"");
			getANode(snakeHead, upper).setSnake(getANode(snakeTail, upper));
			getANode(snakeTail, upper).setTypeOfBox(4);
			getANode(snakeTail, upper).setBoxInformation(information+"");
			putSnakes(i+1,snakes);
		}
	}
	public int chooseBoxesForSnakes(int a,int b,int number){
		if(!cycleThroughSavedNumbers(number, first)) {
			addToSavedNumbers(number);
			return number;
		}
		else{
			//System.out.println(number+" esta ocupado");
			if(number-1<b){
				number = (int)Math.floor(Math.random()*(size-a)+b);
			}
			return chooseBoxesForSnakes(a,b,number-1);
		}
	}
	public int chooseBoxes(int a,int b,int number,int limit) {
			if(!cycleThroughSavedNumbers(number, first)) {
				addToSavedNumbers(number);
				return number;
			}
			else {
				if(number >= size-limit) {
					number = (int)Math.floor(Math.random()*(size-a)+b);
					//System.out.println("se propone "+number);
				}
				return chooseBoxes(a,b,number+1,limit);
			}	
		}
		
		
	
	public void addToSavedNumbers(int number) {
		SavedNumber newNumber = new SavedNumber(number);
		newNumber.setNext(first);
		first = newNumber;
	}
	
	public boolean cycleThroughSavedNumbers(int number,SavedNumber toFind) {
		if(toFind==null) {
			return false;
		}
		if(number == toFind.getData()) {
			//System.out.println(" Si lo encontro");
			return true;
		}
		else{
			return cycleThroughSavedNumbers(number,toFind.getNext());
		}
	}
	
	public String getBoardInformation(Boolean firstTime) {
		Node last = upper;
		toDown(last,firstTime);
		return boardString;
	}

	public Node getMatrix(){
		return matrix;
	}

	public String getBoxesInformation(){
		Node last = upper;
		seeInformation(last);
		return boxesInformation;
	}

	public boolean seeInformation(Node down) {
		if(down.getDown()==null) {
			anotherToTheRight(down);
			return true;
		}
		else {
			anotherToTheRight(down);
			return seeInformation(down.getDown());
		}
	}

	public Node getANode(int number,Node node) {
		if(node.getDown()==null) {
			return advance(node,number);
		}
		else {
			if(advance(node,number)!=null){
				//System.out.println(" entra ");
				return advance(node,number);
			}
			else {
				return getANode(number,node.getDown());
			}
			
		}
	}


	public Node advance(Node right,int number) {
		if(right.getNext()==null) {
			if(right.getPosition()==number) {
				return right;
			}
			else {
				//System.out.println("aca esta el error");
				return null;
			}
					
		}
		else {
			if(right.getPosition()==number) {
				return right;
			}
			else {
				return advance(right.getNext(),number);
			}
		}
	}

	public Node getABox(int number){
		return getABox(number,upper);
	}

	private Node getABox(int number,Node node) {
		if(node.getDown()==null) {
			return advance2(node,number);
		}
		else {
			if(advance2(node,number)!=null){
				//System.out.println(" entra ");
				return advance2(node,number);
			}
			else {
				return getABox(number,node.getDown());
			}

		}
	}

	public Node advance2(Node right,int number) {
		if(right.getNext()==null) {
			if(right.getRealPosition()==number) {
				return right;
			}
			else {
				//System.out.println("aca esta el error");
				return null;
			}

		}
		else {
			if(right.getRealPosition()==number) {
				return right;
			}
			else {
				return advance2(right.getNext(),number);
			}
		}
	}
	
	public boolean toDown(Node down,Boolean firstTime) {
		if(down.getDown()==null) {
			toTheRight(down,firstTime);
			return true;
		}
		else {
			toTheRight(down,firstTime);
			return toDown(down.getDown(),firstTime);
		}
	}
	
	public Node toTheRight(Node right,Boolean firstTime) {
		if(right.getNext()==null) {
			boardString += "[";
			if(firstTime){
				boardString += " "+right.getPosition(); //+" ";+right.getRealPosition();
			}
			boardString += " "+right.getPieceString()+" "+right.getBoxInformation()+" ]"+"\n";

					
			return right;
		}
		else {
			boardString += "[";
			if(firstTime){
				boardString += " "+right.getPosition(); //+" ";+right.getRealPosition();
			}
			boardString += " "+right.getPieceString()+" "+right.getBoxInformation()+" ]";
			return toTheRight(right.getNext(),firstTime);
		}
	}

	public Node anotherToTheRight(Node right) {
		if(right.getNext()==null) {
			if(right.getTypeOfBox()==1) {
				boxesInformation += "LADDER: "+right.getPosition()+" -> "+right.getLadder().getPosition()+"\n";
			}
			else if(right.getTypeOfBox()==3){
				boxesInformation += "SNAKE: "+right.getPosition()+" -> "+right.getSnake().getPosition()+"\n";
			}
			return right;
		}
		else {
			if(right.getTypeOfBox()==1) {
				boxesInformation += "LADDER: "+right.getPosition()+" -> "+right.getLadder().getPosition()+"\n";
			}
			else if(right.getTypeOfBox()==3){
				boxesInformation += "SNAKE: "+right.getPosition()+" -> "+right.getSnake().getPosition()+"\n";
			}
			return anotherToTheRight(right.getNext());
		}
	}
	
	public Node upper(Node up) {
		if(up.getUp()==null) {
			return up;
		}
		else {
			return upper(up.getUp());
		}
	}

	/*Getters*/

	public int getSize() {
		return size;
	}

	public int getSnakes() {
		return snakes;
	}

	public int getLadders() {
		return ladders;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public String getMovementInformation() {
		return movementInformation;
	}

	public void setMovementInformation(String movementInformation) {
		this.movementInformation = movementInformation;
	}

	public GamePiece getWinnerGP() {
		return winnerGP;
	}

	public void setWinnerGP(GamePiece winnerGP) {
		this.winnerGP = winnerGP;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public String getCharacters(){
		getCharacters(gp);
		return characters;
	}

	public void getCharacters(GamePiece g){
		if(g==null){
			characters += "";
		}
		else{
			characters+=g.getCharacter();
			getCharacters(g.getNext());
		}
	}

	public char getWinnerPiece() {
		return winnerPiece;
	}

	public void setWinnerPiece(char winnerPiece) {
		this.winnerPiece = winnerPiece;
	}
}
