package com.ireslab.sendx.electra.electra;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Nitin
 *
 */
public class StellarConstantsTest {

	public static final String ASSET_CODE = "TMW";

	public static final String HORIZON_TESTNET_URL = "https://horizon-testnet.stellar.org";
	public static final String HORIZON_LIVENET_URL = "https://horizon.stellar.org";

	public static final String ISSUING_ACCOUNT_SECRET_KEY = "SC5RMDT3VA5A4KVEOVB5LCGQBPKQ5TWMSQ65DOWYBJNZDBWWKWSB732W";
	public static final String ISSUING_ACCOUNT_PUBLIC_KEY = "GBCDYKPXJTL5T2G2VXJCRN7ZJVO6FEMOVFQRR72IBXFJRW6XYIKMRMYF";

	public static final String BASE_ACCOUNT_SECRET_KEY = "SDDV7YK2UPGTWS5BF3UOW25JX4GNPO5EISSARUCP5GH3EMBHFXZ6TLFJ";
	public static final String BASE_ACCOUNT_PUBLIC_KEY = "GCDJFUJUPRWAH4AHZ376GKAJMD7Y5MUWY6MPCNUU3XTXOPV45TBN75DT";

	public static final String BASE_ACCOUNT_TRUSTLINE_LIMIT = "50000";
	public static final String USER_ACCOUNT_TRUSTLINE_LIMIT = "1000";

	public static final boolean IS_TESTNET_NETWORK = true;

	
	public static void main(String[] args) {
		
		System.out.println(new BCryptPasswordEncoder().encode("Admin@123"));
	}
}
