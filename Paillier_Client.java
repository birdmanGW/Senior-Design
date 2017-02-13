import java.math.*;
import java.util.*;
import java.io.*;

/*
 * Generates Keys, Mask
 * E(Threshold, Mask)
 *
 * Returns <lambda, g, n, cipherMask, cipherThreshold>
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

    public String[] returnValues(BigInteger cipherThreshold, BigInteger cipherMask) {
    	String[] returnvalues = new String[5];
    	returnvalues[0] = String.valueOf(lambda);
    	returnvalues[1] = String.valueOf(g);
    	returnvalues[2] = String.valueOf(n);
    	returnvalues[3] = String.valueOf(cipherMask);
    	returnvalues[4] = String.valueOf(cipherThreshold);

    	return returnvalues;

    }

    // str = [threshold]
	public static void main(String[] str) {

		Paillier_Client paillier = new Paillier_Client();

		Random rand = new Random();

		BigInteger threshold = new BigInteger(str[0]);
		BigInteger mask = new BigInteger(String.valueOf(rand.nextInt()));

		BigInteger cipherThreshold = paillier.Encryption(threshold);
		BigInteger cipherMask = paillier.Encryption(mask);

		paillier.returnValues(cipherThreshold, cipherMask);

	}
}