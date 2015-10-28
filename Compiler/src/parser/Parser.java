package parser;

import symtable.SymbolTableElement;
import lexical.Scanner;
import lexical.Token;
import core.Core;

/**
 * This class is in charge of administrate the parsing process.
 * @author natafrank
 *
 */
public class Parser
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
	 * Adds  new error to the map.
	 * @param line Line of code where the error was found.
	 * @param error 
	 */
	public void addError(String error)
	{
		core.addParsingError(error);
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
	
	public boolean addElementToSymbolTable(SymbolTableElement e)
	{
		return core.addElementToSymbolTable(e);
	}
}
