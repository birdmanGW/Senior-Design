import java.math.*;
import java.util.*;
import java.io.*;


/*
 * Decrypt given ciphertextResult with given keys
 *
 * Returns result
 */
public class Paillier_Verifier {

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

    public Paillier_Verifier(String[] str) {

    	lambda  = new BigInteger(str[0]);
    	g 	    = new BigInteger(str[1]);
    	n 	    = new BigInteger(str[2]);
    	nsquare = n.multiply(n);
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

    public BigInteger returnValues(BigInteger result) {
    	return result;
    }


    // str = [lambda, g, n, cipherTextResult]
    public static void main(String[] str) {

    	Paillier_Verifier paillier = new Paillier_Verifier(str);
    	
    	BigInteger cipherTextResult = new BigInteger(str[3]);

    	BigInteger result = paillier.Decryption(cipherTextResult);

    	paillier.returnValues(result);


    }
}