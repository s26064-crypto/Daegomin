import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * JUnit 없이 돌아가는 간단한 테스트.
 * 실행: java -cp out BbangBbangiTest
 */
public class BbangBbangiTest {

    private static int failures = 0;

    public static void main(String[] args) throws IOException {
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

        if (failures > 0) {
            System.out.println("실패한 테스트: " + failures + "개");
            System.exit(1);
        }
        System.out.println("모든 테스트 통과");
    }

    static void hasAtLeast100Answers() {
        int count = new BbangBbangi().answers().size();
        check(count >= 100, "답변이 100개 이상이다 (지금 " + count + "개)");
    }

    static void answersAreNotBlankAndNotDuplicated() {
        List<String> answers = new BbangBbangi().answers();
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
        BbangBbangi bbangBbangi = new BbangBbangi(new Random(42));
        String previous = bbangBbangi.answer();
        boolean ok = true;
        for (int i = 0; i < 1000; i++) {
            String current = bbangBbangi.answer();
            if (current.equals(previous)) {
                ok = false;
                break;
            }
            previous = current;
        }
        check(ok, "같은 답이 연달아 두 번 나오지 않는다");
    }

    static void everyAnswerCanComeOut() {
        BbangBbangi bbangBbangi = new BbangBbangi(new Random(7));
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 20000; i++) {
            seen.add(bbangBbangi.answer());
        }
        check(seen.size() == bbangBbangi.answers().size(), "모든 답변이 한 번 이상 나온다");
    }

    static void blankInputAsksAgain() throws IOException {
        String output = runWith("\n   \n내가 연애를 할 수 있을까요?\n끝\n");
        check(count(output, "빵빵이: \"") == 1, "빈 입력에는 답을 뽑지 않는다");
        check(count(output, "고민을 적어야 대답해 주지!") == 2, "빈 입력이면 다시 물어본다");
    }

    static void quitWordEndsEvenWithSpaces() throws IOException {
        String output = runWith("  끝  \n다음 고민\n");
        check(count(output, "빵빵이: \"") == 0, "앞뒤에 공백이 있어도 '끝'이면 바로 끝난다");
        check(output.contains("다음에 또 고민 생기면 와!"), "끝날 때 인사를 한다");
    }

    static void endOfInputEndsCleanly() throws IOException {
        String output = runWith("고민 하나\n");
        check(count(output, "빵빵이: \"") == 1, "입력이 끝나기 전까지 답한다");
        check(output.contains("다음에 또 고민 생기면 와!"), "입력이 끝나도 인사하고 깔끔하게 끝난다");
    }

    static void replyAsksAgainWhenBlank() {
        String reply = new BbangBbangi(new Random(1)).reply("   ");
        check(reply.equals("고민을 적어야 대답해 주지!"), "빈 고민에는 다시 물어본다");
    }

    static void replyQuotesOneOfTheAnswers() {
        BbangBbangi bbangBbangi = new BbangBbangi(new Random(1));
        String reply = bbangBbangi.reply("시험 잘 볼 수 있을까?");
        boolean quoted = reply.startsWith("\"") && reply.endsWith("\"");
        String inside = reply.substring(1, reply.length() - 1);
        check(quoted && bbangBbangi.answers().contains(inside), "대답은 답변 하나를 따옴표로 감싼 것이다");
    }

    static void windowShowsTheQuestionAndAnAnswer() {
        BbangBbangiWindow window = new BbangBbangiWindow(new BbangBbangi(new Random(1)));
        window.worryField.setText("  내가 연애를 할 수 있을까요?  ");
        window.ask();
        String shown = window.answerArea.getText();
        check(shown.startsWith("Q. 내가 연애를 할 수 있을까요?\n\n빵빵이: \""), "화면에 질문과 빵빵이 대답이 나온다");
        check(window.worryField.getText().isEmpty(), "물어본 뒤 입력칸이 비워진다");
    }

    static void windowAsksAgainOnBlank() {
        BbangBbangiWindow window = new BbangBbangiWindow(new BbangBbangi(new Random(1)));
        window.worryField.setText("   ");
        window.ask();
        check(window.answerArea.getText().equals("빵빵이: 고민을 적어야 대답해 주지!"), "화면에서도 빈 고민이면 다시 물어본다");
    }

    private static String runWith(String input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Main.run(new BufferedReader(new StringReader(input)), out, new BbangBbangi(new Random(1)));
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
