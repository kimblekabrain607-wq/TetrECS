package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

public class ScoresList extends VBox {
    
    //Global variable for width value
    private int width = 100;

    /**
     * SimpleListProperty to keep track of the scores to diplay.
     */
    private SimpleListProperty<Pair<String, Integer>> scoresList = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList());


    public ScoresList(){

        //Set default properties of the custom UI component
        setPrefWidth(width);
        setSpacing(5);
        setPadding(new Insets(10,10,10,10));
        //getStyleClass().add("userlist");
        setAlignment(Pos.TOP_CENTER);

        //Set initial opacity
        setOpacity(0);

        //LISTENER  
        scoresList.addListener((a, b ,c) -> {
            Platform.runLater(() -> {updateScores();});
        });
    }

    /**
    * Bind method to bind the Scoreslist simplelistproperty to the simpleListProperty in the ScoresScene 
    */
    public void bind(ObservableValue<? extends ObservableList<Pair<String, Integer>>> input){
        scoresList.bind(input);
    }

    /**
     * Clear old scores and update with new scores vbox
     */
    public void updateScores(){
        getChildren().clear();
        //Add all scores again
        for(int i = 0; i <= Math.min(scoresList.getSize() - 1, 9); i++){
            var score = new Text(scoresList.get(i).getKey() + ": " + scoresList.get(i).getValue().toString());
            score.getStyleClass().add("scorelist");
            getChildren().add(score);
        }
    }

    /**
     * Method to fade in the scoresList
     */
    public void reveal(){
        FadeTransition fadeIn = new FadeTransition(new Duration(7000),this);
        fadeIn.setFromValue(0);
        fadeIn.setFromValue(1);
        fadeIn.play();
    }
}
