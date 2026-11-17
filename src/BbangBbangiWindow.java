import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 빵빵이의 고민 해결소 화면 버전.
 * 고민을 적고 엔터를 치거나 '물어보기'를 누르면 빵빵이가 대답한다.
 * 실행: java -cp out BbangBbangiWindow
 */
public class BbangBbangiWindow extends JPanel {

    private static final Color BREAD = new Color(255, 243, 214);
    private static final Color CRUST = new Color(150, 90, 40);
    private static final Color INK = new Color(60, 40, 25);

    final JTextField worryField = new JTextField();
    final JButton askButton = new JButton("물어보기");
    final JTextArea answerArea = new JTextArea("빵빵이: 무슨 고민 있어? 적어 봐!");

    private final BbangBbangi bbangBbangi;

    BbangBbangiWindow(BbangBbangi bbangBbangi) {
        this.bbangBbangi = bbangBbangi;

        setLayout(new BorderLayout(0, 16));
        setBackground(BREAD);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JLabel title = new JLabel("빵빵이의 고민 해결소");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
        title.setForeground(CRUST);

        JLabel prompt = new JLabel("고민을 적어 줘");
        prompt.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        prompt.setForeground(INK);

        worryField.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        askButton.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.add(worryField, BorderLayout.CENTER);
        inputRow.add(askButton, BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        title.setAlignmentX(LEFT_ALIGNMENT);
        prompt.setAlignmentX(LEFT_ALIGNMENT);
        inputRow.setAlignmentX(LEFT_ALIGNMENT);
        top.add(title);
        top.add(Box.createVerticalStrut(14));
        top.add(prompt);
        top.add(Box.createVerticalStrut(6));
        top.add(inputRow);

        answerArea.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        answerArea.setForeground(INK);
        answerArea.setEditable(false);
        answerArea.setFocusable(false);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        answerArea.setOpaque(false);

        JScrollPane answerScroll = new JScrollPane(answerArea);
        answerScroll.setBorder(null);
        answerScroll.setOpaque(false);
        answerScroll.getViewport().setOpaque(false);

        add(top, BorderLayout.NORTH);
        add(answerScroll, BorderLayout.CENTER);

        worryField.addActionListener(e -> ask()); // 엔터
        askButton.addActionListener(e -> ask());  // 버튼 클릭
    }

    /** 입력칸에 적힌 고민을 빵빵이에게 물어보고, 대답을 화면에 보여 준다. */
    void ask() {
        String worry = worryField.getText().strip();
        String reply = "빵빵이: " + bbangBbangi.reply(worry);
        answerArea.setText(worry.isEmpty() ? reply : "Q. " + worry + "\n\n" + reply);
        worryField.setText("");
        worryField.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // 기본 모양 그대로 써도 괜찮다
            }

            BbangBbangiWindow content = new BbangBbangiWindow(new BbangBbangi());
            JFrame frame = new JFrame("빵빵이의 고민 해결소");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(content);
            frame.setSize(560, 360);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            content.worryField.requestFocusInWindow();
        });
    }
}
