package com.wilson.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Value("${server.http.port}")
    private int httpPort;

    @Value("${server.port}")
    private int httpsPort;

//    @Bean
//    public ConfigurableTomcatWebServerFactory ServletWebServerFactoryServletContainer() {
//        TomcatServletWebServerFactory tomcatServletWebServerFactory =new TomcatServletWebServerFactory();
//        Connector connector =new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setPort(httpPort);
//        tomcatServletWebServerFactory.addAdditionalTomcatConnectors(connector);
//        return tomcatServletWebServerFactory;
//    }


    @Bean
    //配置http某个端口自动跳转https
    public TomcatServletWebServerFactory servletContainer() {

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //监听的http端口
        connector.setPort(httpPort);
        connector.setSecure(false);
        //跳转的https端口
        connector.setRedirectPort(httpsPort);
        return connector;
    }
}
