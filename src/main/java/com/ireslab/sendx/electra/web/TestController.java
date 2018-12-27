package com.ireslab.sendx.electra.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.stellar.sdk.AccountMergeOperation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.dto.StellarTransactionConfigDto;
import com.ireslab.sendx.electra.dto.TransactionDto;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.ClientAgentInvitation;
import com.ireslab.sendx.electra.entity.ClientUser;
import com.ireslab.sendx.electra.entity.Country;
import com.ireslab.sendx.electra.entity.TransactionDetail;
import com.ireslab.sendx.electra.entity.TransactionLimit;
import com.ireslab.sendx.electra.model.ApprovelResponse;
import com.ireslab.sendx.electra.model.AssetDetails;
import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserProfileResponse;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.ClientUserRepository;
import com.ireslab.sendx.electra.repository.CountryRepository;
import com.ireslab.sendx.electra.repository.TransactionDetailRepository;
import com.ireslab.sendx.electra.repository.TransactionLimitRepository;
import com.ireslab.sendx.electra.service.ClientAgentInvitationService;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ProfileImageService;
import com.ireslab.sendx.electra.service.TransactionManagementService;
import com.ireslab.sendx.electra.stellar.StellarTransactionManager;
import com.ireslab.sendx.electra.utils.CommonUtils;

@RestController
@RequestMapping(value = "/test/**")
public class TestController {
	private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
	@Autowired
	private ProfileImageService profileImageService;
	
	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private ClientAgentInvitationService clientAgentInvitationService;

	@Autowired
	private TransactionDetailRepository transactionDetailRepository;

	@Autowired
	private TransactionLimitRepository transactionLimitRepository;
	
	
	
	@Autowired
	private ClientRepository clientRepo;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private StellarTransactionManager stellarTransactionManager;
	
	@Autowired
	private ClientUserRepository clientUserRepo;
	
	@Autowired
	private TransactionDetailRepository txnDetailRepo;
	
	@Autowired
	private com.ireslab.sendx.electra.stellar.AccountMergeOperation accountMergeOperation;

	@RequestMapping(value = "saveImage", method = RequestMethod.GET)
	public void testSaveImage(HttpServletRequest request) {

		String imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAANYAAADrCAMAAAAi2ZhvAAAAdVBMVEX///8AAAAvLy/6+vrt7e2GhoZ7e3taWlra2tq0tLTKysrR0dH39/fw8PCNjY2qqqoQEBDn5+ebm5u/v7/e3t5wcHClpaW4uLhpaWlCQkJNTU3CwsJbW1tISEidnZ1hYWF2dnYmJiYcHBwpKSkLCwsXFxc6Ojq9jO5dAAAE0UlEQVR4nO3c6XLqMAwFYLZAFhIghLKVspTy/o94oVymLT0OMhAkOueb6fQPycgksWVboVYjIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIqIn1UzTtHWw/9/VDuYe0jjKlotN/cu2Me2Mgl7ytM1Lx6NG3W2RJdoR+ivyRUmTTiZz7Th9NHtTQZuO2i3taIUGK3Gbjpes0I5YYDDxa9TByHz/kfk36uBNO+5SyeZyC7Cp4Ufs7dpGHQy1o3dpl4bd/1TygVw7fszVAa5HUdzqdptH3STbOT640m4BEuBY2+Hvjw4c13Xy+Kgv6cFAR46eIMVfwvKxMV82QFFuY/cB4Rp+DY+LWARlS5u09JAZalfwoHhlxijES0MRzEeih8QrhGYgJXfgf0vULkMZYgLCmwmOg5OXyqMVQ0OWZCrV/QAHtisPV2oNomtKDgzR5bIya26B2DqyQ3Nw6Hu10YoNb7iV0MjwUmm0YhEITZq4ot6mbmNaiSYk4m8cjV42knmU4ImbVaDLJepvqtZeN85teuKjO3afrlvEoFk77aDuAN2FYJb2bNBqlZ1U42oo1fjQDuoO0F1oJYO6AVrasDWfvApKvtbaQd0Opcr1gXZUt0PNMjX7vw7KC1faQd0Ozbr+wMOF8qd6+XLcM4BZvKElqCs1UbP+QFqImnV5nVFXK0yS+TjaGw/jJEmKwa9JPVrRMLuP1wyjrOPYqetPJ0HeS4pjxzACnxgrR4+FOdw7+G03yuBHDY7HcRut1/qx1qxBULZF/KTNitGSy7M3a+7a737mZhXCXuK5mnVlZZCDfKWxUsX7XVtlZAkUVzCcNBrTzmuns/9b7NayjtJEs5yVQbNeiDZAuoMwmefBauGs/rLQLLi7vZcLKs+aRYwutYFmOcaqm7ZN9LsMXPE5ke++oYVd9Wbh58pn3EGzfu1xC/eBXqvNaJtWuVmwistzDR1t0ypPI2HC5DkHRPmJ7qQfLaB7b0+hMhzdJRr4goLvSVDxk+qCGizv8e6bt+AkqiXksHP3Pgs4x7aCYOXQ95z5ngQlGcKiqWrAe9D7YUejsWpKCF9U8D4L2jFR7QjRoyWp+/wJ5ZSq9UGvICD/3WxwEt1SfzTgeNeXofRLd6n6LlWOKNHV3bRDV8t7MAYbC6rdO85zvZsFzmEwffd9IxCNWsp1kmgRwzfJAPegdmkQGnE8++YuOIV2CQ0q0+37nQKkGOrvO8Hkyeu7Rrv86rULMNX16jPAF6N+sWo1tJnqM1VKwfEG3kGGmz8ea2EgqfSerlUA1vXIhx2wxripMlwxuPwufSUVPZo2qmfguy/CGwml7lZKxfEekGR2gm5gM+8rwEpbybduvIoQvp1brzcurEagBQz9gfgb1w7/pCTdKGABh43u4sT56zMzx7wpRCVp1lrl/PmFz0sWnd1Y3TB3/P6TtVZdLF9YZvlLNBy/5NnSWb3Rt/RcnSS3Vtot7PSBP3j+vNMZC4kglsh/i+vc2vR7TcOy34IrYaCypNz8iisW2Hh7ulxY0tnDRhntKn6Ly3/x6Zvp2MSL02JJ7iru+jLrGZjd+yvm2cQxmm0muem+77JWMo/esqB9EBxyjTjUXtwkIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIioof4BwsQMUYcrAhuAAAAAElFTkSuQmCC";

		System.out.println("TestController.testSaveImage()");
		profileImageService.saveImage("profile", "9711355293", imageBase64);

	}

	@RequestMapping(value = "saveInvitation", method = RequestMethod.GET)
	public void testSaveInvitation(HttpServletRequest request) {

		/*
		 * ClientAgentInvitation clientAgentInvitation = new ClientAgentInvitation();
		 * clientAgentInvitation.setClientId(1);
		 * clientAgentInvitation.setEmailAddress("mahto.mailbox@yopmail.com");
		 * clientAgentInvitation.setInvitationDate(new Date());
		 * clientAgentInvitation.setRegister(false);
		 * clientAgentInvitationService.saveInvitationDetail(clientAgentInvitation);
		 * System.out.println("saving iDetails..!!");
		 */
		List<String> list = new ArrayList<>();
		list.add("mahto.mailbox@gmail.com");
		list.add("mahto.mailbox@yopmail.com");
		for (ClientAgentInvitation iterable_element : clientAgentInvitationService.findClientAgentInvitationList(1,
				list)) {
			System.out.println(iterable_element.getEmailAddress());
		}

	}

	@RequestMapping(value = "transactionLimit", method = RequestMethod.GET)
	public void transactionLimit(HttpServletRequest request) {

		System.out.println("transactionLimit--  d3c4b2dd-b568-4f95-804f-c7d58c375e78");

		// SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-M-dd
		// hh:mm:ss");
		// SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-M-dd");
		// System.out.println(simpleDateFormat.format(element.getTransactionDate())+"
		// "+simpleDateFormat.format(new Date()));
		// System.out.println(simpleDateFormat.format(element.getTransactionDate()).equals(simpleDateFormat.format(new
		// Date())));

		System.out.println(validateUserTopUp("d3c4b2dd-b568-4f95-804f-c7d58c375e78"));

	}

	private boolean validateUserTopUp(String correlationId) {

		Double dailyLimit = 0.0; // 1000
		Double monthlyLimit = 0.0; // 4000
		Integer noOfTran = 0; // 4
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd");
		
		List<TransactionDetail> transactionDetailList = transactionDetailRepository.findAllTransactionOfMonth(correlationId, false);
		System.out.println("transaction Detail List size - "+transactionDetailList.size());
		
		for (TransactionDetail transactionDetail : transactionDetailList) {
			
			/*try {
				LOG.debug("\n Transaction Details Data "+objectWriter.writeValueAsString(element));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			if (simpleDateFormat.format(transactionDetail.getTransactionDate()).equals(simpleDateFormat.format(new Date()))) {
				
				try {
					LOG.debug("\n Transaction Details Data "+objectWriter.writeValueAsString(transactionDetail));
					LOG.debug("\n Transaction Date "+simpleDateFormat.format(transactionDetail.getTransactionDate()));
					LOG.debug("\n Current Date "+simpleDateFormat.format(new Date()));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				dailyLimit = dailyLimit + Double.parseDouble(transactionDetail.getTnxData());
				noOfTran++;
			}
			monthlyLimit = monthlyLimit + Double.parseDouble(transactionDetail.getTnxData());

		}
		System.out.println("dailyLimit left used :" + dailyLimit);
		System.out.println("noOfTran used :" + noOfTran);
		System.out.println("monthlyLimit used :" + monthlyLimit);
		List<TransactionLimit> findAll = transactionLimitRepository.findAll();
		TransactionLimit transactionLimit = findAll.get(0);
		if ((noOfTran >= Integer.parseInt(transactionLimit.getTransactionsPerDay()))
				|| (monthlyLimit >= Double.parseDouble(transactionLimit.getMonthlyLimit()))
				|| (dailyLimit >= Double.parseDouble(transactionLimit.getDailyLimit()))) {
			
			return true;

		}
		return false;

	}
	
	@RequestMapping(value = "transactionsDate", method = RequestMethod.GET)
	private void transactionsDate() {
		
		
		Client client = clientRepo.findByClientCorrelationId("96f70532-8650-42ca-883d-9b16ff941dc8");
		
		//countryRepository
    Country country = countryRepository.findCountryByCountryDialCode(client.getCountryDialCode());

   List<ClientUser> findByClient = clientUserRepo.findByClient(client);
   System.out.println(findByClient.size());

   /*List<String> listString = new ArrayList<>();

   for (ClientUser clientUser : findByClient) {
	listString.add(clientUser.getUserCorrelationId());
   }
   listString.add("96b89439-f997-4518-bf70-bed8bc1357c8");*/
   boolean offlineLedger = false;
   boolean allLedger = true;
   List<TransactionDetail> findBySourceCorrelationIdOrDestinationCorrelationId = null;
  if(offlineLedger && !allLedger) {
	  findBySourceCorrelationIdOrDestinationCorrelationId=  txnDetailRepo
		.findBySourceCorrelationIdOrDestinationCorrelationIdAndIsOffline("96f70532-8650-42ca-883d-9b16ff941dc8","96f70532-8650-42ca-883d-9b16ff941dc8",offlineLedger);
 }else if(!offlineLedger && !allLedger) {
	 findBySourceCorrelationIdOrDestinationCorrelationId= txnDetailRepo
		.findBySourceCorrelationIdOrDestinationCorrelationIdAndIsOffline("96f70532-8650-42ca-883d-9b16ff941dc8","96f70532-8650-42ca-883d-9b16ff941dc8",offlineLedger);
 }
 else {
	 findBySourceCorrelationIdOrDestinationCorrelationId=txnDetailRepo.findBySourceCorrelationIdOrDestinationCorrelationId("96f70532-8650-42ca-883d-9b16ff941dc8","96f70532-8650-42ca-883d-9b16ff941dc8");
 }
    

	List<TransactionDto> transactionDtoList = getTransactionDtoList(
			findBySourceCorrelationIdOrDestinationCorrelationId, country.getCountryTimeZone());
	Date date = new Date();
	/*System.out.println("Current date and time :- "+date);
	
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	
	Date date1 = new Date();
	
	System.out.println("Current UTC date and time :- "+date1);*/

   for(TransactionDetail transactionDto:findBySourceCorrelationIdOrDestinationCorrelationId) {
	System.out.println("Source Name : "+transactionDto.getSourceAccountName()+"  Transaction Data : "+transactionDto.getTnxData()+"  Offline  : "+transactionDto.getIsOffline());
   }
		

	}
	
	
	
	
	private List<TransactionDto> getTransactionDtoList(
			List<TransactionDetail> transactionDetails, String countryTimeZone) {
		List<TransactionDto> transactionDtos = new ArrayList<>();

		for (TransactionDetail transactionDetailsObj : transactionDetails) {
			TransactionDto transactionDto = new TransactionDto();
			transactionDto.setType(transactionDetailsObj.getType());
			transactionDto.setOperation(transactionDetailsObj.getOperation());
			transactionDto.setTransactionDate(CommonUtils.formatDateWithTimezone(transactionDetailsObj.getTransactionDate(), countryTimeZone));
			transactionDto.setTransactionSequenceNo(transactionDetailsObj.getTransactionSequenceNo());
			transactionDto.setSourceAccountName(transactionDetailsObj.getSourceAccountName());
			transactionDto.setDestinationAccountName(transactionDetailsObj.getDestinationAccountName());
			transactionDto.setTnxData(transactionDetailsObj.getTnxData());
			transactionDto.setTnxHash(transactionDetailsObj.getTnxHash());
			transactionDtos.add(transactionDto);
		}

		return transactionDtos;
	}

	@RequestMapping(value = "testPage", method = RequestMethod.GET)
	public Page<String> testPage(Pageable pageable) {

		List<String> list =new ArrayList<>();
		list.add("A");
		list.add("B");
		list.add("c");
		list.add("c");
		list.add("c");
		list.add("c");
		list.add("c");
		final Page<String> page = new PageImpl<>(list,pageable,list.size());
		return page;
	}
	
	
	@RequestMapping(value = "testExchange", method = RequestMethod.GET)
	public void testExchange() {

	//	System.out.println("testExchange :"+CommonUtils.tokenConversion("MYR", "INR", "30"));
		
		try {
			accountMergeOperation.mergeAccount(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "testBal", method = RequestMethod.GET)
	public UserProfileResponse testBal() {
		UserProfileResponse userProfileResponse =new UserProfileResponse();
	
		StellarTransactionConfigDto stellarConfig = new StellarTransactionConfigDto();
		stellarConfig.setIsTestNetwork(true);
		stellarConfig.setReceiverAccount(
				new StellarTransactionConfigDto.ReceiverAccount().setPublicKey("GBGT2NMIWKE2SJBX2XKJWLZLXZOFMM7AMWB4Z236PMJO2J7PJLKLW2EJ"));
		UserProfile userProfile = new UserProfile();
		
		// Getting consolidated account balance from stellar
		List<AssetDetails> accountBalances = stellarTransactionManager.getAccountBalance(stellarConfig);
		/*userProfile.setAssetDetails(accountBalances);
		userProfileResponse.setUserProfile(userProfile);
		System.out.println("size :"+accountBalances.size());
		System.out.println("balance "+userProfileResponse.getUserProfile().getAssetDetails().);*/
		
		for (AssetDetails assetDetails : accountBalances) {
			System.out.println("AssetCode :"+assetDetails.getAssetCode());
			System.out.println("AssetQuantity :"+assetDetails.getAssetQuantity());
		}
		
		return userProfileResponse;
	}
	
	public static void main(String args[]) {

		String mNo ="+919185312487";
		String dialCode = "+91";
		
		System.out.println(mNo.replace(dialCode, ""));
	
	}

}
