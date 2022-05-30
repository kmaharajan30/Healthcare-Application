
package biometric;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AES1 
{
    Details dt=new Details();
    AES1()
    {        
        
    }
    
    public Key generateKey(String keyValue, String algo) throws Exception 
    {
        Key key = new SecretKeySpec(keyValue.getBytes(), algo);       			
        return key;
    }
    
    public String encrypt(String Data,String keyVal, String algo) throws Exception 
    {
        Key key = generateKey(keyVal,algo);
		
	
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());		
        String encryptedValue = new BASE64Encoder().encode(encVal);			
        return encryptedValue;
		
    }

    public  String decrypt(String encryptedData,String keyVal, String algo) throws Exception 
    {
       Key key = generateKey(keyVal,algo);		
        Cipher c = Cipher.getInstance(algo);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
	  
        byte[] decValue = c.doFinal(decordedValue);
		
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
}
