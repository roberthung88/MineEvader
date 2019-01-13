import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class Minesweeper implements ActionListener {
	static String name = "";
	static JFrame frame = new JFrame("Mine-evader: Hung & Montoya Productions");
	JLabel time = new JLabel();

	JButton reset = new JButton("Reset");
	static JButton[][] buttons = new JButton[6][18];
	JButton edit = new JButton("Preferences");
	int[][] count = new int[6][18];
	Container grid = new Container();
	private int userMines = 15;
	JButton start = new JButton("Start");
	Timer timer = new Timer(1000, new CountdownTimerListener());
	private int temptime = 50;
	private int timeleft = 50;
	String setMines = "";
	final int MINE = 10;
	final int numMines = 15;
	private int numdown = 0;

	public Minesweeper() {
		edit.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {

				if (timer.isRunning() == true) {
					timer.stop();
					setMines = JOptionPane.showInputDialog(frame,
							"Number of mines (Max: 50)", null);

					if (setMines == null) {
						userMines = numMines;

					} else if (setMines.equals("")) {
						JOptionPane
								.showMessageDialog(frame,
										"Invalid value! Number of mines has to be from 1 - 50! ");
					} else {
						if (Integer.parseInt(setMines) > 50) {
							userMines = 50;
						} else {
							userMines = Integer.parseInt(setMines);
						}
					}

					reset();
					turnOn(true);
					timer.restart();
				}

			}

		});
		start.addActionListener(this);
		reset.addActionListener(this);
		frame.setSize(1000, 500);
		frame.setLayout(new BorderLayout());
		frame.add(time, BorderLayout.NORTH);
		frame.add(start, BorderLayout.EAST);
		frame.add(reset, BorderLayout.WEST);
		frame.add(edit, BorderLayout.SOUTH);

		// frame.setResizable(false);
		// Button grid
		grid.setLayout(new GridLayout(6, 18));
		for (int a = 0; a < buttons.length; a++) {
			for (int b = 0; b < buttons[0].length; b++) {
				buttons[a][b] = new JButton();
				buttons[a][b].addActionListener(this);
				buttons[a][b].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent releasedEvent) {
						if (releasedEvent.getButton() == MouseEvent.BUTTON3) {
							if (timer.isRunning()) {
								if (releasedEvent.getComponent().isEnabled()) {
									((AbstractButton) releasedEvent
											.getComponent())
											.setIcon(new javax.swing.ImageIcon(
													getClass().getResource(
															"flag.jpg")));
									((AbstractButton) releasedEvent
											.getComponent()).setEnabled(false);

								} else {
									((AbstractButton) releasedEvent
											.getComponent()).setIcon(null);
									((AbstractButton) releasedEvent
											.getComponent()).setEnabled(true);
								}

							}

						}
					}
				});
				grid.add(buttons[a][b]);
			}

		}

		frame.add(grid, BorderLayout.CENTER);
		createRandomMines();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public void createRandomMines() {
		// initialize list of random pairs
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int x = 0; x < count.length; x++) {
			for (int y = 0; y < count[0].length; y++) {
				list.add(x * 100 + y);
			}
		}

		// reset counts and pick out numMines mines
		count = new int[6][18];
		for (int a = 0; a < userMines; a++) {
			int choice = (int) (Math.random() * list.size());
			// count is 10 if there's a mine
			count[list.get(choice) / 100][list.get(choice) % 100] = MINE;
			list.remove(choice);
		}

		// initialize neighbour counts
		for (int x = 0; x < count.length; x++) {
			for (int y = 0; y < count[0].length; y++) {

				if (count[x][y] != MINE) {
					int neighbourcount = 0;
					// up left
					if (x > 0 && y > 0 && count[x - 1][y - 1] == MINE) {
						neighbourcount++;
					}
					// up
					if (x > 0 && count[x - 1][y] == MINE) {
						neighbourcount++;
					}
					// up right
					if (x > 0 && y < count[0].length - 1
							&& count[x - 1][y + 1] == MINE) {
						neighbourcount++;
					}
					// right
					if (y < count[0].length - 1 && count[x][y + 1] == MINE) {
						neighbourcount++;
					}
					// down
					if (x < count.length - 1 && count[x + 1][y] == MINE) {
						neighbourcount++;
					}
					// down left
					if ((x < count.length - 1 && y > 0 && count[x + 1][y - 1] == MINE)) {
						neighbourcount++;
					}
					// left
					if (y > 0 && count[x][y - 1] == MINE) {
						neighbourcount++;
					}
					// down right
					if (x < count.length - 1 && y < count[0].length - 1
							&& count[x + 1][y + 1] == MINE) {
						neighbourcount++;
					}
					count[x][y] = neighbourcount;
				}

			}
		}
	}

	public void lostGame() {
		for (int x = 0; x < buttons.length; x++) {
			for (int y = 0; y < buttons[0].length; y++) {
				if (buttons[x][y].isEnabled()) {
					if (count[x][y] != MINE) {
						buttons[x][y].setText(count[x][y] + "");
						buttons[x][y].setEnabled(false);
					} else {
						buttons[x][y].setText("X");
						buttons[x][y].setEnabled(false);
					}
				}
			}
		}
		timer.stop();
		JOptionPane.showMessageDialog(frame,
				"You Lose! Press Start to restart!");

	}

	public void clearZeros(ArrayList<Integer> toClear) {
		if (toClear.size() == 0) {
			return;
		} else {
			int x = toClear.get(0) / 100;
			int y = toClear.get(0) % 100;
			toClear.remove(0);

			if (x > 0 && y > 0 && buttons[x - 1][y - 1].isEnabled()) {// up
																		// left
				buttons[x - 1][y - 1].setText(count[x - 1][y - 1] + "");
				buttons[x - 1][y - 1].setEnabled(false);
				if (count[x - 1][y - 1] == 0) {
					toClear.add((x - 1) * 100 + (y - 1));
				}
			}
			if (y > 0 && buttons[x][y - 1].isEnabled()) {// up
				buttons[x][y - 1].setText(count[x][y - 1] + "");
				buttons[x][y - 1].setEnabled(false);
				if (count[x][y - 1] == 0) {
					toClear.add((x) * 100 + (y - 1));
				}
			}
			if (x < count.length - 1 && y > 0
					&& buttons[x + 1][y - 1].isEnabled()) {// up right
				buttons[x + 1][y - 1].setText(count[x + 1][y - 1] + "");
				buttons[x + 1][y - 1].setEnabled(false);
				if (count[x + 1][y - 1] == 0) {
					toClear.add((x + 1) * 100 + (y - 1));
				}
			}
			if (x > 0 && buttons[x - 1][y].isEnabled()) {// left
				buttons[x - 1][y].setText(count[x - 1][y] + "");
				buttons[x - 1][y].setEnabled(false);
				if (count[x - 1][y] == 0) {
					toClear.add((x - 1) * 100 + (y));
				}
			}

			if (x < count.length - 1 && buttons[x + 1][y].isEnabled()) {// right
				buttons[x + 1][y].setText(count[x + 1][y] + "");
				buttons[x + 1][y].setEnabled(false);
				if (count[x + 1][y] == 0) {
					toClear.add((x + 1) * 100 + (y));
				}
			}
			if (x > 0 && y < count[0].length - 1
					&& buttons[x - 1][y + 1].isEnabled()) {// down left
				buttons[x - 1][y + 1].setText(count[x - 1][y + 1] + "");
				buttons[x - 1][y + 1].setEnabled(false);
				if (count[x - 1][y + 1] == 0) {
					toClear.add((x - 1) * 100 + (y + 1));
				}
			}
			if (y < count[0].length - 1 && buttons[x][y + 1].isEnabled()) {// down
				buttons[x][y + 1].setText(count[x][y + 1] + "");
				buttons[x][y + 1].setEnabled(false);
				if (count[x][y + 1] == 0) {
					toClear.add((x) * 100 + (y + 1));
				}
			}
			if (x < count.length - 1 && y < count[0].length - 1
					&& buttons[x + 1][y + 1].isEnabled()) {// down
				// right
				buttons[x + 1][y + 1].setText(count[x + 1][y + 1] + "");
				buttons[x + 1][y + 1].setEnabled(false);
				if (count[x + 1][y + 1] == 0) {
					toClear.add((x + 1) * 100 + (y + 1));
				}
			}
		}
		clearZeros(toClear);
	}

	public int checkBlocks() {
		if (numdown == 108) {
			return 108;
		} else {
			numdown = 0;
			for (int x = 0; x < buttons.length; x++) {
				for (int y = 0; y < buttons[0].length; y++) {
					if (buttons[x][y].isEnabled() == false) {
						numdown++;
					}
				}
			}
		}

		return numdown;
	}

	public void checkWin() {
		boolean won = true;
		for (int x = 0; x < count.length; x++) {
			for (int y = 0; y < count[0].length; y++) {
				if (count[x][y] != MINE && buttons[x][y].isEnabled() == true) {
					won = false;
				}
			}
		}
		if (won == true) {
			numdown = 108;
			for (int x = 0; x < buttons.length; x++) {
				for (int y = 0; y < buttons[0].length; y++) {
					if (buttons[x][y].isEnabled()) {
						if (count[x][y] == MINE) {
							buttons[x][y].setText("X");
							buttons[x][y].setEnabled(false);
						}

					}
				}

			}
			JOptionPane.showMessageDialog(frame, "You Win!");

			timer.stop();
		}
	}

	public static void turnOn(boolean turn) {
		for (int a1 = 0; a1 < buttons.length; a1++) {
			for (int b = 0; b < buttons[0].length; b++) {
				buttons[a1][b].setEnabled(turn);
			}
		}

	}

	// reset the board
	public void reset() {
		timeleft = temptime;
		createRandomMines();
		for (int x = 0; x < buttons.length; x++) {
			for (int y = 0; y < buttons[0].length; y++) {
				buttons[x][y].setText("");
				buttons[x][y].setEnabled(false);
				buttons[x][y].setIcon(null);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if (event.getSource().equals(reset)) {
			if (timer.isRunning() == true) {

				reset();
				turnOn(true);
				timer.start();
			}

		} else if (event.getSource().equals(start)) {
			if (timer.isRunning() == false) {
				userMines = numMines;
				reset();

				turnOn(true);
				timer.start();

			}

		} else {
			for (int x = 0; x < buttons.length; x++) {
				for (int y = 0; y < buttons[0].length; y++) {
					if (event.getSource().equals(buttons[x][y])) {
						if (count[x][y] == MINE) {
							buttons[x][y].setText("X");
							buttons[x][y].setForeground(Color.RED);
							lostGame();
						} else if (count[x][y] == 0) {
							buttons[x][y].setText(count[x][y] + "");
							buttons[x][y].setEnabled(false);
							ArrayList<Integer> toClear = new ArrayList<Integer>();
							toClear.add(x * 100 + y);
							clearZeros(toClear);
							checkWin();
						} else {
							buttons[x][y].setText(count[x][y] + "");
							buttons[x][y].setEnabled(false);
							checkWin();
						}

					}
				}
			}
		}
	}

	public class CountdownTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (timeleft >= 0) {

				int min = timeleft / 60;
				int second = timeleft % 60;
				if (second < 10) {
					time.setText(String.valueOf(min
							+ ":"
							+ "0"
							+ second
							+ "       Percent done: "
							+ Math.round(((double) checkBlocks() / 108 * 100) * 100.0)
							/ 100.0 + "%"));
				} else {
					time.setText(String.valueOf(min
							+ ":"
							+ second
							+ "       Percent done: "
							+ Math.round(((double) checkBlocks() / 108 * 100) * 100.0)
							/ 100.0 + "%"));
				}

				timeleft--;
			} else {

				lostGame();
			}

		}
	}

	public static void main(String[] args) {

		Minesweeper a = new Minesweeper();

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		SwingUtilities.updateComponentTreeUI(frame);

		turnOn(false);

		name = JOptionPane.showInputDialog(frame,
				"What is your name / nickname?", null);

		if (name == null) {
			JOptionPane.showMessageDialog(frame, "Game Exited!");
			System.exit(0);
		}
		while (name.equals("")) {
			JOptionPane.showMessageDialog(frame, "Please enter something!");
			name = JOptionPane.showInputDialog(frame, "What is YOUR name?",
					null);
			if (name == null) {
				JOptionPane.showMessageDialog(frame, "Game Exited!");
				System.exit(0);
			}
		}

		JOptionPane
				.showMessageDialog(
						frame,
						"Hi ! "
								+ name
								+ " !"
								+ " Press start to play. You can reset the game and set your preferences once you start.");
	}
}
