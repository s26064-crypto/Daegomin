import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * 대훈이의 고민 해결소 화면 버전.
 * 고민을 적고 엔터를 치거나 '물어보기'를 누르면 대훈이가 대답한다.
 * 실행: java -cp out DaehuniWindow
 */
public class DaehuniWindow extends JPanel {

    // 글꼴과 색 (윈도우에 '맑은 고딕'이 없으면 기본 글꼴로 바뀐다)
    private static final String FONT = "Malgun Gothic";
    private static final Color BACKGROUND = new Color(0xFFFFFF);
    private static final Color TITLE = new Color(0x191F28);
    private static final Color TEXT = new Color(0x333D4B);
    private static final Color MUTED = new Color(0x8B95A1);
    private static final Color SOFT_GRAY = new Color(0xF2F4F6);
    private static final Color BLUE = new Color(0x3182F6);

    final RoundTextField worryField = new RoundTextField("예) 내가 연애를 할 수 있을까요?",
            SOFT_GRAY, BLUE, TEXT, MUTED, new Font(FONT, Font.PLAIN, 16));
    final RoundButton askButton = new RoundButton("물어보기", BLUE, Color.WHITE, new Font(FONT, Font.BOLD, 15));
    final JLabel questionLabel = new JLabel();
    final JTextArea answerArea = new JTextArea("대훈이: 무슨 고민 있어? 적어 봐!");

    private final Daehuni daehuni;

    DaehuniWindow(Daehuni daehuni) {
        this.daehuni = daehuni;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(26, 28, 28, 28));

        add(createHeader(), BorderLayout.NORTH);
        add(createAnswerCard(), BorderLayout.CENTER);

        worryField.addActionListener(e -> ask()); // 엔터
        askButton.addActionListener(e -> ask());  // 버튼 클릭
    }

    /** 제목, 설명, 입력칸과 버튼. */
    private JComponent createHeader() {
        JLabel title = new JLabel("대훈이의 고민 해결소");
        title.setFont(new Font(FONT, Font.BOLD, 26));
        title.setForeground(TITLE);

        JLabel subtitle = new JLabel("고민을 적으면 대훈이가 해결책을 알려 줄게");
        subtitle.setFont(new Font(FONT, Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.add(worryField, BorderLayout.CENTER);
        inputRow.add(askButton, BorderLayout.EAST);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        for (JComponent c : new JComponent[] {title, subtitle, inputRow}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(20));
        header.add(inputRow);
        return header;
    }

    /** 질문과 대답이 들어가는 둥근 회색 카드. */
    private JComponent createAnswerCard() {
        questionLabel.setFont(new Font(FONT, Font.PLAIN, 14));
        questionLabel.setForeground(MUTED);
        questionLabel.setVisible(false);

        answerArea.setFont(new Font(FONT, Font.BOLD, 18));
        answerArea.setForeground(TEXT);
        answerArea.setOpaque(false);
        answerArea.setEditable(false);
        answerArea.setFocusable(false);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        RoundPanel card = new RoundPanel(new BorderLayout(0, 8), SOFT_GRAY, 32);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.add(questionLabel, BorderLayout.NORTH);
        card.add(answerArea, BorderLayout.CENTER);

        // 카드가 아래로 늘어나지 않고 내용만큼만 차지하게 위쪽에 붙인다
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(card, BorderLayout.NORTH);
        return holder;
    }

    /** 입력칸에 적힌 고민을 대훈이에게 물어보고, 대답을 화면에 보여 준다. */
    void ask() {
        String worry = worryField.getText().strip();
        questionLabel.setText(worry.isEmpty() ? "" : "Q. " + worry);
        questionLabel.setVisible(!worry.isEmpty());
        answerArea.setText("대훈이: " + daehuni.reply(worry));
        worryField.setText("");
        worryField.requestFocusInWindow();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DaehuniWindow content = new DaehuniWindow(new Daehuni());
            JFrame frame = new JFrame("대훈이의 고민 해결소");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(content);
            frame.setSize(580, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            content.worryField.requestFocusInWindow();
        });
    }
}
