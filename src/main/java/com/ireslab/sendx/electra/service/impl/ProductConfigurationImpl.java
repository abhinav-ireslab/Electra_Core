package com.ireslab.sendx.electra.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ireslab.sendx.electra.dto.GSTChapterDto;
import com.ireslab.sendx.electra.dto.GstHsnCodeDto;
import com.ireslab.sendx.electra.dto.GstHsnResponseDto;
import com.ireslab.sendx.electra.dto.GstSacCodeDto;
import com.ireslab.sendx.electra.dto.GstSacResponseDto;
import com.ireslab.sendx.electra.dto.MerchantDto;
import com.ireslab.sendx.electra.dto.PaymentTermDto;
import com.ireslab.sendx.electra.dto.ProductDto;
import com.ireslab.sendx.electra.entity.Client;
import com.ireslab.sendx.electra.entity.GSTChapter;
import com.ireslab.sendx.electra.entity.GstHsnCode;
import com.ireslab.sendx.electra.entity.GstSacCode;
import com.ireslab.sendx.electra.entity.PaymentTerm;
import com.ireslab.sendx.electra.model.GstHsnCodeModel;
import com.ireslab.sendx.electra.entity.Product;
import com.ireslab.sendx.electra.exceptions.ApiException;
import com.ireslab.sendx.electra.model.GstHsnSacLoadRequest;

import com.ireslab.sendx.electra.model.ProductConfigurationResponse;
import com.ireslab.sendx.electra.model.ProductRequest;
import com.ireslab.sendx.electra.model.ProductResponse;
import com.ireslab.sendx.electra.model.SaveProductRequest;
import com.ireslab.sendx.electra.model.SaveProductResponse;
import com.ireslab.sendx.electra.properties.MessagesProperties;
import com.ireslab.sendx.electra.repository.ClientRepository;
import com.ireslab.sendx.electra.repository.GSTChapterRepository;
import com.ireslab.sendx.electra.repository.GstHsnCodeRepository;
import com.ireslab.sendx.electra.repository.GstSacCodeRepository;
import com.ireslab.sendx.electra.repository.PaymentTermRepository;
import com.ireslab.sendx.electra.repository.ProductRepository;
import com.ireslab.sendx.electra.service.ProductConfiguration;
import com.ireslab.sendx.electra.utils.AppConstants;
@Primary
@Component
@PropertySource(value = "classpath:electra_config.properties")
@ConfigurationProperties
public class ProductConfigurationImpl extends CommonServiceImpl implements ProductConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ProductConfigurationImpl.class);
	
	private String listEmpty;
	private String resultNotFound;
	private String success;
	
	@Autowired
	private MessagesProperties messagesProperties;


	@Autowired
	private PaymentTermRepository paymentTermRepository;
	
	@Autowired
	private GSTChapterRepository gSTChapterRepository;
	
	@Autowired
	private GstHsnCodeRepository gstHsnCodeRepository;
	
	@Autowired
	private GstSacCodeRepository gstSacCodeRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ClientRepository clientRepo;

	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.impl.CommonServiceImpl#saveProduct(com.ireslab.sendx.electra.model.SaveProductRequest)
	 */
	@Override
	public SaveProductResponse saveProduct(SaveProductRequest productRequest) {
		SaveProductResponse productResponse = new SaveProductResponse();

		// Check Product Code
		String productCode = productRequest.getProductCode();
		
		if(productCode == null || StringUtils.isEmpty(productCode)) {
			
			
			String STRINGCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			StringBuilder salt = new StringBuilder();
			Random rnd = new Random();
			while (salt.length() < 5) { // length of the random string.
				int index = (int) (rnd.nextFloat() * STRINGCHARS.length());
				salt.append(STRINGCHARS.charAt(index));
			}
			 productCode = salt.toString();
			
		}
		
		Product checkProductCode = productRepo.findByProductCode(productCode);
		String response = null;
		if (checkProductCode != null) {
			response = checkProductCode.getProductCode();
		}

		if (response != null && response.length() > 0) {
			productResponse.setMessage("Product code already exists. Please enter a unique product code.");
			productResponse.setSuccess("failure");
			productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
		} else {
			Product product = new Product();
			Client client = getClientDetailsByCorrelationId(productRequest.getClientCorrelationId());
			product.setClientId(client.getClientId());
			product.setProductCode(productCode);
			
			//------------ Product Configuration ---
			  product.setItemCode(productRequest.getItemCode());
			  product.setInvoiceType(productRequest.getInvoiceType());	
			  product.setGstInclusive(productRequest.isGstInclusive());	
			  product.setItemNameOrDesc(productRequest.getItemNameOrDesc());	
			  product.setItemTypeOrChapter(productRequest.getItemTypeOrChapter());	
			  product.setInvoiceType(productRequest.getInvoiceType());	
			  product.setDiscountPercentage(productRequest.getDiscount());
			  product.setItemPrice(productRequest.getItemPrice());
			  product.setAvailableQuantity(productRequest.getAvailableQuantity());
			  product.setSubTotal(productRequest.getTotal().getSubTotal());
			  product.setDiscount(productRequest.getTotal().getDiscount());
			  product.setTotalTaxInclusive(productRequest.getTotal().getTotalTaxInclusive());
			  product.setTotal(productRequest.getTotal().getTotal());
			  product.setCgst(productRequest.getGst().getCgst());
			  product.setSgst_utgst(productRequest.getGst().getSgst_utgst());
			  product.setIgst(productRequest.getGst().getIgst());
			  product.setCustomerNotes(productRequest.getCustomerNotes());
			  product.setTermsAndConditions(productRequest.getTermsAndConditions());
			  product.setPaymentTerms(productRequest.getPaymentTerms());
			
					product.setCreatedDate(new Date());
					
					product.setManufacturingDate(new Date());
					product.setExpiryDate(new Date());
		
					java.util.Date utilDate = new java.util.Date();
					java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
					product.setModifiedDate(timestamp);
					
					product.setProductGroup(productRequest.getItemTypeOrChapter());
			
					if(productRequest.isInterState()) {
						product.setInterState(productRequest.isInterState());
					}
					

			try {
				productRepo.save(product);
			} catch (Exception e) {
				e.printStackTrace();
				productResponse.setMessage("Product not saved.");
				productResponse.setSuccess("failure");
				productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
			}

			productResponse.setMessage("Product saved sucessfully.");
			productResponse.setSuccess("success");
			productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		}

		return productResponse;

	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProductConfiguration#getAllPaymentTerms()
	 */
	@Override
	public ProductConfigurationResponse getAllPaymentTerms() {
		ProductConfigurationResponse productConfigurationResponse = null;
		List<PaymentTerm> paymentTermsList = (List<PaymentTerm>) paymentTermRepository.findAll();
		
		if(paymentTermsList!=null && !paymentTermsList.isEmpty()) {
			
			java.lang.reflect.Type targetListType = new TypeToken<List<PaymentTermDto>>() {
			}.getType();
			List<PaymentTermDto> paymentTerms = modelMapper.map(paymentTermsList, targetListType);
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setPaymentTerms(paymentTerms);
			productConfigurationResponse.setCode(100);
			productConfigurationResponse.setMessage(success);
		}else {
			LOG.info("Payment Terms List is Empty");
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setCode(101);
			productConfigurationResponse.setMessage(listEmpty);
			
		}
		return productConfigurationResponse;
	}
	
	

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProductConfiguration#getChaptersLsit()
	 */
	@Override
	public ProductConfigurationResponse getChaptersLsit() {
		ProductConfigurationResponse productConfigurationResponse = null;
		List<GSTChapter> gstChapterList = (List<GSTChapter>) gSTChapterRepository.findAllCust();
		if(gstChapterList!=null && !gstChapterList.isEmpty()) {
		
		List<GSTChapterDto> gstChapters = new ArrayList<>();
		
		for(GSTChapter gstChapter :gstChapterList) {
			if(!StringUtils.isEmpty(gstChapter.getChapterDescription()) ) {
				
			GSTChapterDto gstChapterDto = new GSTChapterDto();
			gstChapterDto.setChapterDescription(gstChapter.getChapterNumber()+". "+gstChapter.getChapterDescription());
			gstChapterDto.setChapterNumber(gstChapter.getChapterNumber());
			gstChapterDto.setGstChapterId(gstChapter.getGstChapterId());
			gstChapters.add(gstChapterDto);
			}
		}
		
		productConfigurationResponse = new ProductConfigurationResponse();
		productConfigurationResponse.setGstChapters(gstChapters);
		productConfigurationResponse.setCode(100);
		productConfigurationResponse.setMessage(success);
		}else {
			LOG.info("Chapter  List is Empty");
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setCode(101);
			productConfigurationResponse.setMessage(listEmpty);
		}
		return productConfigurationResponse;
	}



	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProductConfiguration#getAllHsnListBasedOnChapter(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public ProductConfigurationResponse getAllHsnListBasedOnChapter(String chapterNo,Pageable pageable) {
		
		Page<GstHsnCode> pages = gstHsnCodeRepository.findByChapterNo(chapterNo, pageable);
		
		GstHsnCode otherHsnCode = gstHsnCodeRepository.findByChapterNoOther();
		
		List<GstHsnCodeDto> list =null;
		ProductConfigurationResponse  productConfigurationResponse =null;
		if(pages!=null) {
			List<GstHsnCode> HSNCodeList = pages.getContent();
			java.lang.reflect.Type targetListType = new TypeToken<List<GstHsnCodeDto>>() {
				}.getType();
				list = modelMapper.map(HSNCodeList, targetListType);
				
				GstHsnResponseDto gstHsnResponseDto = new GstHsnResponseDto();
				gstHsnResponseDto.setHsnCodeList(list);
				gstHsnResponseDto.setNumberOfElements(String.valueOf(pages.getNumberOfElements()));
				gstHsnResponseDto.setTotalPages(String.valueOf(pages.getTotalPages()));
				gstHsnResponseDto.setTotalElements(String.valueOf(pages.getTotalElements()));
				productConfigurationResponse = new ProductConfigurationResponse();
				productConfigurationResponse.setGstHsnResponseDto(gstHsnResponseDto);
				productConfigurationResponse.setCode(100);
				productConfigurationResponse.setMessage(success);
				
				GstHsnCodeDto gstHsnCodeDto = new GstHsnCodeDto();
				gstHsnCodeDto.setChapterNo(otherHsnCode.getChapterNo());
				gstHsnCodeDto.setCgst(otherHsnCode.getCgst());
				gstHsnCodeDto.setDescription(otherHsnCode.getDescription());
				gstHsnCodeDto.setGstHsnId(otherHsnCode.getGstHsnId());
				gstHsnCodeDto.setIgst(otherHsnCode.getIgst());
				gstHsnCodeDto.setRelatedExportImportHsnCode(otherHsnCode.getRelatedExportImportHsnCode());
				gstHsnCodeDto.setSgstUtgst(otherHsnCode.getSgstUtgst());
				gstHsnCodeDto.setHsnCode(otherHsnCode.getHsnCode());
				productConfigurationResponse.setHsnCodeOther(gstHsnCodeDto);
		}else {
			
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setCode(101);
			productConfigurationResponse.setMessage(listEmpty);
		}
		
		
		return productConfigurationResponse;
	}



	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProductConfiguration#getAllSacListBasedOnSearch(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public ProductConfigurationResponse getAllSacListBasedOnSearch(String searchData, Pageable pageable) {
		Page<GstSacCode> pages = gstSacCodeRepository.findAllCustom(searchData, pageable);
		List<GstSacCodeDto> list =null;
		ProductConfigurationResponse  productConfigurationResponse= null;
		if(pages!=null) {
			
			List<GstSacCode> HSNCodeList = pages.getContent();
			
			   java.lang.reflect.Type targetListType = new TypeToken<List<GstSacCodeDto>>() {
				}.getType();
				list = modelMapper.map(HSNCodeList, targetListType);
				GstSacResponseDto gstSacResponseDto = new GstSacResponseDto();
				gstSacResponseDto.setGstSacCodeDtos(list);
				gstSacResponseDto.setNumberOfElements(String.valueOf(pages.getNumberOfElements()));
				gstSacResponseDto.setTotalPages(String.valueOf(pages.getTotalPages()));
				gstSacResponseDto.setTotalElements(String.valueOf(pages.getTotalElements()));
				productConfigurationResponse = new ProductConfigurationResponse();
				productConfigurationResponse.setGstSacResponseDto(gstSacResponseDto);
				productConfigurationResponse.setCode(100);
				productConfigurationResponse.setMessage(success);

		}else {
			
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setCode(101);
			productConfigurationResponse.setMessage(listEmpty);
		}
		
				
		return productConfigurationResponse;
		
	}
	
	
	@Override
	public ProductConfigurationResponse getAllHsnListBasedOnSearch(String searchData, Pageable pageable) {
		/*Page<GstHsnCode> pages = gstHsnCodeRepository.findAllCustom(searchData, pageable);
		List<GstHsnCodeDto> list =null;
		ProductConfigurationResponse  productConfigurationResponse =null;
		if(pages!=null) {
			List<GstHsnCode> HSNCodeList = pages.getContent();
			java.lang.reflect.Type targetListType = new TypeToken<List<GstHsnCodeDto>>() {
				}.getType();
				list = modelMapper.map(HSNCodeList, targetListType);
				
				GstHsnResponseDto gstHsnResponseDto = new GstHsnResponseDto();
				gstHsnResponseDto.setHsnCodeList(list);
				gstHsnResponseDto.setNumberOfElements(String.valueOf(pages.getNumberOfElements()));
				gstHsnResponseDto.setTotalPages(String.valueOf(pages.getTotalPages()));
				gstHsnResponseDto.setTotalElements(String.valueOf(pages.getTotalElements()));
				productConfigurationResponse = new ProductConfigurationResponse();
				productConfigurationResponse.setGstHsnResponseDto(gstHsnResponseDto);
				productConfigurationResponse.setCode(100);
				productConfigurationResponse.setMessage(success);
		}else {
			
			productConfigurationResponse = new ProductConfigurationResponse();
			productConfigurationResponse.setCode(101);
			productConfigurationResponse.setMessage(listEmpty);
		}
		
		
		return productConfigurationResponse;*/
		return null;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.impl.CommonServiceImpl#getProductListForConsole(com.ireslab.sendx.electra.model.ProductRequest)
	 */
	@Override
	public ProductResponse getProductListForConsole(ProductRequest productRequest) {
		
				ProductResponse productResponse = new ProductResponse();

				MerchantDto merchantDto = new MerchantDto();
				List<ProductDto> productlist = new ArrayList<>();
				
				Client client = null;

					client = clientRepo.findByClientCorrelationId(productRequest.getClientCorrelationId());
				
				if (client != null) {
					List<Product> productList = productRepo.findByClientId(client.getClientId());
					for (Product productEntity : productList) {
						ProductDto product = new ProductDto();
						//------------ Product Configuration ---
						  product.setItemCode(productEntity.getItemCode());
						  product.setInvoiceType(productEntity.getInvoiceType());	
						  product.setGstInclusive(productEntity.isGstInclusive());	
						  product.setItemNameOrDesc(productEntity.getItemNameOrDesc());	
						  product.setItemTypeOrChapter(productEntity.getItemTypeOrChapter());
						  product.setInvoiceType(productEntity.getInvoiceType());	
						  product.setDiscountPercentage(productEntity.getDiscountPercentage());
						  product.setItemPrice(productEntity.getItemPrice());
						  product.setAvailableQuantity(productEntity.getAvailableQuantity());
						  product.setSubTotal(productEntity.getSubTotal());
						  product.setDiscount(productEntity.getDiscount());
						  product.setTotalTaxInclusive(productEntity.getTotalTaxInclusive());
						  product.setTotal(productEntity.getTotal());
						  product.setCgst(productEntity.getCgst());
						  product.setSgstUtgst(productEntity.getSgst_utgst());
						  product.setIgst(productEntity.getIgst());
						  product.setCustomerNotes(productEntity.getCustomerNotes());
						  product.setTermsAndConditions(productEntity.getTermsAndConditions());
						  product.setInterState(productEntity.isInterState());
						//----------------------------------------
						product.setProductCode(productEntity.getProductCode());
						product.setProductId(productEntity.getProductId());
						
						product.setPaymentTerms(productEntity.getPaymentTerms());
						
						List<GSTChapterDto> gstChapterDtoList = new ArrayList<>();
						GSTChapterDto gstChapterDto = new GSTChapterDto();
						GSTChapter gstChapter = gSTChapterRepository.findByChapterNumber(productEntity.getItemTypeOrChapter());
						if(gstChapter != null) {
						
						gstChapterDto.setChapterDescription(gstChapter.getChapterNumber()+". "+gstChapter.getChapterDescription());
						gstChapterDto.setChapterNumber(gstChapter.getChapterNumber());
						gstChapterDto.setGstChapterId(gstChapter.getGstChapterId());
						gstChapterDtoList.add(gstChapterDto);
						product.setgSTChapterDto(gstChapterDtoList);
						}
						
						
						productlist.add(product);
					}
					// merchantDto.setCountryDialCode("+91");
					merchantDto.setMobileNumber(client.getContactNumber1());
					merchantDto.setEmailAddress(client.getEmailAddress());
					merchantDto.setFirstName(client.getClientName());
					merchantDto.setLastName(client.getClientName());
					merchantDto.setCompanyCode(client.getCompanyCode());

				}
				productResponse.setProductDetails(productlist);
				productResponse.setMerchantDetails(merchantDto);
				if (productlist.size() > 0) {
					productResponse.setCode(100);
					productResponse.setStatus(HttpStatus.OK.value());
					productResponse.setMessage("Success");
				} else {
					productResponse.setCode(101);
					productResponse.setStatus(HttpStatus.OK.value());
					
					productResponse.setMessage(messagesProperties.productListNotAvailable);
				}
				return productResponse;

	}
	
	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.impl.CommonServiceImpl#editProduct(com.ireslab.sendx.electra.model.SaveProductRequest)
	 */
	@Override
	public SaveProductResponse editProduct(SaveProductRequest productRequest) {
		SaveProductResponse productResponse = new SaveProductResponse();
		int productId = productRequest.getProductId();
		Product product = productRepo.findByProductId(productId);
		Client client = getClientDetailsByCorrelationId(productRequest.getClientCorrelationId());
		
		
		//------------ Product Configuration ---
		  product.setItemCode(productRequest.getItemCode());
		  product.setClientId(client.getClientId());
		  product.setProductCode(productRequest.getProductCode());
		  product.setProductGroup(productRequest.getItemTypeOrChapter());
		  product.setInvoiceType(productRequest.getInvoiceType());	
		  product.setGstInclusive(productRequest.isGstInclusive());	
		  product.setItemNameOrDesc(productRequest.getItemNameOrDesc());	
		  product.setItemTypeOrChapter(productRequest.getItemTypeOrChapter());	
		  product.setInvoiceType(productRequest.getInvoiceType());	
		  product.setDiscountPercentage(productRequest.getDiscount());
		  product.setItemPrice(productRequest.getItemPrice());
		  product.setAvailableQuantity(productRequest.getAvailableQuantity());
		  product.setSubTotal(productRequest.getTotal().getSubTotal());
		  product.setDiscount(productRequest.getTotal().getDiscount());
		  product.setTotalTaxInclusive(productRequest.getTotal().getTotalTaxInclusive());
		  product.setTotal(productRequest.getTotal().getTotal());
		  product.setCgst(productRequest.getGst().getCgst());
		  product.setSgst_utgst(productRequest.getGst().getSgst_utgst());
		  product.setIgst(productRequest.getGst().getIgst());
		  product.setCustomerNotes(productRequest.getCustomerNotes());
		  product.setTermsAndConditions(productRequest.getTermsAndConditions());
		  product.setPaymentTerms(productRequest.getPaymentTerms());
		  product.setInterState(productRequest.isInterState());
		  
		  
		  java.util.Date utilDate = new java.util.Date();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
			product.setModifiedDate(timestamp);
		//----------------------------------------


		try {
			productRepo.save(product);
		} catch (ApiException e) {
			e.printStackTrace();
			productResponse.setMessage("Product not edited.");
			productResponse.setSuccess("failure");
			productResponse.setCode(AppConstants.RESPONSE_FAILUR_CODE);
		}

		productResponse.setMessage("Product updated sucessfully.");
		productResponse.setSuccess("success");
		productResponse.setCode(AppConstants.RESPONSE_SUCCESS_CODE);
		// }

		return productResponse;

	}



	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProductConfiguration#excelDataLoad(com.ireslab.sendx.electra.model.GstHsnSacLoadRequest)
	 */
	@Override
	public ProductConfigurationResponse excelDataLoad(GstHsnSacLoadRequest gstHsnSacLoadRequest) {
		
		List<GstHsnCodeModel> gstHsnCodeModelList = gstHsnSacLoadRequest.getGstHsnCode();
		
		for(GstHsnCodeModel gstHsnCodeModel : gstHsnCodeModelList) {
			
			/*GstHsnCode gstHsnCode = new GstHsnCode();
			
			gstHsnCode.setChapterNo(gstHsnCodeModel.getHsnCode().substring(0, 2));
			gstHsnCode.setDescription(gstHsnCodeModel.getDescription());
			gstHsnCode.setRelatedExportImportHsnCode(gstHsnCodeModel.getRelatedExportImportHsnCode());
			gstHsnCode.setHsnCode(gstHsnCodeModel.getHsnCode());
			
			if(gstHsnCodeModel.getCgst().equalsIgnoreCase("NIL") || gstHsnCodeModel.getCgst().equalsIgnoreCase("#VALUE!") || gstHsnCodeModel.getCgst().equalsIgnoreCase("Nil (Exempt)")) {
				gstHsnCode.setCgst(0.00);
				gstHsnCode.setIgst(0.00);
				gstHsnCode.setSgstUtgst(0.00);
			}
			else {
				gstHsnCode.setCgst(Double.parseDouble(gstHsnCodeModel.getCgst()));
				gstHsnCode.setIgst(Double.parseDouble(gstHsnCodeModel.getIgst()));
				gstHsnCode.setSgstUtgst(Double.parseDouble(gstHsnCodeModel.getSgstUtgst()));
			}
			gstHsnCodeRepository.save(gstHsnCode);*/
			
			GstSacCode gstSacCode = new GstSacCode();
			
			gstSacCode.setChapterNo(gstHsnCodeModel.getHsnCode().substring(0, 2));
			gstSacCode.setDescription(gstHsnCodeModel.getDescription());
			gstSacCode.setAlsoCheck(gstHsnCodeModel.getRelatedExportImportHsnCode());
			gstSacCode.setSacCode(gstHsnCodeModel.getHsnCode());
			
			if(gstHsnCodeModel.getCgst() == null || gstHsnCodeModel.getCgst().equalsIgnoreCase("NIL") || gstHsnCodeModel.getCgst().equalsIgnoreCase("#VALUE!") || gstHsnCodeModel.getCgst().equalsIgnoreCase("Nil (Exempt)")) {
				gstSacCode.setCgst(0.00);
				gstSacCode.setIgst(0.00);
				gstSacCode.setSgstUtgst(0.00);
			}
			else {
				gstSacCode.setCgst(Double.parseDouble(gstHsnCodeModel.getCgst()));
				gstSacCode.setIgst(Double.parseDouble(gstHsnCodeModel.getIgst()));
				gstSacCode.setSgstUtgst(Double.parseDouble(gstHsnCodeModel.getSgstUtgst()));
			}
			gstSacCodeRepository.save(gstSacCode);
			
		}
		//gstHsnCodeRepository.save(gstHsnCode);
		ProductConfigurationResponse  productConfigurationResponse = new ProductConfigurationResponse();
		productConfigurationResponse.setCode(100);
		productConfigurationResponse.setMessage("Data Loaded");
		return productConfigurationResponse;
	}



	public String getListEmpty() {
		return listEmpty;
	}



	public void setListEmpty(String listEmpty) {
		this.listEmpty = listEmpty;
	}



	public String getResultNotFound() {
		return resultNotFound;
	}



	public void setResultNotFound(String resultNotFound) {
		this.resultNotFound = resultNotFound;
	}



	public String getSuccess() {
		return success;
	}



	public void setSuccess(String success) {
		this.success = success;
	}

	
}
