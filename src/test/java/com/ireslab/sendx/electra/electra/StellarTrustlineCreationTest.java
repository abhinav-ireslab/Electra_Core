package com.ireslab.sendx.electra.electra;

import java.io.ByteArrayOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.stellar.sdk.Asset;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse.Extras.ResultCodes;
import org.stellar.sdk.xdr.XdrDataOutputStream;

/**
 * @author Nitin
 *
 */
public class StellarTrustlineCreationTest {

	// private static final String USER_ACCOUNT_SECRET_SEED = "";

	public static void main(String[] args) {

		Server horizonServer = null;
		String xdrTrustlineTransaction = null;

		if (StellarConstantsTest.IS_TESTNET_NETWORK) {
			Network.useTestNetwork();
			horizonServer = new Server(StellarConstantsTest.HORIZON_TESTNET_URL);

		} else {
			Network.usePublicNetwork();
			horizonServer = new Server(StellarConstantsTest.HORIZON_LIVENET_URL);
		}

		// SendX Asset Token
		Asset tokenAsset = Asset.createNonNativeAsset(StellarConstantsTest.ASSET_CODE,
				KeyPair.fromSecretSeed(StellarConstantsTest.ISSUING_ACCOUNT_SECRET_KEY));

		KeyPair userAccountKeyPair = KeyPair.fromSecretSeed(StellarConstantsTest.BASE_ACCOUNT_SECRET_KEY);

		try {
			System.out.println("Creating and submitting trustline transaction to Stellar Network for Asset Token : "
					+ StellarConstantsTest.ASSET_CODE + " with Limit : "
					+ StellarConstantsTest.BASE_ACCOUNT_TRUSTLINE_LIMIT);

			Transaction userAccTrustLineTxn = new Transaction.Builder(
					horizonServer.accounts().account(userAccountKeyPair)).addOperation(

							new ChangeTrustOperation.Builder(tokenAsset,
									StellarConstantsTest.BASE_ACCOUNT_TRUSTLINE_LIMIT).build())
							.build();
			userAccTrustLineTxn.sign(userAccountKeyPair);

			SubmitTransactionResponse baseTrustLineTxnResponse = horizonServer.submitTransaction(userAccTrustLineTxn);
			System.out.println("Sequence Number - " + userAccTrustLineTxn.getSequenceNumber() + ",\nFee - "
					+ userAccTrustLineTxn.getFee() + ",\nisSuccess - " + baseTrustLineTxnResponse.isSuccess()
					+ ",\nResult XDR - " + baseTrustLineTxnResponse.getResultXdr() + ",\nLedger - "
					+ baseTrustLineTxnResponse.getLedger() + ",\nEnvelope XDR - "
					+ baseTrustLineTxnResponse.getEnvelopeXdr());

			// Transaction not success
			if (!baseTrustLineTxnResponse.isSuccess()) {

				ResultCodes resultCodes = baseTrustLineTxnResponse.getExtras().getResultCodes();
				System.err.println("Transaction Result Code - " + resultCodes.getTransactionResultCode()
						+ ",\nTransaction Operation Result Code - " + resultCodes.getOperationsResultCodes().get(0));
				throw new Exception();

			} else {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				org.stellar.sdk.xdr.Transaction.encode(new XdrDataOutputStream(byteArrayOutputStream),
						userAccTrustLineTxn.toXdr());
				xdrTrustlineTransaction = new Base64().encodeAsString(byteArrayOutputStream.toByteArray());

				System.out.println("Account trustline transaction XDR - " + xdrTrustlineTransaction);
			}

		} catch (Exception exp) {
			System.err.println("Error occurred while creating trustline for user account having Account Id : "
					+ userAccountKeyPair.getAccountId() + "\n" + ExceptionUtils.getStackTrace(exp));
		}

	}

}
