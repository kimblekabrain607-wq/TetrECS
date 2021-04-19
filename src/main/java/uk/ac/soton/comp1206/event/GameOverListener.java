package uk.ac.soton.comp1206.event;

/**
 * Listener to handle when the lives run out and the game ends.
 */
public interface GameOverListener {

    /**
     * Handle the game over event
     * @param isOver Boolean
     */
    public void gameOver(Boolean isOver);
}
