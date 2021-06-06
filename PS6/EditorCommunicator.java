import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Thomas Fenaroli, CS10, Spring 2021
 * @author Adam Budin, CS10, Spring 2021
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		System.out.println("client sent: " + msg);
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			// TODO: YOUR CODE HERE
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("client received: " + line);
				String[] splitLine = line.split(" ");
				if (splitLine[0].equals("draw")) {
					MessageHandler.handleDraw(editor.getSketch().getIDToShape(), splitLine);
				}
				else if (splitLine[0].equals("move")) {
					MessageHandler.handleMove(editor.getSketch().getIDToShape(), splitLine, Integer.valueOf(splitLine[1]));
				}
				else if (splitLine[0].equals("recolor")) {
					MessageHandler.handleRecolor(editor.getSketch().getIDToShape(), splitLine, Integer.valueOf(splitLine[1]));
				}
				else if (splitLine[0].equals("delete")) {
					MessageHandler.handleDelete(editor.getSketch().getIDToShape(), Integer.valueOf(splitLine[1]));
				}
				editor.repaint();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}
}
