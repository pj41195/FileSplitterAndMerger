/**
 * Class containing 'View' of joiner
 * 
 * @author : Gagandeep Singh
 * */

package com.gogo.fileSplitterJoiner;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class JoinerView extends JPanel implements ActionListener
{

	/**
	 * Serializable UID
	 */
	private static final long serialVersionUID = 1L;

	/* Buttons for locate, back and join operations */
	static MyJButton locate, back, join;

	/* labels for showing messages */
	static JLabel weWillLoad, parts;

	/*
	 * If user selects this checkbox, parts are deleted after original file is
	 * constructed
	 */
	static JCheckBox deletePartsCheckBox;

	/* File chooser object */
	JFileChooser jfc;

	/* String holding path of file and name of fiel */
	String path, name;

	/* It holds no. of parts of the file */
	int part;

	/*
	 * Flag for toggling cancel state. If user cancels operation in between,
	 * flag is set
	 */
	static boolean cancel = false;

	/*
	 * Flag stating if parts should be deleted after joining or not as per
	 * checkbox state
	 */
	static boolean deleteParts = false;

	JoinerView()
	{
		/* Initialize GUI components */
		locate = new MyJButton(" Locate the first file ");
		back = new MyJButton("Back");
		join = new MyJButton("Join");
		weWillLoad = new JLabel("We'll load the rest");
		parts = new JLabel("No. of parts - 0 parts");
		deletePartsCheckBox = new JCheckBox("Delete parts after joining");
		jfc = new JFileChooser();

		/* set layout, opacity, font foreground and other general properties */
		setLayout(new GridBagLayout());
		deletePartsCheckBox.setOpaque(false);
		ReusableSFM.setFont(new Font(Menu.defaultFont, Font.PLAIN, 28), locate,
				parts, join, weWillLoad, back, deletePartsCheckBox);

		ReusableSFM.setForeground(Color.WHITE, weWillLoad, parts,
				deletePartsCheckBox);

		/* add components to panel using GridBagLayout */
		add(locate, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(20, 20, 0, 20), 0, 0));
		add(weWillLoad, new GridBagConstraints(0, 1,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 0, 10), 0, 0));
		add(parts, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 0, 0, 0), 0, 0));
		add(deletePartsCheckBox, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						10, 20, 0, 10), 0, 0));
		add(back, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(0, 20, 10, 0), 0, 0));
		add(join, new GridBagConstraints(0, 4, GridBagConstraints.REMAINDER, 1,
				1.0, 1.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(0, 0, 10, 20), 0, 0));

		/* Add action listener to all components at once using ReUsableSFM class */
		ReusableSFM.addActionListener(this, back, join, locate);

		/* Disable join button till user selects a file */
		join.setEnabled(false);
	}

	private int countParts()
	{
		// TODO Auto-generated method stub
		int counter = 0;
		String temp = path.substring(0, path.length() - 1);

		/*
		 * count no. of parts of the file by running an infinite loop
		 * 
		 * when FileNotFoundException is thrown, the counting stops
		 */
		while (true)
		{
			try
			{
				FileInputStream in = new FileInputStream(temp + counter);
				in.close();
				counter++;

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				break;
			}
		}

		/* Update no. of parts in the GUI */
		final int tmp = counter;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				parts.setText(tmp + " Parts");
			}
		});
		return counter;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		JButton source = (JButton) e.getSource();

		/* Logic handling back button */
		if (source == back)
			Menu.pane.setSelectedIndex(0);

		/* If user chooses to locate the file */
		else if (source == locate)
		{
			/*
			 * Set file selection mode to FILES ONLY
			 */
		    jfc.setDialogTitle("Open Part files");
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			/* Open dialog and locate the file */
			int ret = jfc.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION)
			{

				/* Enable join button and set it as default button of root pane */
				join.setEnabled(true);
				getRootPane().setDefaultButton(join);

				/* Calculate path and name from file path */
				String tpath, tname;
				tpath = jfc.getSelectedFile().toString();
				tname = tpath.substring(tpath.lastIndexOf('\\') + 1);
				char lastChar = tname.charAt(tname.length() - 1);
				if (!(lastChar == '0'))
				{
					JOptionPane.showMessageDialog(null,
							"This is not a splitted file.", "Error", 0);
					return;
				}
				path = tpath;
				name = tname;
				part = countParts();
				locate.setText(name);

			}
		}

		/* If user chooses to join the file */
		else if (source == join)
		{
			/* If text is cancel, set the flat to true */
			if (source.getText().trim().equals("Cancel"))
			{
				cancel = true;
				return;
			}

			/* Disable Splitter Option and panel while joining */
			Menu.pane.setEnabledAt(1, false);
			Menu.splitter.setEnabled(false);

			/* Set text to CANCEL and disable locate button */
			join.setText(" Cancel");
			locate.setEnabled(false);

			/* Add progressbar to lower panel */
			Menu.globalInstance.lowerBorder.add(Menu.jpb);

			/*
			 * set this flag to true if user has chosen to delete part files,
			 * else set it to false
			 */
			if (deletePartsCheckBox.isSelected())
				deleteParts = true;
			else
				deleteParts = false;

			/*
			 * start joining operation in a seperate thread and pass necessary
			 * argument to its constructor
			 */
			new Thread(new Runnable()
			{
				public void run()
				{
					new JoinerModel(path, name, part);
				}
			}).start();
		}
	}

}
