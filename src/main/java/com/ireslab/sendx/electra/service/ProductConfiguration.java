package com.ireslab.sendx.electra.service;

import org.springframework.data.domain.Pageable;

import com.ireslab.sendx.electra.model.GstHsnSacLoadRequest;
import com.ireslab.sendx.electra.model.ProductConfigurationResponse;
import com.ireslab.sendx.electra.model.ProductRequest;
import com.ireslab.sendx.electra.model.ProductResponse;
import com.ireslab.sendx.electra.model.SaveProductRequest;
import com.ireslab.sendx.electra.model.SaveProductResponse;

/**
 * @author iRESlab
 *
 */
public interface ProductConfiguration {

	public ProductConfigurationResponse getAllPaymentTerms();

	public ProductConfigurationResponse getChaptersLsit();


	public ProductConfigurationResponse getAllHsnListBasedOnChapter(String chapterNo, Pageable pageable);

	public ProductConfigurationResponse getAllSacListBasedOnSearch(String string, Pageable pageable);

	public SaveProductResponse saveProduct(SaveProductRequest saveProductRequest);

	public ProductConfigurationResponse excelDataLoad(GstHsnSacLoadRequest gstHsnSacLoadRequest);

	public ProductResponse getProductListForConsole(ProductRequest productRequest);

	public SaveProductResponse editProduct(SaveProductRequest saveProductRequest);

	public ProductConfigurationResponse getAllHsnListBasedOnSearch(String serarchData, Pageable pageable);

}
