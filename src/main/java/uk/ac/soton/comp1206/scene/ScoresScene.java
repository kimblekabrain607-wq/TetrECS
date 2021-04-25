package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

    /**
     * SimpleListProperty to hold the online scores.
     */
    private SimpleListProperty<Pair<String, Integer>> remoteScores = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList());

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

        //Setting root node
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        //Building and styling Stack Pane
        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("instructions-background");
        root.getChildren().add(scoresPane);

        //Adding Border Pane to the stack pane.
        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        //Add a title image
        var image = new ImageView(new Image(ScoresScene.class.getResource("/images/TetrECS.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(gameWindow.getWidth() * 0.8);

        //Adding "Game Over" title
        var title = new Text("Game Over");
        title.getStyleClass().add("bigtitle");

        //Adding a listener to the communicator
        gameWindow.getCommunicator().addListener(this::loadOnlineScores);
        gameWindow.getCommunicator().send("HISCORES");

        //Creating vbox
        var titleBox = new VBox(image,title);
        titleBox.setAlignment(Pos.CENTER);
        mainPane.setTop(titleBox);
        
        //Load Score from the file and order them
        loadScores("scores.txt");
        orderScores(localScores);

        //Bind the localScores to the scoresList in the Scoreslist class
        ScoresList scoresList = new ScoresList();
        scoresList.bind(localScores);

        //Bind the remoteScores to the scoreList in the Scoreslist class
        ScoresList onlineScores = new ScoresList();
        onlineScores.bind(remoteScores);

        //Create well done message
        var wellDone = new Text("You got a High Score!");
        wellDone.getStyleClass().add("title");

        //Create an HBox for the 2 scoreLists
        var scoreLists = new HBox();
        scoreLists.setSpacing(20);

        //Create a VBox to hold the 2 scorelists and add titles
        var localTitle = new Text("Local Scores");
        localTitle.getStyleClass().add("title");
        var onlineTitle = new Text("Online Scores");
        onlineTitle.getStyleClass().add("title");
        var localBox = new VBox();
        var onlineBox = new VBox();
        localBox.setAlignment(Pos.CENTER);
        onlineBox.setAlignment(Pos.CENTER);

        //Create Textfield
        var textField = new TextField();
    
        //Checks if the player beat a local or online score
        if(localScores.getSize() < 10 || game.getScoreProperty() > localScores.get(9).getValue() || remoteScores.getSize() < 10 || game.getScoreProperty() > remoteScores.get(9).getValue()){
            mainPane.setCenter(textField);
        }else{
            localBox.getChildren().addAll(localTitle, scoresList);
            onlineBox.getChildren().addAll(onlineTitle, onlineScores);
            scoreLists.getChildren().addAll(localBox, onlineBox);
            scoreLists.setAlignment(Pos.CENTER);
            mainPane.setCenter(scoreLists);
            mainPane.setAlignment(scoreLists, Pos.CENTER);
            scoresList.reveal();
            onlineScores.reveal();
        }

        //Display updated scores
        textField.setOnAction((e) -> {
            String name = textField.getText();
            var userScore = new Pair<String,Integer>(name, game.getScoreProperty());
            localScores.add(userScore);
            if(remoteScores.getSize() < 10 || game.getScoreProperty() > remoteScores.get(9).getValue()){
                remoteScores.add(userScore);
                writeOnlineScore(userScore);
            }
            mainPane.getChildren().remove(textField);
            orderScores(localScores);
            orderScores(remoteScores);
            localBox.getChildren().addAll(localTitle, scoresList);
            onlineBox.getChildren().addAll(onlineTitle, onlineScores);
            scoreLists.getChildren().addAll(localBox, onlineBox);
            scoreLists.setAlignment(Pos.CENTER);
            mainPane.setCenter(scoreLists);
            mainPane.setAlignment(scoreLists, Pos.CENTER);
            scoresList.reveal();
            onlineScores.reveal();
            titleBox.getChildren().add(wellDone);
            writeScores();
        });

    }

    /**
     * Method to pull the set of high scores from a file and populate an ordered list
     * @param filename
     */
    public void loadScores(String filename){
        File file = new File(filename);
        if(!file.exists()){
            try {
                Writer writer = new FileWriter(file);
                for(int i = 1000; i <= 10000; i += 1000){
                    writer.write("Oli:" + i + "\n");
                }
                writer.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            Reader reader = new FileReader(file);
            BufferedReader bf = new BufferedReader(reader);
            String line = bf.readLine();
            while(line != null){
                String[] split = line.split(":");
                localScores.add(new Pair<String,Integer>(split[0], Integer.valueOf(split[1])));
                line = bf.readLine();
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to write the scores from the local scores list to a file.
     */
    public void writeScores(){
        File file = new File("scores.txt");
        try {
            Writer writer = new FileWriter(file);
            for(Pair<String,Integer> i : localScores){
                writer.write(i.getKey() + ":" + i.getValue() + "\n");
            }
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * method to order a simple list property
     * @param list
     */
    public void orderScores(SimpleListProperty<Pair<String, Integer>> list){
        list.sort(new Comparator<Pair<String,Integer>>(){

            @Override
            public int compare(Pair<String,Integer> a, Pair<String,Integer> b){
                return b.getValue() - a.getValue();
            }
        
        });
    }

    /**
     * Method which recieves message from the communicator and writes the recieved scores to the remoteScores list
     * @param message
     */
    public void loadOnlineScores(String message){
        if(!message.startsWith("HISCORES")){ return;}
        message = message.replace("HISCORES ", "");
        String[] scores = message.split("\n");
        for(String score : scores){
            String[] parts = score.split(":");
            remoteScores.add(new Pair<String,Integer>(parts[0], Integer.parseInt(parts[1])));
        }
    }

    public void writeOnlineScore(Pair<String,Integer> score){
        //Sending a message to the communicator with the name and score of the player in the right format.
        gameWindow.getCommunicator().send("HISCORE " + score.getKey() + ":" + score.getValue().toString());
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
