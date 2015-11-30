package symtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import core.Core;
import error.Error;

/**
 * Manages all the symbol table operations.
 * @author natafrank
 *
 */
public class SymbolTableManager
{
	private HashMap<String, SymbolTableElement> symbolTable;
	private Core core;
	private boolean error;
	
	/**
	 * Constructor.
	 */
	public SymbolTableManager(Core core)
	{
		this.core = core;
		symbolTable = new HashMap<String, SymbolTableElement>();
		error = false;
	}
	
	/**
	 * Adds a new element to the table.
	 * @param element
	 * @return true if succeded, false if not.
	 */
	public boolean addElement(SymbolTableElement element)
	{
		if(!isInTable(element))
		{
			symbolTable.put(element.getName(), element);
			return true;
		}
		error = true;
		return false;
	}
	
	/**
	 * Checks if an element is in the table. If it is then add the corresponding error.
	 * @param name
	 * @return true if element is in table, if not, returns false.
	 */
	private boolean isInTable(SymbolTableElement element)
	{
		if(!symbolTable.containsKey(element.getName())) return false;
		core.addSemanticError(Error.semanticEntryAlreadyDefined(element.getLine(), 
				element.getName()));
		return true;
	}
	
	/**
	 * Method triggered when is needed to check if an element is registered in the symbol table
	 * so it can be used.
	 * @param element
	 * @return true if registered, if not, returns false.
	 */
	public boolean verifyCall(SymbolTableElement element)
	{
		if(symbolTable.containsKey(element.getName())) return true;
		core.addSemanticError(Error.semanticElementNotDefined(element.getLine(), 
				element.getName()));
		return false;
	}
	
	/**
	 * Gets an element from the table by name.
	 * @param name
	 * @returnn element
	 */
	public SymbolTableElement getElementByName(String name)
	{
		return symbolTable.get(name);
	}
	
	/**
	 * Gets the String of the symbol table.
	 * @return String symbol table.
	 */
	public String getPrintableTable()
	{
		Collection<SymbolTableElement> c = symbolTable.values();
		String buffer = "\n---------------------------------------------------------------------"
				+ "--------------------------------------------\n";
		buffer += "|	 NAME 	|	CLASS	| 	TYPE 	| 	IS_DIM 	|	 DIM	 | 	VALUE 	|	 "
				+ "LINE 	|";
		buffer += "\n----------------------------------------------------------------------------"
				+ "-------------------------------------";	
		for(SymbolTableElement e : c)
		{
			buffer += "\n| " + e.getName() + "	| " + SymbolTableElement.getClassNameByIndex
					(e.getElementClass()) + "	|	"
					+ e.getType() + "	|	" + e.isDimensioned() + "	|	"; 
			ArrayList<Integer> dim = e.getDim();
			if(!dim.isEmpty())
			{
				for(Integer i : dim)
				{
					buffer += "[ " + i + " ] ";
				}
			}
			else buffer += "--";
			buffer += "	|	" + e.getValue() + " |	" + e.getLine() + "  |";
		}
		
		return buffer;
	}
	
	/**
	 * Gets the flag of error.
	 * @return error.
	 */
	public boolean hasError()
	{
		return error;
	}
	
	/**
	 * Turns on and off the flag of errors.
	 * @param flag
	 */
	public void setError(boolean flag)
	{
		error = flag;
	}
	
	/**
	 * Gets the symbol table ready to be used by the code generator.
	 * @return
	 */
	public ArrayList<String> getSymbolTableForCodeGenerator()
	{
		ArrayList<String> symTabCodGen = new ArrayList<String>();
		for(SymbolTableElement e : symbolTable.values())
		{
			String symBuffer = e.getName() + "," + 
					SymbolTableElement.getClassForCodeGen(e.getElementClass()) + "," +
					e.getType() + ",";
			if(!e.isDimensioned()) symBuffer += "0,0,";
			else
			{
				ArrayList<Integer> dim = e.getDim();
				int dimSize = dim.size();
					for(int i = 0; i < dimSize; i+=2)
					{
						int dimDiff = dim.get(i + 1) - dim.get(i);
						symBuffer += dimDiff + ",";
					}
					if(dimSize == 2) symBuffer += "0,";
			}
			symBuffer += "#,";
			
			symTabCodGen.add(symBuffer);
		}
		
		return symTabCodGen;
	}
}
