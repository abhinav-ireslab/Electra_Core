package com.ireslab.sendx.electra.electra;

import java.util.UUID;

import org.apache.commons.text.RandomStringGenerator;

public class TestOperationsClass {

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));

		System.out.println(new RandomStringGenerator.Builder().withinRange('0', 'z').build().generate(10));
	}
}
