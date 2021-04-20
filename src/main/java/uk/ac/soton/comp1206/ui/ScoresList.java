package uk.ac.soton.comp1206.ui;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.scene.ScoresScene;

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
        setSpacing(20);
        setPadding(new Insets(10,10,10,10));
        //getStyleClass().add("userlist");
        setAlignment(Pos.TOP_CENTER);

        //Add Title
        var title = new Text("Local Scores");
        title.getStyleClass().add("title");
        getChildren().add(title);

        //Add the top 10 scores to the VBox.
        for(int i = 0; i <= Math.min(scoresList.getSize() -1, 10); i++){
            HBox scoreEntry = new HBox();
            var name = new Text(scoresList.get(i).getKey());
            var score = new Text(scoresList.get(i).getValue().toString());
            scoreEntry.getChildren().addAll(name, score);
            getChildren().add(scoreEntry);
        }

    }

    /**
    * Bind method to bind the Scoreslist simplelistproperty to the simpleListProperty in the ScoresScene 
    */
    public void bind(ObservableValue<? extends ObservableList<Pair<String, Integer>>> input){
        scoresList.bind(input);
    }
}
