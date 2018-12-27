package com.ireslab.sendx.electra.service;

import com.ireslab.sendx.electra.model.ApprovelRequest;
import com.ireslab.sendx.electra.model.ApprovelResponse;
import com.ireslab.sendx.electra.model.TokenTransferRequest;
import com.ireslab.sendx.electra.model.TokenTransferResponse;
import com.ireslab.sendx.electra.model.TransactionHistoryResponse;
import com.ireslab.sendx.electra.model.TransactionLimitRequest;
import com.ireslab.sendx.electra.model.TransactionLimitResponse;
import com.ireslab.sendx.electra.model.TransactionPurposeRequest;
import com.ireslab.sendx.electra.model.TransactionPurposeResponse;

/**
 * @author iRESlab
 *
 */

public interface TransactionManagementService {

	/**
	 * @param clientCorrelationId
	 * @param userCorrelationId
	 * @return
	 */
	public TransactionHistoryResponse cashOutTokensHistory(String clientCorrelationId, String userCorrelationId);

	public TransactionLimitResponse getTransactionLimitData();

	public ApprovelResponse ekycEkybApprovelList();

	public ApprovelResponse approveEkycEkyb(ApprovelRequest approvelRequest);

	public TransactionLimitResponse updateTransactionLimit(TransactionLimitRequest transactionLimitRequest);

	public ApprovelResponse ekycEkybApprovedList();

	public TransactionPurposeResponse getAllTransactionPurpose(String clientCorrelationId);

	public TokenTransferResponse transactionLimitsForAllowTransfer(TokenTransferRequest tokenTransferRequest);

	public TransactionPurposeResponse addAndUpdateTransactionPurpose(
			TransactionPurposeRequest transactionPurposeRequest);

	public TransactionPurposeResponse deleteTransactionPurpose(TransactionPurposeRequest transactionPurposeRequest);
}
