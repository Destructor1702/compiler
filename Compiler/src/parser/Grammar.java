package parser;

import java.util.ArrayList;
import java.util.Stack;

import codegen.CodeGenerator;
import codegen.CodeInstruction;
import error.Error;
import extra.Numeric;
import lexical.Token;
import symtable.DataType;
import symtable.SymbolTableElement;

public class Grammar implements OperationResult, CodeInstruction
{
	private final static int PRIORITY_HIGH = 1;
	private final static int PRIORITY_LOW = 2;
	
	private Parser parser;
	private CodeGenerator codeGen;
	private boolean errorFound;
	private Token token;
	private String value;
	private int tag;
	
	/**
	 * Variables for the element that will be inserted into the symbol table.
	 */
	private String eName;
	private int eClass;
	private String eType;
	private ArrayList<Integer> eDim;
	private String eValue;
	private int eLine;
	
	/**
	 * Buffers used by the semantic analyzer to manage the parameters in functions and procedures.
	 */
	private boolean hasParameters;
	private ArrayList<Parameter> parameters;

	/**
	 * Flag to indicate if the declaration of a variable is being local, that is, inside a function
	 * or procedure.
	 */
	private boolean isLocalDeclaration;
	private String localFunctionName;
	private boolean hasExpression;
	
	private final static String FUNCTION = "F";
	private final static String PROCEDURE = "P";
	private String functionProcedure = "";
	private boolean hasReturn;
	private String callingFP;
	private boolean rightPartOfAsignation;
	
	/**
	 * Buffers for the operation results.
	 */
	private final static int UNARY_OP = 1;
	private final static int BINARY_OP = 2;
	private Stack<String> typeStack;
	
	/**
	 * Buffer for use of dimensions.
	 */
	private ArrayList<String> dimInUse;
	
	private boolean isInsideDespliega;
	
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
	public Grammar(Parser parser, CodeGenerator codeGen)
	{
		this.parser = parser;
		this.codeGen = codeGen;
		errorFound = false;
		eDim = new ArrayList<Integer>();
		hasParameters = false;
		parameters = new ArrayList<Parameter>();
		isLocalDeclaration = false;
		dimInUse = new ArrayList<String>();
		typeStack = new Stack<String>();
		rightPartOfAsignation = false;
		isInsideDespliega = false;
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
		String nameAux;
		nextToken();
		if(!checkTerminalValue(TERMINAL_PROGRAMA, PRIORITY_LOW, G_PROGAMA)) grammarLibraries();
		checkTerminalValue(TERMINAL_PROGRAMA, PRIORITY_HIGH, G_PROGAMA);
		
		nextToken();
		checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROGAMA);
		eName = value;
		nameAux = value;
		eLine = parser.getLineOfCode();
		eClass = SymbolTableElement.CLASS_PROGRAMA;
		prepareElement();
		
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
					if(!grammarFuncProc()) return;
			}
		}
		checkTerminalValue(TERMINAL_PRINCIPAL, PRIORITY_HIGH, G_PROGAMA);
		codeGen.setMainTag();
		
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
		if(!nameAux.equals(value))
			parser.addSemanticError("Wrong identifier for main program at line " + 
					parser.getLineOfCode() + "\nexpected: " + nameAux + "\narrived: "
					+ value);
		
		nextToken();
		checkTerminalValue(TERMINAL_DOT, PRIORITY_HIGH, G_PROGAMA);
		
		if(hasUnreachableCode())
		{
			parser.addParsingError(Error.createParsingFreeError(parser.getLineOfCode(), 
					"Unreachable code."));
			errorFound = true;
		}
		
		codeGen.addInstruction(OPR, "0", END_OF_PROGRAM);
	}
	
	private boolean grammarLibraries()
	{
		if(!checkTerminalValue(TERMINAL_USANDO, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_LIBRERIA, PRIORITY_HIGH, G_LIBRARIES)) return false;
		
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_LIBRARIES)) return false;
		eName = value;
		eLine = parser.getLineOfCode();
		eClass = SymbolTableElement.CLASS_LIBRERIA;
		prepareElement();
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_LIBRARIES))
		{
			boolean declaring = true;
			while(declaring)
			{
				if(checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_LIBRARIES))
				{
					nextToken();
					if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_LIBRARIES)) 
						return false;
					eName = value;
					prepareElement();
					
					nextToken();
					if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_LIBRARIES))
						declaring = true;
					else declaring = false;
				}
				else declaring = false;
			}
		}
		
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_LIBRARIES)) return false;
		nextToken();
		return true;
	}
	
	private boolean grammarConstantes()
	{
		nextToken();
		DataType dataType = new DataType();
		if(grammarTipo(PRIORITY_HIGH, dataType))
		{
			eType = dataType.getDataType();
			boolean moreConstants = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_CONSTANTES)) return false;
				eName = value;
				eLine = parser.getLineOfCode();
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_ASIGNATION, PRIORITY_HIGH, G_CONSTANTES)) 
					return false;   
				
				nextToken();
				if(grammarLiteral(PRIORITY_HIGH))
				{
					eValue = value;
					if(!checkDataTypeDeclaration(eType, tag, parser.getLineOfCode()))
						return false;
					nextToken();
					if(isLocalDeclaration)
					{
						eClass = SymbolTableElement.CLASS_LOCAL;
						eName = eName + "$" + localFunctionName;
					}
					else
						eClass = SymbolTableElement.CLASS_CONSTANTE;
					prepareElement();
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
		DataType dataType = new DataType();
		if(grammarTipo(PRIORITY_HIGH, dataType))
		{
			eType = dataType.getDataType();
			//Controls the cycle to declare more than one variable of the same type.
			boolean moreVariables = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_VARIABLES)) return false;
				eName = value;
				eLine = parser.getLineOfCode();
				
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
								eValue = value;
								if(isLocalDeclaration)
								{
									eClass = SymbolTableElement.CLASS_LOCAL;
									eName = eName + "$" + localFunctionName;
								}
								else
									eClass = SymbolTableElement.CLASS_VARIABLE;
								if(dataType.isArray())
									eClass = SymbolTableElement.CLASS_DECLARATION_TIPO;
								prepareElement();
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
					else
					{
						moreVariables = true;
						eValue = "";
						if(isLocalDeclaration)
						{
							eClass = SymbolTableElement.CLASS_LOCAL;
							eName = eName + "$" + localFunctionName;
						}
						else
							eClass = SymbolTableElement.CLASS_VARIABLE;
						if(dataType.isArray())
							eClass = SymbolTableElement.CLASS_DECLARATION_TIPO;
						prepareElement();
					}
				}
				else
				{
					moreVariables = false;
					eValue = "";
					if(isLocalDeclaration)
					{
						eClass = SymbolTableElement.CLASS_LOCAL;
						eName = eName + "$" + localFunctionName;
					}
					else
						eClass = SymbolTableElement.CLASS_VARIABLE;
					if(dataType.isArray())
						eClass = SymbolTableElement.CLASS_DECLARATION_TIPO;
					prepareElement();
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
		DataType dataType = new DataType();
		if(grammarTipo(PRIORITY_HIGH, dataType))
		{
			eType = dataType.getDataType();
			boolean moreTypes = false;
			do
			{
				nextToken();
				if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_TIPOS)) return false;
				eName = value;
				eLine = parser.getLineOfCode();
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_ARREGLO, PRIORITY_HIGH, G_TIPOS)) return false;
				
				nextToken();
				if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_TIPOS)) return false;
				
				boolean moreRanges = false;
				do
				{
					if(moreRanges = grammarRange())
					{
						nextToken();
						if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_TIPOS))
						{
							if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, G_TIPOS))
								return false;
						}
						else
						{
							eClass = SymbolTableElement.CLASS_TIPO;
							prepareElement();
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
			if(oneDeclared) return true;
			else
			{
				String[] expected = {TERMINAL_FUNCION, TERMINAL_PROCEDIMIENTO};
				parser.addParsingError(Error.createParsingError(parser.getLineOfCode(), value, expected,
						getGrammarNameByIndex(G_FUNC_PROC)));
			}
		}
	}
	
	private boolean grammarFuncion()
	{
		String nameAux;
		isLocalDeclaration = true;
		hasReturn = false;
		functionProcedure = FUNCTION;
		nextToken();
		DataType dataType = new DataType();
		if(grammarTipo(PRIORITY_HIGH, dataType))
		{
			eType = dataType.getDataType();
			nextToken();
			if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_FUNCION)) return false;
			eName = value;
			nameAux = value;
			eLine = parser.getLineOfCode();
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_FUNCION)) return false;
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_FUNCION))
			{
				if(grammarParams())
				{
					hasParameters = true;
					if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_FUNCION)) 
						return false;
					nextToken();
				}
				else return false;
			}
			else nextToken();
			eClass = SymbolTableElement.CLASS_FUNCION;
			prepareElement();
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
			if(!nameAux.equals(value))
				parser.addSemanticError("Wrong identifier for function at line " + 
						parser.getLineOfCode() + "\nexpected: " + nameAux + "\narrived: "
						+ value);
			
			nextToken();
			if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_FUNCION)) return false;
			
			if(!hasReturn)
				parser.addSemanticError(Error.semanticFreeError(parser
						.getElementByName(localFunctionName).getLine(), 
						"In function: " + localFunctionName + " <return> statement missing."));
			
			localFunctionName = "";
			isLocalDeclaration = false;
			return true;
		}
		return false;
	}
	
	private boolean grammarParams()
	{
		DataType type = new DataType();
		while(true)
		{
			if(grammarTipo(PRIORITY_HIGH, type))
			{
				boolean declaringSameType = false;
				do
				{
					nextToken();
					if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PARAMS)) return false;
					parameters.add(new Parameter(type.getDataType(), value, 
							parser.getLineOfCode()));
					
					nextToken();
					if(checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_PARAMS)) return true;
					if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_PARAMS))
					{
						nextToken();
						if(grammarTipo(PRIORITY_LOW, type)) declaringSameType = false;
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
		String nameAux;
		isLocalDeclaration = true;
		functionProcedure = PROCEDURE;
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		eName = value;
		nameAux = value;
		eLine = parser.getLineOfCode();
		eType = DataType.UNDEFINED;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_PROCEDIMIENTO))
		{
			if(grammarParams())
			{
				hasParameters = true;
				if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_PROCEDIMIENTO)) 
					return false;
				nextToken();
			}
			else return false;
		}
		else nextToken();
		eClass = SymbolTableElement.CLASS_PROCEDIMIENTO;
		prepareElement();
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
		if(!nameAux.equals(value))
			parser.addSemanticError("Wrong identifier for procedure at line " + 
					parser.getLineOfCode() + "\nexpected: " + nameAux + "\narrived: "
					+ value);
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_HIGH, G_PROCEDIMIENTO)) return false;
		
		localFunctionName = "";
		isLocalDeclaration = false;
		return true;
	}
	
	private boolean grammarBlock()
	{
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
					else continue;
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
			String nameAux = value;
			nextToken();
			if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_LOW, G_COMMAND)) 
				return grammarLFunc(nameAux, PRIORITY_HIGH);
			else
				return grammarAsignacion(nameAux);
		}
		
		return false;
	}
	
	private boolean grammarAsignacion(String name)
	{
		SymbolTableElement e = getElementForCall(name);
		if(e == null) return false;
		if(e.getElementClass() == SymbolTableElement.CLASS_CONSTANTE)
		{
			parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
					"Can't reasing a value to the constant '" + e.getName() + "'"));
			return false;
		}
		boolean hasDim = false;
		if(checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_LOW, G_ASIGNACION))
		{
			if(!e.isDimensioned())
			{
				parser.addSemanticError("At grammar 'asignacion' variable '" + name + "' at line: "
						+ parser.getLineOfCode() + " shouldn't be dimensioned.");
				return false;
			}
			if(!grammarUdim(e)) return false;
			nextToken();
			hasDim = true;
		}
		if(e.isDimensioned() && !hasDim)
		{
			parser.addSemanticError("At grammar 'asignacion' variable '" + name + "' at line: "
					+ parser.getLineOfCode() + " has to be dimensioned.");
			return false;
		}
		if(!checkTerminalValue(TERMINAL_ASIGNATION, PRIORITY_HIGH, G_ASIGNACION)) return false;
		
		rightPartOfAsignation = true;
		nextToken();
		if(grammarExpr())
		{
			rightPartOfAsignation = false;
			if(checkTypeFromTypeStack(e.getType()))
			{
				codeGen.addInstruction(STO, "0", name);
				return true;
			}
			return false;
		}
		return false;
	}
	
	private boolean grammarCiclo()
	{
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_CICLO)) return false;
		String id = value;
		SymbolTableElement e = getElementForCall(id);
		if(e == null) return false;
		e.setValue("");//HARDCODED
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_EN, PRIORITY_HIGH, G_CICLO)) return false;
		
		nextToken();
		if(!grammarExpr()) return false;
		checkTypeFromTypeStack(DataType.ENTERO);
		codeGen.addInstruction(STO, "0", id);
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_HASTA, PRIORITY_HIGH, G_CICLO)) return false;
		codeGen.addInstruction(LOD, id, "0");
		int lineFinalJump = codeGen.getInstructionNumber() - 1;
		
		nextToken();
		if(!grammarExpr()) return false;
		checkTypeFromTypeStack(DataType.ENTERO);
		codeGen.addInstruction(OPR, "0", LESS_OR_EQUAL_THAN);
		String tagJumpCond = codeGen.getNextTag();
		codeGen.addInstruction(JMC, "F", tagJumpCond);
		
		boolean hasPaso = false;
		nextToken();
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_LOW, G_CICLO))
		{
			if(checkTerminalValue(TERMINAL_PASO, PRIORITY_LOW, G_CICLO))
			{
				hasPaso = true;
				codeGen.setActiveInstructionBuffer(true);
				nextToken();
				if(!grammarExpr()) return false;
				checkTypeFromTypeStack(DataType.ENTERO);
				nextToken();
			}
		}
		
		grammarStatement();
		codeGen.addInstruction(LOD, id, "0");
		if(!hasPaso)
			codeGen.addInstruction(LIT, "1", "0");
		else
		{
			codeGen.addBufferToMainInstructionSet();
			codeGen.setActiveInstructionBuffer(false);
		}
		codeGen.addInstruction(OPR, "0", ADD);
		codeGen.addInstruction(STO, "0", id);
		if(!checkTerminalValue(TERMINAL_FIN, PRIORITY_HIGH, G_CICLO)) return false;
		String tagFinalJump = codeGen.getNextTag();
		codeGen.addInstruction(JMP, "0", tagFinalJump);
		codeGen.addTagToSymbolTable(tagFinalJump, lineFinalJump);
		int lineJumpConditional = codeGen.getInstructionNumber();
		codeGen.addTagToSymbolTable(tagJumpCond, lineJumpConditional);
		
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
		boolean isDim = false;
		String auxName;
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_LEE)) return false;
		
		nextToken();
		if(!checkTerminalTag(Token.IDENTIFIER, PRIORITY_HIGH, G_LEE)) return false;
		auxName = value;
		SymbolTableElement e = getElementForCall(auxName);
		if(e == null) return false;
		
		nextToken();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_LEE))
		{
			if(grammarUdim(e))
			{
				nextToken();
				isDim = true;
			}
			else return false;
		}
		if(!isDim)
		{
			if(e.isDimensioned())
			{
				parser.addSemanticError("Can't access to variable '" + auxName + "' at line: "
						+ parser.getLineOfCode() + " it is dimensioned.");
				return false;
			}
		}
		return true;
	}
	
	private boolean grammarUdim(SymbolTableElement e)
	{
		dimInUse.clear();
		if(!checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_HIGH, G_UDIM)) return false;
		
		nextToken();
		while(true)
		{
			if(!grammarExpr()) return false;
			nextToken();
			if(!checkTerminalValue(TERMINAL_RIGHT_BRAC, PRIORITY_LOW, G_UDIM))
			{
				if(!checkTerminalValue(TERMINAL_COMA, PRIORITY_HIGH, G_UDIM)) return false;
				nextToken();
			}
			else
			{
				ArrayList<Integer> dimElement = e.getDim();
				int dimElementUseSize = dimElement.size() / 2;
				int dimInUseSize = dimInUse.size();
				if(dimInUse.size() < dimElementUseSize)
				{
					parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
							"Not enough dimensions for variable '" + e.getName() + "'"
									+ "\n Needed  : " + dimElementUseSize
									+ "\n Received: " + dimInUseSize));
					return false;
				}
				else if(dimInUseSize > dimElementUseSize)
				{
					parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
							"Too many dimensions for variable '" + e.getName() + "'"
									+ "\n Needed  : " + dimElementUseSize
									+ "\n Received: " + dimInUseSize));
					return false;
				}
				else
				{
					for(int i = 0; i < dimInUseSize; i++)
					{
						String dimInUseAux = dimInUse.get(i);
						SymbolTableElement eAux;
						int dimAux;
						if(!Numeric.isNumeric(dimInUseAux))
						{
							eAux = getElementForCall(dimInUseAux);
							if(eAux == null)
							{
								parser.addSemanticError(Error.semanticFreeError(parser
										.getLineOfCode(), "Can't get reference from '"
										+ dimInUseAux + "' for dimensioned variable '"
										+ e.getName() + "'"));
								return false;
							}
							dimInUseAux = eAux.getValue();
						}
						try
						{
							dimAux = Integer.parseInt(dimInUseAux);
							if(dimAux < dimElement.get(i) || dimAux > dimElement.get(i + 1))
							{
								parser.addSemanticError(Error.semanticFreeError(parser
										.getLineOfCode(), "Array out of bounds for variable"
												+ " '"+ e.getName() + "'\n"
												+ "Arrived : [ " + dimAux + " ]\n"
												+ "Expected: [ " + dimElement.get(i)
												+ " ] [ " + dimElement.get(i + 1) + " ]"));
								return false;
							}
						}
						catch(NumberFormatException exception)
						{
							parser.addSemanticError(Error.semanticFreeError(parser
									.getLineOfCode(), "Can't get reference from '"
									+ dimInUseAux + "' for dimensioned variable '"
									+ e.getName() + "'"));
							return false;
						}
					}
				}
				return true;
			}
		}
	}
	
	private boolean grammarTermino()
	{
		if(checkTerminalTag(Token.IDENTIFIER, PRIORITY_LOW, G_TERMINO))
		{
			hasExpression = true;
			String id = value;
			nextToken();
			if(checkTerminalValue(TERMINAL_LEFT_BRAC, PRIORITY_LOW, G_TERMINO))
			{
				SymbolTableElement e = getElementForCall(id);
				if(e != null)
				{
					if(!e.isDimensioned())
					{
						parser.addSemanticError("At grammar 'termino' variable '" + id + 
								"' at line: " + parser.getLineOfCode() + " is not dimensioned.");
						return false;
					}
				}
				else return false;
				return grammarUdim(e);
			}
			else if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_LOW, G_TERMINO))
			{
				return grammarLFunc(id, PRIORITY_HIGH);
			}
			else
			{
				pushToken();
				SymbolTableElement e = getElementForCall(id);
				if(e != null)
				{
					typeStack.push(e.getType());
					if(e.isDimensioned())
					{
						parser.addSemanticError("Can't access to variable '" + id + "' at line: "
								+ parser.getLineOfCode() + " it is dimensioned.");
						return false;
					}
					dimInUse.add(e.getName());
					codeGen.addInstruction(LOD, id, "0");
					return true;
				}
				return false;
			}
		}
		else if(grammarLiteral(PRIORITY_LOW))
		{
			hasExpression = true;
			if(tag != Token.IDENTIFIER)
			{
				switch(tag)
				{
					case Token.CONSTANT_ENTERO:
					case Token.CONSTANT_DECIMAL:
					case Token.CONSTANT_ALFANUM:
						 codeGen.addInstruction(LIT, value, "0" );break;
					case Token.CONSTANT_LOGICO:
						if(value.equals(TERMINAL_VERDADERO)) codeGen.addInstruction(LIT, "V", "0");
						else if(value.equals(TERMINAL_FALSO)) codeGen.addInstruction(LIT, "F", "0");
				}
				typeStack.push(DataType.getDataTypeByTokenTag(tag));
				dimInUse.add(value);
			}
			return true;
		}
		else if(checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_TERMINO))
		{
			hasExpression = true;
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
		String op = "";
		if(checkTerminalValue(TERMINAL_OP_SUB, PRIORITY_LOW, G_SIGNO))
		{
			op = TERMINAL_OP_SUB;
			nextToken();
		}
		if(grammarTermino())
		{
			if(!op.equals(""))
			{
				verifyTypeStack(UNARY_OP, op);
				codeGen.addInstruction(OPR, "0", SIGN_MINUS);
			}
			return true;
		}
		return false;
	}
	
	private boolean grammarExpo()
	{
		String op = "";
		while(true)
		{
			if(grammarSigno())
			{
				nextToken();
				if(checkTerminalValue(TERMINAL_OP_EXP, PRIORITY_LOW, G_EXPO))
				{
					op = TERMINAL_OP_EXP;
					nextToken();
				}	
				else
				{
					pushToken();
					if(!op.equals(""))
					{	
						verifyTypeStack(BINARY_OP, op);
						codeGen.addInstruction(OPR, "0", EXP);
					}
					return true;
				}
			}
			else return false;
		}
	}
	
	private boolean grammarMulti()
	{
		String op = "";
		while(true)
		{
			if(!grammarExpo()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_MUL, PRIORITY_LOW, G_MULTI))
			{
				op = TERMINAL_OP_MUL;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_DIV, PRIORITY_LOW, G_MULTI))
			{
				op = TERMINAL_OP_DIV;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_MOD, PRIORITY_LOW, G_MULTI))
			{
				op = TERMINAL_OP_MOD;
				nextToken();
			}
			else
			{
				pushToken();
				if(!op.equals(""))
				{
					verifyTypeStack(BINARY_OP, op);
					if(op.equals(TERMINAL_OP_MUL)) codeGen.addInstruction(OPR, "0", MUL);
					else if(op.equals(TERMINAL_OP_DIV)) codeGen.addInstruction(OPR, "0", DIV);
					else if(op.equals(TERMINAL_OP_MOD)) codeGen.addInstruction(OPR, "0", MOD);
				}
				return true;
			}
		}
	}
	
	private boolean grammarOpSR()
	{
		String op = "";
		while(true)
		{
			if(!grammarMulti()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_ADD, PRIORITY_LOW, G_OP_SR))
			{
				if(!isInsideDespliega)
				{
					//codeGen.addInstruction(OPR, "0", PRINT);
					op = TERMINAL_OP_ADD;
					nextToken();
					
				}
				else
				{
					pushToken();
					return true;
				}
			}
			else if(checkTerminalValue(TERMINAL_OP_SUB, PRIORITY_LOW, G_OP_SR))
			{
				op = TERMINAL_OP_SUB;
				nextToken();
			}
			else
			{
				pushToken();
				if(!op.equals(""))
				{
					verifyTypeStack(BINARY_OP, op);
					if(op.equals(TERMINAL_OP_ADD))codeGen.addInstruction(OPR, "0", ADD);
					else if(op.equals(TERMINAL_OP_SUB)) codeGen.addInstruction(OPR, "0", SUB);
				}
				
				return true;
			}
		}
	}
	
	private boolean grammarOpRel()
	{
		String op = "";
		while(true)
		{
			if(!grammarOpSR()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_OP_LESS_THAN, PRIORITY_LOW, G_OP_REL))
			{
				op = TERMINAL_OP_LESS_THAN;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_GREATER_THAN, PRIORITY_LOW, G_OP_REL))
			{
				op = TERMINAL_OP_GREATER_THAN;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_LESS_OR_EQUAL_THAN, PRIORITY_LOW, G_OP_REL))
			{
				op = TERMINAL_OP_LESS_OR_EQUAL_THAN;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_GREATER_OR_EQUAL_THAN, PRIORITY_LOW, G_OP_REL))
			{
				op = TERMINAL_OP_GREATER_OR_EQUAL_THAN;
				nextToken();
			}
			else if(checkTerminalValue(TERMINAL_OP_DIFFERENT, PRIORITY_LOW, G_OP_REL))
			{
				op = TERMINAL_OP_DIFFERENT;
				nextToken();
			}
			else
			{
				pushToken();
				if(!op.equals(""))
				{
					verifyTypeStack(BINARY_OP, op);
					if(op.equals(TERMINAL_OP_LESS_THAN)) 
						codeGen.addInstruction(OPR, "0", LESS_THAN);
					else if(op.equals(TERMINAL_OP_GREATER_THAN)) 
						codeGen.addInstruction(OPR, "0", GREATER_THAN);
					else if(op.equals(TERMINAL_OP_LESS_OR_EQUAL_THAN)) 
						codeGen.addInstruction(OPR, "0", LESS_OR_EQUAL_THAN);
					else if(op.equals(TERMINAL_OP_GREATER_OR_EQUAL_THAN)) 
						codeGen.addInstruction(OPR, "0", GREATER_OR_EQUAL_THAN);
				}		
				return true;
			}
		}
	}
	
	private boolean grammarOpNo()
	{
		String op = "";
		if(checkTerminalValue(TERMINAL_NO, PRIORITY_LOW, G_OP_NO))
		{
			op = TERMINAL_NO;
			nextToken();
		}
		if(grammarOpRel())
		{
			if(!op.equals(""))
			{
				verifyTypeStack(UNARY_OP, op);
				codeGen.addInstruction(OPR, "0", NOT);
			}	
			return true;
		}
		return false;
	}
	
	private boolean grammarOpy()
	{
		String op = "";
		while(true)
		{
			if(!grammarOpNo()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_Y, PRIORITY_LOW, G_OP_Y))
			{
				op = TERMINAL_Y;
				nextToken();
			}
			else
			{
				pushToken();
				if(!op.equals(""))
				{
					verifyTypeStack(BINARY_OP, op);
					codeGen.addInstruction(OPR, "0", AND);
				}
				return true;
			}
		}
	}
	
	private boolean grammarExpr()
	{
		String op = "";
		while(true)
		{
			if(!grammarOpy()) return false;
			nextToken();
			if(checkTerminalValue(TERMINAL_O, PRIORITY_LOW, G_EXPR))
			{
				op = TERMINAL_O;
				nextToken();
			}
			else
			{
				pushToken();
				if(!op.equals(""))
				{
					verifyTypeStack(BINARY_OP, op);
					codeGen.addInstruction(OPR, "0", OR);
				}
				return true;
			}
		}
	}
	
	private boolean grammarLFunc(String name, int priority)
	{	
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, priority, G_L_FUNC)) return false;
		
		nextToken();
		ArrayList<String> useOfParams = new ArrayList<String>();
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_LOW, G_L_FUNC))
		{
			if(!grammarUparam(useOfParams)) return false;
			nextToken();
		}
		if(!checkTerminalValue(TERMINAL_RIGHT_PAR, PRIORITY_HIGH, G_L_FUNC)) return false;

		String auxFunctionParamsName = "";
		int useOfParmasSize = useOfParams.size();
		if(useOfParmasSize > 0)
		{
			for(int i = 0; i < useOfParmasSize; i ++)
			{
				String aux = auxFunctionParamsName;
				auxFunctionParamsName = "$" + useOfParams.get(i) + aux;
			}
		}
		SymbolTableElement e;
		if(!auxFunctionParamsName.equals("")) e = getElementForCall(name + auxFunctionParamsName);
		else e = getElementForCall(name);
		if(e == null) return false;
		int elementClass = e.getElementClass();
		switch(elementClass)
		{
			case SymbolTableElement.CLASS_FUNCION: callingFP = FUNCTION; break;
			case SymbolTableElement.CLASS_PROCEDIMIENTO: callingFP = PROCEDURE; break;
			default: callingFP = "";
		}
		if(rightPartOfAsignation)
		{
			if(!callingFP.equals(FUNCTION))
			{
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
						"Only functions can asign values."));
				return false;
			}
		}
		return true;
	}
	
	private boolean grammarUparam(ArrayList<String> useOfParams)
	{
		int params = 0;
		while(true)
		{
			if(!grammarExpr()) return false;
			nextToken();
			params += 1;
			if(checkTerminalValue(TERMINAL_COMA, PRIORITY_LOW, G_U_PARAM))
				nextToken();
			else
			{
				pushToken();
				for(int i = 0; i < params; i++)
					useOfParams.add(typeStack.pop());
				for(int i = params - 1; i >= 0; i--)
					typeStack.push(useOfParams.get(i));
				return true;
			}
		}
	}
	
	private boolean grammarDespliega()
	{
		isInsideDespliega = true;
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
				{
					codeGen.addInstruction(OPR, "0", PRINT);
					isInsideDespliega = false;
					return true;
				}
				return false;
			}
			else
			{
				codeGen.addInstruction(OPR, "0", PRINT);
				nextToken();
			}
		}
	}
	
	private boolean grammarRegresa()
	{
		hasReturn = true;
		if(!isLocalDeclaration)
			parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), "Statement "
					+ "<regresa> can only be inside a function or procedure."));
		hasExpression = false;
		nextToken();
		if(!checkTerminalValue(TERMINAL_SEMICOLON, PRIORITY_LOW, G_TERMINO))
		{
			if(!grammarExpr())
				pushToken();
		}
		if(hasExpression)
			if(functionProcedure.equals(FUNCTION))
				checkTypeFromTypeStack(parser.getElementByName(localFunctionName).getType());
			else
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
						"<regresa> statement inside a procedure can't contain an expression." ));
		else
			if(functionProcedure.equals(FUNCTION))
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
						"<regresa> statement inside a function must contain an expression." ));
				
		return true;
	}
	
	private boolean grammarSi()
	{
		nextToken();
		if(!checkTerminalValue(TERMINAL_LEFT_PAR, PRIORITY_HIGH, G_SI)) return false;
		
		nextToken();
		if(!grammarExpr()) return false;
		checkTypeFromTypeStack(DataType.LOGICO);
		
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
	
	private boolean grammarTipo(int priority, DataType type)
	{
		String[] values = {TERMINAL_ENTERO, TERMINAL_DECIMAL, TERMINAL_ALFANUMERICO, 
				TERMINAL_LOGICO};
		//Check for this extra tag.
		int[] tags = {Token.IDENTIFIER};
		if(!checkTerminalValueTag(values, tags, priority, G_TIPO)) return false;
		
		//Add the corresponding data type to the parameter.
		if(value.equals(TERMINAL_ENTERO)) type.setDataType(DataType.ENTERO);
		else if(value.equals(TERMINAL_DECIMAL)) type.setDataType(DataType.DECIMAL);
		else if(value.equals(TERMINAL_ALFANUMERICO)) type.setDataType(DataType.ALFANUMERICO);
		else if(value.equals(TERMINAL_LOGICO)) type.setDataType(DataType.LOGICO);
		else if(tag == Token.IDENTIFIER)
		{
			type.setDataType(value);
			type.setIsArray(true);
		}
		return true;
	}

	private boolean grammarLiteral(int priority)
	{
		int[] tags = {Token.CONSTANT_ENTERO, Token.CONSTANT_DECIMAL, Token.CONSTANT_LOGICO,
				Token.CONSTANT_ALFANUM, Token.IDENTIFIER};
		if(checkTerminalTag(tags, priority, G_LITERAL)) return true;
		return false;
	}
	
	private boolean grammarRange()
	{
		nextToken();
		int[] tags = {Token.CONSTANT_ENTERO, Token.IDENTIFIER};
		if(!checkTerminalTag(tags, PRIORITY_HIGH, G_RANGE)) return false;
		if(tag == Token.CONSTANT_ENTERO)
			eDim.add(Integer.parseInt(value));
		else 
			setIntValueFromSymbolTable(value);
			
		nextToken();
		if(!checkTerminalValue(TERMINAL_HASTA, PRIORITY_HIGH, G_RANGE)) return false;
		
		nextToken();
		if(!checkTerminalTag(tags, PRIORITY_HIGH, G_RANGE)) return false;
		if(tag == Token.CONSTANT_ENTERO)
			eDim.add(Integer.parseInt(value));
		else 
			if(!setIntValueFromSymbolTable(value))
			{
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
						"Imposible to stablish a range from '" + value + "'"));
				return false;
			}
		
		//Check that range goes from low to high
		int sizeDim = eDim.size();
		for(int i = 0; i < sizeDim; i+=2)
		{
			int dim1 = eDim.get(i);
			int dim2 = eDim.get(i + 1);
			if(dim1 > dim2)
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
						"Wrong range from [ " + dim1 + " ] to [ " + dim2 + " ], expected to go"
								+ " from low to high."));
		}
		
		return true;
	}
	
	/**
	 * Adds a value to the arraylist of dimensions.
	 * @param eName
	 * @return true if success, false if not.
	 */
	private boolean setIntValueFromSymbolTable(String eName)
	{
		Integer value = parser.getIntValueFromElement(eName);
		if(value != null)
		{
			eDim.add(value);
			return true;
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
			parser.addParsingError(Error.parsingErrorNoTokenFound(parser.getLineOfCode()));
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
					parser.addParsingError(Error.createParsingError(parser.getLineOfCode(), value, 
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
				
				parser.addParsingError(Error.createParsingError(parser.getLineOfCode(), value, 
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
					parser.addParsingError(Error.createParsingError(parser.getLineOfCode(), value, 
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
				parser.addParsingError(Error.createParsingError(parser.getLineOfCode(), value, 
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
	 * Prepares the element to be inserted into the symbol table.
	 */
	private void prepareElement()
	{
		switch(eClass)
		{
			case SymbolTableElement.CLASS_LIBRERIA:
			case SymbolTableElement.CLASS_PROGRAMA:
				addElementToSymbolTable(eName, eClass, DataType.UNDEFINED,
						false, new ArrayList<Integer>(), "", eLine);
				break;
			
			case SymbolTableElement.CLASS_CONSTANTE:
			case SymbolTableElement.CLASS_VARIABLE:
			case SymbolTableElement.CLASS_LOCAL:
					addElementToSymbolTable(eName, eClass, eType, false, new ArrayList<Integer>(),
							eValue, eLine);
				break;
				
			case SymbolTableElement.CLASS_TIPO:
				addElementToSymbolTable(eName, eClass, eType, true, eDim, "", eLine);
				break;
			case SymbolTableElement.CLASS_DECLARATION_TIPO:
				SymbolTableElement array = parser.getElementByName(eType);
				if(array != null)
				{
					addElementToSymbolTable(eName, SymbolTableElement.CLASS_TIPO,
							array.getType(), true, new ArrayList<Integer>(array.getDim()),
							"", eLine);
				}
				else
					parser.addSemanticError(Error.semanticFreeError(eLine, 
							"The array structure for " + eType + " hasn't been declared yet."));
				break;
				
			case SymbolTableElement.CLASS_PROCEDIMIENTO:
			case SymbolTableElement.CLASS_FUNCION:
				if(hasParameters)
				{
					int paramSize = parameters.size();
					for(int i = 0; i < paramSize; i++)
						eName += "$" + parameters.get(i).getDataType();
					for(int i = 0; i < paramSize; i++)
						prepareParameter(parameters.get(i));
				}
				addElementToSymbolTable(eName, eClass, eType, false, new ArrayList<Integer>(), 
						"", eLine);
				localFunctionName = eName;

				clearParameters();
				break;
				
				default:
					parser.addParsingError("Symbol Table Error.\nNo element class defined for "
							+ "this symbol.");
		}
	}
	
	/**
	 * Prepares a parameter of a function or procedure to be inserted into the symbol table.
	 */
	private void prepareParameter(Parameter p)
	{
		addElementToSymbolTable(p.getId() + "$" + eName, SymbolTableElement.CLASS_PARAMETRO, 
				p.getDataType(), false, new ArrayList<Integer>(), "", p.getLineOfCode());
	}
	
	/**
	 * Adds a new element to the symbol table with the stored information inside the
	 * element's buffers.
	 */
	private void addElementToSymbolTable(String eName, int eClass, String eType, 
			boolean eDimensioned, ArrayList<Integer> eDim, String eValue, int eLine)
	{
		parser.addElementToSymbolTable(new SymbolTableElement(eName, eClass, eType, 
				eDimensioned, new ArrayList<Integer>(eDim), eValue, eLine));
		eDim.clear();
	}
	
	/**
	 * Checks if a datatype matches with the tag provided.
	 * @param dataType
	 * @param tag
	 * @param line
	 * @return
	 */
	private boolean checkDataTypeDeclaration(String dataType, int tag, int line)
	{
		switch(tag)
		{
			case Token.CONSTANT_ENTERO:
				if(dataType.equals(DataType.ENTERO)) return true;
					parser.addSemanticError(Error.semanticDataType(line, 
							DataType.getDataTypeName(dataType)));
				return false;
			case Token.CONSTANT_DECIMAL:
				if(dataType.equals(DataType.DECIMAL)) return true;
					parser.addSemanticError(Error.semanticDataType(line, 
							DataType.getDataTypeName(dataType)));
				return false;
			case Token.CONSTANT_LOGICO:
				if(dataType.equals(DataType.LOGICO)) return true;
					parser.addSemanticError(Error.semanticDataType(line, 
							DataType.getDataTypeName(dataType)));
				return false;
			case Token.CONSTANT_ALFANUM:
				if(dataType.equals(DataType.ALFANUMERICO)) return true;
					parser.addSemanticError(Error.semanticDataType(line, 
							DataType.getDataTypeName(dataType)));
				return false;
			default:
					parser.addSemanticError(Error.semanticDataType(line, 
							DataType.getDataTypeName(dataType)));
				return false;
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
	
	/**
	 * Gets the result from the operation and stores it in the type stack.
	 * @param arity Operator's arity.
	 * @param operator
	 */
	private void verifyTypeStack(int arity, String operator)
	{
		switch(arity)
		{
			case UNARY_OP:
			{	
				String operand = typeStack.pop();
				String operation = operator + operand;
				typeStack.push(getOperationResult(operation));
				break;
			}
			case BINARY_OP:
			{
				String operand2 = typeStack.pop();
				String operand1 = typeStack.pop();
				String operation = operand1 + operator + operand2;
				typeStack.push(getOperationResult(operation));
				break;
			}
			default:
				parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
						"No arity defined for this operator."));
		}
	}
	
	/**
	 * Gets the element because of a call from a grammar.
	 * @param name
	 * @return element
	 */
	private SymbolTableElement getElementForCall(String name)
	{
		SymbolTableElement e = null;
		//First check for local declaration.
		if(isLocalDeclaration)
			e = parser.getElementByName(name + "$" + localFunctionName);
		if(e == null)
			e = parser.getElementByName(name);
		if(e == null)
			parser.addSemanticError("Element '" + name + "' at line: "
					+ parser.getLineOfCode() + " hasn't been defined yet.");
		return e;
	}
	
	/**
	 * Gets the result of an operation from the OperationResult interface.
	 * @param operation
	 * @return
	 */
	private String getOperationResult(String operation)
	{
		int size = OPERATION.length;
		for(int i = 0; i < size; i++)
		{
			if(operation.equals(OPERATION[i]))
				return RESULT[i];
		}
		
		parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(),
				"Conflict in operation types: " + operation));
		return DataType.UNDEFINED;
	}
	
	/**
	 * Checks if the top of the stack matches with the data type provided and then pops it
	 * from the stack.
	 * @param dataType
	 * @return
	 */
	private boolean checkTypeFromTypeStack(String dataType)
	{
		if(!typeStack.isEmpty())
		{
			String type = typeStack.pop();
			if(type.equals(dataType))
				return true;
			parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
					"Conflict in data type, expected: " + dataType + "\narrived: "
					+ type));
			return false;
				
		}
		parser.addSemanticError(Error.semanticFreeError(parser.getLineOfCode(), 
				"The stack of types is empty, can't resolve it."));
		return false;
	}
	
	/**
	 * Clear the parameters buffers.
	 */
	private void clearParameters()
	{
		hasParameters = false;
		parameters.clear();
	}
}
