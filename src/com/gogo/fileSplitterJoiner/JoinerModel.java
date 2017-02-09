/**
 * Class holding logic for file joiner
 * 
 * @author : Gagandeep Singh
 */
package com.gogo.fileSplitterJoiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class JoinerModel
{
	/*
	 * File output stream , holds the reference of file which will represent
	 * final file (single file)
	 */
	FileOutputStream fos = null;

	/*
	 * File input stream , refers to all the PART files one by one inside the
	 * loop
	 */
	FileInputStream fis = null;

	/* Holds no. of parts of the file */
	int parts;

	/*
	 * Holds genericName, for filename, it is the actual name. Eg. File : a.txt
	 * ...when split, parts were named to a.txt1, a.txt2 and so on.
	 * 
	 * At the time of joining, first filename is read, and last character is
	 * removed to get actual file name
	 */
	String genericName;

	/* Stores time taken to join file parts and generate actual file */
	double startTime, endTime, timeTaken;

	public JoinerModel(String path, String firstFileName, int parts)
	{
		/* stores parts value from constructor to class-wide variable */
		this.parts = parts;

		/* get name of the folder from file name */
		String folder = path.substring(0, path.lastIndexOf('\\'));

		/* get generic name from filename */
		genericName = folder + "\\"
				+ (firstFileName.substring(0, firstFileName.length() - 1));

		/* loop variable */
		int i = 0;

		/*
		 * create byte buffer array, with size as big as size of first file
		 * because all other files would be equal to this size, except the last
		 * file which would be less than this in size
		 */
		byte buffer[] = new byte[(int) new File(genericName + 0).length()];

		/* Create output stream for the resultant file */
		try
		{
			fos = new FileOutputStream(genericName);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* Note system time as start time */
		startTime = System.currentTimeMillis();

		/* we have the no. of parts, so we loop till no of parts */
		while (i < parts)
		{
			try
			{
				/*
				 * If user cancells in between, joining process is stopped and
				 * cleanup method is called
				 */
				if (JoinerView.cancel)
				{
					cleanUp();
					JOptionPane.showMessageDialog(null,
							"The Process was interrupted in between",
							"Joiner stopped", JOptionPane.ERROR_MESSAGE);
					return;
				}
				/*
				 * Read splitted file inside loop, stores the no. of bytes read
				 * and close the stream
				 */
				fis = new FileInputStream(genericName + i);
				int read = fis.read(buffer);
				fis.close();

				/* calculate percentage read and pass to SwingWorker class */
				float x = i * 100, y = parts;
				JoinerSwingWorker jsw = new JoinerSwingWorker(fos, (x / y),
						buffer, read);

				/* Execute SwingWorker */
				jsw.execute();

				/* Wait till class finished its operations */
				while (!(jsw.isDone()))
				{
				}

				/* increment file counter */
				i++;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				/*
				 * Incase some file is deleted after reading no. of files, it
				 * will throw FileNotFoundException. So in that loop will break
				 */
				e.printStackTrace();
				break;
			}
		}

		/*
		 * Call cleanup method to close streams, disable/enable required
		 * components
		 */
		cleanUp();

	}

	private void cleanUp()
	{
		// TODO Auto-generated method stub
		try
		{
			/*
			 * Flush and close all open streams. Please note that flushing is
			 * very important in buffered streams
			 */
			fis.close();
			fos.flush();
			fos.close();

			/* If user has requested to delete the file parts, delete them */
			if (JoinerView.deleteParts)
				for (int i = 0; i < parts; i++)
				{
					Path p = Paths.get(genericName + i);
					Files.delete(p);
				}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* Perform GUI updation inside Event dispatching thread */
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				/*
				 * Reset GUI components, flags, remove progressbar, repaint
				 * panel
				 */

				JoinerView.locate.setText(" Locate the first file ");
				JoinerView.locate.setEnabled(true);
				JoinerView.cancel = false;
				JoinerView.join.setText("Join");
				JoinerView.join.setEnabled(true);
				JoinerView.parts.setText("No. of parts - 0 parts");
				Menu.pane.setEnabledAt(1, true);
				Menu.splitter.setEnabled(true);

				Menu.jpb.setString("Joining : 100 %");
				Menu.jpb.setValue(100);

				Menu.globalInstance.lowerBorder.removeAll();
				Menu.globalInstance.lowerBorder.repaint();
				Menu.globalInstance.lowerBorder.revalidate();

			}
		});

		/* store end time, calculate time taken and show completion message */
		endTime = System.currentTimeMillis();
		timeTaken = (endTime - startTime) / 1000;
		String message = "Successfully joined all available parts\nTime Taken : "
				+ timeTaken;
		if (JoinerView.deleteParts)
			message += "\nFile Parts were deleted";
		JOptionPane.showMessageDialog(null, message, "Files Joined !",
				JOptionPane.INFORMATION_MESSAGE);

	}

	/*
	 * When we need to perform some calculation and update GUI side by side, we
	 * extend SwingWorker class. More information can be found in SwingWorker
	 * documentation by Oracle. This class simply receives references to objects
	 * in the main class, writes to file updates GUI (progressbar) and returns
	 */

	class JoinerSwingWorker extends SwingWorker<Void, Void>
	{
		float donePercentage;
		byte b[];
		final int read;
		FileOutputStream fos;

		public JoinerSwingWorker(FileOutputStream fos, float donePercentage,
				byte b[], int read)
		{
			/* Assign references to class-wide objects */
			this.b = b;
			this.donePercentage = donePercentage;
			this.read = read;
			this.b = b;
			this.fos = fos;
		}

		protected Void doInBackground() throws Exception
		{
			/* write to file and flush */
			fos.write(b, 0, read);
			fos.flush();
			return null;
		}

		protected void done()
		{

			/*
			 * This arithmetic is done to 'trim' the completion percentage upto
			 * two places of decimal
			 */
			String tString = new Double(donePercentage).toString();
			int index = tString.indexOf(".");
			int breakPoint = (index + 3) > tString.length() ? tString.length()
					: (index + 3);
			tString = tString.substring(0, breakPoint);
			
			/* Update JProgressBar */
			Menu.jpb.setString("Joining : " + tString + " %");
			Menu.jpb.setValue((int) (donePercentage));
			Menu.jpb.repaint();
			Menu.jpb.revalidate();
		}
	}
}
