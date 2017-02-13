import java.math.*;
import java.util.*;
import java.io.*;


/*
 * Ranomize plaintext account balances for inputted number of accounts
 * Encrypt each plaintext element respectfully
 *
 * Returns cipherTextList
 */
public class Paillier_Banks {

    /**
     * p and q are two large primes. 
     * lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private static BigInteger lambda;
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

    public Paillier_Banks(String[] str) {
    	setKeys(str);
    }

    public void setKeys(String[] str) {

		lambda    = new BigInteger(str[0]);
		g         = new BigInteger(str[1]);
		n         = new BigInteger(str[2]);
		bitLength = 512;
		nsquare   = n.multiply(n);
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

    public String[] returnValues(String[] ciphertextList) {
    	return ciphertextList;
    }


    // str = [lambda, g, n, accounts]
	public static void main(String[] str) {

		Paillier_Banks paillier = new Paillier_Banks(str);

		//Set min and max randomized values for bank accounts
		int min = 5000;
		int max = 25000;
		int accounts = Integer.valueOf(str[3]);

		ArrayList<BigInteger> plaintextList = paillier.RandomizePlaintexts(min, max, accounts);
    	String[] ciphertextList = new String[accounts*2];

    	//For each plaintext element in plaintextList, encrypt and add to ciphertextList
    	for (int i = 0; i < plaintextList.size(); i++)
    		ciphertextList[i] = String.valueOf(paillier.Encryption(plaintextList.get(i)));

    	paillier.returnValues(ciphertextList);


	}
}