package codegen;

/**
 * Interface that defines the instruction set for the assembler.
 * @author Frank
 *
 */
public interface CodeInstruction 
{
	//MNEMONICS
	public final static String OPR = "OPR";
	public final static String LIT = "LIT";
	public final static String LOD = "LOD";
	public final static String STO = "STO";
	public final static String JMP = "JMP";
	public final static String JMC = "JMC";
	
	//Index Operation.
	public final static String END_OF_PROGRAM = "0";
	public final static String RETURN = "1";
	public final static String ADD = "2";
	public final static String SUB = "3";
	public final static String MUL = "4";
	public final static String DIV = "5";
	public final static String MOD = "6";
	public final static String EXP = "7";
	public final static String SIGN_MINUS = "8";
	public final static String LESS_THAN = "9";
	public final static String GREATER_THAN = "10";
	public final static String LESS_OR_EQUAL_THAN = "11";
	public final static String GREATER_OR_EQUAL_THAN = "12";
	public final static String DIFFERENT = "13";
	public final static String EQUAL = "14";
	public final static String AND = "15";
	public final static String OR = "16";
	public final static String NOT = "17";
	public final static String CLEAR_SCREEN = "18";
	public final static String READ = "19";//Form --> OPR Ide,19
	public final static String PRINT = "20";
	public final static String PRINTLN = "21";
}
