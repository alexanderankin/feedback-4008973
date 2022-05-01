import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ExampleWithIdeContext {
    public static void main(String[] args) {
        DisposableServer disposableServer = HttpServer.create().route(httpServerRoutes -> {
            httpServerRoutes.get("/", (HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) -> {
                return httpServerResponse.status(200).sendString(Mono.just("hi"));
            });
        }).bindNow();

        System.out.println(disposableServer.address());

        HttpClient httpClient = HttpClient.create();
        HttpClient.ResponseReceiver<?> request = httpClient.get().uri(toBaseUrl(disposableServer));
        Response response = request.responseSingle(Response::just).block();

        Objects.requireNonNull(response);
        System.out.println(response);
        assert response.getString().equals("hi");

        disposableServer.disposeNow();
    }

    private static URI toBaseUrl(DisposableServer disposableServer) {
        try {
            return new URI("http", null, disposableServer.host(), disposableServer.port(), "/", null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("could not figure out server '/' uri", e);
        }
    }

    private static class Response {
        private final HttpClientResponse httpClientResponse;
        private final String string;

        private Response(HttpClientResponse httpClientResponse, String string) {
            this.httpClientResponse = httpClientResponse;
            this.string = string;
        }

        public static Mono<Response> just(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono) {
            return byteBufMono.asString().map(string -> new Response(httpClientResponse, string));

        }

        public String getString() {
            return string;
        }

        @Override
        public String toString() {
            return "Response{httpClientResponse=" + httpClientResponse + ", byteBufMono=" + string + '}';
        }
    }
}
