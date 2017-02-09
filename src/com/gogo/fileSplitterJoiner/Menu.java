/** Class responsible for showing main menu of the application 
 * 
 * @author Gagandeep Singh
 * */
package com.gogo.fileSplitterJoiner;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

class Menu extends ReusableJFrame implements ActionListener
{
	/**
	 * Serializable UID
	 */
	private static final long serialVersionUID = 1L;

	/* Labels for choose info */
	JLabel choose;

	/* buttons for splitter and joiners */
	static MyJButton splitter, joiner;

	/* for adding mouse listener to tabbed pane */
	static int myX, myY, deltaX, deltaY;

	/* default insets */
	Insets defInsets = new Insets(0, 0, 0, 0);

	/* default font being used in entire application */
	public static final String defaultFont = "segoe ui light";

	/* global instance of this class to add/remove components on the go */
	static Menu globalInstance;

	/* Progressbar used in splitter and joiner */
	static JProgressBar jpb;

	/* Tabbed pane for tabbed view */
	static JTabbedPane pane;

	/* Menu panel / main panel */
	JPanel menu;

	/* Panels for split and join operations which are put in jtabbedpane */
	static JPanel split, join;

	Menu()
	{
		super("File Splitter Joiner");

		/* initialize GUI components */
		globalInstance = this;
		choose = new JLabel("What do you want to do ?");
		splitter = new MyJButton("Split a file");
		joiner = new MyJButton("Join files");
		jpb = new JProgressBar();
		pane = new JTabbedPane(JTabbedPane.TOP);

		menu = new JPanel();
		split = new SplitterView();
		join = new JoinerView();

		/* set background and layout for panels */
		pane.setBackground(backgroundAndBorderColor);
		menu.setBackground(backgroundAndBorderColor);
		split.setBackground(backgroundAndBorderColor);
		join.setBackground(backgroundAndBorderColor);
		menu.setLayout(new GridBagLayout());

		/* add jtabbedpane to main panel */
		panel.add(pane, new GridBagConstraints(0, 0,
				GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		/* set foreground color for label */
		choose.setForeground(Color.WHITE);

		/* set properties of progress bar */
		jpb.setStringPainted(true);
		jpb.setFont(new Font(defaultFont, Font.PLAIN, 20));
		jpb.setBackground(backgroundAndBorderColor);
		jpb.setForeground(Color.WHITE);

		/* set font for components */
		ReusableSFM.setFont(new Font(defaultFont, Font.PLAIN, 36), splitter,
				joiner);
		ReusableSFM
				.setFont(new Font("open sans light", Font.PLAIN, 40), choose);

		ReusableSFM.setFont(new Font("open sans light", Font.PLAIN, 20), pane);

		/* add components to Menu panel */
		ReusableSFM.add(menu, choose, 0, 0, 0, 1, GridBagConstraints.CENTER,
				new Insets(0, 0, 0, 0));
		ReusableSFM.add(menu, splitter, 0, 1, 1, 1, GridBagConstraints.CENTER,
				new Insets(0, 25, 25, 0));
		ReusableSFM.add(menu, joiner, 1, 1, 1, 1, GridBagConstraints.CENTER,
				new Insets(0, 25, 25, 0));

		/* add tabs to tabbed pane */
		pane.addTab("Menu", new ImageIcon("resources\\menu.png"), menu,
				"Takes to the menu Page");
		pane.addTab("Splitter", new ImageIcon("resources\\split.png"), split,
				"Takes to the splitter option");
		pane.addTab("Joiner", new ImageIcon("resources\\join.png"), join,
				"Takes to the joiner option");

		/*
		 * pane is set to non-focusable so that no border is shown around
		 * selected tab
		 */
		pane.setFocusable(false);

		/*
		 * add same mouse listener to tabbed pane which was added to
		 * ReusableJFrame
		 */
		pane.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				screenX = me.getXOnScreen();
				screenY = me.getYOnScreen();
				myX = getX();
				myY = getY();
			}
		});
		pane.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent me)
			{
				int deltaX = me.getXOnScreen() - screenX;
				int deltaY = me.getYOnScreen() - screenY;
				setLocation(myX + deltaX, myY + deltaY);
			}
		});

		/* add action listener to both buttons so that they can act accordingly */
		ReusableSFM.addActionListener(this, splitter, joiner);

	}

	/*
	 * main method responsible for starting application in event dispatching
	 * thread
	 */
	public static void main(String... args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new Menu();
			}
		});
	}

	/* action performed method for selecting tabs as per user selection */
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == splitter)
			pane.setSelectedIndex(1);
		else
			pane.setSelectedIndex(2);
	}
}
