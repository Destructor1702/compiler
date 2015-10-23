package parser;

import lexical.Terminal;
import lexical.Token;
import error.Error;

public class Grammar implements Terminal
{
	private final static int PRIORITY_HIGH = 1;
	private final static int PRIORITY_LOW = 2;
	
	private Parser parser;
	private boolean errorFound;
	private Token token;
	private String value;
	private int tag;
	
	/**
	 * Index for grammars.
	 */
	public final static int G_PROGAMA = 1;
	public final static int G_BLOCK = 2;
	public final static int G_CICLO = 3;
	public final static int G_COMMAND = 4;
	public final static int G_CONSTANTES = 5;
	public final static int G_DESPLIEGA = 6;
	public final static int G_EXPO = 7;
	public final static int G_EXPR = 8;
	public final static int G_FUNCION = 9;
	public final static int G_FUNC_PROC = 10;
	public final static int G_LEE = 11;
	public final static int G_L_FUNC = 12;
	public final static int G_LIBRARIES = 13;
	public final static int G_LITERAL = 14;
	public final static int G_MULTI = 15;
	public final static int G_OP_NO = 16;
	public final static int G_OP_REL = 17;
	public final static int G_OP_SR = 18;
	public final static int G_OP_Y = 19;
	public final static int G_PARAMS = 20;
	public final static int G_PROCEDIMIENTO =21;
	public final static int G_RANGE = 22;
	public final static int G_RANGO = 23;
	public final static int G_REGRESA = 24;
	public final static int G_SI = 25;
	public final static int G_SIGNO = 26;
	public final static int G_STATEMENT = 27;
	public final static int G_TERMINO = 28;
	public final static int G_TIPO = 29;
	public final static int G_TIPOS = 30;
	public final static int G_UDIM = 31;
	public final static int G_VARIABLES =32;
	public final static int G_U_PARAM = 33;
	public final static int G_ASIGNACION = 34;
	
	/**
	 * Constructor.
	 * @param parser
	 */
	public Grammar(Parser parser)
	{
		this.parser = parser;
		errorFound = false;
	}
	
	/**
	 * Calls the first grammar of the language and start the analysis.
	 * @return
	 */
	public void startGrammaticalAnalysis()
	{
		grammarPrograma();
	}
	
	/**
	 * Grammar for "programa".
	 * @return Grammar analysis result.
	 */
	private void grammarPrograma()
	{	
		nextToken();
		if(!checkTerminalValue(TERMINAL_PROGRAMA, PRIORITY_LOW, G_PROGAMA)) grammarLibraries();
		checkTerminalValue(TERMINAL_PROGRAMA, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_PRINCIPAL, PRIORITY_LOW, G_PROGAMA))
		{
			if(!checkTerminalValue(TERMINAL_INICIO, PRIORITY_LOW, G_PROGAMA))
			{
				//Controls the cycle to declare variables, constants and types.
				boolean stillDeclaring = true;
				//Flag that turns on whenever a declaration of a variable, type or constant
				//is done.
				boolean hasDeclaration = false;
				while(!checkTerminalValue(TERMINAL_INICIO, PRIORITY_LOW, G_PROGAMA) && 
						!checkTerminalValue(TERMINAL_PRINCIPAL, PRIORITY_LOW, G_PROGAMA) && 
						stillDeclaring)
				{
					if(checkTerminalValue(TERMINAL_CONSTANTE, PRIORITY_LOW, G_PROGAMA))
					{
						stillDeclaring = grammarConstantes();
						hasDeclaration = true;
					}
					else if(checkTerminalValue(TERMINAL_TIPO, PRIORITY_LOW, G_PROGAMA))
					{
						stillDeclaring = grammarTipos();
						hasDeclaration = true;
					}
					else
					{
						stillDeclaring = grammarVariables();
						hasDeclaration = true;
					}
					//Triggers the feed of another token if a delaration was done.
					if(hasDeclaration)nextToken();
				}
			}
			if(checkTerminalValue(TERMINAL_INICIO, PRIORITY_LOW, G_PROGAMA))
			{
				nextToken();
				if(checkTerminalValue(TERMINAL_DECLARA, PRIORITY_HIGH, G_PROGAMA)) 
					grammarFuncProc();
			}
		}
		checkTerminalValue(TERMINAL_PRINCIPAL, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_LOW, G_PROGAMA)) grammarBlock();
		checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalValue(TERMINAL_PRINCIPAL, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();						
		checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROGAMA);

		nextToken();
		checkTerminalValue(TERMINAL_DOT, PRIORITY_HIGH, G_PROGAMA);
		
		if(hasUnreachableCode())
		{
			parser.addError(Error.createParsingFreeError(parser.getLineOfCode(), 
					"Unreachable code."));
			errorFound = true;
		}				
	}
	
	private boolean grammarLibraries()
	{
		if(!checkTerminalValue(TERMINAL_USANDO, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_LIBRERIA, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		return true;
	}
	
	private boolean grammarConstantes()
	{
		nextToken();
		if(grammarTipo(PRIORITY_HIGH))
		{
			//Controls the cycle to declare more than one constant of the same type.
			boolean moreConstants = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_CONSTANTES)) return false;
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_ASIGNATION, PRIORITY_HIGH, G_CONSTANTES)) 
					return false;   
				
				nextToken();
				if(grammarLiteral(PRIORITY_HIGH))
				{
					nextToken();
					if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_CONSTANTES))
					{
						if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, G_CONSTANTES)) 
							return false;
						else moreConstants = true;
					}
					else
					{
						moreConstants = false;
						return true;
					}
				}
				else return false;
			}while(moreConstants);
		}
		return false;
	}
	
	private boolean grammarVariables()
	{
		if(grammarTipo(PRIORITY_HIGH))
		{
			//Controls the cycle to declare more than one variable of the same type.
			boolean moreVariables = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_VARIABLES)) return false;
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_VARIABLES))
				{
					if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_VARIABLES))
					{
						if(!checkTerminalValue(TERMINAL_ASIGNATION, PRIORITY_HIGH, G_VARIABLES))
							return false;
						else
						{
							nextToken();
							if(grammarLiteral(PRIORITY_HIGH))
							{
								nextToken();
								if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, 
										G_VARIABLES))
								{
									if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, 
											G_VARIABLES)) 
										return false;
									else moreVariables = true;
								}
								else return true;
							}
							else return false;
						}
					}
					else moreVariables = true;
				}
				else
				{
					moreVariables = false;
					return true;
				}
			}
			while(moreVariables);
		}
		return false;
	}
	
	private boolean grammarTipos()
	{
		nextToken();
		if(grammarTipo(PRIORITY_HIGH))
		{
			boolean moreTypes = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_TIPOS)) return false;
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_ARREGLO, PRIORITY_HIGH, G_TIPOS)) return false;
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_TIPOS)) return false;
				
				boolean moreRanges = false;
				do
				{
					if(moreRanges = grammarRango())
					{
						nextToken();
						if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_TIPOS))
						{
							if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, G_TIPOS))
								return false;
						}
						else
						{
							nextToken();
							if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_TIPOS))
								return false;
							return true;
						}
					}
					else return false;
				}
				while(moreRanges);
			}
			while(moreTypes);
		}
		return false;
	}
	
	private boolean grammarFuncProc()
	{
		//Flag that turns on when a function or procedure has been declared correctly.
		boolean oneDeclared = false;
		while(true)
		{
			nextToken();
			if(checkTerminalValue(TERMINAL_FUNCION, PRIORITY_LOW, G_FUNC_PROC))
			{
				if(!grammarFuncion()) return false;
				oneDeclared = true;
				continue;
			}
			else if(checkTerminalValue(TERMINAL_PROCEDIMIENTO, PRIORITY_LOW, G_PROCEDIMIENTO))
			{
				if(!grammarProcedimiento()) return false;
				oneDeclared = true;
				continue;
			}
			if(oneDeclared)
			{
				//pushToken();
				return true;
			}
			else
			{
				String[] expected = {TERMINAL_FUNCION, TERMINAL_PROCEDIMIENTO};
				parser.addError(Error.createParsingError(parser.getLineOfCode(), value, expected,
						getGrammarNameByIndex(G_FUNC_PROC)));
			}
		}
	}
	
	private boolean grammarFuncion()
	{
		nextToken();
		if(grammarTipo(PRIORITY_HIGH))
		{
			nextToken();
			if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_FUNCION))
			{
				if(grammarParams())
				{
					if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_FUNCION)) 
						return false;
					nextToken();
				}
				else return false;
			}
			if(grammarTipo(PRIORITY_LOW))
			{
				//Declare variables.
				boolean stillDeclaring = true;
				while(stillDeclaring)
				{
					if(!grammarVariables()) return false;
					nextToken();
					if(grammarTipo(PRIORITY_LOW) || checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW,
							G_PROCEDIMIENTO))
						stillDeclaring = true;
					else stillDeclaring = false;
				}
			}
			if(!grammarBlock()) return false;
			
			if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_FUNCION)) return false;
			
			return true;
		}
		return false;
	}
	
	private boolean grammarParams()
	{
		while(true)
		{
			if(grammarTipo(PRIORITY_HIGH))
			{
				boolean declaringSameType = false;
				do
				{
					nextToken();
					if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PARAMS)) return false;
					
					nextToken();
					if(checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_PARAMS)) return true;
					if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_PARAMS))
					{
						nextToken();
						if(grammarTipo(PRIORITY_LOW)) declaringSameType = false;
						else return true;
					}
					else declaringSameType = true;
				}
				while(declaringSameType);
			}
			else return false;
		}
	}
	
	private boolean grammarProcedimiento()
	{
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_PROCEDIMIENTO))
		{
			if(grammarParams())
			{
				if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_PROCEDIMIENTO)) 
					return false;
				nextToken();
			}
			else return false;
		}
		if(grammarTipo(PRIORITY_LOW))
		{
			//Declare variables.
			boolean stillDeclaring = true;
			while(stillDeclaring)
			{
				if(!grammarVariables()) return false;
				nextToken();
				if(grammarTipo(PRIORITY_LOW) || checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW,
						G_PROCEDIMIENTO))
					stillDeclaring = true;
				else stillDeclaring = false;
			}
		}
		if(!grammarBlock()) return false;

		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		return true;
	}
	
	private boolean grammarBlock()
	{
		//nextToken();
		if(!checkTerminalValue(TERMINAL_INICIO, PRIORITY_HIGH, G_BLOCK)) return false;

		nextToken();
		grammarStatement();
		return true;
	}

	private void grammarStatement()
	{
		while(true)
		{
			if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_STATEMENT))
			{
				if(grammarCommand())
				{
					nextToken();
					if(checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_STATEMENT))
						nextToken();
					else return;
				}
				else return;
			}
			else nextToken();
		}
	}
	
	private boolean grammarCommand()
	{
		if(checkTerminalValue(TERMINAL_CICLO, PRIORITY_LOW, G_COMMAND)) 
			return grammarCiclo();
		else if(checkTerminalValue(TERMINAL_LEE, PRIORITY_LOW, G_COMMAND)) 
			return grammarLee();
		else if(checkTerminalValue(TERMINAL_DESPLIEGA, PRIORITY_LOW, G_COMMAND)) 
			return grammarDespliega();
		else if(checkTerminalValue(TERMINAL_REGRESA, PRIORITY_LOW, G_COMMAND)) 
			return grammarRegresa();
		else if(checkTerminalValue(TERMINAL_SI, PRIORITY_LOW, G_COMMAND)) return grammarSi();
		else if(tag == Token.IDENTIFIER)
		{
			nextToken();
			if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_LOW, G_COMMAND)) 
				return grammarLFunc(PRIORITY_HIGH);
			else
				return grammarAsignacion();
		}
		
		return false;
	}
	
	private boolean grammarAsignacion()
	{
		if(checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_LOW, G_ASIGNACION))
		{
			if(!grammarUdim()) return false;
			nextToken();
		}
		
		if(!checkTerminalValue(TERMINAL_ASIGNATION, PRIORITY_HIGH, G_ASIGNACION)) return false;
		
		nextToken();
		return grammarExpr();
	}
	
	private boolean grammarCiclo()
	{
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_EN, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!grammarExpr()) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_HASTA, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!grammarExpr()) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_LOW, G_CICLO))
		{
			if(checkTerminalValue(TERMINAL_PASO, PRIORITY_LOW, G_CICLO))
			{
				nextToken();
				if(!grammarExpr()) return false;
				nextToken();
			}
		}
		grammarStatement();
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_CICLO, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_CICLO)) return false;
		return true;
	}
	
	private boolean grammarLee()
	{
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_LEE)) return false;
		
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_LEE)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_LEE))
		{
			if(grammarUdim())
			{
				nextToken();
				return true;
			}
			return false;
		}
		return true;
	}
	
	private boolean grammarUdim()
	{
		if(!checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_HIGH, G_UDIM)) return false;
		
		nextToken();
		while(true)
		{
			if(!grammarExpr()) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_RIGHT_BRAC, PRIORITY_LOW, G_UDIM))
			{
				if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, G_UDIM)) return false;
			}
			else return true;
		}
	}
	
	private boolean grammarExpr()
	{
		while(true)
		{
			if(!grammarOpy()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_O, PRIORITY_LOW, G_EXPR))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}

	private boolean grammarOpy()
	{
		while(true)
		{
			if(!grammarOpNo()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_Y, PRIORITY_LOW, G_OP_Y))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}
	
	private boolean grammarTermino()
	{
		if(checkTerminalTag(Token.IDENTIFIER, PRIORITY_LOW, G_TERMINO))
		{
			nextToken();
			if(checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_LOW, G_TERMINO))
				return grammarUdim();
			else if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_LOW, G_TERMINO))
				return grammarLFunc(PRIORITY_HIGH);
			else
			{
				pushToken();
				return true;
			}
		}
		else if(grammarLiteral(PRIORITY_LOW)) return true;
		else if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_TERMINO))
		{
			nextToken();
			if(!grammarExpr()) return false;
			nextToken();
			if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_TERMINO)) return false;
			return true;
		}
		return false;
	}
	
	private boolean grammarSigno()
	{
		if(checkTerminalValue(TERMINAL_OP_SUB, PRIORITY_LOW, G_SIGNO))
			nextToken();
		if(grammarTermino()) return true;
		return false;
	}
	
	private boolean grammarExpo()
	{
		while(true)
		{
			if(grammarSigno())
			{
				nextToken();
				if(checkTerminalValue(TERMINAL_OP_EXP, PRIORITY_LOW, G_EXPO))
					nextToken();
				else
				{
					pushToken();
					return true;
				}
			}
			else return false;
		}
	}
	
	private boolean grammarMulti()
	{
		while(true)
		{
			if(!grammarExpo()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_MUL, PRIORITY_LOW, G_MULTI))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_DIV, PRIORITY_LOW, G_MULTI))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_MOD, PRIORITY_LOW, G_MULTI))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}
	
	private boolean grammarOpSR()
	{
		while(true)
		{
			if(!grammarMulti()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_ADD, PRIORITY_LOW, G_OP_SR))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_SUB, PRIORITY_LOW, G_OP_SR))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}
	
	private boolean grammarOpRel()
	{
		while(true)
		{
			if(!grammarOpSR()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_LESS_THAN, PRIORITY_LOW, G_OP_REL))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_GREATER_THAN, PRIORITY_LOW, G_OP_REL))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_LESS_OR_EQUAL_THAN, PRIORITY_LOW, G_OP_REL))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_GREATER_OR_EQUAL_THAN, PRIORITY_LOW, G_OP_REL))
				nextToken();
			else if(checkTerminalValue(TERMINAL_OP_DIFFERENT, PRIORITY_LOW, G_OP_REL))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}
	
	private boolean grammarOpNo()
	{
		if(checkTerminalValue(TERMINAL_NO, PRIORITY_LOW, G_OP_NO))
			nextToken();
		if(grammarOpRel()) return true;
		return false;
	}
	
	private boolean grammarLFunc(int priority)
	{
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, priority, G_L_FUNC)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_L_FUNC))
		{
			if(!grammarUparam()) return false;
			nextToken();
		}
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_L_FUNC)) return false;
		return true;
	}
	
	private boolean grammarUparam()
	{
		while(true)
		{
			if(!grammarExpr()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_U_PARAM))
				nextToken();
			else
			{
				pushToken();
				return true;
			}
		}
	}
	
	private boolean grammarDespliega()
	{
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_DESPLIEGA)) return false;
		
		nextToken();
		while(true)
		{
			if(!grammarExpr()) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_OP_ADD, PRIORITY_LOW, G_DESPLIEGA))
			{
				if(checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_DESPLIEGA))
					return true;
				return false;
			}
			else nextToken();
		}
	}
	
	private boolean grammarRegresa()
	{
		nextToken();
		if(!grammarExpr())
			pushToken();
		return true;
	}
	
	private boolean grammarSi()
	{
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		if(!grammarExpr()) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		grammarStatement();
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_LOW, G_SI))
		{
			if(!checkTerminalValue(TERMINAL_SINO, PRIORITY_LOW, G_SI))
				grammarStatement();
			else
			{
				nextToken();
				grammarStatement();
			}
		}
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_DE, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SI, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_SI)) return false;
		
		return true;
	}
	
	private boolean grammarTipo(int priority)
	{
		String[] values = {TERMINAL_ENTERO, TERMINAL_DECIMAL, TERMINAL_ALFANUMERICO, 
				TERMINAL_LOGICO};
		//Check for this extra tag.
		int[] tags = {Token.IDENTIFIER};
		
		if(!checkTerminalValueTag(values, tags, priority, G_TIPO)) return false;
		return true;
	}

	private boolean grammarLiteral(int priority)
	{
		int[] tags = {Token.CONSTANT_INT, Token.CONSTANT_DECIMAL, Token.CONSTANT_LOGICAL,
				Token.CONSTANT_STRING, Token.IDENTIFIER};
		if(checkTerminalTag(tags, priority, G_LITERAL)) return true;
		return false;
	}
	
	private boolean grammarRango()
	{
		nextToken();
		if(grammarLiteral(PRIORITY_HIGH))
		{
			nextToken();
			if(!checkTerminalValue(TERMINAL_HASTA, PRIORITY_HIGH, G_RANGO)) return false;
			
			nextToken();
			if(grammarLiteral(PRIORITY_HIGH)) return true;
			return false;
		}
		return false;
	}
	/**
	 * Checks if errors where found during grammatical analysis.
	 * @return errorFound.
	 */
	public boolean getErrorFound()
	{
		return errorFound;
	}
	
	/**
	 * Feeds the grammar with the next token obtained by the scanner.
	 */
	private void nextToken()
	{
		token = parser.getToken();
		try
		{
			value = token.getValue();
			tag = token.getTag();
		}
		catch(NullPointerException e)
		{
			parser.addError(Error.parsingErrorNoTokenFound(parser.getLineOfCode()));
			errorFound = true;
		}
	}
	
	/**
	 * Pushes the actual token to the stack.
	 */
	private void pushToken()
	{
		parser.pushTokenToStack(new Token(tag, value));
	}
	
	/**
	 * Checks if there's unreachable code in the input file.
	 * @return if has unreacheable code.
	 */
	private boolean hasUnreachableCode()
	{
		if(parser.getToken() != null) return true;
		else return false;
	}
	
	/**
	 * Checks if the value matches with the terminal.
	 * @param terminal
	 * @param priority
	 * @return true if matched, then false.
	 */
	private boolean checkTerminalValue(String terminal, int priority, int grammarIndex)
	{
		switch(priority)
		{
			case PRIORITY_HIGH:
				if(value.equals(terminal)) return true;
				else
				{
					parser.addError(Error.createParsingError(parser.getLineOfCode(), value, 
							terminal, getGrammarNameByIndex(grammarIndex)));
					errorFound = true;
					return false;
				}
			case PRIORITY_LOW:
				if(value.equals(terminal)) return true;
				return false;
			default: return false;
		}
	}
	
	/**
	 * Checks if the value matches with any of the terminals.
	 * @param terminal
	 * @param priority
	 * @return true if matched, then false.
	 */
	private boolean checkTerminalValueTag(String[] terminals, int[] tags, int priority,
			int grammarIndex)
	{
		switch(priority)
		{
			case PRIORITY_HIGH:
				int tagsLength = tags.length;
				int terminalsLength = terminals.length;
				for(int i = 0; i < terminalsLength; i++)
					if(value.equals(terminals[i])) return true;
				for(int i = 0; i < tagsLength; i++)
					if(tag == tags[i]) return true;
				
				String[] expected = new String[terminalsLength + tags.length];
				int i;
				for(i = 0; i < terminalsLength; i++)
					expected[i] = terminals[i];
				for(int j = 0; j < tagsLength; j++)
					expected[i] = Token.getTagString(tags[j]);
				
				parser.addError(Error.createParsingError(parser.getLineOfCode(), value, 
							expected, getGrammarNameByIndex(grammarIndex)));
					errorFound = true;
					return false;
			case PRIORITY_LOW:
				int tagsSize = tags.length;
				int terminalsSize = terminals.length;
				int x;
				for(x = 0; x < terminalsSize; x++)
					if(value.equals(terminals[x])) return true;
				for(int j = 0; j < tagsSize; j++)
					if(tag == tags[j]) return true;
				return false;
			default: return false;
		}
	}
	
	/**
	 * Checks if the value matches with the terminal.
	 * @param terminal
	 * @param priority
	 * @return true if matched, then false.
	 */
	private boolean checkTerminalTag(int terminalTag, int priority, int grammarIndex)
	{
		switch(priority)
		{
			case PRIORITY_HIGH:
				if(tag == terminalTag) return true;
				else
				{
					parser.addError(Error.createParsingError(parser.getLineOfCode(), value, 
							Token.getTagString(terminalTag), getGrammarNameByIndex(grammarIndex)));
					errorFound = true;
					return false;
				}
			case PRIORITY_LOW:
				if(tag == terminalTag) return true;
				return false;
			default: return false;
		}
	}
	
	/**
	 * Checks if the value matches with the terminal.
	 * @param terminal
	 * @param priority
	 * @return true if matched, then false.
	 */
	private boolean checkTerminalTag(int[] terminalTags, int priority, int grammarIndex)
	{
		switch(priority)
		{
			case PRIORITY_HIGH:
				int size = terminalTags.length;
				for(int i = 0; i < size; i++) 
					if(tag == terminalTags[i]) return true;
				
				String expected[] = new String[size];
				for(int i = 0; i < size; i++)
					expected[i] = Token.getTagString(terminalTags[i]);
				parser.addError(Error.createParsingError(parser.getLineOfCode(), value, 
						expected, getGrammarNameByIndex(grammarIndex)));
				errorFound = true;
				return false;
			case PRIORITY_LOW:
				int length = terminalTags.length;
				for(int i = 0; i < length; i++) 
					if(tag == terminalTags[i]) return true;
				return false;
			default: return false;
		}
	}
	
	/**
	 * Getsthe name of the grammar used.
	 * @param index of grammar.
	 * @return name of grammar.
	 */
	private String getGrammarNameByIndex(int index)
	{
		switch(index)
		{
			case G_PROGAMA: return "programa";
			case G_BLOCK: return "block";
			case G_CICLO: return "ciclo";
			case G_COMMAND: return "command";
			case G_CONSTANTES: return "constantes";
			case G_DESPLIEGA: return "despliega";
			case G_EXPO: return "expo";
			case G_EXPR: return "expr";
			case G_FUNCION: return "funcion";
			case G_FUNC_PROC: return "funcProc";
			case G_LEE: return "lee";
			case G_L_FUNC: return "LFunc";//Call to function. 
			case G_LIBRARIES: return "Libraries";
			case G_LITERAL: return "literal";
			case G_MULTI: return "multi";
			case G_OP_NO: return "opNo";
			case G_OP_REL: return "opRel";
			case G_OP_SR: return "opSR";
			case G_OP_Y: return "opY";
			case G_PARAMS: return "params";
			case G_PROCEDIMIENTO: return "procedimiento";
			case G_RANGE: return "range";
			case G_RANGO: return "rango";
			case G_REGRESA: return "regresa";
			case G_SI: return "si";
			case G_SIGNO: return "signo";
			case G_STATEMENT: return "statement";
			case G_TERMINO:return "termino";
			case G_TIPO: return "tipo";
			case G_TIPOS: return "tipos";
			case G_UDIM: return "udim";
			case G_VARIABLES: return "variables";
			case G_U_PARAM: return "Uparam";//Parameters use.
			case G_ASIGNACION: return "asignacion"; 
			default: return "ERROR";
		}
	}
}
