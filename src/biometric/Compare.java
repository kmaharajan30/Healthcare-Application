
package biometric;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import javax.crypto.Cipher;
import java.text.NumberFormat;


public class Compare 
{
    Details dt=new Details();
    Compare()
    {
        
    }  
    
    public void perform1(String alg,String ms,String ks) 
    {
        try
        {
            // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(alg);//,"BC");
            KeyPair kp=keyGen.genKeyPair();
            PrivateKey priKey=kp.getPrivate();
            PublicKey pubKey=kp.getPublic();
        
            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] enc = cipher.doFinal(ms.getBytes());
        
            cipher.init(Cipher.DECRYPT_MODE, priKey);		
            byte[] dec = cipher.doFinal(enc);
        
        }
        catch(Exception e)
        {
            
        }
    }
}
