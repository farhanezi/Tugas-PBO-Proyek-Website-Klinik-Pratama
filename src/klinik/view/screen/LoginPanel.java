package klinik.view.screen;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import klinik.view.component.PillButton;
import klinik.view.component.SharpPasswordField;
import klinik.view.component.SharpTextField;
import klinik.view.theme.DesignSystem;

/**
 * Layar 1: Login petugas menggunakan GridBagLayout terpusat di jendela.
 */
public class LoginPanel extends JPanel {
    private final SharpTextField txtUsername;
    private final SharpPasswordField txtPassword;
    private final PillButton btnMasuk;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBackground(DesignSystem.BG_CANVAS);

        JPanel centerStack = new JPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setOpaque(false);

        JLabel lblBrand = new JLabel("Klinik Pratama", SwingConstants.CENTER);
        lblBrand.setFont(DesignSystem.fontLight(38f));
        lblBrand.setForeground(DesignSystem.TEXT_PRIMARY);
        lblBrand.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk untuk mengelola data pasien dan kunjungan.", SwingConstants.CENTER);
        lblSub.setFont(DesignSystem.fontRegular(13.5f));
        lblSub.setForeground(DesignSystem.TEXT_SECONDARY);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(DesignSystem.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(DesignSystem.BORDER_SUBTLE, 1),
                new EmptyBorder(28, 30, 26, 30)
        ));
        card.setAlignmentX(CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(380, 320));
        card.setPreferredSize(new Dimension(380, 298));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(DesignSystem.fontRegular(12.5f));
        lblUser.setForeground(DesignSystem.TEXT_SECONDARY);
        lblUser.setAlignmentX(LEFT_ALIGNMENT);

        txtUsername = new SharpTextField("Masukkan username", false);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setPreferredSize(new Dimension(320, 40));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Kata sandi");
        lblPass.setFont(DesignSystem.fontRegular(12.5f));
        lblPass.setForeground(DesignSystem.TEXT_SECONDARY);
        lblPass.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new SharpPasswordField("Masukkan kata sandi");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setPreferredSize(new Dimension(320, 40));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        btnMasuk = new PillButton("Masuk", PillButton.Variant.PRIMARY);
        btnMasuk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnMasuk.setPreferredSize(new Dimension(320, 40));
        btnMasuk.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblHelp = new JLabel("Lupa kata sandi? Hubungi admin klinik.", SwingConstants.CENTER);
        lblHelp.setFont(DesignSystem.fontRegular(12f));
        lblHelp.setForeground(DesignSystem.TEXT_SECONDARY);
        lblHelp.setAlignmentX(LEFT_ALIGNMENT);
        lblHelp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(22));
        card.add(btnMasuk);
        card.add(Box.createVerticalStrut(16));
        card.add(lblHelp);

        centerStack.add(lblBrand);
        centerStack.add(Box.createVerticalStrut(8));
        centerStack.add(lblSub);
        centerStack.add(Box.createVerticalStrut(28));
        centerStack.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(24, 24, 24, 24);
        add(centerStack, gbc);

        txtUsername.addActionListener(e -> {
            if (txtPassword.getPassword().length == 0) {
                txtPassword.requestFocusInWindow();
            } else {
                btnMasuk.doClick();
            }
        });
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void clearPasswordAndFocus() {
        txtPassword.setText("");
        txtPassword.requestFocusInWindow();
    }

    public void resetForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.setError(false);
        txtPassword.setError(false);
    }

    public void setInputError(boolean userError, boolean passError) {
        txtUsername.setError(userError);
        txtPassword.setError(passError);
    }

    public void setLoading(boolean loading) {
        btnMasuk.setEnabled(!loading);
        btnMasuk.setText(loading ? "Memeriksa..." : "Masuk");
    }

    public void addLoginActionListener(ActionListener listener) {
        btnMasuk.addActionListener(listener);
        txtPassword.addActionListener(listener);
    }

    public void focusUsername() {
        txtUsername.requestFocusInWindow();
    }
}
