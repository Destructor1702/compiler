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
}
