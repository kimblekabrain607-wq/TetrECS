package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        menuPane.getChildren().add(mainPane);

        //Game Title Image
        var title = new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        var titleView = new ImageView(title);
        titleView.setFitWidth(gameWindow.getWidth() * 0.8);
        titleView.setPreserveRatio(true);
        mainPane.setCenter(titleView);
        var sequence = new SequentialTransition(doRotateRight(titleView), doRotateLeft(titleView));
        sequence.setCycleCount(Transition.INDEFINITE);
        sequence.play();

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var button = new Button("Single Player");
        button.getStyleClass().add("menuItem");

        //Add an Instructions button that takes you to the instructions scene
        var instructionsButton = new Button("Instructions");
        instructionsButton.getStyleClass().add("menuItem");

        //Add an Exit butto that quits the game
        var exitButton = new Button("Exit");
        exitButton.getStyleClass().add("menuItem");
        
        //Make Vbox for button list and add to bottom of BorderPane
        var buttons = new VBox();
        buttons.getChildren().addAll(button, instructionsButton, exitButton);
        buttons.setAlignment(Pos.CENTER);
        mainPane.setBottom(buttons);
        

        //Bind the Single Player button action to the startGame method in the menu
        button.setOnAction(this::startGame);

        //Bind the instructions button action to the startInstructions method in the menu
        instructionsButton.setOnAction(this::startInstructions);

        //Bind the exit button to the close the game
        exitButton.setOnAction(this::exitGame);

        //Start background music playing on a loop
        Multimedia.playMenuBackground();
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event){
        Multimedia.stopMusic();
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Instructions button is pressed
     * @param event event
     */
    private void startInstructions(ActionEvent event){
        Multimedia.stopMusic();
        gameWindow.startInstructions();
    }

    /**
     * Handle when the Exit button is pressed
     * @param event event
     */
    private void exitGame(ActionEvent event){
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyCode key) {
        switch(key){
            case ESCAPE -> {
                logger.info("Shutting down game.");
                System.exit(0);
            }
        } 
    }

    /**
     * Transition method to animate the Title 
     */
    public Transition doRotateRight(ImageView imageView){
        RotateTransition rotater = new RotateTransition(new Duration(2000), imageView);
        rotater.setFromAngle(-10);
        rotater.setToAngle(10);
        return rotater;
    }

    /**
     * Transition method to animate the Title
     */
    public Transition doRotateLeft(ImageView imageView){
        RotateTransition rotater = new RotateTransition(new Duration(2000), imageView);
        rotater.setFromAngle(10);
        rotater.setToAngle(-10);
        return rotater;
    }
}
