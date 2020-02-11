package com.example.chessapp;
import java.util.ArrayList;

public class ActiveGameModel {

    private ArrayList<Move> moves;
    private Board board;
    private int numMove;


    public ActiveGameModel(){
        moves = new ArrayList<>();
        board = new Board();
        Move init = new Move();
        init.setFen(board.generateFen());
        moves.add(init);
        numMove = 0;
    }




}
