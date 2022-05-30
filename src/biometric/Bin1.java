
package biometric;

public class Bin1 
{
    public int poly;

    public Bin1(int poly) 
    {
        this.poly = poly;
    }

    public Bin1 addTo(Bin1 other) 
    {
        return new Bin1(this.poly ^ other.poly);
    }

    public Bin1 addTo(int other) 
    {
        return new Bin1(this.poly ^ other);
    }

    public void addToSelf(int other) 
    {
        this.poly = this.poly ^ other;
    }

    public Bin1 multiply(Bin1 other) 
    {
        int p = this.poly;
        int q = other.poly;
        Bin1 result = new Bin1(0);
        int mask = 1; 
        for (int i = 0; i < 8; i++) 
        {
            if ((mask & q) != 0) 
            { 
                result.addToSelf(p);
            }
            
            p = p << 1;
            if (p > 255) 
            {
                p = (p ^ 0x1B) & 0xFF;
            }
            mask = mask << 1;
        }
        return result;
    }

}
