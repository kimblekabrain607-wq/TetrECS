package uk.ac.soton.comp1206.event;

/**
 * Listener to listen to the game loop and reset the timer in the UI
 */
public interface GameLoopListener {
    
    /**
     *  Handle when the timer runs out 
     */
    public void gameLoop(long duration);
}
