package com.college.feedback.ui;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Custom TableCellRenderer to display ratings visually using pure Swing JProgressBar.
 * Layer: UI Component
 */
public class RatingBarRenderer extends JProgressBar implements TableCellRenderer {

    private static final Color COLOR_EXCELLENT = new Color(46, 125, 50);  // Green (>= 4.0)
    private static final Color COLOR_AVERAGE   = new Color(239, 108, 0);  // Amber (>= 3.0)
    private static final Color COLOR_CRITICAL  = new Color(198, 40, 40);  // Red (< 3.0)

    public RatingBarRenderer() {
        super(0, 500); // 0.00 to 5.00 scaled by 100
        setStringPainted(true);
        setBorderPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value instanceof Number) {
            double rating = ((Number) value).doubleValue();
            setValue((int) Math.round(rating * 100));
            setString(String.format("%.2f / 5.0", rating));

            if (rating >= 4.0) {
                setForeground(COLOR_EXCELLENT);
            } else if (rating >= 3.0) {
                setForeground(COLOR_AVERAGE);
            } else {
                setForeground(COLOR_CRITICAL);
            }
        } else {
            setValue(0);
            setString(value != null ? value.toString() : "N/A");
            setForeground(Color.GRAY);
        }

        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        return this;
    }
}
