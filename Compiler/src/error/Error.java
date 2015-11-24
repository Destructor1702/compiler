package error;

public class Error
{
	////////////////////////////// *********** LEXICAL ***********///////////////////////////////
	
	public final static String LEXICAL_CREATION = "/* LEXICAL ERROR */\nLexical analyzer couldn't "
			+ "be instantiated.";
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	///////////////////////////// ************ SYNTACTIC *********////////////////////////////////\
	
	public final static String createParsingError(int lineOfCode, String value, String expected,
			String grammar)
	{
		return "At grammar *** " + grammar + " ***\n"
				+ "Syntax error in line: "  + lineOfCode + "\nat '" + value + "' expected <"
				+ expected + ">\n\n";
	}
	
	public final static String createParsingError(int lineOfCode, String value, 
			String[] expected, String grammar)
	{
		String buffer = "At grammar *** " + grammar + " ***\n"
				+"Syntax error in line: "  + lineOfCode + "\nat '" + value + "'\nexpected:";
		int size = expected.length;
		for(int i = 0; i < size; i++)
		{
			buffer += "\n<" + expected[i] + ">";
		}
		buffer += "\n\n";
		return buffer;
	}
	
	public final static String createParsingFreeError(int lineOfCode, String error)
	{
		return "Syntax error in line: "  + lineOfCode + "\n" + error;
	}
	
	public final static String parsingErrorNoTokenFound(int lineOfCode)
	{
		return "Syntax error in line: "  + lineOfCode + "\n" + "No Token found.\n\n";
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	///////////////////////////// ************ SEMANTIC *********////////////////////////////////
	
	public final static String semanticEntryAlreadyDefined(int lineOfCode, String value)
	{
		return "Semantic error in line: " + lineOfCode + "\n< " + value + " > has been defined"
				+ " before.";
	}
	
	public final static String semanticElementNotDefined(int lineOfCode, String value)
	{
		return "Semantic error in line: " + lineOfCode + "\nThe element < " + value + " > hasn't"
				+ " been defined yet.";
	}
	
	public final static String semanticDataType(int lineOfCode, String expected)
	{
		return "Semantic error in line: " + lineOfCode + "\nWrong data type, expected < "  
				+ expected + " >.";
	}
	
	public final static String semanticFreeError(int lineOfCode, String error)
	{
		return "Semantic error in line: " + lineOfCode + "\n" + error;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	

	///////////////////////////// ************ CODE GENERATION *********/////////////////////////////////
	
	public final static String codeGenFreeError(String error)
	{
		return "Code generation error.\n" + error;
	}

    /////////////////////////////////////////////////////////////////////////////////////////////////////
}
