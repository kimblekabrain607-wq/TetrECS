package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Keeps track of current game piece
     */
    protected GamePiece currentPiece;

    /**
     * Keep track of the following piece the player can play
     */
    protected GamePiece followingPiece;

    /**
     * Bindable property for the score.
     */
    private static IntegerProperty scoreProperty = new SimpleIntegerProperty(0);

    /**
     * Bindable property for the level.
     */
    private static IntegerProperty levelProperty = new SimpleIntegerProperty(0);

    /**
     * Bindable property for the lives.
     */
    private static IntegerProperty livesProperty = new SimpleIntegerProperty(3);

    /**
     * Bindable property for the multiplier.
     */
    private static IntegerProperty multiplierProperty = new SimpleIntegerProperty(1);

    /**
     * A NextPieceListener field to keep track of when the next piece changes.
     */
    private NextPieceListener nextPieceListener;

    /**
     * A GameLoopListner field to update the UI with the timer length.
     */
    private GameLoopListener gameLoopListener;

    /**
     * A LineClearedListener field to keep track of when a line is cleared.
     */
    private LineClearedListener lineClearedListener;

    //The Timer
    private Timer gameTimer;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");

        //Spawn new gamepiece and set it as current piece
        currentPiece = spawnPiece();

        //Spawn new game piece for following piece
        followingPiece = spawnPiece();

        //Listen to the current piece and following piece
        nextPieceListener.nextPiece(currentPiece, followingPiece);

        //Add a timer
        gameTimer = new Timer();
        resetTimer();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public Boolean blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if(grid.canPlayPiece(currentPiece, x, y)){
            //Put current piece on the grid with the centre on the block clicked
            grid.playPiece(currentPiece, x, y);
            //Initialise and set following piece
            Set<GameBlockCoordinate> coordinates = afterPiece();
            //Reset timer
            resetTimer();
            //Listen to the lines being cleared
            lineClearedListener.lineCleared(coordinates);
            //Return true if the piece is played
            return true;
        }else{
            return false;
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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
     * Create a new random piece by calling GamePiece.createPiece().
     * @return GamePiece 
     */
    public GamePiece spawnPiece(){
        Random rand = new Random();
        int num1 = rand.nextInt(15);
        GamePiece currentPiece = GamePiece.createPiece(num1);
        return currentPiece;
    }

    /**
     * Replace current piece with the following piece and update the following piece
     */
    public void nextPiece(){
        currentPiece = GamePiece.createPiece(followingPiece.getValue() - 1);
        followingPiece = spawnPiece();
        System.out.println(currentPiece.toString());
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Swap current piece and following piece.
     */
    public void swapCurrentPiece(){
        GamePiece tempPiece = currentPiece;
        currentPiece = followingPiece;
        followingPiece = tempPiece;
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Remove any full vertical/horizontal lines that were made during a play
     */
    public Set<GameBlockCoordinate> afterPiece(){
        int[] columns = new int[5];
        int[] rowsNum = new int[5];
        Set<GameBlockCoordinate> coordinates = new HashSet<GameBlockCoordinate>();
        // Loops through columns and increments if there is an empty block in a column
        for(int i = 0; i<cols; i++){
            for(int j = 0; j<rows; j++){
                if(grid.get(i, j) == 0){
                    columns[i]++;
                }
            }
        }

        // Loops through rows and increments if there is an empty block in a row
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                if(grid.get(j, i) == 0){
                    rowsNum[i]++;
                }
            }
        }
        //Count number of complete column lines
        int linesCol = 0;
        // Loop through array of columns, if column has no empty blocks then loop through rows in that column and set value of all blocks to 0
        for(int i = 0; i < columns.length; i++){
            if(columns[i] == 0){
                linesCol++;
                for(int r = 0; r<rows; r++){
                    grid.set(i, r, 0);
                    coordinates.add(new GameBlockCoordinate(i, r));
                }
            }
        }
        //Count number of complete row lines
        int linesRow = 0;
        // Loop through array of rows, if row has no empty blocks then loop through columns in that row and set value of all blocks to 0
        for(int i = 0; i < rowsNum.length; i++){
            if(rowsNum[i] == 0){
                linesRow++;
                for(int c = 0; c<cols; c++){
                    grid.set(c, i, 0);
                    coordinates.add(new GameBlockCoordinate(c, i));
                }
            }
        }
        logger.info("Lines cleared: " + linesCol);
        logger.info("Rows cleared: " + linesRow);
        int numBlocks = (linesCol * 5) + (linesRow * (5-linesCol));
        logger.info("Number of blocks cleared: "+numBlocks);
        int totalLines = linesCol + linesRow;
        logger.info("Total lines cleared: " + totalLines);
        if(totalLines > 0){
            score(totalLines, numBlocks);
            logger.info("New Score: " + getScoreProperty());
        }
        nextPiece();
        //Increase multiplier if more than one line cleared, resets to 1 if no lines cleared
        if(linesCol > 0 || linesRow > 0){
            setMultiplierProperty(multiplierProperty.get() + 1);
        } else{
            setMultiplierProperty(1);
        }
        //Increase level as score increases
        if(getScoreProperty() > 0){
            int levelNum = (int)Math.floor(getScoreProperty()/ 1000);
            setLevelProperty(levelNum);
        }
        return coordinates;
    }

    /**
     * Rotate the current game piece right
     * @param piece
     */
    public void rotateCurrentPieceRight(){
        currentPiece.rotate(1);
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Rotate the current game piece left
     * @param piece
     */
    public void rotateCurrentPieceLeft(){
        currentPiece.rotate(3);
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Takes the number of @param lines and @param blocks and adds a score.
     */
    public void score(int lines, int blocks){
        setScoreProperty(scoreProperty.get() + (lines * blocks * 10 * multiplierProperty.get()));
    }

    /**
     * Method to get the timer Delay 
     */
    public int getTimerDelay(){
        int timer = 12000 - (500 * getLevelProperty());
        if(timer < 2500){
            return 2500;
        }else{
            return timer;
        }
    }

    /**
     * Method for the Timer to call and reset the timer.
     */
    public void gameLoop(){
        if(getLivesProperty() > 0){
            logger.info("The timer ran out and you lost a life.");
            setLivesProperty(getLivesProperty() - 1);
            nextPiece();
            setMultiplierProperty(1);
            resetTimer();
            logger.info("New timer is " + getTimerDelay());
        }else{
            logger.info("Game has ended, all lives were lost.");
            
        }
    }

    /**
     * Method to reset timer
     */
    public void resetTimer(){
        //Create new task for timer to run
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                gameLoop();
            }
        };
        //Delete old timer
        if(gameTimer != null){
            gameTimer.cancel();
        }
        //Make and run new timer
        gameTimer = new Timer();
        gameTimer.schedule(task, getTimerDelay());
        gameLoopListener.gameLoop(getTimerDelay());
        logger.info("Timer is " + getTimerDelay());
    }

    /**
     * A method to set the NextPieceListener.
     */
    public void setNextPieceListener(NextPieceListener listener){
        this.nextPieceListener = listener;
    }

    /**
     * A method to set the GameLoopListener
     */
    public void setGameLoopListener(GameLoopListener listener){
        this.gameLoopListener = listener;
    }

    /**
     * A method to set the lineClearedListener
     */
    public void setLineClearedListener(LineClearedListener listener){
        this.lineClearedListener = listener;
    }

    /**
     * Accessor methods for Score Property
     * @return IntegerProperty
     */
    public static IntegerProperty scoreProperty(){
        return scoreProperty;
    }

    /**
     * Setter method for Score Property
     * @param score
     */
    public static void setScoreProperty(Integer score){
        scoreProperty().set(score);
    }

    /**
     * Getter method for Score Property
     * @return Integer
     */
    public static Integer getScoreProperty(){
        return scoreProperty().get();
    }

    /**
     * Accessor method for Level Property
     * @return IntegerProperty
     */
    public static IntegerProperty levelProperty(){
        return levelProperty;
    }

    /**
     * Setter method for level Property
     * @param level
     */
    public static void setLevelProperty(Integer level){
        levelProperty().set(level);
    }

    /**
     * Getter method for level property
     * @return Integer
     */
    public static Integer getLevelProperty(){
        return levelProperty().get();
    }

    /**
     * Accessor method for Lives Property
     * @return Integer Property
     */
    public static IntegerProperty livesProperty(){
        return livesProperty;
    }

    /**
     * Setter method for lives property
     * @param lives
     */
    public static void setLivesProperty(Integer lives){
        livesProperty().set(lives);
    }

    /**
     * Getter method for Lives Property
     * @return Integer
     */
    public static Integer getLivesProperty(){
        return livesProperty().get();
    }

    /**
     * Accessor method for Multiplier Property
     * @return IntegerProperty
     */
    public static IntegerProperty multiplierProperty(){
        return multiplierProperty;
    }

    /**
     * Setter method for Multiplier Property
     * @param multi
     */
    public static void setMultiplierProperty(Integer multi){
        multiplierProperty().set(multi);
    }

    /**
     * Getter method for Multiplier Property
     * @return Integer
     */
    public static Integer getMultiplierProperty(){
        return multiplierProperty().get();
    }
}
