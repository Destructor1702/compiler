package symtable;

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
	
	public DataType()
	{
		dataType = UNDEFINED;
	}
	
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public String getDataType()
	{
		return dataType;
	}
}
