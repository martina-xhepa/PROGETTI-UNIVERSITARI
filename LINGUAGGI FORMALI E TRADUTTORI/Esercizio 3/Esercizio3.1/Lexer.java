import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
			
			case '(':
                peek = ' ';
                return Token.lpt;
			
			case ')':
                peek = ' ';
                return Token.rpt;
			
			case '{':
                peek = ' ';
                return Token.lpg;
			
			case '}':
                peek = ' ';
                return Token.rpg;
			
			case '+':
                peek = ' ';
                return Token.plus;
			
			case '-':
                peek = ' ';
                return Token.minus;
			
			case '*':
                peek = ' ';
                return Token.mult;
				
			case '/':
				readch(br); 
				if(peek=='*'){ 
					int state = 0;
					while (state>=0 && state<2 && peek!=(char)-1){
						readch(br);
						switch(state){
				
							case 0: 
								if(peek!='*')
									state=0;
							else if(peek=='*')
								state=1;
							else 
								state=-1;
							break;
				
							case 1: 
								if(peek=='*')
									state=1;
								else if(peek=='/')
									state=2;
								else if(peek!='*' && peek!='/')
									state=0;
								else 
									state=-1;
								break;
				
							case 2: 
								state=-1;
								break;
						}
					}		
					if(state==2){
						peek = ' ';
						return lexical_scan(br);
					}else {  
						System.err.println("Erroneous character"
                            + " after peek : "  + peek );
						return null;}
					
				}else if(peek=='/'){ 
				readch(br);
				while(peek!='\n' && peek!=(char)-1){
				readch(br);}
				return lexical_scan(br);
				
				}else{
                return Token.div;
				}
			
			case ';':
                peek = ' ';
                return Token.semicolon;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
			
			case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
			
			case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    return Token.assign;
                }
			
			case '<':
                readch(br);
                if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else if(peek== '=') {
					peek = ' ';
					return Word.le;
				}else{
					return Word.lt;
				}
				
			case '>':
				readch(br);
				if(peek== '=') {
					peek = ' ';
					return Word.ge;
				}else{
					return Word.gt;
				}
          
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek=='_') {
					String accumulatore ="";
					if(Character.isLetter(peek)){
						while(Character.isLetter(peek) || Character.isDigit(peek) || peek=='_' ){
						accumulatore = accumulatore + String.valueOf(peek);
						readch(br);
						}
					}else{
						
						while(peek=='_'){
							accumulatore = accumulatore + String.valueOf(peek);
							readch(br);
						}
						
					 if(Character.isLetter(peek) || Character.isDigit(peek)){
							while(Character.isLetter(peek) || Character.isDigit(peek) || peek=='_'){
							accumulatore = accumulatore + String.valueOf(peek);
							readch(br);
						}
							
						}else{
							System.err.println("Erroneous character: " 
                                + peek );
								return null;
						}
					
					}
					
					if(peek=='\t' || peek=='\r' || peek =='\n' || peek== (char)-1 
						|| peek==' ' || peek=='(' || peek==')' || peek =='!' 
						|| peek=='{' || peek=='}' || peek=='=' || peek ==';'
						|| peek=='+' || peek=='-' || peek=='*' || peek =='/'
						|| peek=='|' || peek=='&' || peek=='<' || peek =='>'){
					
						switch(accumulatore){
							case "cond": 
								return Word.cond;
							case "when": 
								return Word.when;
							case "then": 
								return Word.then;
							case "else": 
								return Word.elsetok;
							case "while": 
								return Word.whiletok;
							case "do": 
								return Word.dotok;
							case "seq": 
								return Word.seq;
							case "print": 
								return Word.print;
							case "read": 
								return Word.read;
							default:
								return new Word(Tag.ID, accumulatore);}
					}else{
						System.err.println("Erroneous character: " + peek);
						return null;}

                } else if (Character.isDigit(peek)) {
					if(peek=='0'){
						char cifra = peek;
						readch(br);
						if(peek<='9' && peek>='0'){
							System.err.println("Erroneous number"
                            + " after 0 can't be a number : "  + peek );
						return null;}
						
						int num = Character.getNumericValue(cifra); 
						if(peek=='\t' || peek=='\r' || peek =='\n' || peek== (char)-1 
						|| peek==' ' || peek=='(' || peek==')' || peek =='!' 
						|| peek=='{' || peek=='}' || peek=='=' || peek ==';'
						|| peek=='+' || peek=='-' || peek=='*' || peek =='/'
						|| peek=='|' || peek=='&' || peek=='<' || peek =='>'){
						    return new NumberTok(Tag.NUM, num);
						
						}else{
						System.err.println("Erroneous character: " + peek);
						return null;}
					
					}else{
						String accumulatore ="";
						int intero;
						
						while(Character.isDigit(peek)){
						accumulatore = accumulatore + String.valueOf(peek);
						
						readch(br);}
						
						intero = Integer.parseInt(accumulatore);
						if(peek=='\t' || peek=='\r' || peek =='\n' || peek== (char)-1 
						|| peek==' ' || peek=='(' || peek==')' || peek =='!' 
						|| peek=='{' || peek=='}' || peek=='=' || peek ==';'
						|| peek=='+' || peek=='-' || peek=='*' || peek =='/'
						|| peek=='|' || peek=='&' || peek=='<' || peek =='>'){
							return new NumberTok(Tag.NUM, intero);
						}else{
						System.err.println("Erroneous identifier/number: " + accumulatore+peek);
						return null;}
					}
				
						
					
					

                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
		
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        //String path = "...path..."; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader("prova.txt"));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}