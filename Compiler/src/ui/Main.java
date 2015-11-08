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
			Core core = new Core("prueba.udeg");
			core.compile();
		}
	}
}