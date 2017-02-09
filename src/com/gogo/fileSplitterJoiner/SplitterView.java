/**
 * Class responsible for showing GUI of splitter window
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
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class SplitterView extends JPanel implements ActionListener
{
	/**
	 * serializble UID
	 */
	private static final long serialVersionUID = 1L;

	/* Informational labels */
	static JLabel setSize, fileSize, fileType, expectedParts;

	/* Combo box for showing unit of size */
	JComboBox<String> unit;

	/* Textfield taking size as user input */
	static JTextField size;

	/* File chooser for choosing file */
	JFileChooser chooseFile;

	/*
	 * If this is checked, original file is deleted after splitting it into
	 * parts
	 */
	JCheckBox deleteOriginal;

	/* Buttons for choosing and splitting file */
	static MyJButton split, choose;

	/* String variables storing file name and file path */
	String fileName = "", filePath = "";

	/* boolean flag representing user's choice for deleting file */
	static boolean deleteOriginalFile = false;

	/* array storing units of file size */
	final String sizeArray[] = { "Bytes", "KB", "MB", "GB" };

	/* array storing size factor as long variables */
	long factorArray[] = { 1024, 1048576, 1048576 * 1024 };

	/* variable storing file size */
	long sizeOfFile;

	/*
	 * multiplicative factor for file size to display no. of parts as per user
	 * input
	 */
	volatile long factor = 1024;

	/* flag toggled if user presses cancel button */
	public static volatile boolean cancell = false;

	SplitterView()
	{
		/* Initialize GUI objects */
		unit = new JComboBox<String>();
		size = new JTextField(10);
		chooseFile = new JFileChooser();
		deleteOriginal = new JCheckBox("Delete original file after splitting");
		split = new MyJButton("  Split  ");
		choose = new MyJButton("Choose file");
		setSize = new JLabel("Set Size of splitted file");
		fileSize = new JLabel("File Size : ");
		fileType = new JLabel("File Type : ");
		expectedParts = new JLabel("Expected no. of Parts : ");

		/*
		 * Set general properties for GUI components like opacity, foreground,
		 * background and font
		 */
		deleteOriginal.setOpaque(false);
		ReusableSFM.setFont(new Font(Menu.defaultFont, Font.PLAIN, 22),
				setSize, unit, size, deleteOriginal, choose);
		ReusableSFM.setFont(new Font(Menu.defaultFont, Font.PLAIN, 28), split);
		ReusableSFM.setFont(new Font(Menu.defaultFont, Font.PLAIN, 20),
				fileSize, fileType, expectedParts);

		ReusableSFM.setForeground(Color.WHITE, setSize, deleteOriginal,
				fileSize, fileType, expectedParts);

		/* Add items to combo box */
		unit.addItem("KB");
		unit.addItem("MB");
		unit.addItem("GB");

		/* set panel's layout as GridBagLayout */
		setLayout(new GridBagLayout());

		/* Add components to panel */
		add(choose, new GridBagConstraints(1, 0, 0, 1, 0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(20, 70, 0, 0), 0, 0));
		ReusableSFM.add(this, setSize, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, new Insets(20, 70, 0, 0));
		ReusableSFM.add(this, size, 2, 1, 1, 1, GridBagConstraints.NORTHWEST,
				new Insets(20, 0, 0, 0));
		ReusableSFM.add(this, unit, 3, 1, 1, 1, GridBagConstraints.NORTHWEST,
				new Insets(20, 0, 0, 0));
		add(new JSeparator(), new GridBagConstraints(0, 3,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 70, 0, 70), 0, 0));
		ReusableSFM.add(this, fileSize, 1, 4, 0, 1,
				GridBagConstraints.NORTHWEST, new Insets(0, 70, 0, 0));
		ReusableSFM.add(this, fileType, 1, 5, 0, 1,
				GridBagConstraints.NORTHWEST, new Insets(0, 70, 0, 0));
		ReusableSFM.add(this, expectedParts, 1, 6, 0, 1,
				GridBagConstraints.NORTHWEST, new Insets(0, 70, 0, 0));
		ReusableSFM.add(this, deleteOriginal, 1, 2, 0, 1,
				GridBagConstraints.WEST, new Insets(0, 70, 10, 0));
		ReusableSFM.add(this, split, 1, 7, 0, 1, GridBagConstraints.CENTER,
				new Insets(0, 0, 0, 0));

		/* add action listener to buttons and combo box */
		ReusableSFM.addActionListener(this, choose, split);
		unit.addActionListener(this);

		/*
		 * add caret listener to textfield to show no. of parts as per user
		 * input
		 */
		size.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				updateExpectedParts();
			}
		});

		/* disable button and field till user selects the file */
		split.setEnabled(false);
		size.setEnabled(false);
	}

	void updateExpectedParts()
	{
		try
		{
			/*
			 * get file size from textfield
			 * 
			 * If textfield is empty or contains characters, it raises
			 * NumberFormatException and control is trasferred to catch block
			 */
			long x = new Long(size.getText());

			/* update size as per factor */
			x *= factor;
			long parts = 0;
			parts += (sizeOfFile / x) + 1;

			/* set size accordingly */
			expectedParts.setText("Expected no. of Parts : " + parts
					+ " Parts \n");

		}
		catch (Exception exp)
		{
			/*
			 * set proper text on label if invalid data is entered or if field
			 * is empty
			 */
			if (!size.getText().equals(""))
				expectedParts.setText("Invalid Size");
			else
				expectedParts.setText("Expected no. of Parts : ");
		}
	}

	public void actionPerformed(ActionEvent ae)
	{
		Object source = ae.getSource();
		if (source instanceof JButton)
		{
			MyJButton src = (MyJButton) ae.getSource();

			/* if user chooses to browse to choose the file */
			if (src == choose)
			{
				chooseFile.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int retVal = chooseFile.showOpenDialog(null);

				if (!(retVal == JFileChooser.APPROVE_OPTION))
					return;
				else
				{
					/* get name and path of selected file */
					fileName = chooseFile.getSelectedFile().getName();
					filePath = chooseFile.getSelectedFile().getAbsolutePath();

					/* call method to get type of file and its size */
					getSizeTypeAndParts(filePath);

					/* Update GUI accordingly */
					choose.setText(fileName);
					split.setEnabled(true);
					size.setEnabled(true);
					getRootPane().setDefaultButton(split);
				}

			}
			else if (src == split)
			{
				/* If user chooses to cancell, a flag is set */
				if (src.getText().trim().equals("Cancel"))
				{
					cancell = true;
					return;
				}

				/*
				 * update flag if user selects option to delete original file
				 * afterwards
				 */
				if (deleteOriginal.isSelected())
					deleteOriginalFile = true;
				else
					deleteOriginalFile = false;

				/*
				 * Disable joiner from main window and from tabbed view so that
				 * joiner cannot be accessed when splitting occurs
				 */
				Menu.pane.setEnabledAt(2, false);
				Menu.joiner.setEnabled(false);
				long data = 0;
				try
				{
					/*
					 * take user input from textbox
					 * 
					 * it is enclosed in try block so that it does not throw
					 * NumberFormatException if non-integer value is entered
					 */
					data = Long.parseLong(size.getText());
				}
				catch (NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog(null,
							"Enter some integer value for file size");
					return;
				}
				/* Update retuired GUI components */
				choose.setEnabled(false);
				split.setText(" Cancel ");

				/* calculate value in bytes using array */
				data *= factorArray[unit.getSelectedIndex()];
				final long d = data;
				Menu.globalInstance.lowerBorder.add(Menu.jpb);

				/* Call splitter view in a seperate thread */
				new Thread(new Runnable()
				{
					public void run()
					{
						new SplitterModel(filePath, d);
					}
				}).start();
			}
		}
		else if (source == unit)
		{
			/* when user sets the unit, we need to update "expected parts" field */
			factor = factorArray[unit.getSelectedIndex()];
			updateExpectedParts();
		}
	}

	/*
	 * Method responsible for loading size and type of file using predefined
	 * arrays
	 */
	private void getSizeTypeAndParts(String path)
	{
		File f = new File(path);
		int counter = 0;
		sizeOfFile = f.length();
		
		/* get file size */
		if (sizeOfFile == 0L)
		{
			JOptionPane.showMessageDialog(null,
					"No Valid file found on specified location",
					"File not found", 0);
			return;
		}
		String extension = f
				.toString()
				.substring(f.toString().lastIndexOf(".") + 1,
						f.toString().length()).toUpperCase();
		long tempSize = (long) sizeOfFile;
		
		/* this loop marks the position of size inside array */
		while (tempSize >= 1024)
		{
			counter++;
			tempSize /= 1024;
		}
		
		/* Check extension with some commonly used extensions */
		if (extension.equals("JAVA") || extension.equals("C")
				|| extension.equals("CPP") || extension.equals("CS")
				|| extension.equals("CSS") || extension.equals("HTML")
				|| extension.equals("JS") || extension.equals("PHP")
				|| extension.equals("XML") || extension.equals("VB"))
			extension += " Source File";
		else if (extension.equals("JPG") || extension.equals("BMP")
				|| extension.equals("PNG") || extension.equals("GIF")
				|| extension.equals("TIFF") || extension.equals("ICO"))
			extension += " File (Image/Icon)";
		else if (extension.equals("MPG") || extension.equals("MPEG")
				|| extension.equals("MP4") || extension.equals("AVI")
				|| extension.equals("3GP") || extension.equals("RMVB")
				|| extension.equals("WMV") || extension.equals("MKV")
				|| extension.equals("VOB") || extension.equals("MOV")
				|| extension.equals("FLV"))
			extension += " File (Video)";
		else if (extension.equals("MP3") || extension.equals("WMA")
				|| extension.equals("FLAC") || extension.equals("AAC")
				|| extension.equals("AMF") || extension.equals("AMR")
				|| extension.equals("M4A") || extension.equals("M4R")
				|| extension.equals("OGG") || extension.equals("MP2")
				|| extension.equals("WAV"))
			extension += " File (Audio)";
		else if (extension.equals("EXE") || extension.equals("CMD")
				|| extension.equals("BAT") || extension.equals("DMG")
				|| extension.equals("MSI"))
			extension += " (Executable File)";
		else
			extension += " Doc/File";
		
		/* update GUI accordingly */
		fileSize.setText("File Size : " + tempSize + " "
				+ sizeArray[counter] + " [Approx] ");
		fileType.setText("File Type : " + extension);

	}
}
