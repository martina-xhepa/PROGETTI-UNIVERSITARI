import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void prog() {
		if(look.tag==Token.assign.tag || look.tag==Tag.PRINT || look.tag==Tag.READ || 
		   look.tag==Tag.COND || look.tag==Tag.WHILE || look.tag==Token.lpg.tag){
			   
			statlist();
			match(Tag.EOF);
			
		}else{
			error("Errore in prog");
		}
    }

    private void statlist() {
		if(look.tag==Token.assign.tag || look.tag==Tag.PRINT || look.tag==Tag.READ || 
		   look.tag==Tag.COND || look.tag==Tag.WHILE || look.tag==Token.lpg.tag){
			   
				stat();
				statlistp();
				
		}else{
			error("Errore in statlist");
		}
    }

    private void statlistp() {
		switch (look.tag) {
			case ';':
				match(Token.semicolon.tag);
				stat();
				statlistp();
				break;
				
			case '}':
			case Tag.EOF:
				break;
				
			default: 
				error("Errore in statlistp");
		}
    }

    private void stat() {
        switch (look.tag) {
			case '=':
				match(Token.assign.tag);
				match(Tag.ID);
				expr();
				break;
				
			case Tag.PRINT:
				match(Tag.PRINT);
				match(Token.lpt.tag);
				exprlist();
				match(Token.rpt.tag);
				break;
				
			case Tag.READ:
				match(Tag.READ);
				match(Token.lpt.tag);
				match(Tag.ID);
				match(Token.rpt.tag);
				break;
				
			case Tag.COND:
				match(Tag.COND);
				whenlist();
				match(Tag.ELSE);
				stat();
				break;
				
			case Tag.WHILE:
				match(Tag.WHILE);
				match(Token.lpt.tag);
				bexpr();
				match(Token.rpt.tag);
				stat();
				break;
				
			case '{':
				match(Token.lpg.tag);
				statlist();
				match(Token.rpg.tag);
				break;
	
			default: 
				error("Errore in stat");
		}
    }

    private void whenlist() {
        if(look.tag==Tag.WHEN){
			whenitem();
			whenlistp();
		}else{
			error("Errore in whenlist");
		}
    }

    private void whenlistp() {
        switch(look.tag){
			case Tag.WHEN:
				whenitem();
				whenlistp();
				break;
				
			case Tag.ELSE:
				break;
				
			default:
				error("Errore in whenlistp");
		}
	}
	
	private void whenitem() {
		if(look.tag==Tag.WHEN){
			match(Tag.WHEN);
			match(Token.lpt.tag);
			bexpr();
			match(Token.rpt.tag);
			match(Tag.DO);
			stat();
		}else{
			error("Errore in whenitem");
		}
	}
	
	private void bexpr() {
		if(look.tag==Tag.RELOP){
			match(Tag.RELOP);
			expr();
			expr();
		}else{
			error("Errore in bexpr");
		}
	}
	
	private void expr(){
		switch(look.tag){
			case '+':
				match(Token.plus.tag);
				match(Token.lpt.tag);
				exprlist();
				match(Token.rpt.tag);
				break;
				
			case '*':
				match(Token.mult.tag);
				match(Token.lpt.tag);
				exprlist();
				match(Token.rpt.tag);
				break;
				
			case '-' :
				match(Token.minus.tag);
				expr();
				expr();
				break;
			case '/':
			
				match(Token.div.tag);
				expr();
				expr();
				break;
				
			case Tag.ID :
				match(Tag.ID);
				break;
				
			case Tag.NUM:
				match(Tag.NUM);
				break;
				
			default:
				error("Errore in expr");
		}
		
	}
	
	private void exprlist(){
		if(look.tag=='+' || look.tag=='-' || look.tag =='*' || look.tag =='/' 
			|| look.tag== Tag.ID || look.tag == Tag.NUM){
			expr();
			exprlistp();
		}else{
			error("Errore in exprlist");
		}
		
	}
	
	private void exprlistp(){
		switch(look.tag){
			case '+' :
			case '-' :
			case '*' :
			case '/' :
			case Tag.ID :
			case Tag.NUM:
				expr();
				exprlistp();
				break;
				
			case ')' :
				break;
				
			default: 
				error("Errore exprlistp");
		}
		
	}
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        //String path = "...path..."; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader("prova.txt"));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}