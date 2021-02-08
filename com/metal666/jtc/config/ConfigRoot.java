package com.metal666.jtc.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ConfigRoot {

	@JsonProperty
	public List<ConfigItem> configs;

}
