/*Qui ho da costante numerica fino alla and &&*/
public class Tag {
    public final static int
	EOF = -1, NUM = 256, ID = 257, RELOP = 258,
	COND = 259, WHEN = 260, THEN = 261, ELSE = 262, 
	WHILE = 263, DO = 264, SEQ = 265, PRINT = 266, READ = 267, 
	OR = 268, AND = 269;
}
/*
Questa classe contiene delle variabili statiche quindi nel 
codice vi possiamo accedere direttamente usando ad esempio 
Tag.COND e andiamo direttamente a prendere il valore 259 come
costante numerica final, quindi non potrò mai andare a modificarla 
dal codice.
Essendo statica accediamo appunto dal nome della classe, che non 
devo istanziare. 
Quindi se devo modificare qualche cosa, aggiungere qualche token
o che o faccio direttamente da questa classe.
All'interno del codice avremo accesso al nome dei tag usando la classe Tag
Ad esempio con Tag.NUM potrò avere:
	...
	return Tag.NUM;
	...
	
Questo invece di scrivere return <256...>
*/