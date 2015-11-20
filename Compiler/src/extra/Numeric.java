package extra;

/**
 * Class to define some extra numeric functions for many purposes.
 * @author natafrank
 *
 */
public class Numeric
{
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
		  Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
