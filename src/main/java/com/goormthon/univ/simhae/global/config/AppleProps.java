package com.goormthon.univ.simhae.global.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apple")
public record AppleProps (String clientId, String issuer, String jwkSetUri) {}
