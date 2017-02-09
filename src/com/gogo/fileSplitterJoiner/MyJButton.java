/**
 * This class extends standard JButton class and overrides its certain
 * properties. This is because it is easy to change properties in one class
 * rather than changing in each object after creating it.
 * 
 * @author Gagandeep Singh
 */
package com.gogo.fileSplitterJoiner;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;

class MyJButton extends javax.swing.JButton
{
	/**
	 * Serializable UID
	 */
	private static final long serialVersionUID = 1L;

	MyJButton(String s)
	{
		/*
		 * add space on both sides of text so that text looks wider than actual
		 * content
		 */
		super(" " + s + " ");
		/*
		 * focus is set to false so that button doesn't show any inner borders
		 * on selection
		 */
		setFocusable(false);

		/* set foreground to same color as panel */
		setForeground(ReusableJFrame.backgroundAndBorderColor);

		/* set background color of all buttons to white */
		setBackground(Color.WHITE);

		/* set border to 1 pixel black border */
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		/* set button select color as gray */
		UIManager.put("Button.select", Color.GRAY);
	}

	/*
	 * set text method also adds space on both sides of text for buttons to look
	 * wider
	 */
	public void setText(String text)
	{
		// TODO Auto-generated method stub
		super.setText(" " + text + " ");
	}
}
