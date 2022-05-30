package biometric;

public class State 
{
    public int [] state = new int[16];
    public State(int [] state)
    {
        this.state = state;
    } 
    public State()
    {
        
    }    
    public Poly1 collumnAsWord(int x)
    {
        return new Poly1(state[x],state[4+x],state[8+x], state[12+x]);
    }
    
    public void wordToCollumn(Poly1 w, int x)
    {
        state[x] = w.x0.poly;
        state[4+x] = w.x1.poly;
        state[8+x] = w.x2.poly;
        state[12+x] = w.x3.poly;
    }
    

    @Override
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) 
        {
            for (int j = 0; j < 4; j++) 
            {
                sb.append("{");
                sb.append(Integer.toHexString(state[i*4+j]));
                sb.append("} ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
   
}
