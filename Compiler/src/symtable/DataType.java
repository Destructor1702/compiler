package symtable;

import lexical.Token;

/**
 * Class used to denote a data type as an string.
 * @author natafrank
 *
 */
public class DataType
{
	/**
	 * Indexes for dataTypes.
	 */
	public final static String UNDEFINED = "U";
	public final static String ENTERO = "E";
	public final static String DECIMAL = "D";
	public final static String LOGICO = "L";
	public final static String ALFANUMERICO = "A";
	public final static String LAMBDA = "0";
	
	private String dataType;
	private boolean isArray;
	
	public final static String getDataTypeByTokenTag(int tag)
	{
		switch(tag)
		{
			case Token.CONSTANT_ENTERO: return ENTERO;
			case Token.CONSTANT_DECIMAL: return DECIMAL;
			case Token.CONSTANT_ALFANUM: return ALFANUMERICO;
			case Token.CONSTANT_LOGICO: return LOGICO;
			default: return UNDEFINED;
		}
	}
	
	public final static String getDataTypeName(String dataType)
	{
		if(dataType.equals(ALFANUMERICO)) return Token.getTagString(Token.CONSTANT_ALFANUM);
		else if(dataType.equals(ENTERO)) return Token.getTagString(Token.CONSTANT_ENTERO);
		else if(dataType.equals(DECIMAL)) return Token.getTagString(Token.CONSTANT_DECIMAL);
		else if(dataType.equals(LOGICO)) return Token.getTagString(Token.CONSTANT_LOGICO);
		return "UNDEFINED";
	}
	
	public DataType()
	{
		dataType = UNDEFINED;
		isArray = false;
	}
	
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public String getDataType()
	{
		return dataType;
	}
	
	public boolean isArray()
	{
		return isArray;
	}
	
	public void setIsArray(boolean isArray)
	{
		this.isArray = isArray;
	}
}
