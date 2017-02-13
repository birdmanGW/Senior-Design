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

    public BigInteger returnValues(BigInteger cipherTextResult) {
    	return cipherTextResult;
    }


	//[cipherTextList, n, cipherThreshold]
	public static void main(String[] str) {

		Paillier_eVerify paillier = new Paillier_eVerify(str);

		String[] ciphertextList = new String[] {str[0]};

		BigInteger cipherThreshold = new BigInteger(str[2]);

		BigInteger ciphertextSum = paillier.ciphertextSummation(ciphertextList);

		BigInteger cipherTextResult = (ciphertextSum.multiply(cipherThreshold.modInverse(nsquare))).mod(nsquare);

		paillier.returnValues(cipherTextResult);

	}

}