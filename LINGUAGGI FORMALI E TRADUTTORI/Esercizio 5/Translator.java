import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
			int lnext_prog = code.newLabel();
			statlist(lnext_prog);
			code.emitLabel(lnext_prog);
			match(Tag.EOF);
			try {
				code.toJasmin();
			}
			catch(java.io.IOException e) {
				System.out.println("IO error\n");
			};
		   }else{ error("Errore in prog");}
    }
	
	private void statlist(int lnext_prog) {
		if(look.tag==Token.assign.tag || look.tag==Tag.PRINT || look.tag==Tag.READ || 
		   look.tag==Tag.COND || look.tag==Tag.WHILE || look.tag==Token.lpg.tag){
			    int next = code.newLabel();
				stat(next);
				code.emitLabel(next);
				statlistp(lnext_prog);
				
		}else{
			error("Errore in statlist");
		}
    }
	
	private void statlistp(int lnext_prog) {
		switch (look.tag) {
			case ';':
				match(Token.semicolon.tag);
				int next = code.newLabel();
				stat(next);
				code.emitLabel(next);
				statlistp(lnext_prog);
				break;
				
			case '}':
			case Tag.EOF:
				break;
				
			default: 
				error("Errore in statlistp");
		}
    }

    private void stat(int next) {
        switch(look.tag) {
			case '=':
				match(Token.assign.tag);
				if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }   
				match(Tag.ID);
				expr();
				code.emit(OpCode.istore,id_addr); 
				}
                else
                    error("Error in grammar (stat) after = with " + look);
				break;
				
			case Tag.PRINT:
				match(Tag.PRINT);
				match(Token.lpt.tag);
				exprlist(1);
				match(Token.rpt.tag);
				break;
				
            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,id_addr);  
                }
                else
                    error("Error in grammar (stat) after read( with " + look);
                break;
				
			case Tag.COND:
				match(Tag.COND);
				whenlist(next);
				match(Tag.ELSE);
				stat(next);
				break;
				
			case Tag.WHILE:
				match(Tag.WHILE);
				match(Token.lpt.tag);
				int vero = code.newLabel();
				int falso = next;
				int prossimo = code.newLabel();
				code.emitLabel(prossimo);
				bexpr(vero, falso);
				match(Token.rpt.tag);
				code.emitLabel(vero);
				stat(prossimo);
				code.emit(OpCode.GOto, prossimo );
				break;
				
			case '{':
				match(Token.lpg.tag);
				statlist(next);
				match(Token.rpg.tag);
				break;
				
			default: 
				error("errore in stat");
        }
     }
	 
	  private void whenlist(int next) {
        if(look.tag==Tag.WHEN){
			whenitem(next);
			whenlistp(next);
		}else{
			error("Errore in whenlist");
		}
    }

    private void whenlistp(int next) {
        switch(look.tag){
			case Tag.WHEN:
				whenitem( next);
				whenlistp( next);
				break;
				
			case Tag.ELSE:
				break;
				
			default:
				error("Errore in whenlistp");
		}
	}
	
	private void whenitem(int next) {
		if(look.tag==Tag.WHEN){
			match(Tag.WHEN);
			match(Token.lpt.tag);
			int vero = code.newLabel();
			int falso = code.newLabel();
			bexpr(vero, falso);
			match(Token.rpt.tag);
			match(Tag.DO);
			code.emitLabel(vero);
			stat(next);
			code.emit(OpCode.GOto, next);
			code.emitLabel(falso);
		}else{
			error("Errore in whenitem");
		}
	}
		
	 
	 private void bexpr(int vero, int falso) {
		if(look.tag==Tag.RELOP){
			String booleano =((Word)look).lexeme;
			match(Tag.RELOP);
			expr();
			expr();
			switch(booleano){
				case "==":
					code.emit(OpCode.if_icmpeq, vero);
					break;
				case "<>":
					code.emit(OpCode.if_icmpne, vero);
					break;
				case "<=":
					code.emit(OpCode.if_icmple, vero);
					break;
				case ">=":
					code.emit(OpCode.if_icmpge, vero);
					break;
				case "<":
					code.emit(OpCode.if_icmplt, vero);
					break;
				case ">":
					code.emit(OpCode.if_icmpgt, vero);
					break;
			}
			
		code.emit(OpCode.GOto, falso);
		
		}else{
			error("Errore in bexpr");
		}
	}

    private void expr( ) {
        switch(look.tag) {
			case '+':
				int operazione = Token.plus.tag;
				match('+');
				match('(');
				exprlist(operazione);
				match(')');
				break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
			case '*':
				int operazion = Token.mult.tag;
				match('*');
				match('(');
				exprlist(operazion);
				match(')');
				break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
			case Tag.ID :
				int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }          
				match(Tag.ID);
				code.emit(OpCode.iload,id_addr); 
				break;
				
			case Tag.NUM:
				int num_value=((NumberTok)look).attribute;
				match(Tag.NUM);
				code.emit(OpCode.ldc, num_value);
				break;
				
			default:
				error("Errore in expr");
        }
    }
	
	private void exprlist(int operazione){
		if(look.tag=='+' || look.tag=='-' || look.tag =='*' || look.tag =='/' 
			|| look.tag== Tag.ID || look.tag == Tag.NUM){
			expr();
			if(operazione==1){code.emit(OpCode.invokestatic,1);}
			exprlistp(operazione);
		}else{
			error("Errore in exprlist");
		}
		
	}
	
	private void exprlistp(int operazione){
		switch(look.tag){
			case '+' :
			case '-' :
			case '*' :
			case '/' :
			case Tag.ID :
			case Tag.NUM:
				expr();
				switch(operazione){
					case '+':
						code.emit(OpCode.iadd);
						break;
					case '*':
						code.emit(OpCode.imul);
						break;
					case 1:
						code.emit(OpCode.invokestatic,1);
						break;
					default: 
						error("operation error");
				}
				
				exprlistp(operazione);
				break;
				
			case ')' :
				break;
				
			default: 
				error("Errore exprlistp");
		}
		
	}
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\marti\\Desktop\\Progetto LFT_ Martina Xhepa 891242\\Esercizio 5\\prova.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

