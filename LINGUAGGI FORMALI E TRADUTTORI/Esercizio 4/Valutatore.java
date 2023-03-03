import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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

    public void start() { 
		int expr_val;
		if(look.tag=='(' || look.tag==256){
			expr_val = expr();
			match(Tag.EOF);
			System.out.println(expr_val);
		}else{
			error("Errore in start");
		}
	}

    private int expr() { 
		int term_val, exprp_val=0;
		if(look.tag=='(' || look.tag==256){
			term_val = term();
			exprp_val = exprp(term_val);
		}else {
			error("Errore in expr");
		}
		return exprp_val;
		}

    private int exprp(int exprp_i) {
		int term_val, exprp_val=0;
		switch (look.tag) {
			case '+':
				match('+');
				term_val = term();
				exprp_val = exprp(exprp_i + term_val);
				break;
			case '-':
				match('-');
				term_val = term();
				exprp_val = exprp(exprp_i + term_val);
				break;
			case ')':
			case Tag.EOF:
				exprp_val=exprp_i;
			break;		
			default: 
			error("Errore in exprp");
			}
			return exprp_val;
			}

    private int term() { 
		int fact_val, term_val=0, termp_i, termp_val;
		if(look.tag == '(' || look.tag == Tag.NUM){
            fact_val = fact();
			termp_i=fact_val;
			termp_val=termp(termp_i);
			term_val=termp_val;
		}else{
            error("Errore in term");
		}
		return term_val;
    }
    
    private int termp(int termp_i) { 
		int termp1_i, fact_val, termp_val=0, termp1_val;
		switch (look.tag) {
			case '*':
				match(Token.mult.tag);
				fact_val=fact();
				termp1_i= termp_i*fact_val;
				termp1_val = termp(termp1_i);
				termp_val= termp1_val;
				break;
			case '/':
				match(Token.div.tag);
				fact_val=fact();
				termp1_i= termp_i/fact_val;
				termp1_val = termp(termp1_i);
				termp_val= termp1_val;
				break;
			case '+':
			case '-':
			case ')':
			case Tag.EOF:
				termp_val=termp_i;
			break;	
			default:
				error("Errore in termp");
			}
			return termp_val; 
    }
    
    private int fact() { 
		int fact_val=0, expr_val;
		int num_value;
		switch (look.tag) {
			case '(':
				match(Token.lpt.tag);
				expr_val=expr();
				match(Token.rpt.tag);
				fact_val=expr_val;
				break;
			case Tag.NUM:
				num_value=((NumberTok)look).attribute;
				match(Tag.NUM);
				fact_val= num_value;
				break;
			default:
				error("Errore in fact");
        
		}
		return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        //String path = "...path..."; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader("prova.txt"));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}