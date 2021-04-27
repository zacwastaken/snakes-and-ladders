package model;

import exceptions.GameAlreadyWonException;

public class Game {
    private Board gameBoard;
    private Player actualWinner;
    private Player bestScores;

    public Game(){
        gameBoard=null;
        bestScores=null;
        actualWinner=null;
    }

    public void startGame(int rows,int columns,int snakes, int ladders, int players){
        gameBoard = new Board(rows,columns,snakes,ladders,players);
    }
    public void restartGame(){
        gameBoard = null;
    }
    public String move(){
        if(gameBoard != null){
            try{
                gameBoard.movePieces();
                return gameBoard.getMovementInformation();
            } catch(GameAlreadyWonException w){
                int score = gameBoard.getWinnerGP().getMovements()*gameBoard.getSize();
                int rows = gameBoard.getRows();
                int columns = gameBoard.getColumns();
                int ladders = gameBoard.getLadders();
                int snakes = gameBoard.getSnakes();
                int players = gameBoard.getPlayers();
                String characters = gameBoard.getCharacters();
                char wChar = gameBoard.getWinnerPiece();
                actualWinner = new Player("",score,rows,columns,ladders,snakes,players,characters,wChar);
                restartGame();
                return w.getMessage();
            }
        }
        else{
            return "No board created";
        }
    }

    public void createWinner(String name){
        Player newPlayer = new Player(name,actualWinner.getScore(), actualWinner.getRows(), actualWinner.getColumns(), actualWinner.getLadders(), actualWinner.getSnakes(), actualWinner.getPlayers(), actualWinner.getCharacters(), actualWinner.getWinner());

    }

    public Board getBoard() {
        return gameBoard;
    }
}