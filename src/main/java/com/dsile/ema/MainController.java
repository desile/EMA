package com.dsile.ema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    EthereumBean ethereumBean;

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @RequestMapping("/best")
    String bestBlock() {
        return ethereumBean.getBestBlock();
    }

}
