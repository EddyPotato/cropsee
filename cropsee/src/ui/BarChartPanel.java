package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private Map<String, Integer> chartData;
    private final Color barColor = new Color(30, 138, 56); // Green color from your UI
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

        // Chart setup
        int padding = 50;
        int chartWidth = getWidth() - padding * 2;
        int chartHeight = getHeight() - padding * 2;

        // Draw background and axes
        g2d.setColor(Color.WHITE);
        g2d.fillRect(padding, padding, chartWidth, chartHeight);

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(padding, padding, padding, padding + chartHeight); // Y-axis
        g2d.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight); // X-axis

        // Handle empty data
        int barCount = chartData.size();
        if (barCount == 0) {
            g2d.setColor(Color.GRAY);
            drawCenteredText(g2d, "No crop data available", getWidth() / 2, getHeight() / 2);
            return;
        }

        // Calculate bar dimensions
        int barWidth = (chartWidth - 20) / barCount;
        int maxValue = chartData.values().stream().max(Integer::compare).orElse(1);

        // Draw bars
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

    private void drawCenteredText(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x - textWidth/2, y);
    }
    
    public void setData(Map<String, Integer> newData) {
        this.chartData = newData;
    }
}