import java.math.*;
import java.util.*;
import java.io.*;
import java.lang.*;

/*
 * Generates Keys, Mask
 * E(Threshold, Mask)
 * Randomizes "Accounts" Plaintesxts
 * E(plaintesxts)
 *
 * Returns <cipherTesxtList, n, threshold, cipherThreshold, cipherHMask>
 */
public class Paillier_Client {


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

    /**
     * Sets up the public key and private key.
     */


    public Paillier_Client() {
    	KeyGeneration();
    }

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
     * RETURNS:
     * "cipherTextList"
     */
    public String returnValue0(String[] ciphertextList) {

    	StringBuilder strb = new StringBuilder();

        for (int i = 0; i < ciphertextList.length; i++)
            strb.append(ciphertextList[i] + ",");

    	String result = strb.toString();
    	
    	return result;
    }

    /*
     * RETURNS:
     * "threshold"
     */
    public String returnValue1(BigInteger threshold) {

    	String result = String.ValueOf(threshold);

    	return result;
    }

    /*
     * RETURNS:
     * "cipherThreshold"
     */
    public String returnValue2(BigInteger cipherThreshold) {

    	String result = String.ValueOf(cipherThreshold);
    	
    	return result;
    }

    /*
     * RETURNS:
     * "cipherMask"
     */
    public String returnValue3(BigInteger cipherMask) {

    	String result = String.ValueOf(cipherMask);
    	
    	return result;
    }

    // str = [threshold, #banks]
	public static void main(String[] str) {

		Paillier_Client paillier = new Paillier_Client();

		Random rand = new Random();

		//read threshold from args input
		BigInteger threshold = new BigInteger(str[0]);
		//Create random mask between 1 and 100
		BigInteger mask = new BigInteger(String.valueOf(rand.nextInt((100 - 5) + 1) + 5));

		BigInteger cipherThreshold = paillier.Encryption(threshold);
		BigInteger cipherMask = paillier.Encryption(mask);

		//Set min and max randomized values for bank accounts
		int min = 5000;
		int max = 25000;
		int accounts = Integer.valueOf(str[1]);

		ArrayList<BigInteger> plaintextList = paillier.RandomizePlaintexts(min, max, accounts);
    	String[] ciphertextList = new String[accounts*2];

    	//For each plaintext element in plaintextList, encrypt and add to ciphertextList
    	for (int i = 0; i < plaintextList.size(); i++)
    		ciphertextList[i] = String.valueOf(paillier.Encryption(plaintextList.get(i)));

		String value0 = paillier.returnValue0(ciphertextList);

		String value1 = paillier.returnValue1(threshold);

		String value2 = paillier.returnValue2(cipherThreshold);

		String value3 = paillier.returnValue3(cipherMask)

	}
}