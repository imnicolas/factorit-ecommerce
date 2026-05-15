package com.ecommerce.factorIT.Config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Configuración SOAP con Spring WS.
 *
 * - El servlet de SOAP queda mapeado en /ws/*
 * - El WSDL se publica automáticamente en /ws/ecommerce.wsdl
 * - Las operaciones se definen en src/main/resources/xsd/ecommerce.xsd
 * - Las clases Java se generan en build time con jaxb2-maven-plugin
 *   bajo el paquete com.ecommerce.factorIT.soap.gen
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    private static final String NS = "http://ecommerce.factorit.com/soap";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * El nombre del bean ("ecommerce") define la URL del WSDL: /ws/ecommerce.wsdl
     */
    @Bean(name = "ecommerce")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema ecommerceSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("EcommercePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(NS);
        wsdl11Definition.setSchema(ecommerceSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema ecommerceSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ecommerce.xsd"));
    }
}
