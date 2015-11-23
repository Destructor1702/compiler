package codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import core.Core;

/**
 * This class is in charge of the code generation of the compiling file.
 * @author Frank
 *
 */
public class CodeGenerator
{
	private Core core;
	private BufferedWriter writer;
	private int instructionNumber;
	private ArrayList<String> instructions;
	
	/**
	 * Constructor.
	 * @param core
	 */
	public CodeGenerator(Core core)
	{
		this.core = core;
		instructionNumber = 0;
		instructions = new ArrayList<String>();
		String fileName = core.getFileName();
		try
		{
			writer = new BufferedWriter(new FileWriter(fileName.substring(0, fileName.lastIndexOf("."))));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			core.addCodeGenError("Error while creating bytecode file.");
		}
	}
	
	/**
	 * Adds a new instruction to the vector of instructions.
	 * @param mnemonic
	 * @param p1 parameter 1
	 * @param p2 parameter 2
	 */
	public void addInstruction(String mnemonic, String p1, String p2)
	{
		instructions.add(instructionNumber++ + " " + mnemonic + " " + p1 + "," + p2);
	}
}
