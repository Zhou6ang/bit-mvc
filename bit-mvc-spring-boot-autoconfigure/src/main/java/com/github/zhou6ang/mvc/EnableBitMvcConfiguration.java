package com.github.zhou6ang.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.github.zhou6ang.mvc.servlet.BitDispatcherServlet;

@Configuration
@ComponentScan
@ConditionalOnClass({BitDispatcherServlet.class})
@EnableConfigurationProperties({BitMvcProperties.class})
public class EnableBitMvcConfiguration {

}
