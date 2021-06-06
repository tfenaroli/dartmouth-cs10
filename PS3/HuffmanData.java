/**
 * stores character and frequency
 @author Thomas Fenaroli, Dartmouth CS 10, Spring 2021
 @author Adam Budin, Dartmouth CS 10, Spring 2021
 */
public class HuffmanData {
    private char character;
    private int frequency;

    /**
     * constructor
     * @param character     character
     * @param frequency     frequency
     */
    public HuffmanData(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    /**
     * gets character
     * @return  character
     */
    public char getCharacter() {
        return character;
    }

    /**
     * gets frequency
     * @return  frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * string representation
     * @return  string
     */
    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
