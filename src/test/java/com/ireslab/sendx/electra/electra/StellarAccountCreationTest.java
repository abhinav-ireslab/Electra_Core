package com.ireslab.sendx.electra.electra;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

public class StellarAccountCreationTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		/* for(int i = 0; i<4; i++){ */

		System.out.println("\n\n");
		// create a completely new and unique pair of keys.
		// see more about KeyPair objects:
		// https://stellar.github.io/java-stellar-sdk/org/stellar/sdk/KeyPair.html

		KeyPair pair = KeyPair.random();

		System.out.println("Secret Seed - " + new String(pair.getSecretSeed()));
		System.out.println("Public Key - " + pair.getAccountId());

		String friendbotUrl = String.format("https://horizon-testnet.stellar.org/friendbot?addr=%s",
				pair.getAccountId());

		URLConnection urlConnection = new URL(friendbotUrl).openConnection();
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");

		InputStream response = urlConnection.getInputStream();// new URL(friendbotUrl).openStream();
		String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
		System.out.println("SUCCESS! You have a new account :)\n" + body);

		Server server = new Server("https://horizon-testnet.stellar.org");
		AccountResponse account = server.accounts().account(pair);
		System.out.println("Balances for account " + pair.getAccountId());
		for (AccountResponse.Balance balance : account.getBalances()) {
			System.out.println(String.format("Type: %s, Code: %s, Balance: %s", balance.getAssetType(),
					balance.getAssetCode(), balance.getBalance()));
		}
	}
}
