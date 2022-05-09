import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
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
    private static final Scheduler schedulerFromLib = Schedulers.boundedElastic();
    private static final Scheduler schedulerCustom = Schedulers.newBoundedElastic(5, 10, "schedulerCustom");

    @SuppressWarnings("DuplicatedCode") // for debugging the ide!
    public static void main(String[] args) {
        DisposableServer disposableServer = HttpServer.create().route(httpServerRoutes -> {
            System.out.println("configuring routes");
            httpServerRoutes.get("/", (HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) -> {
                // issues warning, the expected way
                //noinspection CallingSubscribeInNonBlockingScope
                Mono.fromCallable(() -> 1).subscribe();

                // no warning issued, also expected
                Mono.fromCallable(() -> 1).subscribeOn(schedulerFromLib).subscribe();

                // no warning issued, unlike the issue I thought I was seeing
                Mono.fromCallable(() -> 1).subscribeOn(schedulerCustom).subscribe();

                // the only real issue, no warning issued
                Mono.fromCallable(() -> { Thread.sleep(100); return 1; }).subscribeOn(Schedulers.immediate()).subscribe();

                Mono<String> stringMono = Mono.fromCallable(() -> {
                    // tangentially related, but not what i was going for...
                    //noinspection BlockingMethodInNonBlockingContext
                    Thread.sleep(100);
                    return "hi";
                }).subscribeOn(schedulerFromLib);

                return httpServerResponse.status(200).sendString(stringMono);
            });
        }).bindNow();

        System.out.println(disposableServer.address());

        HttpClient httpClient = HttpClient.create();
        HttpClient.ResponseReceiver<?> request = httpClient.get().uri(toBaseUrl(disposableServer));
        Response response = request.responseSingle(Response::just).block();

        Objects.requireNonNull(response);
        System.out.println(response);
        if (!response.getString().equals("hi")) {
            throw new AssertionError("expected 'hi', got: " + response.getString());
        }

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
