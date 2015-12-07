package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import core.Core;

/**
 * @author natafrank
 * Frame that will package a working area.
 * It makes available to load files, compile, edit and also it has a status output.
 */
public class MainFrame extends Frame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	//Frame variables.
	private final static int WIDTH = 1200;
	private final static int HEIGHT = 650;
	private final static int LOCATION = 50;
	
	//Tags for components.
	private final static String TAG_LOAD = "Load";
	private final static String TAG_COMPILE = "Compile";
	private final static String TAG_EXIT = "Exit";
	
	private Button btnLoad;
	private Button btnCompile;
	private Button btnExit;
	private Panel pnlButton;
	private Panel pnlEditor;
	private TextArea txtEditor;
	private TextArea txtLines;
	private TextArea txtStatus;
	private String fileName;
	//private Scrollbar scroll;
	private int line;
	
	private File f = null;
	
	/**
	 * Initializes the frame.
	 */
	public MainFrame()
	{
		//Configuring.
		super("Compiler");
		addWindowListener(this);
		btnLoad = new Button(TAG_LOAD);
		btnLoad.addActionListener(this);
		btnCompile = new Button(TAG_COMPILE);
		btnCompile.addActionListener(this);
		btnCompile.setEnabled(false);
		btnExit = new Button(TAG_EXIT);
		btnExit.addActionListener(this);
		//scroll = new Scrollbar();
		pnlButton = new Panel();
		pnlEditor = new Panel(new BorderLayout());
		txtStatus = new TextArea();
		txtEditor = new TextArea();
		txtEditor.setEditable(false);
		txtLines = new TextArea("", 1, 3, TextArea.SCROLLBARS_HORIZONTAL_ONLY);
		txtLines.setEditable(false);
		line = 1;
		txtLines.append(line + "\n");
		txtStatus.setEditable(false);
		
		//Packing.
		pnlButton.add(btnLoad);
		pnlButton.add(btnCompile);
		pnlButton.add(btnExit);
		//pnlEditor.add(txtLines, BorderLayout.WEST);
		pnlEditor.add(txtEditor, BorderLayout.CENTER);
		setLayout(new BorderLayout());
		add(pnlButton, BorderLayout.NORTH);
		add(pnlEditor, BorderLayout.CENTER);
		add(txtStatus, BorderLayout.SOUTH);
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		
		//Listener.
		txtEditor.addTextListener(new TextListener()
		{
			@Override
			public void textValueChanged(TextEvent e)
			{
				String text = txtEditor.getText();
				int nextLineChars = text.length() - text.replace("\n", "").length();
				txtLines.setText("");
				for(int i = 1; i <= nextLineChars + 1; i++)
				{
					txtLines.append(i + "\n");
				}
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		
		if(action.equals(TAG_LOAD))
		{
			FileDialog fileDialog = new FileDialog(this);
			fileDialog.setVisible(true);
			fileName = fileDialog.getFile();
			if(fileName.endsWith(".udeg"))
			{
				if(fileName != null)
				{
					btnCompile.setEnabled(true);
					txtStatus.append("Selected file: " + fileName + "\n\n");
					try
					{
						f = new File(fileDialog.getDirectory(), fileName);
						FileReader r = new FileReader(f);
						BufferedReader br = new BufferedReader(r);
						char[] text = new char[Integer.parseInt(
								String.valueOf(f.length()))];
						br.read(text, 0, Integer.parseInt(
								String.valueOf(f.length())));
						txtEditor.append(String.valueOf(text));
						br.close();
						txtStatus.setText("");
					}
					catch (FileNotFoundException e1)
					{
						e1.printStackTrace();
					}
					//txtEditor.append()
					catch (NumberFormatException e1)
					{
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				else
					txtStatus.append("File is NULL, can't continue.");
			}
			else
				txtStatus.append("Only files with extension \".udeg\" can be compiled.\n"
						+ "Select another file.\n");
		}
		if(action.equals(TAG_COMPILE))
		{
			txtStatus.append("Compiling...\n\n");
			Core core = new Core(txtStatus, f.getAbsolutePath());
			core.compile();
		}
		if(action.equals(TAG_EXIT))
		{
			System.exit(0);
		}
	}
	
	/**
	 * WindowListener overrides.
	 */
	@Override
	public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	}
	public void windowOpened(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}

	
	public static void main(String[] args)
	{
		MainFrame mf = new MainFrame();
		mf.setLocation(LOCATION, LOCATION);
		mf.setVisible(true);
	}
}
