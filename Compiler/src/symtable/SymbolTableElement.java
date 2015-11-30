package symtable;

import java.util.ArrayList;

/**
 * Defines the structure of the elements stored in the symbol table.
 * @author natafrank
 *
 *	The structure goes as follow:
 *	---------------------------------------------------------------------------------------------
 *	| 	NAME	|	CLASS	|	DATA TYPE	|	IS DIMENSIONED	|	DIM	|	VALUE	|	LINE	|
 *	---------------------------------------------------------------------------------------------
 *	
 */
public class SymbolTableElement
{
	/**
	 * Classes.
	 */
	public final static int CLASS_TIPO = 1;
	public final static int CLASS_CONSTANTE = 2;
	public final static int CLASS_VARIABLE = 3;
	public final static int CLASS_FUNCION = 4;
	public final static int CLASS_PROCEDIMIENTO = 5;
	public final static int CLASS_PARAMETRO = 6;
	public final static int CLASS_LOCAL = 7;
	public final static int CLASS_LIBRERIA = 8;
	public final static int CLASS_PROGRAMA = 9;
	public final static int CLASS_DECLARATION_TIPO = 10;
	
	/**
	 * Data Types.
	 */
	private String name;
	private int elementClass;
	private String type;
	private	boolean dimensioned;
	private ArrayList<Integer> dim;
	private String value;
	private int line;
	
	/**
	 * Constructor.
	 * @param name
	 * @param elementClass
	 * @param type
	 * @param isDimensioned
	 * @param value
	 */
	public SymbolTableElement(String name, int elementClass, String type, boolean dimensioned, 
			ArrayList<Integer> dim, String value, int line)
	{
		this.name = name;
		this.elementClass = elementClass;
		this.type = type;
		this.dimensioned = dimensioned;
		this.dim = dim;
		this.value = value;
		this.line = line;
	}

	/**
	 * Getters.
	 */
	public String getName(){return name;}
	public int getElementClass(){return elementClass;}
	public String getType(){return type;}
	public boolean isDimensioned(){return dimensioned;}
	public ArrayList<Integer> getDim(){return dim;}
	public String getValue(){return value;}
	public int getLine(){return line;}
	
	/**
	 * Setters
	 */
	public void setName(String name)
	{
		this.name = name;	
	}

	public void setElementClass(int elementClass)
	{
		this.elementClass = elementClass;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setDimensioned(boolean dimensioned)
	{
		this.dimensioned = dimensioned;
	}

	public void setDim(ArrayList<Integer> dim)
	{
		this.dim = dim;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setLine(int line)
	{
		this.line = line;
	}
	
	/**
	 * Gets the class name
	 * @param index
	 * @return class name
	 */
	public final static String getClassNameByIndex(int index)
	{
		switch(index)
		{
			case CLASS_CONSTANTE: return "CONSTANTE";
			case CLASS_FUNCION: return "FUNCION";
			case CLASS_LIBRERIA: return "LIBRERIA";
			case CLASS_LOCAL: return "LOCAL";
			case CLASS_PARAMETRO: return "PARAMETRO";
			case CLASS_PROCEDIMIENTO: return "PROCEDIMIENTO";
			case CLASS_PROGRAMA: return "PROGRAMA";
			case CLASS_TIPO: return "TIPO";
			case CLASS_VARIABLE: return "VARIABLE";
			case CLASS_DECLARATION_TIPO: return "DECLARACION TIPO";
			
			default: return "ERROR";
		}
	}
	
	/**
	 * Gets the class id
	 * @param index
	 * @return class id
	 */
	public final static String getClassForCodeGen(int index)
	{
		switch(index)
		{
			case CLASS_CONSTANTE: return "C";
			case CLASS_FUNCION: return "F";
			case CLASS_LIBRERIA: return "H";
			case CLASS_LOCAL: return "L";
			case CLASS_PARAMETRO: return "P";
			case CLASS_PROCEDIMIENTO: return "F";
			case CLASS_PROGRAMA: return "_P";
			case CLASS_TIPO: return "V";
			case CLASS_VARIABLE: return "V";
			case CLASS_DECLARATION_TIPO: return "V";
			
			default: return "ERROR";
		}
	}
	
	public final static String getIsDimForCodeGen(boolean isDim)
	{
		if(isDim) return "1";
		else return "0";
	}
	
	public final static String getDimForCodeGen(ArrayList<Integer> dim)
	{
		return String.valueOf(dim.size());
	}
	
	public final static String getValueForCodeGen(String value)
	{
		if(value.equals("")) return "#";
		return value;
	}
}
