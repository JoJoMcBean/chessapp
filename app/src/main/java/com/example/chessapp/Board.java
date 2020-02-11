package com.example.chessapp;

public class Board {
    private char[][] board;//rank, file
    private boolean whiteMove;
    private String castling;
    private int passantTarget;
    private int halfmove;
    private int fullmove;
    private int whiteKingPos;
    private int blackKingPos;
    private final String BASE_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public Board(){
        board = new char[8][8];
        initializeBoard();
    }

    public void initializeBoard(){
        convertFen(BASE_FEN);//TODO figure out of this is how you use res strings
    }

    public String generateFen() {
        //TODO
        String s = "";
        int counter = 0;
        for(int a = 7; a >= 0; a--){
            for(int b = 0; b <= 7; b++){
                if(board[a][b] == '-')
                    counter++;
                else {
                    if(counter > 0)
                        s = s + counter;
                    counter = 0;
                    s = s + board[a][b];
                }
            }
            if(counter > 0)
                s = s + counter;
            if(a != 0)
                s = s + "/";
            counter = 0;
        }
        if(whiteMove)
            s = s + " w ";
        else
            s = s + " b ";
        if(castling.equals(""))
            s = s + "- ";
        else
            s = s + castling + " ";
        if(passantTarget == -1)
            s = s + "-";
        else {
            switch(passantTarget%8){
                case 0: s = s + "a"; break;
                case 1: s = s + "b"; break;
                case 2: s = s + "c"; break;
                case 3: s = s + "d"; break;
                case 4: s = s + "e"; break;
                case 5: s = s + "f"; break;
                case 6: s = s + "g"; break;
                case 7: s = s + "h"; break;
            }
            s = s + (passantTarget/8+1);
        }
        s = s + " " + halfmove + " " + fullmove;
        return s;
    }

    public void convertFen(String f) {
        //TODO
        String sub = new String(f);
        String partial = sub.substring(0,sub.indexOf(" "));
        int counter = 0,a = 7, b = 0;
        char c;
        for(int index = 0; index < partial.length(); index++){
            c = partial.charAt(index);
            if(c == '/'){
                a --;
                b = 0;
            } else if(c >= '1' && c <= '8'){
                counter = (int)(c - '0');
                while(counter > 0){
                    board[a][b] = '-';
                    counter--;
                    b++;
                }
            } else {
                board[a][b] = c;
                if(c == 'K')
                    whiteKingPos = 8*a+b;
                else if (c == 'k')
                    blackKingPos = 8*a+b;
                b++;
            }
        }
        sub = sub.substring(sub.indexOf(" ")+1);
        whiteMove = sub.charAt(0) == 'w';
        sub = sub.substring(sub.indexOf(" ")+1);
        castling = sub.substring(0,sub.indexOf(" "));
        sub = sub.substring(sub.indexOf(" ")+1);
        if(sub.charAt(0) == '-')
            passantTarget = -1;
        else{
            passantTarget = 8*((int)(sub.charAt(1)-'1'))+((int)(sub.charAt(0)-'a'));
        }
        sub = sub.substring(sub.indexOf(" ")+1);
        halfmove = (int)(sub.charAt(0)-'0');
        fullmove = (int)(sub.charAt(2)-'0');
    }

    public void makeMove(int rank1, int file1, int rank2, int file2){
        //TODO
        char c = board[rank1][file1];
        String s = getLegalMoves(rank1,file1);
        if(s.charAt(8*rank2+file2) == '1'){
            passantTarget = -1;
            halfmove++;
            if(!whiteMove)
                fullmove++;
            whiteMove = !whiteMove;
            if(c == 'p' || c == 'P' || board[rank2][file2] != '-')
                halfmove = 0;
            board[rank2][file2] = c;
            board[rank1][file1] = '-';
            if((c == 'K' || c == 'k')){
                if(file2 - file1 == 2){
                    board[rank1][file1+1] = board[rank1][file1+3];
                    board[rank1][file1+3] = '-';
                } else if (file1 - file2 == 2){
                    board[rank1][file1-1] = board[rank1][file1-4];
                    board[rank1][file1-4] = '-';
                }
                if(c == 'K'){
                    whiteKingPos = 8*rank2+file2;
                    castling = castling.replaceAll("K","");
                    castling = castling.replaceAll("Q","");
                } else {
                    blackKingPos = 8*rank2+file2;
                    castling = castling.replaceAll("k","");
                    castling = castling.replaceAll("q","");
                }
            }
            if((c == 'P' || c == 'p')){
                if(rank2 - rank1 == 2)
                    passantTarget = 8*rank2+file2-8;
                else if (rank2 - rank1 == -2)
                    passantTarget = 8*rank2+file2+8;
                else if (8*rank2+file2 == passantTarget){
                    board[rank1][file2] = '-';
                } else if (rank2 == 0)
                    board[rank2][file2] = 'q';//TODO add option to promote to any piece
                else if (rank2 == 7)
                    board[rank2][file2] = 'Q';
            }
            if(c == 'R')
                if(rank1 == 0 && file1 == 0)
                    castling = castling.replaceAll("Q","");
                else if (rank1 == 0 && file1 == 7)
                    castling = castling.replaceAll("K","");
            if(c == 'r')
                if (rank1 == 7 && file1 == 0)
                    castling = castling.replaceAll("q","");
                else if (rank1 == 7 && file1 == 7)
                    castling = castling.replaceAll("k","");

        }

    }

    public String getLegalMoves(int rank, int file){//8*rank+file
        char[] s = new char[65];
        s[64] = '\0';
        for (int x = 0; x < 64; x ++)
            s[x] = '0';
        int r = color(rank, file);
        char c = board[rank][file];
        int color,a,b;
        if(r != 4 && (whiteMove && r == 1 || !whiteMove && r == -1)){
            //black pawn
            if( c == 'p'){
                if(color((rank-1),(file+1)) == 1 || passantTarget == 8*rank-7+file)
                    s[8*rank-7+file] = '1';
                if(color((rank-1),(file-1)) == 1 || passantTarget == 8*rank+file-9)
                    s[8*rank+file-9] = '1';
                if(color((rank-1),file) == 0){
                    s[8*rank-8+file] = '1';
                    if((color((rank-2),file) == 0) && rank == 6)
                        s[8*rank+file-16] = '1';
                }
            }
            //white pawn
            if(c == 'P'){
                if(color((rank+1),(file+1)) == 1 || passantTarget == 8*rank+9+file)
                    s[8*rank+9+file] = '1';
                if(color((rank+1),(file-1)) == 1 || passantTarget == 8*rank+file+7)
                    s[8*rank+file+7] = '1';
                if(color((rank+1),file) == 0){
                    s[8*rank+8+file] = '1';
                    if((color((rank+2),file) == 0) && rank == 1)
                        s[8*rank+file+16] = '1';
                }
            }
            //king
            if(c == 'K' || c == 'k'){
                for(a = rank - 1; a <= rank + 1; a++){
                    for(b = file - 1; b <= file + 1; b++){
                        color = color(a,b);
                        if(!(a == rank && b == file) && color != 4 && !isTargeted(!whiteMove, a,b) && color != r)
                            s[8*a+b] = '1';
                    }
                }
                if((whiteMove && castling.contains("K") || !whiteMove && castling.contains("k")) && s[8*rank+file+1] == '1' && !isTargeted(!whiteMove,rank,file+2) && board[rank][file+1] == '-' && board[rank][file+2] == '-')
                    s[8*rank+file+2] = '1';
                if((whiteMove && castling.contains("Q") || !whiteMove && castling.contains("q")) && s[8*rank+file-1] == '1' && !isTargeted(!whiteMove,rank,file-2) && board[rank][file-1] == '-' && board[rank][file-2] == '-' && board[rank][file-3] == '-')
                    s[8*rank+file-2] = '1';
                }


            //TODO knight
            if(c == 'n' || c == 'N'){
                for(a = rank - 2; a <= rank + 2; a++){
                    for(b = file - 2; b <= file + 2; b++){
                        color = color(a,b);
                        if(color != 4 && color != r && (rank-a)*(rank-a)+(file-b)*(file-b) == 5)
                            s[8*a+b] = '1';
                    }
                }
            }


            //TODO rook
            if(c == 'r' || c == 'R' || c == 'q' || c == 'Q'){
                a = rank + 1;
                b = file;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a + 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }
                a = rank - 1;
                b = file;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a - 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

                a = rank;
                b = file + 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        b = b + 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

                a = rank;
                b = file - 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        b = b - 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

            }

            //TODO bishop
            if(c == 'b' || c == 'B' || c == 'q' || c == 'Q'){
                a = rank + 1;
                b = file + 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a + 1;
                        b = b + 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }
                a = rank - 1;
                b = file + 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a - 1;
                        b = b + 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

                a = rank + 1;
                b = file - 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a + 1;
                        b = b - 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

                a = rank - 1;
                b = file - 1;
                color = color(a,b);
                while(color != 4){
                    if(color == 0){
                        s[8*a+b] = '1';
                        a = a - 1;
                        b = b - 1;
                        color = color(a,b);
                    } else if(color == r){
                        break;
                    } else {
                        s[8*a+b] = '1';
                        break;
                    }
                }

            }






        }

        //pin handler
        board[rank][file] = '-';
        boolean pin = whiteMove && isTargeted(false,whiteKingPos/8,whiteKingPos%8) || !whiteMove && isTargeted(true,blackKingPos/8,blackKingPos%8);
        board[rank][file] = c;
        if(pin){
            char dst = '-';
            int dr = 0, df = 0;
            for(int x = 0; x < 64; x++){
                if(s[x] == '1'){
                    dr = x/8;
                    df = x%8;
                    dst = board[dr][df];
                    board[rank][file] = '-';
                    board[dr][df] = c;
                    if(whiteMove && isTargeted(false,whiteKingPos/8,whiteKingPos%8) || !whiteMove && isTargeted(true,blackKingPos/8,blackKingPos%8)){
                        s[x] = '0';
                    }
                    board[rank][file] = c;
                    board[dr][df] = dst;
                }
            }
        }

        return new String(s);
    }

    public boolean isTargeted(boolean byWhite, int rank, int file){
        int x, y, j;
        char c;
        for (int i = 0; i < 25; i++){
            x = rank + (i / 5) - 2;
            y = file + (i % 5) - 2;
            j = color(x,y);
            if(j != 4){
                c = board[x][y];
                if (((rank - x)*(rank - x)+(file - y)*(file - y) == 5 )&&((c == 'n' && !byWhite)||(c == 'N' && byWhite))){
                    return true;
                }
            }
        }


        x = rank + 1;
        y = file;
        if(color(x,y) != 4 && (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'R' || c == 'Q') && byWhite || (c == 'r' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x + 1;
        }

        x = rank - 1;
        y = file;
        if(color(x,y) != 4 && (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'R' || c == 'Q') && byWhite || (c == 'r' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x - 1;
        }

        x = rank;
        y = file + 1;
        if(color(x,y) != 4 && (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'R' || c == 'Q') && byWhite || (c == 'r' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            y = y + 1;
        }

        x = rank;
        y = file - 1;
        if(color(x,y) != 4 && (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'R' || c == 'Q') && byWhite || (c == 'r' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            y = y - 1;
        }

        x = rank + 1;
        y = file + 1;
        if(color(x,y) != 4 && ((!byWhite && board[x][y] == 'p') || (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite)))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'B' || c == 'Q') && byWhite || (c == 'b' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x + 1;
            y = y + 1;
        }

        x = rank + 1;
        y = file - 1;
        if(color(x,y) != 4 && ((!byWhite && board[x][y] == 'p') || (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite)))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'B' || c == 'Q') && byWhite || (c == 'b' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x + 1;
            y = y - 1;
        }

        x = rank - 1;
        y = file + 1;
        if(color(x,y) != 4 && ((byWhite && board[x][y] == 'P') || (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite)))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'B' || c == 'Q') && byWhite || (c == 'b' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x - 1;
            y = y + 1;
        }

        x = rank - 1;
        y = file - 1;
        if(color(x,y) != 4 && ((byWhite && board[x][y] == 'P') || (board[x][y] == 'K' && byWhite || board[x][y] == 'k' && !byWhite)))
            return true;
        while(color(x,y) != 4){
            c = board[x][y];
            if(c != '-'){
                if ((c == 'B' || c == 'Q') && byWhite || (c == 'b' || c == 'q') && !byWhite)
                    return true;
                break;
            }
            x = x - 1;
            y = y - 1;
        }


        return false;
    }

    public int color(int rank, int file) {
        if(rank < 0 || rank > 7 || file < 0 || file > 7)
            return 4;
        char c = board[rank][file];
        if (c == '-')
            return 0;
        if(c <= 'z' && c >= 'a')
            return -1;
        if(c <= 'Z' && c >= 'A')
            return 1;
        return 4;
    }

    public char get(int rank, int file){
        return board[rank][file];
    }

    public boolean isWhiteMove() {
        return whiteMove;
    }

    public int getHalfmove() {
        return halfmove;
    }

    public int getFullmove() {
        return fullmove;
    }
}
