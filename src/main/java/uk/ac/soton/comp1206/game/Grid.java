package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private Logger logger = LogManager.getLogger(Grid.class);
    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Takes a @param GamePiece with given @param x and @param y of the grid and @return true or false if it can be played.
     */
    public Boolean canPlayPiece(GamePiece piece, int x, int y){
        int[][] block = piece.getBlocks();
        // Make array of 9 to check all blocks around param block
        int[] surrounding = new int[9];
        surrounding[0] = get(x-1, y-1);
        surrounding[1] = get(x-1, y);
        surrounding[2] = get(x-1, y+1);
        surrounding[3] = get(x, y-1);
        surrounding[4] = get(x, y);
        surrounding[5] = get(x, y+1);
        surrounding[6] = get(x+1, y-1);
        surrounding[7] = get(x+1, y);
        surrounding[8] = get(x+1, y+1);
        // check array against block
        int count = 0;
        int fails = 0;
        for(int[] i : block){
            for(int coord : i){
                if((coord > 0 && surrounding[count] > 0) || (coord > 0 && surrounding[count] == -1)){
                    fails++;
                    count++;
                } else{
                    count++;
                }
            }
        }
        if(fails > 0){
            logger.info("Not able to play piece");
            return false;
        } else{
            logger.info("Able to play piece");
            return true;
        }
    }

    /**
     * Takes a @param Gamepiece with a given @param x and @param y of the grid and places the piece on the grid by it's centre.
     */
    public void playPiece(GamePiece piece, int x, int y){
        if(canPlayPiece(piece, x, y)){
            int blockNum = piece.getValue();
            int[][] blocks = piece.getBlocks();
            // create array of array of x and y coordinates
            int[][] surrounding = new int[9][2];
            // Manually create array of 3*3 array of coordinates around given x and y
            surrounding[0][0] = x-1;
            surrounding[0][1] = y-1;
            surrounding[1][0] = x-1;
            surrounding[1][1] = y;
            surrounding[2][0] = x-1;
            surrounding[2][1] = y+1;
            surrounding[3][0] = x;
            surrounding[3][1] = y-1;
            surrounding[4][0] = x;
            surrounding[4][1] = y;
            surrounding[5][0] = x;
            surrounding[5][1] = y+1;
            surrounding[6][0] = x+1;
            surrounding[6][1] = y-1;
            surrounding[7][0] = x+1;
            surrounding[7][1] = y;
            surrounding[8][0] = x+1;
            surrounding[8][1] = y+1;
            logger.info(surrounding.length);
            // Make counter of steps through 3*3 space around given x and y
            int count = 0;
            // for loop of block piece, when there should be a block at that coordinate, assign it the value of the given GamePiece
            for(int[] i : blocks){
                for(int coord : i){
                    if(coord != 0){
                        logger.info(surrounding[count][0]);
                        logger.info(surrounding[count][1]);
                        set(surrounding[count][0], surrounding[count][1], blockNum);
                        count++;
                    }else{
                        count++;
                    }
                }
            }
            System.out.println("Playing piece " + piece.toString());
        }
    }
}
