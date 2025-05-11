package ui;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class BarChartPanel extends JPanel {
    private Map<String, Integer> chartData;
    private final Color barColor = new Color(30, 138, 56);
    private final Color textColor = Color.BLACK;
    
    public BarChartPanel(Map<String, Integer> data) {
        this.chartData = data;
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Handle empty data case
        if (chartData.isEmpty() || chartData.values().stream().allMatch(v -> v == 0)) {
            drawNoDataMessage(g2d);
            return;
        }

        int padding = 50;
        int chartWidth = getWidth() - padding * 2;
        int chartHeight = getHeight() - padding * 2;

        // Draw chart background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(padding, padding, chartWidth, chartHeight);

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(padding, padding, padding, padding + chartHeight); // Y-axis
        g2d.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight); // X-axis

        int barCount = chartData.size();
        int barWidth = (chartWidth - 20) / barCount;
        
        // Prevent division by zero
        int maxValue = Math.max(1, chartData.values().stream()
                .max(Integer::compare)
                .orElse(1));

        int x = padding + 10;
        for (Map.Entry<String, Integer> entry : chartData.entrySet()) {
            int barHeight = (int) ((double) entry.getValue() / maxValue * chartHeight);

            g2d.setColor(barColor);
            g2d.fillRect(x, padding + chartHeight - barHeight, barWidth - 5, barHeight);

            g2d.setColor(textColor);
            drawCenteredText(g2d, String.valueOf(entry.getValue()),
                             x + (barWidth - 5) / 2, padding + chartHeight - barHeight - 15);
            drawCenteredText(g2d, entry.getKey(),
                             x + (barWidth - 5) / 2, padding + chartHeight + 20);

            x += barWidth;
        }
    }

    private void drawNoDataMessage(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Tahoma", Font.BOLD, 20));
        String message = "No data available";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2;
        g2d.drawString(message, x, y);
    }

    private void drawCenteredText(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x - textWidth/2, y);
    }
    
    public void setData(Map<String, Integer> newData) {
        this.chartData = newData;
        repaint();
    }
}