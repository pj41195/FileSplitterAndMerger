/** This class holds a set of static final methods
 * which are used at many places
 * 
 * for the convenience, these methods are coded in one class
 * as 'static' methods so that they can be accessed anywhere by name of the class 
 * 
 * @author Gagandeep Singh
 */

package com.gogo.fileSplitterJoiner;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

class ReusableSFM
{

	/* Used to add component on a panel having gridbag layout */
	public static final void add(JPanel p, JComponent component, int gridx,
			int gridy, int gridwidth, int gridheight, int posInCell,
			Insets insets)
	{
		p.add(component, new GridBagConstraints(gridx, gridy, gridwidth,
				gridheight, 1.0, 1.0, posInCell, GridBagConstraints.NONE,
				insets, 0, 0));
	}

	/* Used to add component on a dialog having gridbag layout */
	public static final void add(JDialog d, JComponent component, int gridx,
			int gridy, int gridwidth, int gridheight, int posInCell,
			Insets insets)
	{
		d.add(component, new GridBagConstraints(gridx, gridy, gridwidth,
				gridheight, 1.0, 1.0, posInCell, GridBagConstraints.NONE,
				insets, 0, 0));
	}

	/* Set foreground for multiple components in one LOC */
	public static final void setForeground(Color c, JComponent... Comp)
	{
		for (JComponent C : Comp)
			C.setForeground(c);
	}

	/* Set fonts for multiple components in one LOC */
	public static final void setFont(Font f, JComponent... c)
	{
		for (JComponent C : c)
			C.setFont(f);
	}

	/* add actionlisteners for multiple components using one LOC */
	public static final void addActionListener(ActionListener l,
			AbstractButton... c)
	{
		for (AbstractButton C : c)
			C.addActionListener(l);
	}

	/* Enable / Disable multiple components using one LOC */
	public static final void enable(boolean state, JComponent... c)
	{
		for (JComponent C : c)
			C.setEnabled(state);
	}
}
