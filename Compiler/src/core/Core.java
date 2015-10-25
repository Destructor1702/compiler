package core;

import java.awt.TextArea;
import java.util.ArrayList;

import lexical.Scanner;
import parser.Parser;
import error.Error;

/**
 * @author natafrank
 *
 * It's in charge of synchronization between all the elements in the compiler.
 */
public class Core
{
	private String fileName;
	private TextArea txtStatus;
	private ArrayList<String> errors;
	
	/**
	 * Constructor for terminal execution.
	 * @param fileName File to compile.
	 */
	public Core(String fileName)
	{
		this.fileName = fileName;
		txtStatus = null;
		errors = new ArrayList<String>();
	}
	
	/**
	 * Constructor with GUI.
	 * @param txtStatus
	 * @param fileName
	 */
	public Core(TextArea txtStatus, String fileName)
	{
		this.fileName = fileName;
		this.txtStatus = txtStatus;
	}
	
	/**
	 * Inicializes the compiling process.
	 */
	public void compile()
	{
		Scanner scanner = new Scanner(fileName);
		if(scanner.getStatus() == Scanner.STATUS_GOOD)
		{
			Parser parser = new Parser(this, scanner);
			int parsingResult = parser.startParsing();
			switch(parsingResult)
			{
				case Parser.RESULT_OK:
					print("Parsing OK.");
					break;
				case Parser.RESULT_ERROR:
					int errorsLength = errors.size();
					for(int i = 0; i < errorsLength; i++)
					{
						print(errors.get(i));
					}
					break;
				default:
					print("Unexpected Result.");
			}
		}
		else print(Error.LEXICAL_CREATION);
	}
	
	/**
	 * Adds  new error to the map.
	 * @param line Line of code where the error was found.
	 * @param error 
	 */
	public void addError(String error)
	{
		errors.add(error);
	}
	
	/**
	 * Prints on the executing main.
	 * @param message
	 */
	public void print(String message)
	{
		if(txtStatus != null)
		{
			txtStatus.append("--------------------------------------------------------------\n" +
					message + "\n");
		}
		else
		{
			System.out.print("--------------------------------------------------------------\n" +
					message + "\n");
		}
	}
}
