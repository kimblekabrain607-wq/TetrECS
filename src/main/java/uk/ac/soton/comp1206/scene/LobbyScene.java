package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    //Vbox for channels
    private final VBox channels = new VBox();

    //Timer to repeatedly send messages to the communicator
    private Timer timer;

    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        
    }

    @Override
    public void build() {
        
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        //Styling StackPane and adding to the root
        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);

        //Styling BorderPane and adding it to the stackPane
        var mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        lobbyPane.getChildren().add(mainPane);

        //Listen for messages from the gameWindow
        gameWindow.getCommunicator().addListener((message) -> {
            Platform.runLater(() -> {incomingMessages(message);});
        });

        //Create timer to repeatingly request channel lists
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                gameWindow.getCommunicator().send("LIST");
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 5000);

        //Adding Multiplayer title
        var title = new Text("Mulitplayer");
        title.getStyleClass().add("title");
        mainPane.setTop(title);
        mainPane.setAlignment(title, Pos.CENTER);

        //Add left control panel
        var vbox = new VBox(10);
        mainPane.setLeft(vbox);
        
        //Add current games title and button to host a new game.
        var currentTitle = new Text("Current Games");
        currentTitle.getStyleClass().add("title");
        var nameField = new TextField();
        //nameField.setOpacity(0);
        vbox.getChildren().addAll(currentTitle, nameField,channels);

        //Set textfield to add channel to the channel lists
        nameField.setOnAction((e) -> {
            gameWindow.getCommunicator().send("CREATE " + nameField.getText().toString());
            nameField.clear();
            nameField.setDisable(false);
        });

        //Play background music
        Multimedia.playMenuBackground();
    }

    /**
     * 
     */
    public void incomingMessages(String message) {
        //check for different messages
        if(message.startsWith("CHANNELS ")){
            logger.info("Processing Channels message");
            //Removes header
            message = message.replace("CHANNELS ", "");
            logger.info("Removing header");
            //split on each next line
            String[] channelNames = message.split("\n");
            logger.info("splitting message");
            //clear vbox and add all channels
            channels.getChildren().clear();
            logger.info("clearing vbox");
            for(String name : channelNames){
                logger.info("Adding Channels to vbox");
                var button = new Button(name);
                button.setOnAction((e) -> {
                    gameWindow.getCommunicator().send("JOIN");
                });
                channels.getChildren().add(button);
            }
        }
        if(message.startsWith("JOIN ")){
            gameWindow.getCommunicator().send("LIST");
            //Open Vbox on the right

        }
        if(message.startsWith("ERROR ")){
            message = message.replace("ERROR ", "");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.show();
        }
    }

    @Override
    public void keyPressed(KeyCode key) {
        switch(key){
            case ESCAPE -> {
                logger.info("Returning to the main menu.");
                Multimedia.stopMusic();
                timer.cancel();
                gameWindow.startMenu();
            }
        }         
    }
    
}
