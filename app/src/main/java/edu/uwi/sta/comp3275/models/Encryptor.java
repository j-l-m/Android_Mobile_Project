package edu.uwi.sta.comp3275.models;

/**
 * Created by JMungal on 09/03/2016.
 */
import android.os.Environment;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Encryptor{


    private String secret;
    private static final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp4";

    public Encryptor(String secret) {

        this.secret = secret;
    }


    private byte[] createKey(String secret){
        byte[] key = null;
        try {
            key = (secret).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private Cipher createCipher(){
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return aesCipher;
    }


    public void encrypt(){
        try{
            byte[] key = createKey(this.secret);
            SecretKeySpec aesKey= new SecretKeySpec(key, "AES");
            Cipher aesCipher = createCipher();


            FileInputStream fileInputStream=null;

            File file = new File(this.filepath);

            byte[] bFile = new byte[(int) file.length()];

            try {
                //convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();


                System.out.println("byte[] created");
            }catch(Exception e){
                e.printStackTrace();
            }


            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] fileEncrypted = aesCipher.doFinal(bFile);


            try  {
                FileOutputStream fos = new FileOutputStream(this.filepath);
                fos.write(fileEncrypted);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

