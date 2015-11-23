package lexical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

/**
 * @author natafrank
 *
 * It's in charge of read the file and return tokens based on the patterns.
 */
public class Scanner
{
	private final static String[] KEYWORDS = {"usando", "libreria", "procedimiento", "programa", 
		"principal", "constante", "entero", "decimal", "alfanumerico", "logico", "arreglo", "de",
		"tipo", "hasta", "declara", "inicio", "fin", "si", "sino", "regresa", "funcion", "paso", 
		"ciclo", "mientras", "despliega", "lee"};
	private final static String[] LOGICAL_CONSTANTS = {"verdadero", "falso"};
	private final static String[] LOGICAL_OPERATORS = {"no", "y", "o"}; 
	
	private final static int ACP = 999;
	private final static int ERR = -1;
	private final static int SIZE_BUFFER = 4096;
	private final static int SENTINEL = SIZE_BUFFER - 1;
	private final static char EOF = '\0';
	
	private final static int TRANSITION_MATRIX[][] = 
		{
			//		l		d		+		>		<		=		"		:		.		del		/
			/*0*/{	1,		12,		8,		2,		4,		6,		15,		10,		17,		17,		18},
			/*1*/{	1,		1,		ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*2*/{	ACP,	ACP,	ACP,	ACP,	ACP,	3,		ACP,	ACP,	ACP,	ACP,	ACP},
			/*3*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP, 	ACP,	ACP},
			/*4*/{	ACP,	ACP,	ACP,	9,		ACP,	5,		ACP,	ACP,	ACP,	ACP,	ACP},
			/*5*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP, 	ACP,	ACP},
			/*6*/{	ACP,	ACP,	ACP,	ACP,	ACP,	7,		ACP,	ACP,	ACP,	ACP,	ACP},
			/*7*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*8*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*9*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*10*/{	ERR,	ERR,	ERR,	ERR,	ERR,	11,		ERR,	ERR,	ERR,	ERR,	ERR},
			/*11*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*12*/{	ACP,	12,		ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	13,		ACP,	ACP},
			/*13*/{	ERR,	14,		ERR,	ERR,	ERR,	ERR,	ERR,	ERR,	ERR,	ERR,	ERR},
			/*14*/{	ACP,	14,		ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*15*/{	15,		15,		15,		15,		15,		15,		16,		15,		15,		15,		15},
			/*16*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*17*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP},
			/*18*/{	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	ACP,	19},
			/*19*/{	19,		19,		19,		19,		19,		19,		19,		19,		19,		19,		19}
		};
	
	/**
	 * Defines the status of the scanner.
	 */
	private int status;
	public final static int STATUS_GOOD = 1;
	public final static int STATUS_BAD = -1;
	
	//Reading file variables.
	private char buffer[][];
	private int activeBuffer;
	private int forward;
	private boolean openedFile;
	private BufferedReader reader;
	
	private String lexeme;
	private int lineOfCode;
	
	/**
	 * This stack will hold a token as a buffer to make possible the look ahead in tokens.
	 */
	private Stack<Token> tokenStack;
	
	/**
	 * Constructor.
	 * @param fileName Path of the file to compile.
	 */
	public Scanner(String fileName)
	{
		try
		{
			reader = new BufferedReader(new FileReader(fileName));
			openedFile = true;
			forward = 0;
			activeBuffer = 0;
			buffer = new char [2][SIZE_BUFFER];
			buffer[0][SENTINEL] = EOF;
			buffer[1][SENTINEL] = EOF;
			reader.read(buffer[0], 0, SIZE_BUFFER - 1);
			status = STATUS_GOOD;
			lineOfCode = 1;
			tokenStack = new Stack<Token>();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			openedFile = false;
			status = STATUS_BAD;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			openedFile = false;
			status = STATUS_BAD;
		}
	}
	
	/**
	 * Gets the next token that matches a pattern.
	 * @return
	 */
	public Token getToken()
	{
		//First check from the stack if a token is available.
		if(!tokenStack.empty()) return getTokenFromStack();
		
		lexeme = "";
		int state = 0;
		int result = 0;
		char in;
		
		while(result != ACP && result != ERR)
		{
			in = getNextChar();
			while(in == '\n' || in == '\t' || in == ' ')
			{	
				if(state != 0 && state != 15 && state != 19)
					return new Token(getTokenTag(state), lexeme);
				else if(state == 19)
				{
					//Comment.
					if(in == '\n')
					{
						//return new Token(getTokenTag(state), lexeme);
						//If comment detected, initialize all the variables involved.
						lexeme = "";
						state = 0;
						result = 0;
					}
					else
					{
						lexeme += in;
						in = getNextChar();
						continue;
					}
				}
				else if(state == 15)
				{
					if(in != '\n') break;
					else return new Token(getTokenTag(state), lexeme);
						
				}
				else
				{
					in = getNextChar();
					if(in == EOF)
					{
						closeFile();
						return new Token(getTokenTag(state), lexeme);
					}
				}
			}		
			result = changeState(state, in);
			if(result >= 0)
			{
				if(result != ACP)
				{
					state = result;			
					lexeme += in;
				}
				else forward--;
			}
			else
			{
				openedFile = false;
				return new Token(getTokenTag(state), lexeme);
			}
		}
		
		return new Token(getTokenTag(state), lexeme);
	}
	
	/**
	 * Get the next char from the file.
	 * @return
	 */
	private char getNextChar()
	{
		char nextChar = buffer[activeBuffer][forward];
		if(nextChar == EOF)
		{
			if(forward == SENTINEL)
			{
				if(activeBuffer == 0)
					activeBuffer = 1;
				else if(activeBuffer == 1)
					activeBuffer = 0;
				else
				{
					closeFile();
					return EOF;
				}
				
				try
				{
					reader.read(buffer[activeBuffer], 0, SIZE_BUFFER - 1);
					forward = 0;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					closeFile();
				}
			}
		}
		if(nextChar == '\n') lineOfCode++;
		forward++;
		return nextChar;
	}

	/**
	 * Gets the tag that correspondes to the token.
	 * @param state Where the automata is.
	 * @return Tag for the token.
	 */
	private int getTokenTag(int state)
	{
		switch(state)
		{
			case 1:
				if(isKeyword())
					return Token.KEYWORD;
				if(isLogicalConstant())
					return Token.CONSTANT_LOGICO;
				if(isLogicalOpertator())
					return Token.OPERATOR_LOGICAL;
				return Token.IDENTIFIER;
			case 2:
				return Token.OPERATOR_LOGICAL;
			case 3:
				return Token.OPERATOR_LOGICAL;
			case 4:
				return Token.OPERATOR_LOGICAL;
			case 5:
				return Token.OPERATOR_LOGICAL;
			case 6:
				return Token.OPERATOR_LOGICAL;
			case 7:
				return Token.OPERATOR_LOGICAL;
			case 8:
				return Token.OPERATOR_ARITHMETIC;
			case 9:
				return Token.OPERATOR_LOGICAL;
			case 11:
				return Token.OPERATOR_ASIGNATION;
			case 12:
				return Token.CONSTANT_ENTERO;
			case 14:
				return Token.CONSTANT_DECIMAL;
			case 16:
				return Token.CONSTANT_ALFANUM;
			case 17:
				return Token.DELIMITATOR;
			case 18:
				return Token.OPERATOR_ARITHMETIC;
			case 19:
				return Token.COMMENT;
			default:
				return ERR;
		}
	}
	
	/**
	 * Gets the column from the transition matrix.
	 * @param in Character to get the column.
	 * @return	Column where the character is.
	 */
	private int getColumn(char in)
	{
		final int LET_CAP_BEG = 65;
		final int LET_CAP_END = 90;
		final int LET_LOW_BEG = 97;
		final int LET_LOW_END = 122;
		final int DIG_BEG = 48;
		final int DIG_END = 57;
		
		if(in == '_' || (in >= LET_CAP_BEG && in <= LET_CAP_END) || (in >= LET_LOW_BEG && 
			in <= LET_LOW_END))
			return 0;
		if(in >= DIG_BEG && in <= DIG_END)
			return 1;
		if(in == '+' || in == '-' || in == '*' || in == '%' || in == '^')
			return 2;
		if(in == '>')
			return 3;
		if(in == '<')
			return 4;
		if(in == '=')
			return 5;
		if(in == '"')
			return 6;
		if(in == ':')
			return 7;
		if(in == '.')
			return 8;
		if(in == '[' ||in == ']' ||in == '(' ||in == ')' ||in == ',' ||in == ';')
			return 9;
		if(in == '/')
			return 10;
		
		return ERR;
	}
	
	/**
	 * Changes the state.
	 * @param state Actual state.
	 * @param in Input from file.
	 * @return New state.
	 */
	private int changeState(int state, char in)
	{
		if(state != 15)
		{
			int column = getColumn(in);
			if(column >= 0)
				return TRANSITION_MATRIX[state][column];
			else
				return ERR;
		}
		else
		{
			if(in == '"') return 16;
			else return 15;
		}
	}
	
	/**
	 * Check if the lexeme is a keyword.
	 * @return
	 */
	private boolean isKeyword()
	{
		int length = KEYWORDS.length;
		for(int i = 0; i < length; i++)
		{
			if(lexeme.equals(KEYWORDS[i]))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the lexeme is a logical constant.
	 * @return
	 */
	private boolean isLogicalConstant()
	{
		int length = LOGICAL_CONSTANTS.length;
		for(int i = 0; i < length; i++)
		{
			if(lexeme.equals(LOGICAL_CONSTANTS[i]))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the lexeme is a logical operator.
	 * @return
	 */
	private boolean isLogicalOpertator()
	{
		int length = LOGICAL_OPERATORS.length;
		for(int i = 0; i < length; i++)
		{
			if(lexeme.equals(LOGICAL_OPERATORS[i]))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if there are more tokens avaliable.
	 * @return
	 */
	public boolean hasToken()
	{
		return openedFile;
	}
	
	/**
	 * Closes the file.
	 */
	private void closeFile()
	{
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		openedFile = false;
	}
	
	/**
	 * Get the status of the parser.
	 * @return status.
	 */
	public int getStatus()
	{
		return status;
	}
	
	/**
	 * Gets the line of code for the actual input.
	 * @return lineOfCode.
	 */
	public int getLineOfCode()
	{
		return lineOfCode;
	}
	
	/**
	 * Adds a token to the stack.
	 * @param token
	 */
	public void pushTokenToStack(Token token)
	{
		tokenStack.push(token);
	}
	
	/**
	 * Gest a token from the stack.
	 * @return
	 */
	public Token getTokenFromStack()
	{
		return tokenStack.pop();
	}
}
