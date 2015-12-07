package ui;

import core.Core;

/**
 * @author natafrank
 *
 * Executes the compiler in console mode.
 */
public class Main
{
	public static void main(String args[])
	{
		//if(args.length > 1)
		{
			String file = "Source Files/TeTruena.udeg";
			if(file.endsWith(".udeg"))
			{
				Core core = new Core(file);
				core.compile();
			}
			else System.out.println("File nos supported.");
		}
	}
}