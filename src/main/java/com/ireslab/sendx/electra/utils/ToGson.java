package com.ireslab.sendx.electra.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.ireslab.sendx.electra.dto.Gst;
import com.ireslab.sendx.electra.dto.Total;
import com.ireslab.sendx.electra.model.SaveProductRequest;

public class ToGson {
	
public  static void main(String args[]) throws JsonGenerationException, JsonMappingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	Gson g=new Gson();
	SaveProductRequest saveProductRequest =new SaveProductRequest();
	saveProductRequest.setTotal(new Total());
	saveProductRequest.setGst(new Gst());
	String jsonString = g.toJson(new SaveProductRequest());
	System.out.println(mapper.writeValueAsString(saveProductRequest));
}

}
