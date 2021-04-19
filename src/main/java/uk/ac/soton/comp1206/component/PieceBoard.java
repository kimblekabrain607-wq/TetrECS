package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard{

    //Boolean to keep track of whether a cirle is on the piece board.
    protected boolean displayCircle;

    //Logger
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    
    public PieceBoard(double width, double height, boolean displayCircle){
        super(3, 3, width, height);
        this.displayCircle = displayCircle;
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

    /**
     * Override the inherited createBlock method
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y){
        //Create the block using the inherited method
        GameBlock block = super.createBlock(x, y);
        //Check if piece board should have a circle
        if(displayCircle && x == 1 && y == 1){
            block.showCircle();
        }
        //Override mouse entered event from GameBoard
        block.setOnMouseEntered((e) -> {});

        //Override mouse exited event from GameBoard 
        block.setOnMouseExited((e) -> {});

        return block;
    }
}
