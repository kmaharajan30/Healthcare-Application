
package biometric;

public class Poly1 
{
    Bin1 x3, x2, x1, x0;

    public Poly1(int a0, int a1, int a2, int a3) 
    {
        x3 = new Bin1(a3);
        x2 = new Bin1(a2);
        x1 = new Bin1(a1);
        x0 = new Bin1(a0);
    }
    
    public Poly1() 
    {
        x3 = new Bin1(0);
        x2 = new Bin1(0);
        x1 = new Bin1(0);
        x0 = new Bin1(0);
    }
    

    public Poly1(Bin1 a0, Bin1 a1, Bin1 a2, Bin1 a3) 
    {
        x3 = a3;
        x2 = a2;
        x1 = a1;
        x0 = a0;
    }

    public Poly1 addTo(Poly1 other) 
    {
        return new Poly1(this.x0.addTo(other.x0),
                this.x1.addTo(other.x1),
                this.x2.addTo(other.x2),
                this.x3.addTo(other.x3));

    }

    public Poly1 multiply(Poly1 other) {
        Bin1 d3 = this.x3.multiply(other.x0)
                .addTo(this.x2.multiply(other.x1))
                .addTo(this.x1.multiply(other.x2))
                .addTo(this.x0.multiply(other.x3));
        Bin1 d2 = this.x2.multiply(other.x0)
                .addTo(this.x1.multiply(other.x1))
                .addTo(this.x0.multiply(other.x2))
                .addTo(this.x3.multiply(other.x3));
        Bin1 d1 = this.x1.multiply(other.x0)
                .addTo(this.x0.multiply(other.x1))
                .addTo(this.x3.multiply(other.x2))
                .addTo(this.x2.multiply(other.x3));
        Bin1 d0 = this.x0.multiply(other.x0)
                .addTo(this.x3.multiply(other.x1))
                .addTo(this.x2.multiply(other.x2))
                .addTo(this.x1.multiply(other.x3));
        return new Poly1(d0, d1, d2, d3);
    }

}
