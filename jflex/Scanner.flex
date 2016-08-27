import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%class Scanner
%public
%unicode
%cup
%line
%column

%{
    public static int yyPreviousPublicLine;
    public static int yyPublicLine;
    StringBuffer string = new StringBuffer();

    public static int getCurrentLexeme(){
        if(Scanner.yyPreviousPublicLine != Scanner.yyPublicLine){
            return Scanner.yyPreviousPublicLine;
        }
        return Scanner.yyPublicLine;
    }

    private Symbol symbol(int type){
        yyPreviousPublicLine = yyLine;
        yyPublicLine = yyline+1;
        return new Symbol(type, yyline +1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value){
        yyPreviousPublicLine = yyLine;
        yyPublicLine = yyline+1;
        return new Symbol(type, yyline +1, yycolumn+1, value);
    }

    private void errorReport(int line, String message){
        throw new RuntimeException("Lexical error at line "+line+": " + message);
    }

    private int yywrap(){
        return 1;
    }
%}



Comment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

LineTerminator = \r|\n|\r\n
BlankSpace = {LineTerminator} | [ \t\f]

/* Comments */

Comments = {LineComment} | {BlockComment}
LineComment = "//" {InputCharacter}* {LineTerminator}?
BlockComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

/* Patterns */
letter = [A-Za-z]
digit =  [0-9]
alphanumeric = {letter}|{digit}

%%

<YYINITIAL> {

	"include"				{ return symbol(sym.INCLUDE, yytext()); }
	"auto"					{ return symbol(sym.AUTO, yytext()); }
	"break"					{ return symbol(sym.BREAK, yytext()); }
	"case"					{ return symbol(sym.CASE, yytext()); }
	"char"					{ return symbol(sym.CHAR, yytext()); }
	"const"					{ return symbol(sym.CONST, yytext()); }
	"continue"				{ return symbol(sym.CONTINUE, yytext()); }
	"default"				{ return symbol(sym.DEFAULT, yytext()); }
	"do"					{ return symbol(sym.DO, yytext()); }
	"double"				{ return symbol(sym.DOUBLE, yytext()); }
	"else"					{ return symbol(sym.ELSE, yytext()); }
	"enum"					{ return symbol(sym.ENUM, yytext()); }
	"extern"				{ return symbol(sym.EXTERN, yytext()); }
	"float"					{ return symbol(sym.FLOAT, yytext()); }
	"for"					{ return symbol(sym.FOR, yytext()); }
	"goto"					{ return symbol(sym.GOTO, yytext()); }
	"if"					{ return symbol(sym.IF, yytext()); }
	"inline"				{ return symbol(sym.INLINE, yytext()); }
	"int"					{ return symbol(sym.INT, yytext()); }
	"long"					{ return symbol(sym.LONG, yytext()); }
	"register"				{ return symbol(sym.REGISTER, yytext()); }
	"restrict"				{ return symbol(sym.RESTRICT, yytext()); }
	"return"				{ return symbol(sym.RETURN, yytext()); }
	"short"					{ return symbol(sym.SHORT, yytext()); }
	"signed"				{ return symbol(sym.SIGNED, yytext()); }
	"sizeof"				{ return symbol(sym.SIZEOF, yytext()); }
	"static"				{ return symbol(sym.STATIC, yytext()); }
	"struct"				{ return symbol(sym.STRUCT, yytext()); }
	"switch"				{ return symbol(sym.SWITCH, yytext()); }
	"typedef"				{ return symbol(sym.TYPEDEF, yytext()); }
	"union"					{ return symbol(sym.UNION, yytext()); }
	"unsigned"				{ return symbol(sym.UNSIGNED, yytext()); }
	"void"					{ return symbol(sym.VOID, yytext()); }
	"volatile"				{ return symbol(sym.VOLATILE, yytext()); }
	"while"					{ return symbol(sym.WHILE, yytext()); }
	"_Alignas"              { return symbol(sym.ALIGNAS, yytext()); }
	"_Alignof"              { return symbol(sym.ALIGNOF, yytext()); }
	"_Atomic"               { return symbol(sym.ATOMIC, yytext()); }
	"_Bool"                 { return symbol(sym.BOOL, "bool"); }
	"_Complex"              { return symbol(sym.COMPLEX, yytext()); }
	"_Generic"              { return symbol(sym.GENERIC, yytext()); }
	"_Imaginary"            { return symbol(sym.IMAGINARY, yytext()); }
	"_Noreturn"             { return symbol(sym.NORETURN, yytext()); }
	"_Static_assert"        { return symbol(sym.STATIC_ASSERT, yytext()); }
	"_Thread_local"         { return symbol(sym.THREAD_LOCAL, yytext()); }
	"__func__"              { return symbol(sym.FUNC_NAME, yytext()); }

	{CM}					{ /* comentarios */ }


	/*{IDE}								{ return addIdentifierType(yytext()); }*/
	{IDE}								{ return symbol(sym.IDENTIFIER, yytext()); }

	{HP}{H}+{IS}?						{ return symbol(sym.I_CONSTANT, yytext()); }
	{NZ}{D}*{IS}?						{ return symbol(sym.I_CONSTANT, yytext()); }
	"0"{O}*{IS}?						{ return symbol(sym.I_CONSTANT, yytext()); }

	{CP}?"'"([^'\\\n]|{ES})+"'"			{ return symbol(sym.C_CONSTANT, yytext()); }

	{D}+{E}{FS}?						{ return symbol(sym.F_CONSTANT, yytext()); }
	{D}*"."{D}+{E}?{FS}?				{ return symbol(sym.F_CONSTANT, yytext()); }
	{D}+"."{E}?{FS}?					{ return symbol(sym.F_CONSTANT, yytext()); }
	{HP}{H}+{P}{FS}?					{ return symbol(sym.F_CONSTANT, yytext()); }
	{HP}{H}*"."{H}+{P}{FS}?				{ return symbol(sym.F_CONSTANT, yytext()); }
	{HP}{H}+"."{P}{FS}?					{ return symbol(sym.F_CONSTANT, yytext()); }

	({SP}?\"([^\"\\\n]|{ES})*\"{WS}*)+	{ return symbol(sym.STRING_LITERAL, yytext()); }

	"..."				{ return symbol(sym.ELLIPSIS, yytext()); }
	">>="				{ return symbol(sym.RIGHT_ASSIGN, yytext()); }
	"<<="				{ return symbol(sym.LEFT_ASSIGN, yytext()); }
	"+="				{ return symbol(sym.ADD_ASSIGN, yytext()); }
	"-="				{ return symbol(sym.SUB_ASSIGN, yytext()); }
	"*="				{ return symbol(sym.MUL_ASSIGN, yytext()); }
	"/="				{ return symbol(sym.DIV_ASSIGN, yytext()); }
	"%="				{ return symbol(sym.MOD_ASSIGN, yytext()); }
	"&="				{ return symbol(sym.AND_ASSIGN, yytext()); }
	"^="				{ return symbol(sym.XOR_ASSIGN, yytext()); }
	"|="				{ return symbol(sym.OR_ASSIGN, yytext()); }
	">>"				{ return symbol(sym.RIGHT_OP, yytext()); }
	"<<"				{ return symbol(sym.LEFT_OP, yytext()); }
	"++"				{ return symbol(sym.INC_OP, yytext()); }
	"--"				{ return symbol(sym.DEC_OP, yytext()); }
	"->"				{ return symbol(sym.PTR_OP, yytext()); }
	"&&"				{ return symbol(sym.AND_OP, yytext()); }
	"||"				{ return symbol(sym.OR_OP, yytext()); }
	"<="				{ return symbol(sym.LE_OP, yytext()); }
	">="				{ return symbol(sym.GE_OP, yytext()); }
	"=="				{ return symbol(sym.EQ_OP, yytext()); }
	"!="				{ return symbol(sym.NE_OP, yytext()); }

	/* novos coisinhas */

	";"					{ return symbol(sym.SEMICOLON, yytext()); }
	("{"|"<%")			{ return symbol(sym.LEFT_BRACKET, yytext()); }
	("}"|"%>")			{ return symbol(sym.RIGHT_BRACKET, yytext()); }
	","					{ return symbol(sym.COMMA, yytext()); }
	":"					{ return symbol(sym.COLON, yytext()); }
	"="					{ return symbol(sym.ASSIGNMENT, yytext()); }
	"("					{ return symbol(sym.LEFT_PARENTESIS, yytext()); }
	")"					{ return symbol(sym.RIGHT_PARENTESIS, yytext()); }
	("["|"<:")			{ return symbol(sym.LEFT_SQ_BRACK, yytext()); }
	("]"|":>")			{ return symbol(sym.RIGHT_SQ_BRACK, yytext()); }
	"."					{ return symbol(sym.DOT, yytext()); }
	"&"					{ return symbol(sym.AND_BINARY, yytext()); }
	"!"					{ return symbol(sym.NEG, yytext()); }
	"~"					{ return symbol(sym.NEG_BINARY, yytext()); }
	"-"					{ return symbol(sym.MINUS, yytext()); }
	"+"					{ return symbol(sym.PLUS, yytext()); }
	"*"					{ return symbol(sym.TIMES, yytext()); }
	"/"					{ return symbol(sym.DIV, yytext()); }
	"%"					{ return symbol(sym.MOD, yytext()); }
	"<"					{ return symbol(sym.LESS_THAN, yytext()); }
	">"					{ return symbol(sym.GREATER_THAN, yytext()); }
	"^"					{ return symbol(sym.XOR_BINARY, yytext()); }
	"|"					{ return symbol(sym.OR_BINARY, yytext()); }
	"?"					{ return symbol(sym.QUESTION, yytext()); }
	"#"					{ return symbol(sym.POUND, yytext()); }

	{WS}+				{ /* whitespace separates tokens */ }
	/*.					{ /* discard bad characters */ }*/
}