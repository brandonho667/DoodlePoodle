import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {

	final String client;
	final boolean isClient;
	String ip;
	int port = 5454;
	boolean reset = false;
	Timer time;
	ArrayList<PrintWriter> PW;
	ArrayList<BufferedReader> BR;

	ServerSocket ss;
	Socket s;
	Queue<Line> lq = new LinkedList<Line>();
	PrintWriter pw;
	BufferedReader br;
	ArrayList<Player> userList = new ArrayList<Player>();
	Line line;
	boolean startTimer = false;
	Queue<String> messages = new LinkedList<String>();
	String connectName = "";
	int tpr;
	int currTime;
	String answer;
	String currRevealed;
	String category;

	class ServerThread extends Thread {
		Socket soc;
		BufferedReader br1;

		public ServerThread(Socket s1) {
			soc = s1;
			try {
				br1 = new BufferedReader(new InputStreamReader(
						soc.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * reads what users send in, including chatbox, drawing lines, Player
		 * updates, and connections
		 */
		public void run() {
			String msg = null;
			String[] data1;
			String connect = "Connect", disconnect = "Disconnect", chat = "Chat", li = "Line", play = "player";
			while (true) {
				try {
					msg = br1.readLine();
					data1 = msg.split(":");
					if (data1[0].equals("time")) {
						tpr = Integer.parseInt(data1[1]);
					} else if (data1[0].equals("answer")) {
						answer = data1[1];
						category = data1[2];
						currRevealed = data1[3];
					} else if (data1[0].equals("currTime")) {
						currTime = Integer.parseInt(data1[1]);
					} else if (data1[0].equals("reset")) {
						userList.get(0).setDrawing(false);
						userList.add(userList.remove(0));
						userList.get(0).setDrawing(true);
						reset = true;
					} else if (data1.length > 5 && data1[8].equals(li)) {
						System.out.println(msg);
						line = decrypt(msg);
						lq.add(line);
					} else if (data1[2].equals(chat)) {
						messages.add(data1[0] + ": " + data1[1] + "\n");
					} else if (data1[2].equals(connect)) {
						boolean b = false;
						Player p = new Player(data1[0]);
						if (userList.size() != 0) {
							for (Player p2 : userList) {
								if (p2.toString().equals(p.toString())) {
									b = true;
									break;
								}
							}
						}
						if (!b) {
							userList.add(p);
							// send(p);
						}
						messages.add(data1[0] + " " + data1[1] + "\n");
						System.out.println(userList);
						if (userList.size() >= 3) {
							userList.get(0).setDrawing(true);
							System.out.println("I'm in");
							startTimer = true;
						}
					} else if (data1[4].equals(play)) {
						int index = -1;
						if (userList.size() != 0) {
							for (int i = 0; i < userList.size(); i++) {
								if (userList.get(i).toString()
										.equals(decryptP(msg).toString())) {
									index = i;
									break;
								}
							}
						}
						if (index != -1) {
							userList.set(index, decryptP(msg));
						} else {
							userList.add(decryptP(msg));
						}
					}
					for (int i = 0; i < PW.size(); i++) {
						PW.get(i).println(msg);
						PW.get(i).flush();
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (soc == null) {
						try {
							soc.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Makes a server or a client connection object
	 * 
	 * @param client
	 *            determines whether Server object is server or client connector
	 * @param _ip
	 *            the ip address of the user
	 */
	public Server(String client, String _ip) {

		ip = _ip;
		this.client = client;
		if (client.equals("client"))
			isClient = true;
		else
			isClient = false;

		if (!isClient) {
			PW = new ArrayList<PrintWriter>();
			BR = new ArrayList<BufferedReader>();
			try {
				ss = new ServerSocket(port);
				System.out.println("you're server");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to receive connection from client");
			}

		} else {
			try {
				s = new Socket(_ip, port);
				pw = new PrintWriter(
						new OutputStreamWriter(s.getOutputStream()));
				br = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to connect client to server");
			}
		}
	}

	/**
	 * sending drawing lines to the server to be sent to other clients
	 * 
	 * @param al
	 *            Line object to send, send encrypts it into a string
	 */
	public synchronized void send(Line al) {
		String message = encrypt(al);
		if (isClient) {
			pw.println(message);
			pw.flush();
		} else {
			// System.out.println(PW.size());
			for (int i = 0; i < PW.size(); i++) {
				PW.get(i).println(message);
				PW.get(i).flush();
			}
		}
	}

	/**
	 * sending chat messages to the server to be sent to other clients
	 * 
	 * @param message
	 *            chat message to send
	 */
	public synchronized void send(String message) {
		if (isClient) {
			pw.println(message);
			pw.flush();
		} else {
			// System.out.println(PW.size());
			for (int i = 0; i < PW.size(); i++) {
				PW.get(i).println(message);
				PW.get(i).flush();
			}
		}
	}

	/**
	 * sending Player updates to the server to be sent to other clients
	 * 
	 * @param p
	 *            Player object to send, send encrypts it into a string
	 */
	public synchronized void send(Player p) {
		String message = encrypt(p);
		if (isClient) {
			pw.println(message);
			pw.flush();
		} else {
			// System.out.println(PW.size());
			for (int i = 0; i < PW.size(); i++) {
				PW.get(i).println(message);
				PW.get(i).flush();
			}
		}
	}

	/**
	 * reads what users send in, including chatbox, drawing lines, Player
	 * updates, and connections
	 */
	public void receive() {

		String msg = null;
		String[] data1;
		String connect = "Connect", disconnect = "Disconnect", chat = "Chat", li = "Line", play = "player";
		while (true) {
			try {
				msg = br.readLine();
				data1 = msg.split(":");
				if (data1[0].equals("time")) {
					tpr = Integer.parseInt(data1[1]);
				} else if (data1[0].equals("answer")) {
					answer = data1[1];
					category = data1[2];
					currRevealed = data1[3];
				} else if (data1[0].equals("currTime")) {
					currTime = Integer.parseInt(data1[1]);
				} else if (data1[0].equals("reset")) {
					userList.get(0).setDrawing(false);
					userList.add(userList.remove(0));
					userList.get(0).setDrawing(true);
					reset = true;
				} else if (data1.length > 5 && data1[8].equals(li)) {
					System.out.println(msg);
					line = decrypt(msg);
					lq.add(line);
				} else if (data1[2].equals(chat)) {
					messages.add(data1[0] + ": " + data1[1] + "\n");
				} else if (data1[2].equals(connect)) {
					boolean b = false;
					Player p = new Player(data1[0]);
					if (userList.size() != 0) {
						for (Player p2 : userList) {
							if (p2.toString().equals(p.toString())) {
								b = true;
								break;
							}
						}
					}
					if (!b) {
						userList.add(p);
						// send(p);
					}
					messages.add(data1[0] + " " + data1[1] + "\n");
					System.out.println(userList);
					if (userList.size() >= 3) {
						userList.get(0).setDrawing(true);
						System.out.println("I'm in");
						startTimer = true;
					}
				} else if (data1[4].equals(play)) {
					int index = -1;
					if (userList.size() != 0) {
						for (int i = 0; i < userList.size(); i++) {
							if (userList.get(i).toString()
									.equals(decryptP(msg).toString())) {
								index = i;
								break;
							}
						}
					}
					if (index != -1) {
						userList.set(index, decryptP(msg));
					} else {
						userList.add(decryptP(msg));
					}
				}

			} catch (IOException e) {
				System.out.println("oof");
				e.printStackTrace();
			} finally {
				if (s == null) {
					try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Client connection has dropped");
					}
				}
			}
		}
	}

	/**
	 * connects client to server
	 */
	public void run() {
		if (isClient) {
			receive();
		} else {
			while (true) {
				try {
					// System.out.println("LLLLLLLL");
					System.out.println("a");
					s = ss.accept();
					System.out.println("b");
					pw = new PrintWriter(new OutputStreamWriter(
							s.getOutputStream()));
					br = new BufferedReader(new InputStreamReader(
							s.getInputStream()));
					// System.out.println("HIII");
					PW.add(pw);
					BR.add(br);
					Thread t = new Thread(new ServerThread(s));
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}

		}
	}

	/**
	 * encrypts line object into a string
	 * 
	 * @param al
	 *            input Line object
	 * @return String representation of Line object
	 */
	public static String encrypt(Line al) { // Line object:

		// string format:
		// "x1:y1:x2:y2:thick:red:green:blue:"

		String encrypted = "";

		Line temp = al;
		encrypted += temp.getX1() + ":" + temp.getY1() + ":" + temp.getX2()
				+ ":" + temp.getY2() + ":" + temp.getThick() + ":"
				+ temp.getColor().getRed() + ":" + temp.getColor().getGreen()
				+ ":" + temp.getColor().getBlue() + ":" + "Line";
		return encrypted;
	}

	/**
	 * decrypts string into Line object
	 * 
	 * @param str
	 *            input String
	 * @return Line object representation of String
	 */
	public static Line decrypt(String str) {

		String[] data = str.split(":");
		Line line = new Line(Integer.parseInt(data[0]),
				Integer.parseInt(data[1]), Integer.parseInt(data[2]),
				Integer.parseInt(data[3]), Integer.parseInt(data[4]),
				Integer.parseInt(data[5]), Integer.parseInt(data[6]),
				Integer.parseInt(data[7]));
		return line;
	}

	/**
	 * encrypts a Player object into a string to be sent as a message to the
	 * Server
	 * 
	 * @param play
	 *            input Player object
	 * @return String representation of Player object
	 */
	public static String encrypt(Player play) {
		return play.username + ":" + play.score + ":" + play.isDrawing + ":"
				+ play.guessed + ":" + "player";
	}

	/**
	 * decrypts string into Player object
	 * 
	 * @param msg
	 *            input String
	 * @return Player object representation of String
	 */
	public static Player decryptP(String msg) {
		String[] data = msg.split(":");
		return new Player(data[0], Integer.parseInt(data[1]),
				Boolean.parseBoolean(data[2]), Boolean.parseBoolean(data[3]));
	}

	public Queue<Line> getLineQueue() {
		return lq;
	}

	/**
	 * removes first element in queue to be drawn
	 * 
	 * @return first Line object in queue
	 */
	public Line pollQ() {
		return lq.poll();
	}

	public ArrayList<Player> getPlayers() {
		return userList;
	}

	public int getTPR() {
		return tpr;
	}

	public String getAnswer() {
		return answer;
	}

	/**
	 * iterates through the players to count number of players guessed, which
	 * controls time
	 * 
	 * @return number of players that have guessed the answer
	 */
	public int numGuessed() {
		int c = 0;
		for (Player p : userList) {
			if (p.guessed) {
				c++;
			}
		}
		return c;
	}

	public int getCurrTime() {
		return currTime;
	}

	public boolean isReset() {
		return reset;
	}

	public String getCurrRevealed() {
		return currRevealed;
	}

	public String getCategory() {
		return category;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

}