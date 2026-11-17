import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/** 알약 모양으로 완전히 둥근 버튼. 마우스를 올리거나 누르면 색이 조금 진해진다. */
public class RoundButton extends JButton {

    private final Color fill;

    public RoundButton(String text, Color fill, Color textColor, Font font) {
        super(text);
        this.fill = fill;
        setFont(font);
        setForeground(textColor);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setBorder(new EmptyBorder(11, 24, 11, 24));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = fill;
        if (getModel().isPressed()) {
            color = fill.darker().darker();
        } else if (getModel().isRollover()) {
            color = fill.darker();
        }
        g2.setColor(color);
        int arc = getHeight(); // 높이만큼 둥글게 = 알약 모양
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g); // 글자는 원래대로 그린다
    }
}
