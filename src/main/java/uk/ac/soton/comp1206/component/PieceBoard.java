package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.event.LeftClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard{
    
    public PieceBoard(double width, double height){
        super(3, 3, width, height);
        build();
    }

    public void displayPiece(GamePiece piece){
        int blockNum = piece.getValue();
        int[][] blocks = piece.getBlocks();
        // create array of array of x and y coordinates
        int[][] surrounding = new int[9][2];
        // Manually create array of 3*3 array of coordinates around given x and y
        surrounding[0][0] = 0;
        surrounding[0][1] = 0;
        surrounding[1][0] = 0;
        surrounding[1][1] = 1;
        surrounding[2][0] = 0;
        surrounding[2][1] = 2;
        surrounding[3][0] = 1;
        surrounding[3][1] = 0;
        surrounding[4][0] = 1;
        surrounding[4][1] = 1;
        surrounding[5][0] = 1;
        surrounding[5][1] = 2;
        surrounding[6][0] = 2;
        surrounding[6][1] = 0;
        surrounding[7][0] = 2;
        surrounding[7][1] = 1;
        surrounding[8][0] = 2;
        surrounding[8][1] = 2;
        // Make counter of steps through 3*3 space around given x and y
        int count = 0;
        // for loop of block piece, when there should be a block at that coordinate, assign it the value of the given GamePiece
        for(int[] i : blocks){
            for(int coord : i){
                if(coord != 0){
                    grid.set(surrounding[count][0], surrounding[count][1], blockNum);
                    count++;
                }else{
                    grid.set(surrounding[count][0], surrounding[count][1], 0);
                    count++;
                }
            }
        }
    }
}
