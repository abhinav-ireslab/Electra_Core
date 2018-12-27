



#DELETE_ALL_ACCOUNTS_ELECTRA
#===========================
USE `electra_schema`;
DELETE FROM `client_user_token`;
DELETE FROM `client_users`;
DELETE FROM `oauth_access_token`;
DELETE FROM `oauth_refresh_token`;
DELETE FROM `transaction_details`;



#DELETE_ALL_ACCOUNTS_SGT
#=======================;
USE sendx_sgt_electra_integration_schema;
DELETE FROM `transaction_details`;
DELETE FROM `topup_transactions`;
DELETE FROM `stellar_account`;
DELETE FROM `scheduled_transactions`;
DELETE FROM `oauth_refresh_token`;
DELETE FROM `oauth_access_token`;
DELETE FROM `account_verification`;
DELETE FROM `profile`;
DELETE FROM `account`;



#UPDATE-SGT-ACCOUNT-VERIFICATION
#===============================
USE sendx_sgt_electra_integration_schema;
UPDATE account_verification SET modified_date=CURRENT_TIMESTAMP