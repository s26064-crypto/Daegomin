import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * JUnit 없이 돌아가는 간단한 테스트.
 * 실행: java -cp out DaehuniTest
 */
public class DaehuniTest {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        hasAtLeast100Answers();
        answersAreNotBlankAndNotDuplicated();
        neverRepeatsTheSameAnswerTwiceInARow();
        everyAnswerCanComeOut();
        blankInputAsksAgain();
        quitWordEndsEvenWithSpaces();
        endOfInputEndsCleanly();
        replyAsksAgainWhenBlank();
        replyQuotesOneOfTheAnswers();
        windowShowsTheQuestionAndAnAnswer();
        windowAsksAgainOnBlank();
        serverReadsTheWorryFromTheAddress();
        serverAnswersOverHttp();

        if (failures > 0) {
            System.out.println("실패한 테스트: " + failures + "개");
            System.exit(1);
        }
        System.out.println("모든 테스트 통과");
    }

    static void hasAtLeast100Answers() {
        int count = new Daehuni().answers().size();
        check(count >= 100, "답변이 100개 이상이다 (지금 " + count + "개)");
    }

    static void answersAreNotBlankAndNotDuplicated() {
        List<String> answers = new Daehuni().answers();
        Set<String> seen = new HashSet<>();
        boolean ok = true;
        for (String answer : answers) {
            if (answer.isBlank() || !seen.add(answer)) {
                System.out.println("  문제 있는 답변: [" + answer + "]");
                ok = false;
            }
        }
        check(ok, "빈 답변이나 똑같은 답변이 없다");
    }

    static void neverRepeatsTheSameAnswerTwiceInARow() {
        Daehuni daehuni = new Daehuni(new Random(42));
        String previous = daehuni.answer();
        boolean ok = true;
        for (int i = 0; i < 1000; i++) {
            String current = daehuni.answer();
            if (current.equals(previous)) {
                ok = false;
                break;
            }
            previous = current;
        }
        check(ok, "같은 답이 연달아 두 번 나오지 않는다");
    }

    static void everyAnswerCanComeOut() {
        Daehuni daehuni = new Daehuni(new Random(7));
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 20000; i++) {
            seen.add(daehuni.answer());
        }
        check(seen.size() == daehuni.answers().size(), "모든 답변이 한 번 이상 나온다");
    }

    static void blankInputAsksAgain() throws IOException {
        String output = runWith("\n   \n내가 연애를 할 수 있을까요?\n끝\n");
        check(count(output, "대훈이: \"") == 1, "빈 입력에는 답을 뽑지 않는다");
        check(count(output, "고민을 적어야 대답해 주지!") == 2, "빈 입력이면 다시 물어본다");
    }

    static void quitWordEndsEvenWithSpaces() throws IOException {
        String output = runWith("  끝  \n다음 고민\n");
        check(count(output, "대훈이: \"") == 0, "앞뒤에 공백이 있어도 '끝'이면 바로 끝난다");
        check(output.contains("다음에 또 고민 생기면 와!"), "끝날 때 인사를 한다");
    }

    static void endOfInputEndsCleanly() throws IOException {
        String output = runWith("고민 하나\n");
        check(count(output, "대훈이: \"") == 1, "입력이 끝나기 전까지 답한다");
        check(output.contains("다음에 또 고민 생기면 와!"), "입력이 끝나도 인사하고 깔끔하게 끝난다");
    }

    static void replyAsksAgainWhenBlank() {
        String reply = new Daehuni(new Random(1)).reply("   ");
        check(reply.equals("고민을 적어야 대답해 주지!"), "빈 고민에는 다시 물어본다");
    }

    static void replyQuotesOneOfTheAnswers() {
        Daehuni daehuni = new Daehuni(new Random(1));
        String reply = daehuni.reply("시험 잘 볼 수 있을까?");
        boolean quoted = reply.startsWith("\"") && reply.endsWith("\"");
        String inside = reply.substring(1, reply.length() - 1);
        check(quoted && daehuni.answers().contains(inside), "대답은 답변 하나를 따옴표로 감싼 것이다");
    }

    static void windowShowsTheQuestionAndAnAnswer() {
        DaehuniWindow window = new DaehuniWindow(new Daehuni(new Random(1)));
        window.worryField.setText("  내가 연애를 할 수 있을까요?  ");
        window.ask();
        check(window.questionLabel.getText().equals("Q. 내가 연애를 할 수 있을까요?")
                && window.questionLabel.isVisible(), "화면에 질문이 나온다");
        check(window.answerArea.getText().startsWith("대훈이: \""), "화면에 대훈이 대답이 나온다");
        check(window.worryField.getText().isEmpty(), "물어본 뒤 입력칸이 비워진다");
    }

    static void windowAsksAgainOnBlank() {
        DaehuniWindow window = new DaehuniWindow(new Daehuni(new Random(1)));
        window.worryField.setText("   ");
        window.ask();
        check(window.answerArea.getText().equals("대훈이: 고민을 적어야 대답해 주지!"), "화면에서도 빈 고민이면 다시 물어본다");
        check(!window.questionLabel.isVisible(), "빈 고민이면 질문 줄을 숨긴다");
    }

    static void serverReadsTheWorryFromTheAddress() {
        check(DaehuniServer.worryFrom("worry=%EB%82%B4%EA%B0%80+%EC%97%B0%EC%95%A0").equals("내가 연애"),
                "주소에서 한글 고민을 읽는다");
        check(DaehuniServer.worryFrom("x=1&worry=%EC%8B%9C%ED%97%98").equals("시험"), "다른 값이 섞여 있어도 고민을 찾는다");
        check(DaehuniServer.worryFrom(null).isEmpty(), "주소에 아무것도 없으면 빈 고민이다");
        check(DaehuniServer.worryFrom("worry=").isEmpty(), "worry= 뒤가 비어 있으면 빈 고민이다");
    }

    static void serverAnswersOverHttp() throws Exception {
        DaehuniServer server = new DaehuniServer(0, Path.of("web"), new Daehuni(new Random(1)));
        server.start();
        try {
            String base = "http://localhost:" + server.port();
            String worry = URLEncoder.encode("내가 연애를 할 수 있을까요?", StandardCharsets.UTF_8);

            HttpResponse<String> answer = get(base + "/answer?worry=" + worry);
            check(answer.statusCode() == 200 && answer.body().startsWith("\"") && answer.body().endsWith("\""),
                    "서버가 대훈이 대답을 돌려준다");

            HttpResponse<String> blank = get(base + "/answer?worry=");
            check(blank.body().equals("고민을 적어야 대답해 주지!"), "서버도 빈 고민이면 다시 물어본다");

            HttpResponse<String> page = get(base + "/");
            check(page.statusCode() == 200 && page.body().contains("대훈이의 고민 해결소")
                    && page.headers().firstValue("Content-Type").orElse("").startsWith("text/html"),
                    "첫 화면(HTML)을 보여 준다");
            check(get(base + "/app.js").body().contains("/answer"), "JS 파일을 보여 준다");
            check(get(base + "/style.css").statusCode() == 200, "CSS 파일을 보여 준다");

            check(get(base + "/src/Main.java").statusCode() == 404, "정해진 파일 말고는 보여 주지 않는다");
        } finally {
            server.stop();
        }
    }

    private static HttpResponse<String> get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static String runWith(String input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Main.run(new BufferedReader(new StringReader(input)), out, new Daehuni(new Random(1)));
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static int count(String text, String part) {
        int count = 0;
        int index = text.indexOf(part);
        while (index != -1) {
            count++;
            index = text.indexOf(part, index + part.length());
        }
        return count;
    }

    private static void check(boolean ok, String name) {
        System.out.println((ok ? "[통과] " : "[실패] ") + name);
        if (!ok) {
            failures++;
        }
    }
}
