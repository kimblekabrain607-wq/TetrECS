package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The NextPieceListener is used to display the next piece the player will use without directly connecting the UI and the Game logic.
 */
public interface NextPieceListener {
    
    /**
     * A method which takes the next @param GamePiece 
     */
    public void nextPiece(GamePiece nextPiece, GamePiece followingPiece);
}
