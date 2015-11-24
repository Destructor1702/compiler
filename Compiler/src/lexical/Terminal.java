package lexical;

/**
 * Interface that describes all the terminals involved in the language.
 * @author natafrank
 *
 */
public interface Terminal
{
	/**
	 * Grammar terminals.
	 */
	final static String TERMINAL_PROGRAMA = "programa";
	final static String TERMINAL_PRINCIPAL = "principal";
	final static String TERMINAL_INICIO = "inicio";
	final static String TERMINAL_CONSTANTE = "constante";
	final static String TERMINAL_TIPO = "tipo";
	final static String TERMINAL_DECLARA = "declara";
	final static String TERMINAL_FIN = "fin";
	final static String TERMINAL_DE = "de";
	final static String TERMINAL_DOT = ".";
	final static String TERMINAL_ENTERO = "entero";
	final static String TERMINAL_DECIMAL = "decimal";
	final static String TERMINAL_ALFANUMERICO = "alfanumerico";
	final static String TERMINAL_LOGICO = "logico";
	final static String TERMINAL_ASIGNATION = ":=";
	final static String TERMINAL_SEMICOLON = ";";
	final static String TERMINAL_COMA = ",";
	final static String TERMINAL_ARREGLO = "arreglo";
	final static String TERMINAL_LEFT_PAR = "(";
	final static String TERMINAL_RIGHT_PAR = ")";
	final static String TERMINAL_HASTA = "hasta";
	final static String TERMINAL_FUNCION = "funcion";
	final static String TERMINAL_PROCEDIMIENTO = "procedimiento";
	final static String TERMINAL_LIBRERIA = "libreria";
	final static String TERMINAL_USANDO = "usando";
	final static String TERMINAL_CICLO = "ciclo";
	final static String TERMINAL_LEE = "lee";
	final static String TERMINAL_DESPLIEGA = "despliega";
	final static String TERMINAL_REGRESA = "regresa";
	final static String TERMINAL_SI = "si";
	final static String TERMINAL_SINO = "sino";
	final static String TERMINAL_EN = "en";
	final static String TERMINAL_PASO = "paso";
	final static String TERMINAL_LEFT_BRAC = "[";
	final static String TERMINAL_RIGHT_BRAC = "]";
	final static String TERMINAL_OP_SUB = "-";
	final static String TERMINAL_OP_ADD = "+";
	final static String TERMINAL_OP_EXP = "^";
	final static String TERMINAL_OP_MUL = "*";
	final static String TERMINAL_OP_DIV = "/";
	final static String TERMINAL_OP_MOD = "%";
	final static String TERMINAL_OP_LESS_THAN = "<";
	final static String TERMINAL_OP_GREATER_THAN = ">";
	final static String TERMINAL_OP_LESS_OR_EQUAL_THAN = "<=";
	final static String TERMINAL_OP_GREATER_OR_EQUAL_THAN = ">=";
	final static String TERMINAL_OP_DIFFERENT = "<>";
	final static String TERMINAL_NO = "no";
	final static String TERMINAL_Y = "y";
	final static String TERMINAL_O = "o";
	final static String TERMINAL_VERDADERO = "verdadero";
	final static String TERMINAL_FALSO = "falso";
}
