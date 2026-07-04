package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class PieChartPanel extends JPanel {
    private String[] labels;
    private double[] values;
    private Color[] colors;
    private String title;

    public PieChartPanel(String title, String[] labels, double[] values, Color[] colors) {
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

        // Calculate total
        double total = 0;
        for (double v : values) total += v;

        if (total == 0) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.setColor(Color.GRAY);
            g2.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        // Draw title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(44, 62, 80));
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

        // Pie chart dimensions
        int pieX = 40;
        int pieY = 40;
        int pieSize = Math.min(getWidth() - 80, getHeight() - 120);
        if (pieSize > 200) pieSize = 200;

        // Center the pie
        pieX = (getWidth() - pieSize) / 2 - 60;
        pieY = 40;

        // Draw pie slices
        double startAngle = 0;
        for (int i = 0; i < values.length; i++) {
            double arcAngle = (values[i] / total) * 360;
            g2.setColor(colors[i]);
            g2.fill(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, arcAngle, Arc2D.PIE));

            // Draw border for each slice
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, arcAngle, Arc2D.PIE));

            startAngle += arcAngle;
        }

        // Draw legend on the right side
        int legendX = pieX + pieSize + 20;
        int legendY = pieY + 20;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int i = 0; i < labels.length; i++) {
            // Color box
            g2.setColor(colors[i]);
            g2.fillRoundRect(legendX, legendY + (i * 30), 15, 15, 3, 3);

            // Label text
            g2.setColor(new Color(44, 62, 80));
            String text = labels[i] + " (" + (int) values[i] + ")";
            g2.drawString(text, legendX + 22, legendY + (i * 30) + 13);
        }

        // Draw percentage labels on pie
        startAngle = 0;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        for (int i = 0; i < values.length; i++) {
            double arcAngle = (values[i] / total) * 360;
            if (values[i] > 0) {
                double midAngle = Math.toRadians(startAngle + arcAngle / 2);
                int labelX = (int) (pieX + pieSize / 2 + (pieSize / 3.5) * Math.cos(midAngle));
                int labelY = (int) (pieY + pieSize / 2 - (pieSize / 3.5) * Math.sin(midAngle));
                String pct = String.format("%.0f%%", (values[i] / total) * 100);
                g2.setColor(Color.WHITE);
                g2.drawString(pct, labelX - 10, labelY + 5);
            }
            startAngle += arcAngle;
        }
    }
}