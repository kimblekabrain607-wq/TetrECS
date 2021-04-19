package uk.ac.soton.comp1206.event;

import java.util.Set;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Listener that takes a set of GameBlockCoordinates to run the fadeOut method on them
 */
public interface LineClearedListener {
    
    /**
     * Handle the lineCleared event 
     * @param Set<GameBlockCoordinate>
     */
    public void lineCleared(Set<GameBlockCoordinate> coordinates);
}
