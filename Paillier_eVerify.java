import java.math.*;
import java.util.*;
import java.io.*;


/*
 * Sum encryped accounts balances
 * Subtract from cipherTextSum cipherThreshold
 * Apply mask (work in progress)
 *
 * Returns cipherTextSumMask
 */
public class Paillier_eVerify {

	private static BigInteger n, nsquare;


	public Paillier_eVerify(String[] str) {

		n = new BigInteger(str[1]);
		nsquare = n.multiply(n);
	}


	/*
	 * Sums an EVEN number of ciphertexts (Checking + Savings)
	 */
    public BigInteger ciphertextSummation(String[] ciphertextList) {

        BigInteger ciphertext1;
        BigInteger ciphertext2;
        BigInteger ciphertextSum = new BigInteger("1");

        for (int n = 0; n < ciphertextList.length; n = n + 2) 
        {

            ciphertext1 = new BigInteger(ciphertextList[n]);
            ciphertext2 = new BigInteger(ciphertextList[n + 1]);

            ciphertextSum = ciphertextSum.multiply(ciphertext1).multiply(ciphertext2);

        }

        ciphertextSum = ciphertextSum.mod(nsquare);

        return ciphertextSum;
    } 

    public void returnValues(BigInteger cipherTextResult) {
    	//return cipherTextResult;
    	System.out.println(cipherTextResult);
    }


	//[cipherTextList, n, cipherThreshold]
	public static void main(String[] str) {

		Paillier_eVerify paillier = new Paillier_eVerify(str);

		String[] ciphertextList = str[0].split("\\,");
		
		BigInteger cipherThreshold = new BigInteger(str[2]);

		BigInteger ciphertextSum = paillier.ciphertextSummation(ciphertextList);

		BigInteger cipherTextResult = (ciphertextSum.multiply(cipherThreshold.modInverse(nsquare))).mod(nsquare);

		paillier.returnValues(cipherTextResult);

		/*
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
		*/
	}

}