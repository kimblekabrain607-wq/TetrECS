package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    /**
     * Build the Instructions layout
     */
    public void build(){
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("instructions-background");
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        //Add Title
        var title = new Text("Instructions");
        title.getStyleClass().add("title");
        title.setStyle("-fx-font-size: 25;");

        //Add text
        var instructionsText = new Text();
        instructionsText.setText("TetrECS is a fast-paced gravity-free block placement game, where you must survive by clearing rows through careful placement of the\nupcoming blocks before the time runs out. Lose all 3 lives and you're destroyed!");
        instructionsText.setTextAlignment(TextAlignment.CENTER);
        instructionsText.getStyleClass().add("instructionsText");

        //Add instructions image to centre
        var image = new Image(InstructionsScene.class.getResource("/images/Instructions.png").toExternalForm());
        var imageView = new ImageView(image);
        imageView.setFitWidth(530);
        imageView.setPreserveRatio(true);

        //Create Grid Pieces title
        var gridTitle = new Text("Game Pieces");
        gridTitle.getStyleClass().add("title");
        gridTitle.setStyle("-fx-font-size: 15;");

        //Create Vbox for Text
        var Vbox = new VBox();
        Vbox.getChildren().addAll(title, instructionsText, imageView, gridTitle);
        Vbox.setAlignment(Pos.CENTER);
        mainPane.setTop(Vbox);
        mainPane.setAlignment(Vbox, Pos.CENTER);

        //Create Grid pane of piece boards to show pieces
        var gridPieces = new GridPane();
        var pieceB1 = new PieceBoard(50, 50, false);
        pieceB1.displayPiece(GamePiece.createPiece(0));
        var pieceB2 = new PieceBoard(50, 50, false);
        pieceB2.displayPiece(GamePiece.createPiece(1));
        var pieceB3 = new PieceBoard(50, 50, false);
        pieceB3.displayPiece(GamePiece.createPiece(2));
        var pieceB4 = new PieceBoard(50, 50, false);
        pieceB4.displayPiece(GamePiece.createPiece(3));
        var pieceB5 = new PieceBoard(50, 50, false);
        pieceB5.displayPiece(GamePiece.createPiece(4));
        var pieceB6 = new PieceBoard(50, 50, false);
        pieceB6.displayPiece(GamePiece.createPiece(5));
        var pieceB7 = new PieceBoard(50, 50, false);
        pieceB7.displayPiece(GamePiece.createPiece(6));
        var pieceB8 = new PieceBoard(50, 50, false);
        pieceB8.displayPiece(GamePiece.createPiece(7));
        var pieceB9 = new PieceBoard(50, 50, false);
        pieceB9.displayPiece(GamePiece.createPiece(8));
        var pieceB10 = new PieceBoard(50, 50, false);
        pieceB10.displayPiece(GamePiece.createPiece(9));
        var pieceB11 = new PieceBoard(50, 50, false);
        pieceB11.displayPiece(GamePiece.createPiece(10));
        var pieceB12 = new PieceBoard(50, 50, false);
        pieceB12.displayPiece(GamePiece.createPiece(11));
        var pieceB13 = new PieceBoard(50, 50, false);
        pieceB13.displayPiece(GamePiece.createPiece(12));
        var pieceB14 = new PieceBoard(50, 50, false);
        pieceB14.displayPiece(GamePiece.createPiece(13));
        var pieceB15 = new PieceBoard(50, 50, false);
        pieceB15.displayPiece(GamePiece.createPiece(14));
        gridPieces.add(pieceB1, 0, 0);
        gridPieces.add(pieceB2, 0, 1);
        gridPieces.add(pieceB3, 0, 2);
        gridPieces.add(pieceB4, 1, 0);
        gridPieces.add(pieceB5, 1, 1);
        gridPieces.add(pieceB6, 1, 2);
        gridPieces.add(pieceB7, 2, 0);
        gridPieces.add(pieceB8, 2, 1);
        gridPieces.add(pieceB9, 2, 2);
        gridPieces.add(pieceB10, 3, 0);
        gridPieces.add(pieceB11, 3, 1);
        gridPieces.add(pieceB12, 3, 2);
        gridPieces.add(pieceB13, 4, 0);
        gridPieces.add(pieceB14, 4, 1);
        gridPieces.add(pieceB15, 4, 2);
        mainPane.setCenter(gridPieces);
        gridPieces.setAlignment(Pos.CENTER);
        gridPieces.setHgap(20);
        gridPieces.setVgap(20);
        mainPane.setAlignment(gridPieces, Pos.CENTER);

        //Start menu music
        Multimedia.playMenuBackground();
    }

    /**
     * Initialise the instructions page
     */
    public void initialise(){
        //Listen for escape key pressed to return to the menu scene.
        scene.setOnKeyPressed((e) -> {
            if(e.getCode() != KeyCode.ESCAPE) return;
            logger.info("Returning to Menu");
            gameWindow.startMenu();
        });
    }

    @Override
    public void keyPressed(KeyCode key) {
        switch(key){
            case ESCAPE -> {
                logger.info("Returning to main menu");
                Multimedia.stopMusic();
                gameWindow.startMenu();
            }
        } 
    }
}
