package com.ireslab.sendx.electra.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ireslab.sendx.electra.ElectraConfig;
import com.ireslab.sendx.electra.service.ProfileImageService;

@Service
public class ProfileImageServiceImpl implements ProfileImageService {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileImageServiceImpl.class);

	@Autowired
	private ElectraConfig electraConfig;
	
	private static final String URI_SEPARATOR = "/";

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProfileImageService#getImageDataAsInputStream(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public byte[] getImageDataAsInputStream(String mobileNumber, HttpServletRequest request) {
		
		// System.out.println("testing image name :"+mobileNumber);
		InputStream is = request.getServletContext().getResourceAsStream("/images/" + mobileNumber);
		if (is != null) {

			try {
				return IOUtils.toByteArray(is);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ireslab.sendx.electra.service.ProfileImageService#saveImage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveImage(String imageName, String correlationId, String imageBase64) {

		Date date = new Date();
		String time = "" + date.getTime();
		String imageUrl = null;

		String formatImageName = null;
		if (imageName.equals("profile")) {
			formatImageName = "profile-" + correlationId + "-" + time + ".jpg";
		} else if (imageName.equals("scanDocumentFrontPart")) {
			formatImageName = "scanDocumentFrontPart-" + correlationId + "-" + time + ".jpg";
		} else if (imageName.equals("scanDocumentBackPart")) {
			formatImageName = "scanDocumentBackPart-" + correlationId + "-" + time + ".jpg";
		}
		else if (imageName.equals("idProof")) {
			formatImageName = "idProof-" + correlationId + "-" + time + ".jpg";
		}
		if (imageBase64 != null) {

			

			byte[] imageByte = Base64.decodeBase64(imageBase64);
			

			String catalinaHome = System.getenv("CATALINA_HOME");
			catalinaHome = (catalinaHome == null) ? System.getProperty("catalina.home") : catalinaHome;

			StringBuilder directory = new StringBuilder();
			directory.append(catalinaHome);
			directory.append(File.separator);
			directory.append("webapps");
			directory.append(File.separator);
			directory.append(electraConfig.imageDirectoryRelativePath);
			directory.append(File.separator);
			directory.append(formatImageName);

			
			FileOutputStream fileOutputStream = null;
			
			try {
				LOG.info("writting image to directory :" + directory);
				fileOutputStream = new FileOutputStream(directory.toString());
				fileOutputStream.write(imageByte);
				imageUrl = electraConfig.appBaseUrl + URI_SEPARATOR + electraConfig.imageDirectoryRelativePath
						+ URI_SEPARATOR + formatImageName;
			} catch (IOException e) {
				LOG.info("error occured while writting image to directory :" + directory);
				e.printStackTrace();
			}
			finally {
				
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return imageUrl;
	}
	
	
	private static void setDpi(byte[] imageData, int dpi) {
	    imageData[13] = 1;
	    imageData[14] = (byte) (dpi >> 8);
	    imageData[15] = (byte) (dpi & 0xff);
	    imageData[16] = (byte) (dpi >> 8);
	    imageData[17] = (byte) (dpi & 0xff);
	}

	
	
}
