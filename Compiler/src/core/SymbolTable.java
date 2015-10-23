package core;

import java.util.HashMap;

import lexical.Token;

/**
 * @author natafrank
 *
 * Defines the structure of the symbol table used across the compiler.
 */
public class SymbolTable
{
	private HashMap<Integer, SymbolTableElement> symbolTable;
	private int index;
	
	public SymbolTable()
	{
		symbolTable = new HashMap<Integer, SymbolTableElement>();
		index = 0;
	}
	
	public void addElement(Token token)
	{
		SymbolTableElement element = new SymbolTableElement(getNameToken(token.getTag()),
				token.getValue());
		symbolTable.put(index++, element);
	}
	
	public String getNameToken(int tokenTag)
	{
		switch(tokenTag)
		{
			case Token.KEYWORD:
				return "KEY";
			case Token.IDENTIFIER:
				return "ID";
			case Token.OPERATOR_ARITHMETIC:
				return "ARTOP";
			case Token.OPERATOR_RELATIONAL:
				return "RELOP";
			case Token.OPERATOR_LOGICAL:
				return "LOGOP";
			case Token.OPERATOR_ASIGNATION:
				return "ASIOP";
			case Token.CONSTANT_STRING:
				return "CNTSTR";
			case Token.CONSTANT_INT:
				return "CNTINT";
			case Token.CONSTANT_DECIMAL:
				return "CNTDEC";
			case Token.CONSTANT_LOGICAL:
				return "CNTLOG";
			case Token.DELIMITATOR:
				return "DEL";
			default:
				return "ERR";
		}
	}
}
