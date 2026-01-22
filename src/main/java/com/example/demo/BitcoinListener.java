package com.example.demo;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.List;

class MovingAverage {
    private final double[] buffer;
    private final int size;
    private double curSum = 0.0;
    private int head = 0;
    private int count = 0;

    public MovingAverage(int n) {
        this.size = n;
        this.buffer = new double[size];
    }
    public void addNumber(double price) {
        curSum += price;
        curSum -= buffer[head];
        buffer[head] = price;
        head++;
        if (head == size) {
            head = 0;
        }
        if (count < size) {
            count++;
        }
    }
    public double getAverage() {
        if (count == 0) return 0.0;
        return curSum / count;
    }
}

@Service
public class BitcoinListener {

    private final Map<String, MovingAverage> averages = new java.util.HashMap<>();

    @Autowired
    private SimpMessagingTemplate template;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void connectToMarket() {
        // NEW URL: Binance Public Stream (No API Key needed)
        String marketUrl = "wss://stream.binance.com:9443/stream?streams=" +
                "btcusdt@trade/" +
                "ethusdt@trade/" +
                "solusdt@trade/" +
                "xrpusdt@trade/" +
                "dogeusdt@trade";

        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(marketUrl), new WebSocketListener())
                .join();

        System.out.println("SYSTEM: Connected to Binance Feed.");
    }

    private class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("WEBSOCKET: Connection Open!");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                String json = data.toString();
                JsonNode root = mapper.readTree(json);

                // Binance JSON format: {"p": "96000.00", ...} where 'p' is price
                if (root.has("data")) {
                    JsonNode tradeData = root.get("data");

                    // Format it to look nice (remove extra decimals
                    String symbol = tradeData.get("s").asText(); // e.g., "BTCUSDT"
                    String price = tradeData.get("p").asText();

                    double priceVal = Double.parseDouble(price);
                    averages.computeIfAbsent(symbol, k -> new MovingAverage(100)).addNumber(priceVal);
                    String message = String.format("%s: $%.5f", symbol, priceVal);

                    System.out.println(message);
                    template.convertAndSend("/topic/numbers", message);
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            webSocket.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("WEBSOCKET ERROR: " + error.getMessage());
        }
    }
}