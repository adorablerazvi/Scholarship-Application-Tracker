package ui;

import javax.swing.*;
import java.awt.*;

public class BarChartPanel extends JPanel {
    private String[] labels;
    private double[] values;
    private Color[] colors;
    private String title;

    public BarChartPanel(String title, String[] labels, double[] values, Color[] colors) {
        this.title = title;
        this.labels = labels;
        this.values = values;
        this.colors = colors;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(350, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(44, 62, 80));
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

        if (values.length == 0) return;

        // Find max value
        double maxVal = 0;
        for (double v : values) if (v > maxVal) maxVal = v;
        if (maxVal == 0) maxVal = 1;

        // Chart area
        int chartX = 60;
        int chartY = 45;
        int chartWidth = getWidth() - 90;
        int chartHeight = getHeight() - 100;
        int barCount = values.length;
        int gap = 15;
        int barWidth = (chartWidth - (barCount + 1) * gap) / barCount;
        if (barWidth > 60) barWidth = 60;

        // Recalculate to center bars
        int totalBarsWidth = barCount * barWidth + (barCount + 1) * gap;
        chartX = (getWidth() - totalBarsWidth) / 2;

        // Draw Y-axis line
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(chartX, chartY, chartX, chartY + chartHeight);

        // Draw X-axis line
        g2.drawLine(chartX, chartY + chartHeight, chartX + totalBarsWidth, chartY + chartHeight);

        // Draw horizontal grid lines
        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
        for (int i = 1; i <= 4; i++) {
            int y = chartY + chartHeight - (int) ((i / 4.0) * chartHeight);
            g2.drawLine(chartX, y, chartX + totalBarsWidth, y);

            // Y-axis labels
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setStroke(new BasicStroke(1));
            String yLabel = String.valueOf((int) (maxVal * i / 4));
            g2.drawString(yLabel, chartX - 30, y + 4);
            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
        }

        // Draw bars
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < barCount; i++) {
            int x = chartX + gap + i * (barWidth + gap);
            int barHeight = (int) ((values[i] / maxVal) * chartHeight);
            int y = chartY + chartHeight - barHeight;

            // Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(x + 3, y + 3, barWidth, barHeight, 5, 5);

            // Bar with gradient
            GradientPaint gradient = new GradientPaint(
                x, y, colors[i],
                x, y + barHeight, colors[i].darker()
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

            // Border
            g2.setColor(colors[i].darker());
            g2.drawRoundRect(x, y, barWidth, barHeight, 5, 5);

            // Value on top of bar
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String valStr = String.valueOf((int) values[i]);
            FontMetrics fm2 = g2.getFontMetrics();
            int textWidth = fm2.stringWidth(valStr);
            g2.drawString(valStr, x + (barWidth - textWidth) / 2, y - 5);

            // Label below bar
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            fm2 = g2.getFontMetrics();
            textWidth = fm2.stringWidth(labels[i]);
            g2.drawString(labels[i], x + (barWidth - textWidth) / 2, chartY + chartHeight + 18);
        }
    }
}