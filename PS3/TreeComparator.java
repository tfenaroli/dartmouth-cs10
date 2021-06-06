import java.util.Comparator;

/**
 * comparator for priority queue
 @author Thomas Fenaroli, Dartmouth CS 10, Spring 2021
 @author Adam Budin, Dartmouth CS 10, Spring 2021
 */
public class TreeComparator implements Comparator<BinaryTree<HuffmanData>> {
    /**
     * compares two tree items
     * @param tree1     first tree
     * @param tree2     second tree
     * @return  1, 0, -1 depending on frequencies
     */
    @Override
    public int compare(BinaryTree<HuffmanData> tree1, BinaryTree<HuffmanData> tree2) {
        if (tree1.getData().getFrequency() > tree2.getData().getFrequency()) {return 1;}
        else if (tree1.getData().getFrequency() == tree2.getData().getFrequency()) {return 0;}
        else {return -1;}
    }
}
