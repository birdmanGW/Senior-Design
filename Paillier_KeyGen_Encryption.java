/*
 * Paillier Key Generation & Encryption:
 * Ranomizes plaintext elements
 * used by client side app
 * Must send keys to  
 */

import java.math.*;
import java.util.*;
import java.io.*;

public class Paillier_KeyGen_Encryption {

    /**
     * p and q are two large primes. 
     * lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private static BigInteger p,  q,  lambda;
    /**
     * n = p*q, where p and q are two large primes.
     */
    public static BigInteger n;
    /**
     * nsquare = n*n
     */
    public static BigInteger nsquare;
    /**
     * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
     */
    private static BigInteger g;
    /**
     * number of bits of modulus
     */
    private static int bitLength;


     public Paillier_KeyGen_Encryption() {
     	KeyGeneration();
     }


    /**
     * Sets up the public key and private key.
     */
    public void KeyGeneration() {
        bitLength = 512;
        /*Constructs two randomly generated positive BigIntegers that are probably prime, with the specified bitLength and certainty.*/
        p = new BigInteger(bitLength / 2, 64, new Random());
        q = new BigInteger(bitLength / 2, 64, new Random());

        n = p.multiply(q);
        nsquare = n.multiply(n);

        g = new BigInteger("2");
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)).divide(
                p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));
        /* check whether g is good.*/
        if (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
            System.out.println("g is not good. Choose g again.");
            System.exit(1);
        }
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function automatically generates random input r (to help with encryption).
     * @param m plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);

    }


	/**
     * Decrypts ciphertext c. plaintext m = L(c^lambda mod n^2) * u mod n, where u = (L(g^lambda mod n^2))^(-1) mod n.
     * @param c ciphertext as a BigInteger
     * @return plaintext as a BigInteger
     */
    public static BigInteger Decryption(BigInteger c) {
        BigInteger u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }


    /**
     * Randomizes plaintext elements from user inputed number of bank accounts
     * @param min minimum ranomized threshold value
     * @param max maximum ranomized threshold value
     * @oaram accounts number of bank accounts to create ranomized plaintext values for
     */
    public ArrayList<BigInteger> RandomizePlaintexts(int min, int max, int accounts) {

    	Random rand = new Random();
    	ArrayList<BigInteger> plaintextlist = new ArrayList<BigInteger>();

    	for (int i = 0; i < accounts; i++)
    	{
    		BigInteger check = BigInteger.valueOf(rand.nextInt((max - min) + 1) + min);
    		BigInteger save = BigInteger.valueOf(rand.nextInt((max - min) + 1) + min);
    		System.out.println("Checking (" + i + "): " + check);
    		System.out.println("Savings (" + i + "): " + save);
    		plaintextlist.add(check);
    		plaintextlist.add(save);
    	}

    	return plaintextlist;
    }

	/*
	 * Sums an EVEN number of ciphertexts (Checking + Savings)
	 */
    public BigInteger ciphertextSummation(ArrayList<BigInteger> ciphertextList) {

        BigInteger ciphertext1;
        BigInteger ciphertext2;
        BigInteger ciphertextSum = new BigInteger("1");

        for (int n = 0; n < ciphertextList.size(); n = n + 2) 
        {

            ciphertext1 = ciphertextList.get(n);
            ciphertext2 = ciphertextList.get(n + 1);

            ciphertextSum = ciphertextSum.multiply(ciphertext1).multiply(ciphertext2);

        }

        ciphertextSum = ciphertextSum.mod(nsquare);

        return ciphertextSum;
    } 


    /*
     * 
     * GENERATES PLAINTEXT ELEMENTS FOR 'ACCOUNTS' ACCOUNTS
     * ACCOUNTS ARE USER INPUT
     * 1. GENERATE KEYS
     * 2. PLAINTEXT ELEMENTS RANDOMELY GENEREATED FOR CHECKING AND SAVINGS FOR 'ACCOUNTS' ACCOUNTS
     * 3. ENCRYPTS ALL PLAINTEXT ELEMENTS WITH GENERATED KEYS
     * 4. SEND (CIPHERTEXTS, N) IN ARRAYLIST TO EVERIFY
     * 5. SEND KEYS (G, N, LAMBDA) TO VERIFY APP
     *
     */
    public static void main(String[] str) {

    	Paillier_KeyGen_Encryption paillier = new Paillier_KeyGen_Encryption();

        Random rand = new Random();

    	int min = 5000;
    	int max = 25000;
    	int accounts = Integer.valueOf(str[0]);
    	BigInteger sum = new BigInteger("0");
    	BigInteger threshold = new BigInteger(str[1]);
        BigInteger mask = new BigInteger(String.valueOf(rand.nextInt((100 - 10) + 1) + 10));
        BigInteger cipherThreshold = paillier.Encryption(new BigInteger(str[1]));

    	BigInteger ciphertextSum;
    	BigInteger cipherTextResult;


    	ArrayList<BigInteger> plaintextList = paillier.RandomizePlaintexts(min, max, accounts);
    	ArrayList<BigInteger> ciphertextList = new ArrayList<BigInteger>();

    	//For each plaintext element in plaintextList, encrypt and add to ciphertextList
    	for (int i = 0; i < plaintextList.size(); i++)
    	{
    		ciphertextList.add(paillier.Encryption(plaintextList.get(i)));
    		sum = sum.add(plaintextList.get(i));
    	}


    	ciphertextSum = paillier.ciphertextSummation(ciphertextList);

    	cipherTextResult = (ciphertextSum.multiply(cipherThreshold.modInverse(nsquare))).mod(nsquare);


    	System.out.println("Plaintext Sum: " + sum + " || Threshold: " + threshold);
    	//System.out.println("ciphertextSum: " + ciphertextSum);
    	//System.out.println("cipherThreshold: " + cipherThreshold);
    	//System.out.println("ciphertextSum - cipherThreshold: " + cipherTextResult);
        System.out.println("Mask: " + mask);
        System.out.println();
        System.out.println("Sum - threshold: " + sum.subtract(threshold));
        //System.out.println("(Sum - threshold): " + (sum.subtract(threshold)));
    	//System.out.println("(Sum - threshold) * mask: " + (sum.subtract(threshold)).multiply(mask));
    	System.out.println();
        if (paillier.Decryption(cipherTextResult).compareTo(new BigInteger("100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000")) == 1)
        {
            BigInteger ans = (paillier.Decryption(cipherTextResult.multiply(mask).mod(n))).subtract(n);
            BigInteger ans1 = paillier.Decryption(cipherTextResult).subtract(n);
            //System.out.println("D(cipherTextSum - cipherThreshold (negative = failed)): " + ans1);
            //System.out.println("D(cipherTextSum(mask) - cipherThreshold (negative = failed)): " + ans);
            System.out.println("You do not exceed minimum qualifying balance for verification.  Failed.");
        }
        else
        {
            //System.out.println("D(cipherTextSum - cipherThreshold): " + paillier.Decryption(cipherTextResult));
            //System.out.println("D(cipherTextSum(mask) - cipherThreshold): " + paillier.Decryption(cipherTextResult.multiply(mask).mod(n)));
            System.out.println("You exceed minimum qualifying balance for verification!");
        }
        //System.out.println("D( (cipherTextSum - cipherThreshold) ): " + paillier.Decryption(cipherTextResult));
        //System.out.println("D( (cipherTextSum - cipherThreshold) * mask ): " + paillier.Decryption(cipherTextResult.multiply(mask)));



    }


}