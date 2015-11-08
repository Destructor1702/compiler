package parser;

/**
 * Class to define the structure of the parameters to be handled by the symbol table.
 * Elements like this: <DataType, Identifier>.
 * @author natafrank
 *
 */
public class Parameter
{
	private String dataType;
	private String id;
	private int lineOfCode;
	
	public Parameter(String dataType, String id, int lineOfCode)
	{
		this.dataType = dataType;
		this.id = id;
		this.lineOfCode = lineOfCode;
	}
	
	//Getters
	public String getDataType()
	{
		return dataType;
	}
	
	public String getId()
	{
		return id;
	}
	
	public int getLineOfCode()
	{
		return lineOfCode;
	}
}
