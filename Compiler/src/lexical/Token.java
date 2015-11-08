package lexical;

/**
 * @author natafrank
 *
 * Defines the class where a token belongs.
 */
public class Token
{
	//Classes.
	public final static int KEYWORD = 0;
	public final static int IDENTIFIER = 1;
	public final static int OPERATOR_ARITHMETIC = 2;
	public final static int OPERATOR_RELATIONAL = 3;
	public final static int OPERATOR_LOGICAL = 4;
	public final static int OPERATOR_ASIGNATION = 5;
	
	public final static int CONSTANT_ALFANUM = 6;
	public final static int CONSTANT_ENTERO = 7;
	public final static int CONSTANT_DECIMAL = 8;
	public final static int CONSTANT_LOGICO = 9;
	
	public final static int DELIMITATOR = 10;
	public final static int COMMENT = 11;
	
	private int tag;
	private String value;
	
	public Token(int tag)
	{
		this.tag = tag;
	}
	
	public Token(int tag, String value)
	{
		this.tag = tag;
		this.value = value;
	}
	
	public int getTag()
	{
		return tag;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public static String getTagString(int tag)
	{
		switch(tag)
		{
			case KEYWORD: return "Keyword";
			case IDENTIFIER: return "Identifier";
			case OPERATOR_ARITHMETIC: return "Arithmetic Operator";
			case OPERATOR_RELATIONAL: return "Relational Operator";
			case OPERATOR_LOGICAL: return "Logical Operator";
			case OPERATOR_ASIGNATION: return "Asignation Operator";
			case CONSTANT_DECIMAL: return "Decimal Constant";
			case CONSTANT_ENTERO: return "Int Constant";
			case CONSTANT_LOGICO: return "Logical Constant";
			case CONSTANT_ALFANUM: return "Alfanum Constant";
			case DELIMITATOR: return "Delimitator";
			case COMMENT: return "Comment";
			default: return "Error";
		}
	}
}
