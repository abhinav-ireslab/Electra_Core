package com.ireslab.sendx.electra.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author iRESlab
 *
 */
public interface ProfileImageService {
	
	
	
	public String saveImage(String imageName,String mobileNumber,String imageBase64);
	
	public byte[] getImageDataAsInputStream(String mobileNumber,HttpServletRequest request);
	
	
}
