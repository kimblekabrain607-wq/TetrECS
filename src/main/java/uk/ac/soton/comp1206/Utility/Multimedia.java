package uk.ac.soton.comp1206.Utility;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Class to handle playing audio and background music
 */
public class Multimedia{

    private static MediaPlayer audio;

    private static MediaPlayer background;

    /**
     * Method to play audio file
     * @param file
     */
    public static void playAudio(String file){
        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();

        try {
            Media play = new Media(toPlay);
            audio = new MediaPlayer(play);
            audio.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to play the Game background music on a loop.
     */
    public static void playGameBackground(){
        String toPlay = Multimedia.class.getResource("/music/game.wav").toExternalForm();

        try {
            Media play = new Media(toPlay);
            background = new MediaPlayer(play);
            background.play();
            background.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to play background music on a loop.
     */
    public static void playMenuBackground(){
        String toPlay = Multimedia.class.getResource("/music/menu.mp3").toExternalForm();

        try {
            Media play = new Media(toPlay);
            background = new MediaPlayer(play);
            background.play();
            background.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to stop the current music playing
     */
    public static void stopMusic(){
        try {
            background.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}