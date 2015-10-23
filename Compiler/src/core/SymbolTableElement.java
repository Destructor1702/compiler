package core;

/**
 * @author natafrank
 *
 * Defines the structure of the elements contained in the symbol table.
 */
public class SymbolTableElement
{
	String name;
	String value;//Token tag.
	
	public SymbolTableElement(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
}
