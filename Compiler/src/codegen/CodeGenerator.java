package codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import error.Error;

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
	private int tagNumber;
	private ArrayList<String> instructions;
	private ArrayList<String> symbolTableIns;
	private ArrayList<String> tags;
	private boolean hasError;
	private String mainTag;
	
	/**
	 * Constructor.
	 * @param core
	 */
	public CodeGenerator(Core core)
	{
		this.core = core;
		instructionNumber = 1;
		tagNumber = 1;
		instructions = new ArrayList<String>();
		tags = new ArrayList<String>();
		hasError = false;
		String fileName = core.getFileName();
		try
		{
			writer = new BufferedWriter(new FileWriter(fileName.substring(0, fileName
					.lastIndexOf(".")) + ".eje"));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			core.addCodeGenError(Error.codeGenFreeError("Error while creating bytecode file."));
			hasError = true;
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
	
	/**
	 * Generates the file ready to execute.
	 * @return
	 */
	public boolean generateFile()
	{	
		try
		{
			//Write main tag.
			writer.write(mainTag);
			
			//Write tags.
			int tagsSize = tags.size();
			for(int i = 0; i < tagsSize; i++)
				writer.write(tags.get(i) + "\n");
			
			//Write symbol table.
			symbolTableIns = core.getSymbolTableForCodeGenerator();
			int symTabSize = symbolTableIns.size();
			for(int i = 0; i < symTabSize; i++)
				writer.write(symbolTableIns.get(i) + "\n");
			
			//Write separator
			writer.write("@\n");
			
			//Write instructions.
			int instructionsSize = instructions.size();
			for(int i = 0; i < instructionsSize; i++)
				writer.write(instructions.get(i) + "\n");

			writer.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			core.addCodeGenError(Error.codeGenFreeError("Error while writing into the "
					+ "bytecode file."));
			hasError = true;
			return false;
		}
	}
	
	/**
	 * Gets the error status of th ecode generator.
	 * @return
	 */
	public boolean hasError()
	{
		return hasError;
	}
	
	public void setMainTag()
	{
		mainTag = "_P,I,I," + instructionNumber + ",0,#,\n";
	}
	
	public String getNextTag()
	{
		return "_E" + tagNumber++;
	}
	
	public void addTagToSymbolTable(String tagName, int line)
	{
		String tag = tagName + ",I,I," + line + ",0,#,";
		tags.add(tag);
	}
	
	public int getInstructionNumber()
	{
		return instructionNumber;
	}
	
	//public void addTag(String tag)
}
