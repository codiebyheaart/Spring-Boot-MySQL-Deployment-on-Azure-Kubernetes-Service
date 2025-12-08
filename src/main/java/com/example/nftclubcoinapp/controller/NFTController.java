package com.example.nftclubcoinapp.controller;

import com.example.nftclubcoinapp.util.StaticResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class NFTController {
    @GetMapping
    public Map<String, Object> getResponse() {
        return StaticResponseUtil.getStaticResponse("nft");
    }
}
