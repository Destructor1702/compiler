package parser;

import lexical.Scanner;
import lexical.Terminal;
import lexical.Token;
import symtable.SymbolTableElement;
import core.Core;
import error.Error;

/**
 * This class is in charge of administrate the parsing process.
 * @author natafrank
 *
 */
public class Parser implements Terminal
{		
	/**
	 * Parsing state results.
	 */
	public final static int RESULT_OK = 0;
	public final static int RESULT_ERROR = -1;
	public final static int ERROR_THREAD = -2;
	
	/**
	 * Holds the errors found in the parsing process.
	 */
	private Scanner scanner;
	private Core core;
		
	/**
	 * Default constructor.
	 * @param scanner Scanner to get the tokens.
	 */
	public Parser(Core core, Scanner scanner)
	{
		this.scanner = scanner;
		this.core = core;
	}
	
	/**
	 * Calls the scanner to get a token.
	 * @return token.
	 */
	public Token getToken()
	{
		if(scanner.hasToken())
		{
			Token token = scanner.getToken();
			if(token.getTag() >= 0) 
				return token;
			else return null;
		}
		else return null;
	}
	
	/**
	 * Starts the parsing from the initial grammar.
	 * @return result of grammatical analysis.
	 */
	public int startParsing()
	{
		Grammar grammar = new Grammar(this);
		grammar.startGrammaticalAnalysis();
		if(!grammar.getErrorFound()) return RESULT_OK;
		return RESULT_ERROR;
	}
	
	/**
	 * Adds a parsing error.
	 * @param error 
	 */
	public void addParsingError(String error)
	{
		core.addParsingError(error);
	}
	
	/**
	 * Adds a semantic error.
	 * @param error 
	 */
	public void addSemanticError(String error)
	{
		core.addSemanticError(error);
	}
	
	/**
	 * Gets the line of code of the input.
	 * @return lineOfCode.
	 */
	public int getLineOfCode()
	{
		return scanner.getLineOfCode();
	}
	
	/**
	 * Pushes a token to the stack.
	 * @param token
	 */
	public void pushTokenToStack(Token token)
	{
		scanner.pushTokenToStack(token);
	}
	
	/**
	 * Checks if the scanner has more tokens.
	 * @return
	 */
	public boolean hasToken()
	{
		return scanner.hasToken();
	}
	
	/**
	 * Adds an element to the symbol table.
	 * @param e element
	 * @return
	 */
	public boolean addElementToSymbolTable(SymbolTableElement e)
	{
		return core.addElementToSymbolTable(e);
	}
	
	/**
	 * Gets the value of an element by its name.
	 * @param name
	 * @return element's value.
	 */
	public String getValueFromSymbolTable(String name)
	{
		return getElementByName(name).getValue();
	}
	
	/**
	 * Gets the value of an element by its integer representation.
	 * @param name
	 * @return Integer value or null if error ocurrs.
	 */
	public Integer getIntValueFromElement(String name)
	{
		String value = getValueFromSymbolTable(name);
		Integer intValue = null;
		try
		{
			intValue = Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			core.addSemanticError(Error.semanticDataType(getLineOfCode(), TERMINAL_ENTERO));
		}
		return intValue;
	}
	
	/**
	 * Gets an element from the symbol table by its name.
	 * @param name
	 * @return element
	 */
	private SymbolTableElement getElementByName(String name)
	{
		return core.getElementByName(name);
	}
}
