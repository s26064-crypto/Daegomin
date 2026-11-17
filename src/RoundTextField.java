import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * 알약 모양 입력칸. 테두리 없이 연한 배경만 있고, 클릭하면 파란 테두리가 생긴다.
 * 비어 있을 때는 연한 글씨로 예시 문구를 보여 준다.
 */
public class RoundTextField extends JTextField {

    private final String placeholder;
    private final Color fill;
    private final Color focusLine;
    private final Color placeholderColor;

    public RoundTextField(String placeholder, Color fill, Color focusLine,
                          Color textColor, Color placeholderColor, Font font) {
        this.placeholder = placeholder;
        this.fill = fill;
        this.focusLine = focusLine;
        this.placeholderColor = placeholderColor;
        setFont(font);
        setForeground(textColor);
        setCaretColor(textColor);
        setOpaque(false);
        setBorder(new EmptyBorder(11, 20, 11, 16));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = getHeight();
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        if (hasFocus()) {
            g2.setColor(focusLine);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc - 2, arc - 2);
        }

        if (getText().isEmpty()) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont());
            Insets insets = getInsets();
            FontMetrics metrics = g2.getFontMetrics();
            int y = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;
            g2.drawString(placeholder, insets.left, y);
        }
        g2.dispose();
        super.paintComponent(g); // 입력한 글자와 커서
    }
}
