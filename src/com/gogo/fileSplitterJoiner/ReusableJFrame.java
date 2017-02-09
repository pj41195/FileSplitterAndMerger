
/**
 * @author : Gagandeep Singh
 * 
 * This class extends JFrame and overrides certain visible properties of the
 * frame, creates its own title bar, minimize and close button. It also sets the
 * frame size according to the screen size.
 * 
 * The class implements a special mouse listener using which we can move the
 * application from any part of the visible window. Moreover, the frame has a
 * fixed size and is not resizable.
 */
package com.gogo.fileSplitterJoiner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ReusableJFrame extends JFrame
{
	/**
	 * Serializable ID and general refernce declarations
	 */
	private static final long serialVersionUID = 6592812976391860499L;

	/* Object holding screen size */
	Dimension screenSize;

	/*
	 * Integers used for storing following parameters screenX : width of screen;
	 * screenY : height of screen; the other four variables control screen
	 * position using mouse listener
	 */
	static int screenX, screenY, myX, myY, deltaX, deltaY;

	/* Upper and lower panels color for all panels */
	public static final Color upperAndLowerColor = Color.BLACK;

	/* background color for all panels */
	public static final Color backgroundAndBorderColor = new Color(142, 68, 173);

	/* Labels for close and minimize operations */
	JLabel cross, minimize;

	/* Panels for lower, upper and main content */
	JPanel panel, upperBorder, flowPanel, lowerBorder;

	/* Label holding title for application */
	JLabel title;

	public ReusableJFrame(String titleString)
	{
		/* set look and feel (TinyLookAndFeel) for the application */
		try
		{
			UIManager
					.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* get screen size and store in integer variables */
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenX = (int) screenSize.getWidth();
		screenY = (int) screenSize.getHeight();

		/* initialise GUI components */
		cross = new JLabel(" X  ");
		minimize = new JLabel("  -  ");
		title = new JLabel("  " + titleString);

		panel = new JPanel();
		upperBorder = new JPanel();
		lowerBorder = new JPanel();
		flowPanel = new JPanel();

		/* set background and layout for panels */
		upperBorder.setBackground(upperAndLowerColor);
		lowerBorder.setBackground(upperAndLowerColor);
		flowPanel.setBackground(upperAndLowerColor);

		panel.setLayout(new GridBagLayout());
		panel.setBackground(backgroundAndBorderColor);
		upperBorder.setLayout(new BorderLayout());
		lowerBorder.setLayout(new BorderLayout());

		/*
		 * set foreground and font for multiple components using ReusableSFM
		 * class
		 */
		ReusableSFM.setFont(new Font("open sans light", Font.PLAIN, 20), title);
		ReusableSFM
				.setFont(new Font("roboto", Font.PLAIN, 20), cross, minimize);
		ReusableSFM.setForeground(Color.WHITE, cross, minimize, title);

		/* Now, add components to panels */
		flowPanel.add(minimize);
		flowPanel.add(cross);
		flowPanel.setOpaque(false);
		upperBorder.add(flowPanel, BorderLayout.EAST);
		upperBorder.add(title); // not centered, towards left
		add(upperBorder, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(lowerBorder, BorderLayout.SOUTH);

		/*
		 * Our minimize and cross are labels (not buttons), so we add
		 * mouselisteners to them rather than action listeners
		 */
		cross.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				System.exit(0);
			}
		});
		minimize.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				setState(ICONIFIED);
			}
		});

		/*
		 * This mouse listener allows moving the application from any part
		 * within the window When mouse is first pressed, it notes the location
		 * of the application in the screen, and when it is dragged, the delta
		 * value is updated and application moves
		 */
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				screenX = me.getXOnScreen();
				screenY = me.getYOnScreen();
				myX = getX();
				myY = getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent me)
			{
				int deltaX = me.getXOnScreen() - screenX;
				int deltaY = me.getYOnScreen() - screenY;
				setLocation(myX + deltaX, myY + deltaY);
			}
		});
		
		/* Set general container properties */
		setSize((int) (screenX / 1.8), (int) (screenY / 1.9));
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(backgroundAndBorderColor);
		setVisible(true);
	}
}