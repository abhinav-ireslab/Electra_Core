package com.ireslab.sendx.electra.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ireslab.sendx.electra.entity.TransactionDetail;

public interface TransactionDetailRepository extends PagingAndSortingRepository<TransactionDetail, Integer> {

	/*
	 * @Query("SELECT t from TransactionDetail t where (t.senderCorelationId = :corelationId1 AND t.receiverCorelationId = :corelationId2) OR (t.senderCorelationId = :corelationId2 AND t.receiverCorelationId = :corelationId1)"
	 * ) List<TransactionDetail>
	 * findBySenderCorelationIdAndReceiverCorelationIdOrVisaVsersa(@Param(
	 * "corelationId1") String corelationId1, @Param("corelationId2") String
	 * corelationId2);
	 */

	// By name only
	
	Page<TransactionDetail> findBySourceAccountNameContainingOrDestinationAccountNameContaining(String name1,
			String name2, Pageable pageable);
	
	// By name only
	@Query("select d from TransactionDetail d where (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> findBySourceAccountNameContainingOrDestinationAccountNameContainingCust(@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName,@Param("list") List<String> list, Pageable pageable);

	/*
	 * @Query("SELECT t FROM TransactionDetail t WHERE t.sourceAccountName LIKE %:searchTerm% OR t.destinationAccountName LIKE %:searchTerm%"
	 * ) public Page<TransactionDetail> searchWithJPQLQuery(@Param("searchTerm")
	 * String searchTerm,Pageable pageable);
	 */

	//
	/*Page<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId(String corelationId1,
			String corelationId2, Pageable pageable);*/
	
	
	List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId(String corelationId1,
			String corelationId2);
	
	@Query("select d from TransactionDetail d where (d.sourceCorrelationId =:corelationId1 OR d.destinationCorrelationId =:corelationId2) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationIdAndIsOffline(@Param("corelationId1") String corelationId1,
			@Param("corelationId2") String corelationId2, @Param("offline") boolean offline);

	/*
	 * @Modifying
	 * 
	 * @Transactional
	 * 
	 * @Query("select d from TransactionDetail d where d.transactionDate >= :from and d.transactionDate <= :to "
	 * ) // @Query("SELECT c FROM TransactionDetail c WHERE c.transactionDate
	 * BETWEEN // CAST(':from' AS DATE) AND ':to AS DATE'") List<TransactionDetail>
	 * findByTransactionDateBetween(@Param("from") Date from, @Param("to") Date to);
	 */
	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	// @Query("select d from TransactionDetail d where (d.transactionDate BETWEEN
	// :from AND :to) AND (d.sourceCorrelationId=:sourceCorrelationId OR
	// d.destinationCorrelationId=:destinationCorrelationId) ORDER BY
	// d.transactionDate DESC")
	// @Query("select d from TransactionDetail d where (d.transactionDate BETWEEN
	// :from AND :to)AND (d.sourceCorrelationId :sourceCorrelationId OR
	// d.destinationCorrelationId :destinationCorrelationId)")
	Page<TransactionDetail> findByTransactionDateCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("list") List<String> list, Pageable pageable);

	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> findByTransactionDateAndNameCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName, @Param("list") List<String> list,
			Pageable pageable);

	@Query("select d from TransactionDetail d where d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> customMethod(@Param("list") List<String> list, Pageable pageable);
	
	@Query("select d from TransactionDetail d where (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	List<TransactionDetail> customTransactionDetail(@Param("list") List<String> list, @Param("offline") boolean offline);
	
	@Query("select d from TransactionDetail d where (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	List<TransactionDetail> findByAccountNameContainingCustom(@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName,@Param("list") List<String> list, @Param("offline") boolean offline);

	
	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	List<TransactionDetail> findTransactionByTransactionDateAndNameCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName, @Param("list") List<String> list, @Param("offline") boolean offline);
	
	
	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	List<TransactionDetail> findTransactionByTransactionDateCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("list") List<String> list, @Param("offline") boolean offline);
	
	
	/*
	 * List<TransactionDetail>
	 * findByTransactionDateGreaterThanEqualAndTransactionDateLessThanEqual(Dateis
	 * fromDate, Date toDate);
	 */
	
	@Query(nativeQuery=true,value="select * from transaction_details t where t.is_fee=:isfee and t.source_correlation_id=:sourceCorrelationId and MONTH(transaction_date) = MONTH(CURDATE())")
	List<TransactionDetail> findAllTransactionOfMonth(@Param("sourceCorrelationId") String sourceCorrelationId,@Param("isfee") boolean isfee);
	
	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.isOffline=:offline) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> findByDateOnlineOfflineCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("list") List<String> list, @Param("offline") boolean offline, Pageable pageable);
	
	@Query("select d from TransactionDetail d where (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.isOffline=:offline) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> findByNameOnlineOfflineCust(@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName,@Param("list") List<String> list, @Param("offline") boolean offline, Pageable pageable);
	
	@Query("select d from TransactionDetail d where (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) AND (d.isOffline=:offline) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> customOnlineOfflineMethod(@Param("list") List<String> list, @Param("offline") boolean offline, Pageable pageable);
	
	@Query("select d from TransactionDetail d where (d.transactionDate BETWEEN :from AND :to) AND (d.isOffline=:offline) AND (d.sourceAccountName like %:sourceAccountName% OR d.destinationAccountName like %:destinationAccountName%) AND (d.sourceCorrelationId IN :list OR d.destinationCorrelationId IN :list) ORDER BY d.transactionDate DESC")
	Page<TransactionDetail> findByDateAndNameOnlineOfflineCustom(@Param("from") Date from, @Param("to") Date to,
			@Param("sourceAccountName") String sourceAccountName,
			@Param("destinationAccountName") String destinationAccountName, @Param("list") List<String> list,
			 @Param("offline") boolean offline,Pageable pageable);
}
