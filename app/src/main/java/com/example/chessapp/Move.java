package com.example.chessapp;

public class Move {
    private int rank1,file1,rank2,file2;
    private String fen; //board after move

    public Move (int r1, int f1, int r2, int f2){
        rank1 = r1;
        rank2 = r2;
        file1 = f1;
        file2 = f2;
        fen = "";
    }

    public Move () {
        this(-1,-1,-1,-1);
    }

    public int getRank1() {
        return rank1;
    }

    public void setRank1(int rank1) {
        this.rank1 = rank1;
    }

    public int getFile1() {
        return file1;
    }

    public void setFile1(int file1) {
        this.file1 = file1;
    }

    public int getRank2() {
        return rank2;
    }

    public void setRank2(int rank2) {
        this.rank2 = rank2;
    }

    public int getFile2() {
        return file2;
    }

    public void setFile2(int file2) {
        this.file2 = file2;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = new String(fen);
    }
}
