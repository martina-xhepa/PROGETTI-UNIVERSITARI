public class UnoCinque{

	public static boolean scan(String s){
		
		int state = 0;
		int i = 0;
		
		while (state>=0 && i<s.length()){
			final char ch = s.charAt(i++);
			
			switch(state){
			
				case 0: 
					if(ch<='K' && ch>='A')
						state=1;
					else if(ch<='Z' && ch>='L')
						state=2;
					else 
						state=-1;
					break;
					
				case 1: 
						if((ch<='z' && ch>='a'))
							state=1;
						else if((ch<='9' && ch>='0') && ch%2==0)
							state=3;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=4;
						else 
							state=-1;
						break;
				
				case 2:
						if((ch<='z' && ch>='a'))
							state=2;
						else if((ch<='9' && ch>='0') && ch%2==0)
							state=6;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=5;
						else 
							state = -1;
						break;
						
				
				case 3:
						if((ch<='9' && ch>='0') && ch%2==0)
							state=3;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=4;
						else 
							state = -1;
						break;
						
				case 4:
						if((ch<='9' && ch>='0') && ch%2==0)
							state=3;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=4;
						else 
							state = -1;
						break;
				
				case 5: 
						if((ch<='9' && ch>='0') && ch%2==0)
							state=6;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=5;
						else 
							state = -1;
						break;
						
				case 6:
						if((ch<='9' && ch>='0') && ch%2==0)
							state=6;
						else if((ch<='9' && ch>='1') && ch%2==1)
							state=5;
						else 
							state = -1;
						break;
						
			}
		}
		return (state==3 || state==5);
	
	}

	public static void main(String[] args){
		
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
		
	}
}