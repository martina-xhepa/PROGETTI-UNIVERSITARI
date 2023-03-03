public class NumberTok extends Token {
	public int attribute;
    public NumberTok(int tag, int s) { super(tag); attribute=s; }
    public String toString() { return "<" + tag + ", " + attribute + ">"; }
}
