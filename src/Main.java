import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * 대훈이의 고민 해결소.
 * 고민을 적으면 대훈이가 해결책 하나를 랜덤으로 알려 준다. '끝'을 적으면 끝난다.
 */
public class Main {

    static final String QUIT_WORD = "끝";

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, inputCharset()));
        run(in, System.out, new Daehuni());
    }

    static void run(BufferedReader in, PrintStream out, Daehuni daehuni) throws IOException {
        out.println("=== 대훈이의 고민 해결소 ===");
        out.println("고민을 적어 주면 대훈이가 해결책을 알려 줄게!");

        while (true) {
            out.println();
            out.print("고민을 적어 줘 (끝내려면 '" + QUIT_WORD + "') > ");
            out.flush();

            String line = in.readLine();
            if (line == null) { // Ctrl+Z 등으로 입력이 끝난 경우
                break;
            }

            String worry = line.strip();
            if (worry.equals(QUIT_WORD)) {
                break;
            }

            out.println("대훈이: " + daehuni.reply(worry));
        }

        out.println();
        out.println("대훈이: 다음에 또 고민 생기면 와! 뿡!");
    }

    /**
     * 터미널이 쓰는 문자 인코딩을 고른다.
     * 윈도우 터미널은 보통 cp949라서, 이걸 맞춰 줘야 한글로 '끝'을 쳐도 알아듣는다.
     */
    private static Charset inputCharset() {
        Console console = System.console();
        if (console != null) {
            return console.charset();
        }
        String stdoutEncoding = System.getProperty("stdout.encoding");
        if (stdoutEncoding != null && Charset.isSupported(stdoutEncoding)) {
            return Charset.forName(stdoutEncoding);
        }
        return Charset.defaultCharset();
    }
}
