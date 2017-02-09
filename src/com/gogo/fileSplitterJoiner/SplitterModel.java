/**
 * File containing Splitter Logic
 * 
 * @author Gagandeep Singh
 */
package com.gogo.fileSplitterJoiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class SplitterModel
{
	/* Buffer holding file's contents */
	byte buffer[];

	/* holds file size and no of time to execute the loop */
	long fileSize, iterations;

	/* stream to open the file */
	FileInputStream fin = null;

	/* string holding absolute file path */
	String absPath;

	/* Variables storing start time, end time and time taken */
	double startTime, endTime, timeTaken;

	public SplitterModel(String absPath, long size)
	{
		this.absPath = absPath;
		/* set buffer size */
		buffer = new byte[(int) size];
		// get file size
		fileSize = new File(absPath).length();
		iterations = fileSize / size;

		/*
		 * If split file size is not a factor of file size, then iterations
		 * should be increased by one because there would be one file with size
		 * less than size of all other files.
		 * 
		 * Eg. File size : 50MB ...if split file size is 6MB then there would be
		 * 8 files of size 6MB each and last file would be 2MB in size (8*6 + 2
		 * = 50)
		 */
		if (fileSize % size != 0)
			iterations++;

		try
		{
			/* read file for splitting */
			fin = new FileInputStream(absPath);

			/*
			 * stores file size and file read (divided by 100 to show
			 * percentage)
			 */
			double sizeD = size, fileSizeD = fileSize / 100;

			/* set start time equal to system time */
			startTime = System.currentTimeMillis();

			/* read file 'iterations' timre and store in part files */
			for (int i = 0; i < iterations; i++)
			{
				/*
				 * If user cancells in between, show some message, perform
				 * cleanup and return
				 */
				if (SplitterView.cancell)
				{
					cleanUp();
					JOptionPane.showMessageDialog(null,
							"The Process was interrupted in between",
							"Splitter stopped", JOptionPane.ERROR_MESSAGE);
					return;
				}

				/* read data in buffer in write to individual parts */
				int read = fin.read(buffer, 0, (int) size);
				FileOutputStream fout = new FileOutputStream(absPath + i);

				/*
				 * update GUI using SwingWorker class, execute and wait till its
				 * done
				 */
				SplitterSwingWorker ssw = new SplitterSwingWorker(read, buffer,
						sizeD * i, fileSizeD, fout);
				ssw.execute();
				while (!(ssw.isDone()))
				{
				}
			}
			/* perform cleanup after loop ends */
			cleanUp();

			/* take system time as end time, calculate time taken for splitting */
			endTime = System.currentTimeMillis();
			timeTaken = (endTime - startTime) / 1000;
			String msg = "The file was splitted into " + iterations
					+ " parts and saved in same folder\nTimeTaken : "
					+ timeTaken + " Seconds";
			if (SplitterView.deleteOriginalFile)
				msg += "\nOriginal file was deleted";
			JOptionPane.showMessageDialog(null, msg, "Success",
					JOptionPane.PLAIN_MESSAGE);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cleanUp()
	{
		try
		{
			/*
			 * Close the stream and delete original file if user has selected
			 * that option
			 */
			fin.close();
			if (SplitterView.deleteOriginalFile)
			{
				Path path = Paths.get(absPath);
				Files.delete(path);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Update GUI, enable /disable required buttons, remove progress bar
		 * inside event dispatching thread
		 */
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				SplitterView.choose.setText("Choose file");
				SplitterView.choose.setEnabled(true);
				SplitterView.cancell = false;
				SplitterView.split.setText("  Split  ");
				SplitterView.split.setEnabled(false);
				SplitterView.size.setEnabled(false);
				SplitterView.fileSize.setText("File Size : ");
				SplitterView.fileType.setText("File Type : ");
				SplitterView.expectedParts.setText("Expected no. of Parts : ");

				Menu.pane.setEnabledAt(2, true);
				Menu.joiner.setEnabled(true);

				Menu.jpb.setString("Splitting : 100 %");
				Menu.jpb.setValue(100);
				Menu.globalInstance.lowerBorder.removeAll();
				Menu.globalInstance.lowerBorder.repaint();
				Menu.globalInstance.lowerBorder.revalidate();

			}
		});
	}

	/*
	 * The class responsible for updating GUI while performing some
	 * calculations/read-write
	 */
	class SplitterSwingWorker extends SwingWorker<Void, Void>
	{
		double sizeD, fileSizeD;
		byte b[];
		final int read;
		FileOutputStream fos;

		/* Obtain main class references using constructor */
		public SplitterSwingWorker(int read, byte b[], double sizeD,
				double fileSizeD, FileOutputStream fos)
		{
			this.b = b;
			this.sizeD = sizeD;
			this.fileSizeD = fileSizeD;
			this.read = read;
			this.b = b;
			this.fos = fos;
		}
		
		/* Do time consuming read/write operation*/
		protected Void doInBackground() throws Exception
		{
			fos.write(b, 0, read);
			fos.flush();
			fos.close();
			return null;
		}
		
		/* Update the same in GUI */
		protected void done()
		{
			double donePercentage = sizeD / fileSizeD;

			String tString = new Double(donePercentage).toString();
			int index = tString.indexOf(".");
			int breakPoint = (index + 3) > tString.length() ? tString.length()
					: (index + 3);
			tString = tString.substring(0, breakPoint);
			Menu.jpb.setString("Splitting : " + tString + " %");
			Menu.jpb.setValue((int) (donePercentage));
			Menu.jpb.repaint();
			Menu.jpb.revalidate();
		}
	}
}
