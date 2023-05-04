package navi.benchmark;

import org.openjdk.jmh.annotations.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class HttpRequestBenchmark {
    static final String BASE_POST_URL = "http://PLACEHOLDER";
    static final String BASE_GET_URL = "http://PLACEHOLDER";
    @Param({"1", "10", "100"})
    public int postRequestsPerSecond;

    @Param({"1", "10", "100"})
    public int getRequestsPerSecond;

    private String postUrl;
    private String getUrl;
    private String requestBody;
    private HttpClient httpClient;
    private HttpRequest httpPostRequest;
    private HttpRequest httpGetRequest;


    @Setup
    public void setup() throws InterruptedException {
        postUrl = generatePostUrl();
        getUrl = generateGetUrl();
        requestBody = generateRequestBody();
        httpClient = HttpClient.newHttpClient();
        httpPostRequest = HttpRequest.newBuilder().uri(URI.create(postUrl)).POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json").build();

        httpGetRequest = HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testHttpPostBenchmark() {
        makeRequests(postRequestsPerSecond, httpPostRequest);
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testHttpGetBenchmark() {
        makeRequests(getRequestsPerSecond, httpGetRequest);
    }

    private void makeRequests(int requestsPerSecond, HttpRequest request) {
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>(requestsPerSecond);

        for (int i = 0; i < requestsPerSecond; i++) {
            CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            futures.add(future);
        }

        // Wait for all requests to complete and log the results
        for (CompletableFuture<HttpResponse<String>> future : futures) {
            try {
                HttpResponse<String> response = future.join(); // Wait for the request to complete
                // System.out.println("Response status code: " + response.statusCode());
                // System.out.println("Response body: " + response.body());
            } catch (CompletionException e) {
                System.err.println("Request failed: " + e.getCause().getMessage());
            }
        }
    }


    private String generatePostUrl() {
        // Generate a new URL for each iteration with the iteration index included
        return String.format("%s", BASE_POST_URL);
    }

    private String generateGetUrl() {
        return String.format("%s/http://64.226.105.150:4000/v1/search?text=/%s", BASE_GET_URL, "Toronto");
    }

    private String generateRequestBody() {
        return "{}";
    }
}