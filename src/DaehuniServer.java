import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * 대훈이의 고민 해결소 사이트 버전 (자바 웹 서버).
 * 화면(web 폴더의 HTML, CSS, JS)을 보여 주고, 대답은 자바(Daehuni)가 골라서 돌려준다.
 *
 * 실행: java -cp out DaehuniServer          → http://localhost:8080
 *       java -cp out DaehuniServer 9000     → 포트 바꾸기
 *       java -cp out DaehuniServer 8080 C:\경로\web → web 폴더 위치 직접 알려 주기
 */
public class DaehuniServer {

    /** 브라우저에 보여 줄 수 있는 파일. 여기 없는 주소는 전부 404. */
    private record WebFile(String name, String contentType) {
    }

    private static final Map<String, WebFile> WEB_FILES = Map.of(
            "/", new WebFile("index.html", "text/html; charset=utf-8"),
            "/style.css", new WebFile("style.css", "text/css; charset=utf-8"),
            "/app.js", new WebFile("app.js", "text/javascript; charset=utf-8")
    );

    private final HttpServer server;

    DaehuniServer(int port, Path webDir, Daehuni daehuni) throws IOException {
        // 내 컴퓨터(localhost)에서만 접속할 수 있게 연다
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);

        // 대답: /answer?worry=고민 → "대훈이의 대답"
        server.createContext("/answer", exchange -> {
            String worry = worryFrom(exchange.getRequestURI().getRawQuery());
            send(exchange, 200, "text/plain; charset=utf-8", daehuni.reply(worry).getBytes(StandardCharsets.UTF_8));
        });

        // 화면 파일: /, /style.css, /app.js
        server.createContext("/", exchange -> {
            WebFile file = WEB_FILES.get(exchange.getRequestURI().getPath());
            if (file == null) {
                send(exchange, 404, "text/plain; charset=utf-8", "그런 페이지는 없어!".getBytes(StandardCharsets.UTF_8));
                return;
            }
            send(exchange, 200, file.contentType(), Files.readAllBytes(webDir.resolve(file.name())));
        });
    }

    void start() {
        server.start();
    }

    void stop() {
        server.stop(0);
    }

    int port() {
        return server.getAddress().getPort();
    }

    /** "worry=%EB%82%B4%EA%B0%80" 같은 주소 뒷부분에서 고민 글자를 꺼낸다. 없으면 빈 글자. */
    static String worryFrom(String rawQuery) {
        if (rawQuery == null) {
            return "";
        }
        for (String pair : rawQuery.split("&")) {
            if (pair.startsWith("worry=")) {
                return URLDecoder.decode(pair.substring("worry=".length()), StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private static void send(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path webDir = args.length > 1 ? Path.of(args[1]) : Path.of("web");

        if (!Files.isDirectory(webDir)) {
            System.out.println("web 폴더를 찾을 수 없어요: " + webDir.toAbsolutePath());
            System.out.println("프로젝트 폴더에서 실행하거나, web 폴더 위치를 두 번째 값으로 알려 주세요.");
            return;
        }

        DaehuniServer server = new DaehuniServer(port, webDir, new Daehuni());
        server.start();
        System.out.println("대훈이의 고민 해결소 사이트가 열렸어요: http://localhost:" + server.port());
        System.out.println("끄려면 Ctrl+C");
    }
}
