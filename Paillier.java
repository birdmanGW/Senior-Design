/**
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 * more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.*;
import java.util.*;
import java.io.*;

/**
 * Paillier Cryptosystem <br><br>
 * References: <br>
 * [1] Pascal Paillier, "Public-Key Cryptosystems Based on Composite Degree Residuosity Classes," EUROCRYPT'99.
 *    URL: <a href="http://www.gemplus.com/smart/rd/publications/pdf/Pai99pai.pdf">http://www.gemplus.com/smart/rd/publications/pdf/Pai99pai.pdf</a><br>
 * 
 * [2] Paillier cryptosystem from Wikipedia. 
 *    URL: <a href="http://en.wikipedia.org/wiki/Paillier_cryptosystem">http://en.wikipedia.org/wiki/Paillier_cryptosystem</a>
 * @author Kun Liu (kunliu1@cs.umbc.edu)
 * @version 1.0
 */
public class Paillier {

    /**
     * p and q are two large primes. 
     * lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private BigInteger p,  q,  lambda;
    /**
     * n = p*q, where p and q are two large primes.
     */
    public BigInteger n;
    /**
     * nsquare = n*n
     */
    public BigInteger nsquare;
    /**
     * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
     */
    private BigInteger g;
    /**
     * number of bits of modulus
     */
    private int bitLength;
    /**
     * index in ArrayList
     */
     public int value;  
    /**
     * isummation of plaintexts
     */
     public BigInteger plaintextSum;  
    /**
     * summation of ciphertexts
     */
     public BigInteger ciphertextSum;  

    /**
     * Constructs an instance of the Paillier cryptosystem.
     * @param bitLengthVal number of bits of modulus
     * @param certainty The probability that the new BigInteger represents a prime number will exceed (1 - 2^(-certainty)). The execution time of this constructor is proportional to the value of this parameter.
     */
    public Paillier(int bitLengthVal, int certainty) {
        KeyGeneration(bitLengthVal, certainty);
    }

    /**
     * Constructs an instance of the Paillier cryptosystem with 512 bits of modulus and at least 1-2^(-64) certainty of primes generation.
     */
    public Paillier() {
        KeyGeneration(512, 64);
    }

    /**
     * Sets up the public key and private key.
     * @param bitLengthVal number of bits of modulus.
     * @param certainty The probability that the new BigInteger represents a prime number will exceed (1 - 2^(-certainty)). The execution time of this constructor is proportional to the value of this parameter.
     */
    public void KeyGeneration(int bitLengthVal, int certainty) {
        bitLength = bitLengthVal;
        /*Constructs two randomly generated positive BigIntegers that are probably prime, with the specified bitLength and certainty.*/
        p = new BigInteger(bitLength / 2, certainty, new Random());
        q = new BigInteger(bitLength / 2, certainty, new Random());

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
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function explicitly requires random input r to help with encryption.
     * @param m plaintext as a BigInteger
     * @param r random plaintext to help with encryption
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m, BigInteger r) {
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
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
    public BigInteger Decryption(BigInteger c) {
        BigInteger u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }

    public ArrayList<BigInteger> ReadPlaintext(String file) throws IOException {

        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
 
        String plaintext;

        ArrayList<BigInteger> plaintextList = new ArrayList();
        
        while ((plaintext = br.readLine()) != null)
            plaintextList.add(new BigInteger(plaintext));
            

        return plaintextList;
    }

    public BigInteger ciphertextSummation(ArrayList<BigInteger> ciphertextList) {

        BigInteger ciphertext1;
        BigInteger ciphertext2;
        BigInteger ciphertextSum = new BigInteger("1");

        for (int n = 0; n < ciphertextList.size(); n++) {
        
            if (n == ciphertextList.size() - 1) {
                ciphertext1 = ciphertextList.get(n);
                ciphertext2 = new BigInteger("1");
            }
            else {
                ciphertext1 = ciphertextList.get(n);
                ciphertext2 = ciphertextList.get(n + 1);
            }

            ciphertextSum = ciphertext1.multiply(ciphertext2).multiply(ciphertextSum);

            n++;
        }

        ciphertextSum = ciphertextSum.mod(nsquare);

        return ciphertextSum;
    } 

    public BigInteger plaintextSummation(ArrayList<BigInteger> plaintextList) {

        BigInteger plaintext1;
        BigInteger plaintext2;
        BigInteger plaintextSum = new BigInteger("0");

        for (int n = 0; n < plaintextList.size(); n++) {
        
            if (n == plaintextList.size() - 1) {
                plaintext1 = plaintextList.get(n);
                plaintext2 = new BigInteger("0");
            }
            else {
                plaintext1 = plaintextList.get(n);
                plaintext2 = plaintextList.get(n + 1);
            }

            plaintextSum = plaintext1.add(plaintext2).add(plaintextSum);

            n++;
        }

        plaintextSum = plaintextSum.mod(n);

        return plaintextSum;
    } 

    /**
     * main function
     * @param str intput string
     */
    public static void main(String[] str) throws IOException, FileNotFoundException {
        /* instantiating an object of Paillier cryptosystem*/
        Paillier paillier = new Paillier();

        BigInteger plaintextSum;
        BigInteger ciphertextSum;

        /* gather all plaintexts in BigInteger Array*/
        ArrayList<BigInteger> plaintextList = paillier.ReadPlaintext("plaintexts.txt");

        /* initialize ciphertextList*/
        ArrayList<BigInteger> ciphertextList = new  ArrayList();

        BigInteger ciphertext1;

        BigInteger ciphertext2;

        //BigInteger r = new BigInteger(paillier.bitLength, new Random());

        BigInteger threshold = new BigInteger("4357");
        BigInteger cipherThreshold = paillier.Encryption(threshold);

        /* encryption*/
        for (BigInteger plaintext : plaintextList) {
            ciphertextList.add(paillier.Encryption(plaintext));
        }

        /* sum Ciphertexts */
        ciphertextSum = paillier.ciphertextSummation(ciphertextList);

        /* sum PLaintexts */
        plaintextSum = paillier.plaintextSummation(plaintextList);
        
        System.out.println("Ciphertext Sum [function] : " + ciphertextSum);
        System.out.println("Threshold Ciphertext : " + cipherThreshold);
        System.out.println();
        System.out.println("CipherSum - cipherThreshold = " + ciphertextSum.subtract(cipherThreshold));
        System.out.println("D(CipherSum) - D(cipherThreshold) = " + paillier.Decryption(ciphertextSum).subtract(paillier.Decryption(cipherThreshold)));
        //System.out.println("Ciphertext Sum [hardcoded] : " + ciphertextList.get(9).multiply(ciphertextList.get(8).multiply(ciphertextList.get(7).multiply(ciphertextList.get(6).multiply(ciphertextList.get(5).multiply(ciphertextList.get(4).multiply(ciphertextList.get(3).multiply(ciphertextList.get(2).multiply(ciphertextList.get(1).multiply(ciphertextList.get(0)))))))))).mod(paillier.nsquare) );

        System.out.println("D(Ciphertext) Sum : " + paillier.Decryption(ciphertextSum));
        System.out.println("D(cipherThreshold) : " + paillier.Decryption(cipherThreshold));



        /*
        BigInteger em1 = paillier.Encryption(m1);
        BigInteger em2 = paillier.Encryption(m2);
        BigInteger em3 = paillier.Encryption(m3);
        BigInteger em4 = paillier.Encryption(m4);
        BigInteger em5 = paillier.Encryption(m5);
        BigInteger em6 = paillier.Encryption(m6);
        BigInteger em7 = paillier.Encryption(m7);
        BigInteger em8 = paillier.Encryption(m8);
        BigInteger em9 = paillier.Encryption(m9);

        /* printout encrypted text*/
        /*
        System.out.println(em1);
        System.out.println(em2);
        System.out.println(em3);
        System.out.println(em4);
        System.out.println(em5);
        System.out.println(em6);
        System.out.println(em7);
        System.out.println(em8);
        System.out.println(em9);
        */

        /* printout decrypted text */
        /*
        System.out.println(paillier.Decryption(em1).toString());
        System.out.println(paillier.Decryption(em2).toString());
        */

        
        /* test homomorphic properties -> D(E(m1)*E(m2) mod n^2) = (m1 + m2) mod n 
        BigInteger product_em1em2 = em9.multiply(em8.multiply(em7.multiply(em6.multiply(em5.multiply(em4.multiply(em3.multiply(em2.multiply(em1)))))))).mod(paillier.nsquare);
        BigInteger sum_m1m2 = m9.add(m8.add(m7.add(m6.add(m5.add(m4.add(m3.add(m2.add(m1)))))))).mod(paillier.n);
        System.out.println("original sum: " + sum_m1m2.toString());
        System.out.println("decrypted sum: " + paillier.Decryption(product_em1em2).toString());

        /* test homomorphic properties -> D(E(m1)^m2 mod n^2) = (m1*m2) mod n */
        /*
        BigInteger expo_em1m2 = em1.modPow(m2, paillier.nsquare);
        BigInteger prod_m1m2 = m1.multiply(m2).mod(paillier.n);
        System.out.println("original product: " + prod_m1m2.toString());
        System.out.println("decrypted product: " + paillier.Decryption(expo_em1m2).toString());
        */
    }
}