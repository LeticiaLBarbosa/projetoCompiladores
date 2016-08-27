package Example;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.Location;
%%
%cup
%line
%column
%char
%class Scanner
%{
	public Scanner(java.io.InputStream r, ComplexSymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	public Symbol symbol(String plaintext,int code){
	    return sf.newSymbol(plaintext,code,new Location("",yyline+1, yycolumn +1,yychar), new Location("",yyline+1,yycolumn+yylength(),yychar));
	}
	public Symbol symbol(String plaintext,int code,Integer number){
	    return sf.newSymbol(plaintext,code,new Location("",yyline+1, yycolumn +1,yychar), new Location("",yyline+1,yycolumn+yylength(),yychar),number);
	}
	private ComplexSymbolFactory sf;
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%%
";" { return symbol("Semicolon",sym.SEMI); }
"+" { return symbol("Plus",sym.PLUS); }
"*" { return symbol("Times",sym.TIMES); }
"(" { return symbol("Left Bracket",sym.LPAREN); }
")" { return symbol("Right Bracket",sym.RPAREN); }
[0-9]+ { return symbol("Integral Number",sym.NUMBER, new Integer(yytext())); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
