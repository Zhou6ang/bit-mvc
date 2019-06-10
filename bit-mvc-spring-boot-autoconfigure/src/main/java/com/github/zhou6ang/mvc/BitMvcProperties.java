package com.github.zhou6ang.mvc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="bit-mvc")
public class BitMvcProperties {

}
