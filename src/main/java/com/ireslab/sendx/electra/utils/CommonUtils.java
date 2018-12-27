package com.ireslab.sendx.electra.utils;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import com.ireslab.sendx.electra.entity.Exchange;
import com.ireslab.sendx.electra.repository.ExchangeRepository;

/**
 * @author Nitin
 *
 */
public class CommonUtils {
	@Autowired
	private static ExchangeRepository exchangeRepository;
	
	private static final String TRANSACTION_DATE_FORMAT = "MMM dd, yyyy";
	private static final String TRANSACTION_TIME_FORMAT = "hh:mm a";

	private static final SimpleDateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT);
	private static final SimpleDateFormat transactionTimeFormat = new SimpleDateFormat(TRANSACTION_TIME_FORMAT);
	private static final String TRANSACTION_TIME_TIMEZONE = "GMT+8";
	private static final TimeZone gmt8TimeZone = TimeZone.getTimeZone(TRANSACTION_TIME_TIMEZONE);
	public static String transactionDate(Date date) {

		//transactionDateFormat.setTimeZone(gmt8TimeZone);
		return transactionDateFormat.format(date);
	}

	public static String transactionTime(Date date) {

		//transactionTimeFormat.setTimeZone(gmt8TimeZone);
		return transactionTimeFormat.format(date);
	}
	
	
	
	public static String formatDate(Date date) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");  
		String     strDate = formatter.format(date);  
        return strDate;
	}
	
    public static String formatDateWithTimezone(Date date,String timeZone) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");  
		/*formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String  utcDate = formatter.format(date);
		Date utcdate = null;
		try {
			utcdate = formatter.parse(utcDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String  currentate = formatter.format(date);
        return currentate;
	}
	
	
    public static String formatDate(Date date, String format) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);  
		String     strDate = formatter.format(date);  
        return strDate;
	}
    
    
    
 public static Date formatDateWithTimezoneForDevice(Date date,String timeZone) {
		
	 SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
	 SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String  utcDateString = formatter.format(date);
		Date utcdate = null;;
		try {
			utcdate = dateFormatter.parse(utcDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return utcdate;
		
	}
    
    
    public static Date getSubscriptionDurations(String subscriptionDurations, Date invitedOn) {
		
    	Date current =invitedOn;
		System.out.println(current);
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		if(subscriptionDurations.equals("0")) {
			   subscriptionDurations="360";
			  }
		cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)+Integer.parseInt(subscriptionDurations)));
		cal.add(Calendar.DATE, 6);
		current = cal.getTime();
		//System.out.println(current);
		return current;
	}
    
    
    public static String tokenConversion(String to, String from, String token) {
    	
    	double ammoutToBeTransfered=0.0;
    	
    	Exchange exchange = exchangeRepository.findByExchangeTokenAndNativeCurrency(from,to);
    	
    	ammoutToBeTransfered =Double.parseDouble(token)*Double.parseDouble(exchange.getExchangeRate());
    	
    	return ""+ammoutToBeTransfered;
    	
    }
    
    
    public static String formatPhoneNumber(String phoneNum) {
        StringBuilder sb = new StringBuilder(15);
        StringBuilder temp = new StringBuilder(phoneNum);

       /* while (temp.length() < 10)
            temp.insert(0, "0");*/

        char[] chars = temp.toString().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (i == 3)
                sb.append("-");
            else if (i == 6)
                sb.append("-");
            
            sb.append(chars[i]);
        }

        return sb.toString();
    }

    
    public static String splitDialCodeAndMobileNumber(String mobileNumberWithDialCode, String countryDialCode) {
    	
    	
   // String temp ="+919015293893";
    
    int size =countryDialCode.length();
    String moblie="";
    char mobileNumberWithDialCodeChar[] =mobileNumberWithDialCode.toCharArray();
    for(int i=size;i<=mobileNumberWithDialCode.length()-1;i++) {
    	
    	moblie=moblie+mobileNumberWithDialCodeChar[i];
    }
    	
    	return moblie;
    }
    
    
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(splitDialCodeAndMobileNumber("+919015293893", "+91"));
		

		/*
		 * System.out.println(RandomStringUtils.randomAlphanumeric(10));
		 * 
		 * System.out.println(StringUtils.remove(UUID.randomUUID().toString(), "-"));
		 * 
		 * System.out.println(new
		 * String(Base64.getEncoder().encode("sendx:sendx".getBytes())));
		 * System.out.println(new BCryptPasswordEncoder().encode("electra"));
		 */
		/*System.out.println(
				Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest("1".getBytes())));*/
	
	   //System.out.println(formatDate(new Date()));
	  //System.out.println(formatDate(new Date(), "yyyy-MM-dd"));
	//System.out.println(getSubscriptionDurations("0",new Date()));
		
		//System.out.println(tokenConversion("INR", "MYR", "35"));
		
	
	}
}
