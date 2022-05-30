
package biometric;

public class Parser 
{
    public static State[] getStateBlocks(String str) 
    {
        str = str.trim();
        if (str.length() % 8 != 0) 
        {
            int remainder = (str.length() % 8);
            for (int i = 0; i < (8 - remainder); i++) 
            {
                str += " ";
            }
        }
        State[] result = new State[str.length() / 8];
        for (int i = 0; i < str.length() / 8; i++) 
        {
            result[i] = getStateFromText(str.substring(i * 8, (i + 1) * 8));
        }
        return result;
    }
    
    public static State getStateFromText(String str) 
    {        
        State result = new State();
        for (int j = 0; j < 4; j++) 
        {
            result.wordToCollumn(charsToWord(str.charAt(j * 2), str.charAt(j * 2 + 1)), j);
        }
        return result;
    }
    
    public static Poly1 charsToWord(char ch1, char ch2) 
    {
        int c1 = (int) ch1;
        int c2 = (int) ch2;
        return new Poly1((c1 & 0xff00) >> 8,
                (c1 & 0x00ff),
                (c2 & 0xff00) >> 8,
                (c2 & 0x00ff));
    }    
    public static char[] wordToString(Poly1 word) 
    {
        char[] result = {
            Character.toChars((word.x0.poly << 8) | word.x1.poly)[0],
            Character.toChars((word.x2.poly << 8) | word.x3.poly)[0]};
        return result;
    }

    public static String getStringFromState(State[] states) 
    {
        String result = "";
        for (State state : states) 
        {
            for (int i = 0; i < 4; i++) 
            {
                char [] chr = wordToString(state.collumnAsWord(i));		
                result += Character.toString(chr[0]) + Character.toString(chr[1]);
            }
        }
        return result.trim();
    }
}
