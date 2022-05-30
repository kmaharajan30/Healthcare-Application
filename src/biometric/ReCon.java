
package biometric;
import java.util.ArrayList;

public class ReCon
{
    public ArrayList<Poly1> rcon = new ArrayList<>();
        
    
    private ReCon() 
    {
        rcon.add(new Poly1(0x1, 0x0, 0x0, 0x0));
        Bin1 p = new Bin1(0x2);
        rcon.add(new Poly1(0x2, 0x0, 0x0, 0x0));
        for (int i = 2; i < 14; i++) 
        {
            p = p.multiply(new Bin1(0x2));
            Poly1 temp = new Poly1(p.poly, 0x0, 0x0, 0x0);
            rcon.add(temp);        
        }
    }
    
    public static ReCon getInstance() 
    {
        return RConHolder.INSTANCE;
    }
    
    private static class RConHolder 
    {
        private static final ReCon INSTANCE = new ReCon();
    }
}
