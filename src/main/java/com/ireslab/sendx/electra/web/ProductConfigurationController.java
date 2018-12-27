package com.ireslab.sendx.electra.web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.sendx.electra.model.GstHsnSacLoadRequest;
import com.ireslab.sendx.electra.model.ProductConfigurationRequest;
import com.ireslab.sendx.electra.model.ProductConfigurationResponse;
import com.ireslab.sendx.electra.model.ProductRequest;
import com.ireslab.sendx.electra.model.ProductResponse;
import com.ireslab.sendx.electra.model.SaveProductRequest;
import com.ireslab.sendx.electra.model.SaveProductResponse;
import com.ireslab.sendx.electra.service.CommonService;
import com.ireslab.sendx.electra.service.ProductConfiguration;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/product/**")

public class ProductConfigurationController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductConfigurationController.class);
	
	@Autowired
	private ProductConfiguration productConfiguration;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ObjectWriter objectWriter;
	
	
	/**
	 * use to save product in database.
	 * 
	 * @param saveProductRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "saveProduct", method = RequestMethod.POST)
	public ResponseEntity<SaveProductResponse> saveProduct(@RequestBody SaveProductRequest saveProductRequest)
			throws JsonProcessingException {
		LOG.debug("Save Product Request received : " + objectWriter.writeValueAsString(saveProductRequest));
		SaveProductResponse saveProductResponse = productConfiguration.saveProduct(saveProductRequest);
		LOG.info("Response sent after save product ");
		return new ResponseEntity<>(saveProductResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to get product list for client management console.
	 * 
	 * @param productRequest
	 * @param clientCorrelationId
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "productListForConsole/{clientCorrelationId}", method = RequestMethod.POST)
	public ResponseEntity<ProductResponse> productListForConsole(@RequestBody ProductRequest productRequest, @PathVariable(value = "clientCorrelationId") String clientCorrelationId)
			throws JsonProcessingException {
		LOG.info("Request received for product list for management console : " + objectWriter.writeValueAsString(productRequest));
		productRequest.setClientCorrelationId(clientCorrelationId);
		//ProductResponse productResponse = commonService.getProductListForConsole(productRequest);
		ProductResponse productResponse = productConfiguration.getProductListForConsole(productRequest);
		LOG.info("Response send for product list for management console  " );
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to edit product details.
	 * 
	 * @param saveProductRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "editProduct", method = RequestMethod.POST)
	public ResponseEntity<SaveProductResponse> editProduct(@RequestBody SaveProductRequest saveProductRequest)
			throws JsonProcessingException {
		LOG.info("Edit Product Request received : " + objectWriter.writeValueAsString(saveProductRequest));
		SaveProductResponse saveProductResponse = productConfiguration.editProduct(saveProductRequest);
		LOG.info("Response send for edit product details " );
		return new ResponseEntity<>(saveProductResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to delete product from database.
	 * 
	 * @param productCode
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "deleteProduct/{productCode}", method = RequestMethod.GET)
	public ResponseEntity<SaveProductResponse> deleteProduct(@PathVariable("productCode") String productCode)
			throws JsonProcessingException {
		LOG.debug("Delete Product Request received : " + objectWriter.writeValueAsString(productCode));
		SaveProductResponse saveProductResponse = commonService.deleteProduct(productCode);
		LOG.info("Response send after delete product " );
		return new ResponseEntity<>(saveProductResponse, HttpStatus.OK);
	}
	
	/**
	 * use to get payment term list.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getPaymentTerm", method = RequestMethod.GET)
	public ResponseEntity<ProductConfigurationResponse> getPaymentTermList()
			throws JsonProcessingException {
		LOG.info("Payment Term Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.getAllPaymentTerms();
		LOG.info("Payment Term Response json  send  ");
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to get chapter list.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getChaptersList", method = RequestMethod.GET)
	public ResponseEntity<ProductConfigurationResponse> getChaptersList()
			throws JsonProcessingException {
		LOG.info("Chapters Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.getChaptersLsit();
		LOG.info("Chapters list Response sent  " );
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	
	/**
	 * use to get HSN code list based on chapter number.
	 * 
	 * @param productConfigurationRequest
	 * @param pageable
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllHsnListBasedOnChapter", method = RequestMethod.POST)
	public ResponseEntity<ProductConfigurationResponse> getAllHsnListBasedOnChapter(
			@RequestBody ProductConfigurationRequest productConfigurationRequest,
			Pageable pageable)
			throws JsonProcessingException {
		LOG.info("HSN list Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.getAllHsnListBasedOnChapter(productConfigurationRequest.getChapterNo(),pageable);
		LOG.info("HSN list Response send");
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to read data from excel file and save in the database.
	 * 
	 * @param gstHsnSacLoadRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "excelDataLoad", method = RequestMethod.POST)
	public ResponseEntity<ProductConfigurationResponse> excelDataLoad(
			@RequestBody GstHsnSacLoadRequest gstHsnSacLoadRequest)
			throws JsonProcessingException {
		LOG.info("Laod data from excel Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.excelDataLoad(gstHsnSacLoadRequest);
		LOG.info("Laod data from excel Response send");
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	
	/**
	 * use to get SAC code list based on user search.
	 * 
	 * @param productConfigurationRequest
	 * @param pageable
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllSacListBasedOnSearch", method = RequestMethod.POST)
	public ResponseEntity<ProductConfigurationResponse> getAllSacListBasedOnSearch(
			@RequestBody ProductConfigurationRequest productConfigurationRequest,
			Pageable pageable)
			throws JsonProcessingException {
		LOG.info("SAC list Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.getAllSacListBasedOnSearch(productConfigurationRequest.getSerarchData(),pageable);
		LOG.info("SAC list Response send");
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	
	
	/**
	 * use to get HSN code list based on user search.
	 * 
	 * @param productConfigurationRequest
	 * @param pageable
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "getAllHsnListBasedOnSearch", method = RequestMethod.POST)
	public ResponseEntity<ProductConfigurationResponse> getAllHsnListBasedOnSearch(
			@RequestBody ProductConfigurationRequest productConfigurationRequest,
			Pageable pageable)
			throws JsonProcessingException {
		LOG.info("HSN list Request received");
		ProductConfigurationResponse productConfigurationResponse = productConfiguration.getAllHsnListBasedOnSearch(productConfigurationRequest.getSerarchData(),pageable);
		LOG.info("HSN list Response send");
		return new ResponseEntity<>(productConfigurationResponse, HttpStatus.OK);
	}
	

}
