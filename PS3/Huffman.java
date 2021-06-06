import java.io.*;
import java.util.*;

/**
 * Huffman encoding class
 * @author Thomas Fenaroli, Dartmouth CS 10, Spring 2021
 * @author Adam Budin, Dartmouth CS 10, Spring 2021
 */
public class Huffman {
    // establishes instance variables
    // path names
    String pathName;
    String compressedPathName;
    String decompressedPathName;

    // maps
    Map<Character, Integer> frequencyMap;
    Map<Character, String> codeMap;

    // priority queue
    Comparator<BinaryTree<HuffmanData>> compareFrequency;
    PriorityQueue<BinaryTree<HuffmanData>> treeQueue;

    // pathStack
    Stack<String> pathStack;

    // Huffman tree
    BinaryTree<HuffmanData> huffmanTree;

    /**
     * constructor
     * @param pathName  path name
     */
    public Huffman(String pathName) {
        // assigns to path names
        this.pathName = pathName;
        int index = pathName.indexOf('.');
        this.compressedPathName = pathName.substring(0, index) + "_compressed.txt";
        this.decompressedPathName = pathName.substring(0, index) + "_decompressed.txt";

        // assigns to maps
        frequencyMap = new HashMap<>();
        codeMap = new HashMap<>();

        // assigns to treeQueue
        compareFrequency = new TreeComparator();
        treeQueue = new PriorityQueue<>(compareFrequency);

        // assigns to stack
        pathStack = new Stack<>();
    }

    /**
     * creates Huffman tree using priority queue and assigns to huffmanTree
     */
    public void createTree() {
        // builds huffmanTree
        while (treeQueue.size() > 1) {
            BinaryTree<HuffmanData> tree1 = treeQueue.remove();
            BinaryTree<HuffmanData> tree2 = treeQueue.remove();

            BinaryTree<HuffmanData> root = new BinaryTree<>(new HuffmanData('-',
                    tree1.getData().getFrequency() + tree2.getData().getFrequency()), tree1, tree2);
            treeQueue.add(root);
        }
        if (!treeQueue.isEmpty()) {huffmanTree = treeQueue.remove();}
        //System.out.println(huffmanTree);
    }

    /**
     * fills priority queue with binary trees
     */
    public void fillPriorityQueue() {
        for (Map.Entry<Character,Integer> entry : frequencyMap.entrySet()) {
            BinaryTree<HuffmanData> characterTree = new BinaryTree<>(new HuffmanData(entry.getKey(), entry.getValue()));
            treeQueue.add(characterTree);
        }
    }

    /**
     * fills code map by assigning code to each character in text file and traversing binary tree one time
     * @param huffmanTree   Huffman tree being handled
     */
    public void fillCodeMap(BinaryTree<HuffmanData> huffmanTree) {
        // if huffmanTree is a leaf, fill codeMap
        if (huffmanTree.isLeaf()) {
            String code = String.join("", pathStack);
            codeMap.put(huffmanTree.getData().getCharacter(), code);
        }

        // recursively travels tree and modifies stack
        pathStack.push("0");
        if (huffmanTree.hasLeft()) {
            fillCodeMap(huffmanTree.getLeft());
        }
        pathStack.pop();

        pathStack.push("1");
        if (huffmanTree.hasRight()) {
            fillCodeMap(huffmanTree.getRight());
        }
        pathStack.pop();
    }

    /**
     * fills frequency map by reading in from text file
     * @param pathName  path name
     * Catches IOException  if input invalid
     */
    public void fillFrequencyMap(String pathName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        // while there is a character available, fill frequencyMap
        while (input.ready()) {
            char character = (char)input.read();
            if (!frequencyMap.containsKey(character)) {
                frequencyMap.put(character, 1);
            }
            else {
                frequencyMap.put(character, frequencyMap.get(character) + 1);
            }
        }
        input.close();
    }

    /**
     * compresses text file being handled
     * catches IOException  if input invalid
     */
    public void compress() throws IOException {
        // call helper methods
        fillFrequencyMap(pathName);
        fillPriorityQueue();
        createTree();

        // handles fringe case
        if (huffmanTree != null) {
            if (huffmanTree.size() == 1) {
                codeMap.put(huffmanTree.getData().getCharacter(), "0");
            }
            else {
                fillCodeMap(huffmanTree);
            }
        }

        // assign to bitwriter and reader
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);
        BufferedReader br = new BufferedReader(new FileReader(pathName));

        // writes to compressed file
        while (br.ready()) {
            char character = (char) br.read();
            String huff = codeMap.get(character);
            for (int i = 0; i < huff.length(); i++) {
                if (huff.charAt(i) == '0') {
                    bitOutput.writeBit(false);
                }
                else {
                    bitOutput.writeBit(true);
                }
            }
        }
        System.out.println("Compression successful!");
        bitOutput.close();
        br.close();
    }

    /**
     * decompressed file being handled
     * catches IOException  if input invalid
     */
    public void decompress() throws IOException {
        // assigns to bitreader and writer
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(decompressedPathName));
        BinaryTree<HuffmanData> currentNode = huffmanTree;

        // handles fringe case
        if (currentNode != null && currentNode.isLeaf()) {
            while (bitInput.hasNext()) {
                bitInput.readBit();
                bw.write(currentNode.getData().getCharacter());
            }
        }

        // uses tree to write to decompressed file
        while (bitInput.hasNext()) {
            boolean bit = bitInput.readBit();

            if (!bit) {
                currentNode = currentNode.getLeft();
            }

            else {
                currentNode = currentNode.getRight();
            }

            if (currentNode.isLeaf()) {
                bw.write(currentNode.getData().getCharacter());
                currentNode = huffmanTree;
            }
        }

        System.out.println("Decompression successful!");
        bitInput.close();
        bw.close();
    }

    /**
     * tests Huffman.java
     * @param args
     * catches IOException  if input invalid
     */
    public static void main(String args[]) {
        // creates test object
        try {
            Huffman test = new Huffman("inputs/USConstitution.txt");
            test.compress();
            test.decompress();
        }

        catch (IOException e) {
            System.err.println("IOException found");
        }
    }
}