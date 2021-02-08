package com.metal666.jtc.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigItem {

	@JsonProperty
	public String name, params, jar;

}
