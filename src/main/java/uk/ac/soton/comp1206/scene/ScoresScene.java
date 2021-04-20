package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;

public class ScoresScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    /**
     * Global variable to store the final game state of the game.
     */
    private Game game;

    /**
     * SimpleListProperty to hold the local scores.
     */
    private SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList());

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        logger.info("Creating Scores Scene");
    }

    @Override
    public void initialise() {
        
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("instructions-background");
        root.getChildren().add(scoresPane);

        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        //Add a title image
        var image = new ImageView(new Image(this.getClass().getResource("iamges/TetrECS.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(64);
        mainPane.setTop(image);

        //Bind the localScores to the scoresList in the Scoreslist class
        ScoresList scoresList = new ScoresList();
        scoresList.bind(localScores);
    }

    @Override
    public void keyPressed(KeyCode key) {
        switch(key){
            case ESCAPE -> {
                logger.info("Returning to Menu");
                gameWindow.startMenu();
            }
        }
        
    }
    
}
