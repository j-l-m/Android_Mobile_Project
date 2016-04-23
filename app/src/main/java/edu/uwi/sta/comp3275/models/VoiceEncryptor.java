package edu.uwi.sta.comp3275.models;

/**
 *
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


/*
   This class provides encryption and decryption of voice files recorded by the application
   AES 128bit  was utilized for encryption
   To ensure sufficient key size, a hash of the user defined key is performed
 */

public class VoiceEncryptor{

    //the shared key
    private String secret;

    //Constructor, accepts the shared key as a parameter
    public VoiceEncryptor(String secret) {
        this.secret = secret;
    }

    /*
      Creates and returns the encryption key as a byte array
      AES requires a 128bit key
      To provide this a SHA-1 hash of the user defined key is performed
        this produces as 160bit (or 20 byte) key
        Only the first 16 bytes (128bits) are used
        This allows the user to have any size key
     */
    private byte[] createKey(String secret){
        byte[] key = null;
        try {
            key = (secret).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); //take the first 16 bytes

        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /*
      Returns and instance of Cipher class using AES
     */
    private Cipher createCipher(){
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return aesCipher;
    }

    /*
      Convert file to byte array for encryption
     */
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

    /*
       Decrypts an AES 128bit encrypted file
       Initializes the Cipher class for AES
       converts the file to a byte array using getFileBytes
       sets the mode to Decrypt using the AES key
       decrypts the byte array
       overwrites the original file with the decrypted version
     */
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

    /*
       Encrypts an AES 128bit encrypted file
       Initializes the Cipher class for AES
       converts the file to a byte array using getFileBytes
       sets the mode to Encrypt using the AES key
       encrypts the byte array
       overwrites the original file with the encrypted version
     */
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

