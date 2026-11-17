package klinik.view.component;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import klinik.view.theme.DesignSystem;

/**
 * Panel area tabel berbasis CardLayout yang mengganti tampilan antara
 * tabel data (TABLE), kosong (EMPTY), memuat (LOADING), gagal (ERROR), dan tanpa hasil (NO_RESULTS).
 */
public class SwissTablePanel extends JPanel {
    public enum State {
        TABLE,
        EMPTY,
        LOADING,
        ERROR,
        NO_RESULTS
    }

    private final CardLayout cardLayout = new CardLayout();
    private final JTable table;
    private final Set<Integer> monoColumns = new HashSet<>();
    private final Set<Integer> rightAlignedColumns = new HashSet<>();
    private int primaryBoldColumn = 2;
    private int hoveredRow = -1;

    private final JLabel lblEmptyTitle = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblEmptyDesc = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblLoadingTitle = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblLoadingDesc = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblErrorTitle = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblErrorDesc = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblNoResultTitle = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblNoResultDesc = new JLabel("", SwingConstants.CENTER);

    private final PillButton btnRetry = new PillButton("Coba lagi", PillButton.Variant.OUTLINE);
    private final PillButton btnClearSearch = new PillButton("Kosongkan pencarian", PillButton.Variant.OUTLINE);

    public SwissTablePanel(TableModel model, String[] previewHeaders) {
        setLayout(cardLayout);
        setBackground(DesignSystem.BG_CARD);

        table = new JTable(model);
        table.setBackground(DesignSystem.BG_CARD);
        table.setForeground(DesignSystem.TEXT_PRIMARY);
        table.setFont(DesignSystem.fontRegular(13f));
        table.setRowHeight(42);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(DesignSystem.BORDER_SUBTLE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setFocusable(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setBackground(DesignSystem.BG_CARD);
        header.setForeground(DesignSystem.TEXT_SECONDARY);
        header.setFont(DesignSystem.fontRegular(12f));
        header.setUI(new javax.swing.plaf.basic.BasicTableHeaderUI() {
            @Override
            public Dimension getPreferredSize(JComponent c) {
                Dimension d = super.getPreferredSize(c);
                return new Dimension(d.width, 34);
            }
        });
        header.setBorder(new MatteBorder(0, 0, 1, 0, DesignSystem.TEXT_PRIMARY));
        header.setDefaultRenderer(new SwissHeaderRenderer());

        table.setDefaultRenderer(Object.class, new SwissCellRenderer());

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r != hoveredRow) {
                    hoveredRow = r;
                    table.setCursor(r >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.setCursor(Cursor.getDefaultCursor());
                table.repaint();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(header);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(DesignSystem.BG_CARD);
        if (scrollPane.getColumnHeader() != null) {
            scrollPane.getColumnHeader().setBackground(DesignSystem.BG_CARD);
        }
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, State.TABLE.name());
        add(createStateCard(previewHeaders, lblEmptyTitle, lblEmptyDesc, null, false), State.EMPTY.name());
        add(createStateCard(previewHeaders, lblLoadingTitle, lblLoadingDesc, null, false), State.LOADING.name());
        add(createStateCard(previewHeaders, lblErrorTitle, lblErrorDesc, btnRetry, true), State.ERROR.name());
        add(createStateCard(previewHeaders, lblNoResultTitle, lblNoResultDesc, btnClearSearch, false), State.NO_RESULTS.name());

        showState(State.LOADING);
    }

    private JPanel createStateCard(
            String[] previewHeaders,
            JLabel titleLabel,
            JLabel descLabel,
            PillButton actionBtn,
            boolean isError
    ) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(DesignSystem.BG_CARD);

        if (previewHeaders != null && previewHeaders.length > 0) {
            JPanel headerStrip = new JPanel(new java.awt.GridLayout(1, previewHeaders.length));
            headerStrip.setBackground(DesignSystem.BG_CARD);
            headerStrip.setPreferredSize(new Dimension(0, 34));
            headerStrip.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, DesignSystem.TEXT_PRIMARY),
                    new EmptyBorder(0, 32, 0, 12)
            ));
            for (String h : previewHeaders) {
                JLabel lbl = new JLabel(h);
                lbl.setFont(DesignSystem.fontRegular(12f));
                lbl.setForeground(DesignSystem.TEXT_SECONDARY);
                headerStrip.add(lbl);
            }
            wrapper.add(headerStrip, BorderLayout.NORTH);
        }

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(DesignSystem.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 24, 4, 24);

        titleLabel.setFont(DesignSystem.fontSemiBold(15f));
        titleLabel.setForeground(isError ? DesignSystem.DANGER : DesignSystem.TEXT_PRIMARY);
        gbc.gridy = 0;
        center.add(titleLabel, gbc);

        descLabel.setFont(DesignSystem.fontRegular(13f));
        descLabel.setForeground(DesignSystem.TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 24, actionBtn != null ? 16 : 4, 24);
        center.add(descLabel, gbc);

        if (actionBtn != null) {
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 24, 4, 24);
            center.add(actionBtn, gbc);
        }

        wrapper.add(center, BorderLayout.CENTER);
        return wrapper;
    }

    public JTable getTable() {
        return table;
    }

    public PillButton getRetryButton() {
        return btnRetry;
    }

    public PillButton getClearSearchButton() {
        return btnClearSearch;
    }

    public void configureColumn(int index, int minWidth, int prefWidth, int maxWidth, boolean mono, boolean rightAligned) {
        TableColumn col = table.getColumnModel().getColumn(index);
        if (minWidth > 0) {
            col.setMinWidth(minWidth);
        }
        if (prefWidth > 0) {
            col.setPreferredWidth(prefWidth);
        }
        if (maxWidth > 0) {
            col.setMaxWidth(maxWidth);
        } else {
            col.setMaxWidth(Integer.MAX_VALUE);
        }
        if (mono) {
            monoColumns.add(index);
        }
        if (rightAligned) {
            rightAlignedColumns.add(index);
        }
    }

    public void setPrimaryBoldColumn(int colIndex) {
        this.primaryBoldColumn = colIndex;
    }

    public void configureTexts(String entityWord, String emptyInstruction) {
        lblEmptyTitle.setText("Belum ada data " + entityWord);
        lblEmptyDesc.setText(htmlCenter(emptyInstruction));

        lblLoadingTitle.setText("Memuat data " + entityWord + "...");
        lblLoadingDesc.setText(htmlCenter(
                "Aplikasi sedang membaca tabel " + entityWord + " dari klinik.db. Simpan, Ubah, dan Hapus nonaktif sampai selesai."
        ));

        lblErrorTitle.setText("Data " + entityWord + " tidak bisa dimuat");
        lblErrorDesc.setText(htmlCenter(
                "Aplikasi tidak menemukan klinik.db di folder data. Pastikan file ada, lalu klik Coba lagi."
        ));

        lblNoResultTitle.setText("Tidak ada " + entityWord + " dengan nama itu");
        lblNoResultDesc.setText(htmlCenter(
                "Periksa ejaan, atau kosongkan kolom cari untuk melihat semua " + entityWord + "."
        ));
    }

    public void setErrorDetail(String message) {
        if (message != null && !message.isBlank()) {
            lblErrorDesc.setText(htmlCenter(message + " Pastikan file ada, lalu klik Coba lagi."));
        }
    }

    public void showState(State state) {
        cardLayout.show(this, state.name());
    }

    private static String htmlCenter(String text) {
        return "<html><div style='text-align:center; width:430px;'>" + text + "</div></html>";
    }

    private class SwissHeaderRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable tbl,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel lbl = new JLabel(value != null ? value.toString() : "");
            lbl.setOpaque(true);
            lbl.setBackground(DesignSystem.BG_CARD);
            lbl.setForeground(DesignSystem.TEXT_SECONDARY);
            lbl.setFont(DesignSystem.fontRegular(12f));
            boolean right = rightAlignedColumns.contains(column);
            lbl.setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(0, column == 0 ? 4 : 10, 0, 10));
            return lbl;
        }
    }

    private class SwissCellRenderer extends DefaultTableCellRenderer {
        private final DotIndicatorPanel dotPanel = new DotIndicatorPanel();

        @Override
        public Component getTableCellRendererComponent(
                JTable tbl,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            if (column == 0) {
                dotPanel.configure(isSelected, row == hoveredRow);
                return dotPanel;
            }
            super.getTableCellRendererComponent(tbl, value, false, false, row, column);

            Color bg = isSelected
                    ? DesignSystem.ROW_SELECTED
                    : (row == hoveredRow ? DesignSystem.ROW_HOVER : DesignSystem.BG_CARD);
            setBackground(bg);
            setForeground(DesignSystem.TEXT_PRIMARY);

            boolean mono = monoColumns.contains(column);
            boolean right = rightAlignedColumns.contains(column);
            if (mono) {
                setFont(isSelected ? DesignSystem.monoMedium(12.5f) : DesignSystem.monoRegular(12.5f));
            } else if (isSelected && column == primaryBoldColumn) {
                setFont(DesignSystem.fontSemiBold(13f));
            } else {
                setFont(DesignSystem.fontRegular(13f));
            }

            setHorizontalAlignment(right ? SwingConstants.RIGHT : SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    private static class DotIndicatorPanel extends JPanel {
        private boolean selected;
        private boolean hovered;

        DotIndicatorPanel() {
            setOpaque(true);
        }

        void configure(boolean selected, boolean hovered) {
            this.selected = selected;
            this.hovered = hovered;
            setBackground(selected
                    ? DesignSystem.ROW_SELECTED
                    : (hovered ? DesignSystem.ROW_HOVER : DesignSystem.BG_CARD));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (selected) {
                Graphics2D g2 = (Graphics2D) g.create();
                DesignSystem.applyQualityHints(g2);
                g2.setColor(DesignSystem.TEXT_PRIMARY);
                int r = 7;
                int x = (getWidth() - r) / 2;
                int y = (getHeight() - r) / 2;
                g2.fillOval(x, y, r, r);
                g2.dispose();
            }
        }
    }
}
