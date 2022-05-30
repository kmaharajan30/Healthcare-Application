
package biometric;

public class SKey 
{
    private Poly1[] keyExpansion;
    private int Nr; 
    private int Nk; 
    private final int Nb = 4;

    
    public SKey(String cipherKey, boolean unicodeKey) 
    {
        setRoundNumber(cipherKey.length());
        keyExpansion = new Poly1[Nb * (Nr + 1)];
        if (unicodeKey) 
        {
            for (int i = 0; i < Nk; i++) 
            {
                Poly1 word = Parser.charsToWord(
                        cipherKey.charAt(i * 2), cipherKey.charAt(i * 2 + 1));
                keyExpansion[i] = word;
            }
        } 
        else 
        {
            for (int i = 0; i < Nk; i++) 
            {
                Poly1 word = new Poly1();
                String str = cipherKey.substring(i * 8, (i + 1) * 8);
                word.x0 = new Bin1(Integer.parseInt(str.substring(0, 2), 16));
                word.x1 = new Bin1(Integer.parseInt(str.substring(2, 4), 16));
                word.x2 = new Bin1(Integer.parseInt(str.substring(4, 6), 16));
                word.x3 = new Bin1(Integer.parseInt(str.substring(6, 8), 16));
                keyExpansion[i] = word;
            }
        }
        expand();
    }

    private void expand() 
    {
        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            Poly1 temp = keyExpansion[i - 1];
            if (i % Nk == 0) {
                temp = rotWord(temp);
                temp = subWord(temp);
                temp = temp.addTo(ReCon.getInstance().rcon.get(i / Nk - 1));
            } else if ((Nk == 8) && (i % 4 == 0)) {
                temp = subWord(temp);
            }
            temp = temp.addTo(keyExpansion[i - Nk]);
            keyExpansion[i] = temp;
        }
    }

    public int getNr() {
        return Nr;
    }

    public State getRoundKey(int round) 
    {
        State result = new State();
        for (int j = 0; j < 4; j++) {
            result.wordToCollumn(keyExpansion[round * 4 + j], j);
        }
        return result;
    }

    private Poly1 rotWord(Poly1 word) 
    {
        return word.multiply(new Poly1(0, 0, 0, 1));
    }

    private Poly1 subWord(Poly1 word) 
    {
        Poly1 result = new Poly1();
        int x = (word.x0.poly & 0xf0) >> 4;
        int y = (word.x0.poly & 0x0f);
        result.x0.poly = SBox.getInstance().apply(x, y);
        x = (word.x1.poly & 0xf0) >> 4;
        y = (word.x1.poly & 0x0f);
        result.x1.poly = SBox.getInstance().apply(x, y);
        x = (word.x2.poly & 0xf0) >> 4;
        y = (word.x2.poly & 0x0f);
        result.x2.poly = SBox.getInstance().apply(x, y);
        x = (word.x3.poly & 0xf0) >> 4;
        y = (word.x3.poly & 0x0f);
        result.x3.poly = SBox.getInstance().apply(x, y);
        return result;
    }

    private void setRoundNumber(int cipherKeyLenght) 
    {
        switch (cipherKeyLenght) {
            case 32:    
            case 8:     
                Nr = 10;
                Nk = 4;
                break;
            case 48:
            case 12:
                Nr = 12;
                Nk = 6;
                break;
            case 64:
            case 16:
                Nr = 14;
                Nk = 8;
                break;
        }
    }
}
