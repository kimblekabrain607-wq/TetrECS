package uk.ac.soton.comp1206.scene;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    protected GameBoard board;

    /**
     * NextPieceListener to listen to new pieces inside the game.
     */
    private NextPieceListener nextPieceListener;

    /**
     * GamePiece variable to keep track of the current piece to be displayed
     */
    private GamePiece currentPiece;

    /**
     * GameLoopListener to listen to the game and update the timer UI
     */
    private GameLoopListener gameLoopListener;

    /**
     * LineClearedListener to listen to when a line of blocks are cleared.
     */
    private LineClearedListener lineClearedListener;

    /**
     * GameOverListener to listen to the whether the game is over.
     */
    private GameOverListener gameOverListener;

    /**
     * X coordinate where the piece will be played
     */
    private int x = 0;

    /**
     * Y coordinate where the piece will be played
     */
    private int y = 0;

    /**
     * Long to keep track of the current timer length
     */
    private long duration;

    /**
     * Global parallel Transition
     */
    private ParallelTransition parallelTransition;
    
    /**
    * Rectangle
    */
    private Rectangle timerRectangle;

    /**
     * timeline
     */
    Timeline timeline;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 30, 10, 10));
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        var HBox = new HBox();
        mainPane.setTop(HBox);
        HBox.setAlignment(Pos.CENTER);
        
        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Handle when the gameboard is right clicked
        board.setOnRightClicked(() -> {
            game.rotateCurrentPieceRight();
            Multimedia.playAudio("rotate.wav");
        });

        //Create Score Text and bind to score property
        var score = new Text();
        score.textProperty().bind(Game.scoreProperty().asString());
        
        //Adding score and heading to scene
        score.getStyleClass().add("score");
        var scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");
        var scoreBox = new VBox();
        scoreBox.getChildren().add(scoreText);
        scoreBox.getChildren().add(score);
        scoreBox.setAlignment(Pos.CENTER);
        HBox.getChildren().add(scoreBox);
        var title = new Text("Challenge Mode");
        title.getStyleClass().add("title");
        HBox.getChildren().add(title);

        //Create Level Text and bind to level property
        var level = new Text();
        level.textProperty().bind(Game.levelProperty().asString());
        level.getStyleClass().add("level");

        //Create Lives Text and bind to lives property
        var lives = new Text();
        lives.textProperty().bind(Game.livesProperty().asString());
        
        //Adding level text to scene
        lives.getStyleClass().add("lives");
        var livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");
        var livesBox = new VBox();
        livesBox.getChildren().add(livesText);
        livesBox.getChildren().add(lives);
        livesBox.setAlignment(Pos.CENTER);
        HBox.getChildren().add(livesBox);

        //Create Multiplier Text and bind to multiplier property
        var multiplier = new Text();
        multiplier.textProperty().bind(Game.multiplierProperty().asString());
        multiplier.getStyleClass().add("level");

        //Styling header HBox
        HBox.setSpacing(150);

        //Creating PieceBoard to show current piece
        var newPiece = new PieceBoard(100, 100, true);

        //Creating a PieceBoard to show the following piece
        var followingPieceBoard = new PieceBoard(50,50, false);

        // Add a NextPieceListener
        game.setNextPieceListener((nextPiece, followingPiece) -> {
            newPiece.displayPiece(nextPiece);
            followingPieceBoard.displayPiece(followingPiece);
        });

        //Add a LineClearedListener
        game.setLineClearedListener((coordinates) -> {
            board.fadeOut(coordinates);
        });

        //Initialise rectangle timer
        timerRectangle = new Rectangle(gameWindow.getWidth()-40, 30, Color.GREEN);

        //Add a GameLoopListener
        game.setGameLoopListener((duration) -> {
            if(parallelTransition != null){
                parallelTransition.stop();
                timeline.stop();
            }
            logger.info("Resetting timer");
            timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(timerRectangle.widthProperty(), gameWindow.getWidth()-40)),
                new KeyFrame(Duration.millis(duration), new KeyValue(timerRectangle.widthProperty(), 0))
            );
            timeline.play();
            parallelTransition = new ParallelTransition(doFill(duration));
            parallelTransition.play();
        });

        // Handle when the piece board is left clicked
        newPiece.setLeftClickedListener(() -> {
            logger.info("Rotating Piece");
            game.rotateCurrentPieceRight();
            Multimedia.playAudio("rotate.wav");
        });

        //Handle when the second piece board is left clicked
        followingPieceBoard.setLeftClickedListener(() -> {
            logger.info("Swapping pieces");
            game.swapCurrentPiece();
            Multimedia.playAudio("rotate.wav");
        });

        //Handle when the all lives are lost and the game is over.
        game.setGameOverListener((gameOver) -> {
            gameWindow.startScores(game);
        });
        
        //Adding Level, Multiplier and both piece boards to side bar
        var sideBox = new VBox();
        var levelText = new Text("Level");
        levelText.getStyleClass().add("heading");
        var multiplierText = new Text("Multiplier");
        multiplierText.getStyleClass().add("heading");
        sideBox.getChildren().addAll(levelText, level, multiplierText, multiplier, newPiece, followingPieceBoard);
        sideBox.setAlignment(Pos.CENTER);
        mainPane.setRight(sideBox);

        //Adding the timerRectangle to the bottom
        mainPane.setBottom(timerRectangle);

        //Start background music playing on a loop
        Multimedia.playGameBackground();
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clicked
     */
    private void blockClicked(GameBlock gameBlock) {
        Boolean piecePlayed = game.blockClicked(gameBlock);

        //Check if the blocked clicked method return true or false and play sounds accordingly.
        if(piecePlayed){
            Multimedia.playAudio("place.wav");
        }else{
            Multimedia.playAudio("fail.wav");
        }
    }

    /**
     * Transition method to change the colour of the rectangle
     */
    public Transition doFill(long duration){
        FillTransition fillTransition = new FillTransition(new Duration(duration), timerRectangle, Color.GREEN, Color.RED);
        return fillTransition;
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

    @Override
    public void keyPressed(KeyCode key) {
        switch(key){
            case ESCAPE -> {
                logger.info("Returning to main menu");
                Multimedia.stopMusic();
                gameWindow.startMenu();
            }

            case LEFT,A -> {
                if(x > 0){
                    board.getBlock(x, y).paint();
                    logger.info("Moving placement left by 1");
                    x = (x - 1) % game.getCols();
                    board.getBlock(x, y).paintHoverColour();
                }
            }

            case RIGHT,D -> {
                if(x < 5){
                    board.getBlock(x, y).paint();
                    logger.info("Moving placement right by 1");
                    x = (x + 1) % game.getCols();
                    board.getBlock(x, y).paintHoverColour();
                }
            }

            case DOWN,S -> {
                if(y < 5){
                    board.getBlock(x, y).paint();
                    logger.info("Moving placemnt down by 1");
                    y = (y + 1) % game.getRows();
                    board.getBlock(x, y).paintHoverColour();
                }
            }

            case UP,W -> {
                if(y > 0){
                    board.getBlock(x, y).paint();
                    logger.info("Moving placement up by 1");
                    y = (y - 1) % game.getRows();
                    board.getBlock(x, y).paintHoverColour();
                }
            }

            case ENTER,X -> {
                logger.info("Playing Piece");
                game.blockClicked(board.getBlock(x % 5, y % 5));
            }

            case SPACE,R -> {
                logger.info("Swap upcoming pieces");
                game.swapCurrentPiece();
                Multimedia.playAudio("rotate.wav");
            }

            case Q,Z,OPEN_BRACKET -> {
                logger.info("Rotating current piece left");
                game.rotateCurrentPieceLeft();
                Multimedia.playAudio("rotate.wav");
            }

            case E,C,CLOSE_BRACKET -> {
                logger.info("Rotating current piece right");
                game.rotateCurrentPieceRight();
                Multimedia.playAudio("rotate.wav");
            }
        }
    }
}
