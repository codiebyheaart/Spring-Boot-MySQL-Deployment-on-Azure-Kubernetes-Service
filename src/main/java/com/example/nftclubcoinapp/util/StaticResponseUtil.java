package com.example.nftclubcoinapp.util;

import java.util.Map;

public class StaticResponseUtil {
    public static Map<String, Object> getStaticResponse(String type) {
        switch (type) {
            case "auth":
                return Map.of("status", "success", "message", "Logged in successfully");
            case "user":
                return Map.of("userId", "12345", "membership", "Gold", "validTill", "2025-12-31");
            case "token":
                return Map.of("tokenName", "CLUBCOIN", "balance", 1200);
            case "transaction":
                return Map.of("transactions", java.util.List.of("TX123", "TX456"));
            case "nft":
                return Map.of("nfts", java.util.List.of("NFT001", "NFT002"));
            case "club":
                return Map.of("clubs", java.util.List.of("Music Club", "Gaming Club"));
            default:
                return Map.of("error", "Invalid request");
        }
    }
}
