## 👋🏼 Welcome to my journey of learning Java
This is the first mini project I built (obviously with help of AI) and I am happy to present and describe what it does here.

To start off, this is **no complex engine**.

💍 **Ring buffer** is a circular array of fixed size, allowing read and write of data without memory allocation "on the Heap".
Its working principle is as follows:
1) You **feed it** data;
2) When it fills up, **oldest values get overwritten** by new data;
3) Process continues until data stream is terminated.

I first built the ring buffer to understand the **difference between objects and primitives**. It was simply a plain Java class.

Then I learned what **SpringBoot** is and how you can host Java applications on your local machine without any difficult configurations.
I proceeded to build a application that utilised a websocket connected to Binance to fetch Live Crypto Currency prices.

But then I have realised, if the prices change so rapidly, analysts need a clearer way to understand the trends in price fluctuations.
### This is when I remembered the Ring Buffer.
Integrating a circular array (which is a primitive stored on the Stack) into my simple application allowed me to display the moving average of the last 100 entries from the Binance data feed.
This would remove significant changes in currency values and give analysts a more reliable view on crypto prices.

## Key Optimizations

The core of this project focuses on **Zero-Allocation** principles and **O(1) complexity** for real-time data processing.

### 1. Custom Circular Buffer (Ring Buffer)
Instead of using standard Java `Lists` or `Streams` which create garbage and require expensive O(N) loops to calculate averages, this project implements a fixed-size **Circular Buffer**:
* **Zero New Objects:** Uses a pre-allocated primitive `double[]` array.
* **O(1) Complexity:** Calculates the moving average instantly by maintaining a "running sum" (subtracting the exiting value and adding the new entering value).
* **Memory Efficiency:** No resizing or memory churn, keeping the Garbage Collector (GC) pressure near zero during high-volatility market spikes.

### 2. Event-Driven Architecture
* Connects directly to the Binance WebSocket Public Feed (No polling).
* Uses Spring `SimpMessagingTemplate` to push data to the frontend only when trades occur.
* Handles "Warmup Phase" dynamically for 5+ concurrent currency streams (BTC, ETH, SOL, XRP, DOGE).

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot, Spring WebSocket (STOMP).
* **Frontend:** HTML5, JavaScript (SockJS + Stomp.js).
* **Data Source:** Binance Public WebSocket Stream (`wss://stream.binance.com:9443`).

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Binance API](https://img.shields.io/badge/Binance_Stream-F3BA2F?style=for-the-badge&logo=binance&logoColor=black)


Below are instructions on how to launch and run this application.

## ✅ How to Run
### Clone the repo:

```bash
git clone https://github.com/EmilMed/Binance-Tracker-Ring-Buffer-.git
cd Binance-Tracker-Ring-Buffer-
```

### Run the App:

```bash
./mvnw spring-boot:run
```

### View Dashboard: Open your browser to: http://localhost:8080
