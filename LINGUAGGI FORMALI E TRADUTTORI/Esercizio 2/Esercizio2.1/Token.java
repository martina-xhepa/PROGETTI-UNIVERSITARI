public class Token {
    public final int tag;
    public Token(int t) { tag = t;  }//costruttore, posso creare istanza
	                                 //di tipo Token e assegnarli un intero
    public String toString() {return "<" + tag + ">";}
    public static final Token
	not = new Token('!'),
	lpt = new Token('('),
	rpt = new Token(')'),
	lpg = new Token('{'),
	rpg = new Token('}'),
	plus = new Token('+'),
	minus = new Token('-'),
	mult = new Token('*'),
	div = new Token('/'),
	assign = new Token('='),
	semicolon = new Token(';');    
}

/*

Metodi che mi permettono anche di stampare il tag che 
è in una variabile di tipo final
*/
