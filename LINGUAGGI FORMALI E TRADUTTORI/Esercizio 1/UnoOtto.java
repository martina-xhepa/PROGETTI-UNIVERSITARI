public class UnoOtto{ //Martina

	public static boolean scan(String s){
		
		int state=0;
		int i=0;
		
		while(state>=0 && i<s.length()){
			final char ch = s.charAt(i++);
			
			switch(state){
				case 0:
					if(ch=='M')
						state=1;
					else if(ch!='M')
						state=8;
					else
						state=-1;
					break;
					
				case 1:
					if(ch=='a')
						state=2;
					else if(ch!='a')
						state=9;
					else 
						state=-1;
					break;
					
				case 2: 
				if(ch=='r')
					state=3;
				else if(ch!='r')
					state=10;
				else 
					state=-1;
				break;
				
				case 3:
					if(ch=='t')
						state=4;
					else if(ch!='t')
						state=11;
					else 
						state=-1;
					break;
					
				case 4:
					if(ch=='i')
						state=5;
					else if(ch!='1')
						state=12;
					else 
						state=-1;
					break;
					
				case 5:
					if(ch=='n')
						state=6;
					else if(ch!='n')
						state=13;
					else 
						state=-1;
					break;
					
				case 6:
					state=7;
					break;
				
				case 7:
					state=-1;
					break;
					
				case 8:
					if(ch=='a')
						state=9;
					else 
						state=-1;
					break;
					
				case 9:
					if(ch=='r')
						state=10;
					else 
						state=-1;
					break;
					
				case 10:
					if(ch=='t')
						state=11;
					else 
						state=-1;
					break;
					
				case 11:
					if(ch=='i')
						state=12;
					else 
						state=-1;
					break;
				
				case 12:
					if(ch=='n')
						state=13;
					else 
						state=-1;
					break;
					
				case 13:
					if(ch=='a')
						state=7;
					else 
						state=-1;
					break;
				
			}
		}
		
		return(state==7);
	}

	public static void main(String[] args){
		
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
		
	}

}