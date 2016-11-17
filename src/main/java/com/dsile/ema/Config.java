package com.dsile.ema;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    EthereumBean ethereumBean() throws Exception {
        EthereumBean ethereumBean = new EthereumBean();
        //ethereumBean.start();
        Executors.newSingleThreadExecutor().
                submit(ethereumBean::start);

        return ethereumBean;
    }
}
