package edu.uwi.sta.comp3275.models;

/**
 * Created by JM on 09/03/2016.
 */

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

public class VoiceEncryptor{


    private String secret;

    public VoiceEncryptor(String secret) {
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
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return aesCipher;
    }


    private byte[] getFileBytes(String filepath){
        FileInputStream fileInputStream=null;

        File file = new File(filepath);

        byte[] input = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(input);
            fileInputStream.close();


            System.out.println("byte[] created");
        }catch(Exception e){
            e.printStackTrace();
        }

        return input;

    }


    public void decrypt(String filepath){
        try{
            byte[] key = createKey(this.secret);
            SecretKeySpec aesKey= new SecretKeySpec(key, "AES");
            Cipher aesCipher = createCipher();

            byte[] input = getFileBytes(filepath);

            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] fileDecrypted = aesCipher.doFinal(input);

            try  {
                FileOutputStream fos = new FileOutputStream(filepath);
                fos.write(fileDecrypted);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void encrypt(String filepath){
        try{
            byte[] key = createKey(this.secret);
            SecretKeySpec aesKey= new SecretKeySpec(key, "AES");
            Cipher aesCipher = createCipher();

            byte[] input = getFileBytes(filepath);

            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] fileEncrypted = aesCipher.doFinal(input);

            try  {
                FileOutputStream fos = new FileOutputStream(filepath);
                fos.write(fileEncrypted);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

