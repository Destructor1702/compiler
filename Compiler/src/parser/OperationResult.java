package parser;

import lexical.Terminal;
import symtable.DataType;

/**
 * Interface that describes the results derived by the operations.
 * @author natafrank
 *
 */
public interface OperationResult extends Terminal
{
	final static String[] OPERATION = 
	{
		DataType.ALFANUMERICO + TERMINAL_OP_ADD + DataType.ALFANUMERICO,
		
		DataType.ENTERO + TERMINAL_OP_ADD + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_ADD + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_ADD + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_ADD + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_SUB + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_SUB + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_SUB + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_SUB + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_MUL + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_MUL + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_MUL + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_MUL + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_DIV + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_DIV + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_DIV + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_DIV + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_EXP + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_MOD + DataType.ENTERO,
		
		DataType.ENTERO + TERMINAL_OP_LESS_THAN + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_LESS_THAN + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_LESS_THAN + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_LESS_THAN + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_GREATER_THAN + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_GREATER_THAN + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_GREATER_THAN + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_GREATER_THAN + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_LESS_OR_EQUAL_THAN + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_LESS_OR_EQUAL_THAN + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_LESS_OR_EQUAL_THAN + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_LESS_OR_EQUAL_THAN + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_GREATER_OR_EQUAL_THAN + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_GREATER_OR_EQUAL_THAN + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_GREATER_OR_EQUAL_THAN + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_GREATER_OR_EQUAL_THAN + DataType.DECIMAL,
		
		DataType.ENTERO + TERMINAL_OP_DIFFERENT + DataType.ENTERO,
		DataType.ENTERO + TERMINAL_OP_DIFFERENT + DataType.DECIMAL,
		DataType.DECIMAL + TERMINAL_OP_DIFFERENT + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_OP_DIFFERENT + DataType.DECIMAL,
		
		DataType.ALFANUMERICO + TERMINAL_ASIGNATION + DataType.ALFANUMERICO,
		DataType.DECIMAL + TERMINAL_ASIGNATION + DataType.DECIMAL,
		DataType.ENTERO + TERMINAL_ASIGNATION + DataType.ENTERO,
		DataType.DECIMAL + TERMINAL_ASIGNATION + DataType.ENTERO,
		DataType.LOGICO + TERMINAL_ASIGNATION + DataType.LOGICO,
		
		TERMINAL_OP_SUB + DataType.ENTERO,
		TERMINAL_OP_SUB + DataType.DECIMAL,
		
		DataType.LOGICO + TERMINAL_O + DataType.LOGICO,
		DataType.LOGICO + TERMINAL_Y + DataType.LOGICO,
		TERMINAL_NO + DataType.LOGICO
	};
	
	final static String[] RESULT =
	{
		DataType.ALFANUMERICO,
		
		DataType.ENTERO,
		DataType.DECIMAL,
		DataType.DECIMAL,
		DataType.DECIMAL,
		
		DataType.ENTERO,
		DataType.DECIMAL,
		DataType.DECIMAL,
		DataType.DECIMAL,
		
		DataType.ENTERO,
		DataType.DECIMAL,
		DataType.DECIMAL,
		DataType.DECIMAL,
		
		DataType.DECIMAL,
		DataType.DECIMAL,
		DataType.DECIMAL,
		DataType.DECIMAL,
		
		DataType.DECIMAL,
		DataType.ENTERO,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO,
		
		DataType.LAMBDA,
		DataType.LAMBDA,
		DataType.LAMBDA,
		DataType.LAMBDA,
		DataType.LAMBDA,
		
		DataType.ENTERO,
		DataType.DECIMAL,
		
		DataType.LOGICO,
		DataType.LOGICO,
		DataType.LOGICO
	};
}
