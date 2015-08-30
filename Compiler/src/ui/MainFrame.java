package ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author natafrank
 *
 * Frame the enables the load of the file that is going to be compiled.
 * It also contains a TextArea where the status of the compiling process will be shown.
 */
public class MainFrame extends Frame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	//Frame variables.
	private final static int WIDTH = 700;
	private final static int HEIGHT = 500;
	private final static int LOCATION = 200;
	
	//Tags for components.
	private final static String TAG_CHOOSE = "Choose...";
	private final static String TAG_COMPILE = "Compile";
	private final static String TAG_EXIT = "Exit";
	
	private Button btnChoose;
	private Button btnCompile;
	private Button btnExit;
	private Panel pnlButton;
	private TextArea txtStatus;
	
	/**
	 * Initializes the frame.
	 */
	public MainFrame()
	{
		//Configuring.
		super("Compiler");
		addWindowListener(this);
		btnChoose = new Button(TAG_CHOOSE);
		btnChoose.addActionListener(this);
		btnCompile = new Button(TAG_COMPILE);
		btnCompile.addActionListener(this);
		btnCompile.setEnabled(false);
		btnExit = new Button(TAG_EXIT);
		btnExit.addActionListener(this);
		pnlButton = new Panel();
		txtStatus = new TextArea();
		txtStatus.setEditable(false);
		txtStatus.setText("Waiting for file to compile...\n");
		
		//Packing.
		pnlButton.add(btnChoose);
		pnlButton.add(btnCompile);
		pnlButton.add(btnExit);
		setLayout(new BorderLayout());
		add(txtStatus, BorderLayout.CENTER);
		add(pnlButton, BorderLayout.SOUTH);
		setSize(WIDTH, HEIGHT);
		setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();
		
		if(action.equals(TAG_CHOOSE))
		{
			FileDialog fileDialog = new FileDialog(this);
			fileDialog.setVisible(true);
		}
		else if(action.equals(TAG_COMPILE))
		{
			txtStatus.append("Compile.\n");
		}
		else if(action.equals(TAG_EXIT))
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
