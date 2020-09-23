import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Driver extends JPanel implements ActionListener, KeyListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	String color = "black";
	int width = 1280;
	int height = 720;
	int my_variable = 0; // example
	int thiccc = 5;
	ArrayList<Line> lines = new ArrayList<Line>();
	int xprev = -1;
	int yprev = -1;
	int BLACK = 0, RED = 1, GREEN = 2, BLUE = 3, CYAN = 4, MAGENTA = 5,
			YELLOW = 6, WHITE = 7;

	int currentColor = BLACK;
	int r, g, b;
	// fonts
	Font font = new Font("Comic Sans", 1, 50);
	Font font2 = new Font("Courier New", 1, 30);
	int numColors = 8;
	int offset = 60;
	int numPeople = 0;
	String ip = "";
	String side = "";
	Server s;
	String gameType;
	boolean drawing;
	boolean diff = false;
	JFrame frame;
	JPanel panel;
	// Chat variables
	String username;
	boolean isConnected = false;
	BufferedImage pencil;
	JLabel lblPlayers;
	JLabel lblUsername;
	JTextField usernameField;
	JTextField chatTextField;
	JButton connectButton;
	JButton sendButton;
	JTextArea chatTextArea;
	JTextArea playerTextArea;
	JScrollPane chatScrollPane;
	JLabel times;
	JLabel hint;
	boolean half = false;
	boolean quarter = false;

	int pSize = 50;
	int currX;
	int currY;
	int tpr = -1;
	int timecheck = -1;
	int rounds;
	Player p;
	boolean timerStarted = false;
	CountDown c;
	int turns;
	int roundNum;
	int scoreAdd = 0;
	int currTime = 0;
	String answer = "abc";
	int score1;
	int score2;
	int score3;
	boolean check1 = false;
	boolean check2 = false;
	boolean check3 = false;
	boolean guessSent = false;
	boolean sending = false;
	boolean genAnswer = false;
	String currRevealed = "";
	String category = "";
	WordList w;

	public void paint(Graphics g) {

		super.paintComponent(g);
		// bg.paint(g);
		int colorSpacing = (height - offset - 3) / numColors;

		g.setFont(font);

		g.drawString(("Not Skribbl.io"), 0, 870);
		g.setFont(font2);

		// paint sprite

		for (Line l : lines) {
			l.paint(g);
		}
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, width - offset + 52, height - offset + 52);
		g.drawRect(1, 1, width - offset + 50, height - offset + 50);
		g.drawRect(2, 2, width - offset + 48, height - offset + 48);

		g.fillRect(width - offset - 4, 0, offset - 4, height);

		// drawing rectangles for color palette
		g.setColor(Color.BLACK);
		g.fillRect(width - offset, 3 + BLACK * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.RED);
		g.fillRect(width - offset, 3 + RED * colorSpacing, 50, colorSpacing - 3);
		g.setColor(Color.GREEN);
		g.fillRect(width - offset, 3 + GREEN * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.BLUE);
		g.fillRect(width - offset, 3 + BLUE * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.CYAN);
		g.fillRect(width - offset, 3 + CYAN * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.MAGENTA);
		g.fillRect(width - offset, 3 + MAGENTA * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.YELLOW);
		g.fillRect(width - offset, 3 + YELLOW * colorSpacing, 50,
				colorSpacing - 3);
		g.setColor(Color.WHITE);
		g.fillRect(width - offset, 3 + WHITE * colorSpacing, 50,
				colorSpacing - 3);

		// white border
		g.setColor(Color.WHITE);
		g.drawRect(width - offset - 2, 1 + currentColor * colorSpacing,
				offset - 8, colorSpacing);
		g.drawRect(width - offset - 1, 2 + currentColor * colorSpacing,
				offset - 8, colorSpacing - 2);
		g.drawImage(pencil.getScaledInstance(pSize, pSize, 0), currX, currY
				- pSize - 130, null);

	}

	public void update() {
		// updates based upon whether driver belongs to server or client
		if (side.equals("server")) {
			// time, answer, time per round, and revealed letters are all
			// created and controlled by server and sent to clients
			s.send("time:" + tpr);
			// if there are more than 3 players, start game/timer
			if (s.getPlayers().size() >= 3 && !timerStarted) {
				timerStarted = true;
				turns++;
				System.out.println("hacking in progress");
				c = new CountDown(tpr);
				System.out.println("Round: " + roundNum);
			}
			// while game is running
			if (timerStarted) {
				currTime = c.getTime().getTime();
				s.send("currTime:" + currTime);
				// sending boolean implemented for send/receive delay for reset
				// between games
				if (sending) {
					if (s.numGuessed() == 0) {
						sending = false;
					}
				}
				if (!sending) {
					// The amount of time left within a turn halves for every
					// player guessed
					if (s.numGuessed() == s.getPlayers().size() - 1) {
						c.getTime().setReset(true);

					} else if (s.numGuessed() == 1 && !half) {
						c.getTime().setTime(currTime / 2);
						half = true;
					} else if (s.numGuessed() == 2 && s.getPlayers().size() > 3
							&& !quarter) {
						c.getTime().setTime(currTime / 2);
						quarter = true;
					}
				}
				// resetting variables for new round
				if (c.getTime().isReset()) {
					half = false;
					quarter = false;
					genAnswer = false;
					s.send("reset");
					c.getTime().setTime(tpr);
					sending = true;
					currRevealed = "";
					c.getTime().setReset(false);
				}
				if (!genAnswer) {
					answer = w.getRandomWord();
					category = w.getCategory(answer);
					for (int i = 0; i < answer.length(); i++) {
						if (answer.charAt(i) == ' ') {
							currRevealed += " ";
						} else {
							currRevealed += "_";
						}
					}
					genAnswer = true;
				}
				if (timecheck != currTime) {
					currRevealed = reveal(answer, currRevealed, currTime, tpr);
				}
				s.send("answer:" + answer + ":" + category + ":" + currRevealed);
				timecheck = currTime;
			}

		} else if (side.equals("client")) {
			// client receives below variables from server
			tpr = s.getTPR();
			answer = s.getAnswer();
			currTime = s.getCurrTime();
			currRevealed = s.getCurrRevealed();
			category = s.getCategory();

			// scoring is saved for each turn by scoreAdd to be added at end of
			// turn

			if (sending) {
				if (s.numGuessed() == 0) {
					sending = false;
				}
			}
			if (!sending) {
				// continuous update of players according to server userList
				for (Player p2 : s.getPlayers()) {
					if (p2.toString().equals(p.toString())) {
						p.setDrawing(p2.isDrawing());
						p.setScore(p2.getScore());
					}
				}
				// drawer's score is average of the two player scores
				if (p.isDrawing()) {
					// if drawing, hint is the answer
					hint.setText(answer + " : " + category);
					if (s.getPlayers().get(1).isGuessed() && !check1) {
						score1 = currTime * 5;
						check1 = true;
					}
					if (s.getPlayers().get(2).isGuessed() && !check2) {
						score2 = currTime * 5;
						check2 = true;
					} else if (s.getPlayers().size() > 3
							&& s.getPlayers().get(3).isGuessed() && !check3) {
						score3 = currTime * 5;
						check3 = true;
					}
				} else {
					// if guessing hint is currRevealed
					hint.setText(currRevealed + " : " + category);
					if (p.isGuessed() && !guessSent) {
						scoreAdd = currTime * 5;
						p.setScore(p.getScore() + scoreAdd);
						s.send(p);
						guessSent = true;
					}
				}
				if (s.getLineQueue().size() != 0 && !p.isDrawing) {
					System.out.println("receiving");
					if (s.getLineQueue().peek().getX1() == -1) {
						lines.clear();
						s.pollQ();
					} else {
						lines.add(s.pollQ());
					}
				}
			}
			if (s.isReset() && s.getPlayers().size() >= 3) {
				for (Player p2 : s.getPlayers()) {
					if (p2.toString().equals(p.toString())) {
						p.setDrawing(p2.isDrawing());
					}
				}
				if (s.getPlayers().get(s.getPlayers().size() - 1).toString()
						.equals(p.toString())) {
					if (s.getPlayers().size() > 3) {
						scoreAdd = (score1 + score2 + score3) / 3;
					} else {
						scoreAdd = (score1 + score2) / 2;
					}
					p.setScore(p.getScore() + scoreAdd);
				}

				p.setGuessed(false);
				s.send(p);
				clear();
				roundNum++;
				System.out.println("Round: " + roundNum);
				guessSent = false;
				check1 = false;
				check2 = false;
				check3 = false;
				sending = true;
				s.setReset(false);
			}
			if (currTime % 60 < 10) {
				times.setText(currTime / 60 + ":0" + currTime % 60);
			} else {
				times.setText(currTime / 60 + ":" + currTime % 60);
			}
			writeUsers();
		}

		// s.gameStart = (s.userList.size() >= 2);
		if (turns != 0) {
			roundNum = turns / s.getPlayers().size();
		}
		if (currentColor == 0) {
			r = 0;
			g = 0;
			b = 0;
		}
		if (currentColor == 1) {
			r = 255;
			g = 0;
			b = 0;
		}
		if (currentColor == 2) {
			r = 0;
			g = 0;
			b = 255;
		}
		if (currentColor == 3) {
			r = 0;
			g = 255;
			b = 0;
		}
		if (currentColor == 4) {
			r = 0;
			g = 255;
			b = 255;
		}
		if (currentColor == 5) {
			r = 255;
			g = 255;
			b = 0;
		}
		if (currentColor == 6) {
			r = 255;
			g = 0;
			b = 255;
		}
		if (currentColor == 7) {
			r = 255;
			g = 255;
			b = 255;
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		update();
		repaint();
		chat();
	}

	public static void main(String[] arg) throws IOException {
		Driver d = new Driver();
	}

	public Driver() throws IOException {
		Scanner sc = new Scanner(System.in);
		p = new Player();
		w = new WordList();
		System.out.println("client or server");
		while (true) {
			if (side.equals("client") || side.equals("server")) {
				break;
			}
			side = sc.nextLine();
		}
		if (side.equals("server")) {
			System.out.println("How long do you want each turn to be? (sec)");
			tpr = sc.nextInt();
			System.out.println("How many rounds?");
			rounds = sc.nextInt();
		}
		System.out.println("What is the IP Address");
		while (true) {
			if (ip.length() > 0) {
				break;
			}
			ip = sc.nextLine();
		}
		System.out.println(ip);
		s = new Server(side, ip);
		turns = s.getPlayers().size();
		Thread t1 = new Thread(s);
		t1.start();

		frame = new JFrame();
		pencil = ImageIO.read(new File("drawpencil.png"));
		setSize(width, height);
		setBounds(0, 100, width, height);
		frame.setTitle("Doodle Noodle");
		frame.setSize(1600, 900);
		frame.setResizable(false);
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseWheelListener(this);
		frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File(
				"bluebackground.jpg")))));
		this.setBackground(Color.WHITE);
		frame.add(this);

		initialize();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		frame.setBounds(100, 100, 1600, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		// drawing timer
		t = new Timer(1, this);
		t.start();

	}

	Timer t;

	/**
	 * Initializes GUI
	 */
	public void initialize() {

		lblPlayers = new JLabel("Players");
		lblPlayers.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		lblPlayers.setForeground(new Color(130, 200, 255));
		lblPlayers.setBounds(1300, -10, 150, 50);
		frame.getContentPane().add(lblPlayers);

		lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		lblUsername.setForeground(new Color(130, 200, 255));
		lblUsername.setBounds(10, 7, 140, 30);
		frame.getContentPane().add(lblUsername);

		usernameField = new JTextField();
		usernameField.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		usernameField.setBounds(115, 11, 171, 26);
		usernameField.setBackground(new Color(218, 230, 235));
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);

		chatTextField = new JTextField();
		chatTextField.addActionListener(sent);
		chatTextField.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		chatTextField.setBackground(new Color(218, 230, 235));
		chatTextField.setBounds(1300, 602, 274, 23);
		chatTextField.setColumns(10);
		frame.getContentPane().add(chatTextField);

		playerTextArea = new JTextArea();
		playerTextArea.setBackground(SystemColor.controlHighlight);
		playerTextArea.setBounds(1300, 33, 274, 167);
		playerTextArea.setBackground(new Color(218, 230, 235));
		playerTextArea.setForeground(new Color(30, 100, 255));
		playerTextArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
		playerTextArea.setEditable(false);
		frame.add(playerTextArea);

		chatTextArea = new JTextArea();
		chatTextArea.setRows(10);
		chatTextArea.setLineWrap(true);
		chatTextArea.setForeground(new Color(30, 100, 255));
		chatTextArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		chatTextArea.setBackground(new Color(218, 230, 235));
		chatTextArea.setBounds(1300, 211, 274, 380);
		chatTextArea.setEditable(false);

		chatScrollPane = new JScrollPane();
		chatScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatScrollPane.setBounds(1300, 211, 274, 380);
		chatScrollPane.setBackground(new Color(218, 230, 235));
		chatScrollPane.setViewportView(chatTextArea);
		chatScrollPane.setVisible(true);
		frame.getContentPane().add(chatScrollPane);

		sendButton = new JButton("Send");
		sendButton.setForeground(new Color(130, 200, 255));
		sendButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		sendButton.addActionListener(sent);
		sendButton.setBackground(Color.WHITE);
		sendButton.setBounds(1496, 629, 77, 23);
		frame.getContentPane().add(sendButton);

		connectButton = new JButton("Connect");
		connectButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		connectButton.setForeground(new Color(130, 200, 255));
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectButtonActionPerformed(arg0);
			}
		});
		connectButton.setBounds(295, 13, 97, 23);
		frame.getContentPane().add(connectButton);

		times = new JLabel("0");
		times.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
		times.setBounds(10, 20, 100, 100);
		times.setForeground(new Color(130, 200, 255));
		frame.getContentPane().add(times);

		hint = new JLabel("_");
		hint.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		hint.setBounds(230, 20, 700, 100);
		hint.setForeground(new Color(130, 200, 255));
		frame.getContentPane().add(hint);

	}

	public void chat() {
		if (s.messages.size() > 0) {
			String msg = s.messages.poll();
			chatTextArea.append(msg);
			chatTextArea.setCaretPosition(chatTextArea.getDocument()
					.getLength());
		}
		if (s.userList.size() > numPeople) {
			numPeople = s.userList.size();
			writeUsers();
		}
	}

	public void userAdd(String data) {
		s.userList.add(new Player(data));
	}

	public void userRemove(String data) {
		chatTextArea.append(data + " has disconnected.\n");
	}

	public void writeUsers() {
		playerTextArea.setText("");
		for (Player token : s.getPlayers()) {
			playerTextArea.append(token.toString() + ": " + token.getScore()
					+ "\n");
		}
	}

	// KEYPRESSES
	// send message action variable
	/**
	 * If you press enter, it sends the message. Checks input before sending to
	 * other clients in case player gets it correct.
	 */
	private Action sent = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				if (!chatTextField.getText().equals("")) {
					String input = chatTextField.getText().trim();
					if (guess(input, answer.toLowerCase()).equals("correct")) {
						chatTextArea.append(p.toString() + ": " + input + "\n");
						chatTextArea.append(guess(input, answer) + "\n");
						s.send(p.username + ":got it right:" + "Chat");
						System.out.println("setting correct");
						p.setGuessed(true);
					} else if(guess(input, answer) != ""){
						s.send(username + ":" + input + ":" + "Chat");
						chatTextArea.append(guess(input, answer) + "\n");
					} else{
						s.send(username + ":" + input + ":" + "Chat");
					}
				} else {
					chatTextArea.append("Message was not sent. \n");
				}
			}catch (Exception ex) {
				chatTextArea.append("Message was not sent. \n");
			}
			chatTextField.setText("");
			chatTextField.requestFocus();
		}
	};

	/**
	 * Adds user to server
	 * @param evt
	 */
	private void connectButtonActionPerformed(ActionEvent evt) {
		// TODO add your handling code here:
		username = usernameField.getText();
		userAdd(username);
		p = new Player(username);
		isConnected = true;
		s.send(username + ":has connected.:Connect");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("key press " + e.getKeyCode());

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		pressed = true;

	}

	boolean pressed = false;

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public void reset() {

	}

	/**
	 * Checks which color is clicked
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getX() > width - offset) {
			int colorSpacing = (height - 3) / numColors; // Space for
															// one color
			// rectangle.
			int newColor = (e.getY() - 100) / colorSpacing; // Which color
															// number was
			// clicked?
			System.out.println(newColor);

			if (newColor < 0 || newColor > numColors) // Make sure the color
														// number is
				// valid.
				return;
			currentColor = newColor;
		}
	}

	/**
	 * resets drawing pencil
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		pressed = false;
		xprev = -1;
		yprev = -1;
		diff = false;
	}
	
	/**
	 * Drawing lines if mouse is dragged
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (p.isDrawing) {
			currX = e.getX();
			currY = e.getY();
			if (xprev == -1 || yprev == -1) {
				xprev = e.getX();
				yprev = e.getY() - 30 - 100;
				System.out.println("setting");

			}
			s.send(new Line(xprev, yprev, e.getX(), e.getY() - 30 - 100,
					thiccc, r, b, g));
			lines.add(new Line(xprev, yprev, e.getX(), e.getY() - 30 - 100,
					thiccc, r, b, g));
			xprev = e.getX();
			yprev = e.getY() - 30 - 100;
			diff = true;
			System.out.println(s.getPlayers());
		}
	}

	/**
	 * If drawing, pencil follows cursor. Otherwise, it's hidden out of the
	 * screen.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if (p.isDrawing) {
			currX = e.getX();
			currY = e.getY();
		} else {
			currX = -1;
			currY = -1;
		}

	}

	/**
	 * mousewheel controls size of drawing pencil. Scrolling up increases size,
	 * down decreases size.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		if (p.isDrawing) {
			thiccc -= 5 * arg0.getWheelRotation();
			pSize -= 10 * arg0.getWheelRotation();
			if (thiccc < 5) {
				thiccc = 5;
				pSize = 50;
			} else if (thiccc > 500) {
				thiccc = 500;
				pSize = 1040;
			}
		}
	}

	/**
	 * clears drawing panel and lines received from server for new turn
	 */
	public void clear() {
		s.send(new Line(-1, 0, 0, 0, 0, 0, 0, 0));
		lines.clear();
		s.lq.clear();
	}

	/**
	 * determines how many letters of answer to reveal at given time over total
	 * time
	 * 
	 * @param answer
	 * @param currRevealed
	 *            the currently revealed character string
	 * @param seconds
	 *            the current time
	 * @param maxTime
	 *            the total time per turn
	 * @return new currently revealed characters in a String
	 */
	public static String reveal(String answer, String currRevealed,
			int seconds, int maxTime) {
		int intervals = maxTime / answer.length();
		if (seconds % intervals == 0) {
			int index = (int) (Math.random() * answer.length());
			while (!currRevealed.substring(index, index + 1).equals("_")
					&& !currRevealed.substring(index, index + 1).contentEquals(
							" ")) {
				index = (int) (Math.random() * answer.length());
			}
			if (index == currRevealed.length() - 1) {
				currRevealed = currRevealed.substring(0, index)
						+ answer.substring(index, index + 1);
			} else {
				currRevealed = currRevealed.substring(0, index)
						+ answer.substring(index, index + 1)
						+ currRevealed.substring(index + 1);
			}
		}
		return currRevealed;
	}

	/**
	 * Guess takes each input in the chat and checks to see how close to the
	 * answer the input is.
	 * 
	 * @param guess
	 * @param answer
	 * @return It will return nothing if the answer is incorrect,
	 *         "one letter off" if the input is one letter off, and "correct" if
	 *         the input is correct. It will also inform the user if they have
	 *         found a word in the answer.
	 */
	public static String guess(String guess, String answer) {
		guess = guess.toLowerCase();
		answer = answer.toLowerCase();
		String returned = ""; // makes the default return as default
		int incorrects = 0;
		int numberwords = 1;
		ArrayList<Character> answerletters = new ArrayList<Character>(); // Arraylists
		// containing
		// each
		// letter
		// in
		// the
		// word
		ArrayList<Character> guessletters = new ArrayList<Character>();
		String[] words = new String[0];

		if (guess.length() > answer.length() + 1
				|| guess.length() < answer.length() - 1) {

			returned = "";

			words = answer.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (guess.indexOf(words[i]) != -1) {
					returned = "You found a word: " + words[i];
				}
			}

			return returned;
		}

		for (int i = 0; i < answer.length(); i++) {
			answerletters.add(answer.charAt(i));
		}
		for (int i = 0; i < guess.length(); i++) {
			guessletters.add(guess.charAt(i));
		}
		for (int i = 0; i < answerletters.size() && i < guessletters.size(); i++) {
			if (answerletters.get(i) == guessletters.get(i)) {
			} else {
				if (i != answerletters.size() - 1
						&& i != guessletters.size() - 1) {
					if (answerletters.get(i + 1) == guessletters.get(i)) {
						answerletters.remove(i);
					}
					if (answerletters.get(i) == guessletters.get(i + 1)) {
						guessletters.remove(i);
					}
				}
				incorrects++;
			}
		}

		if (guess.equals(answer)) {
			returned = "correct";
		} else {

			if (incorrects == 0 && guess.length() != answer.length()) {
				returned = "You are one letter off";
			}

			if (incorrects == 1) {
				returned = "You are one letter off";
			}

		}
		return returned;
	}

}