package me.alex.youtubedownloader.renderer;

import me.alex.youtubedownloader.models.YoutubeResult;

import javax.swing.*;
import java.awt.*;

public class ResultListCellRenderer extends JLabel implements ListCellRenderer<YoutubeResult> {

    private final JCheckBox checkBox;
    private final JLabel videoLength;

    public ResultListCellRenderer() {
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setEnabled(false);
        checkBox.setHorizontalAlignment(LEFT);
        checkBox.setVerticalAlignment(SwingConstants.CENTER);

        videoLength = new JLabel(" ");
        videoLength.setOpaque(true);
        //set the background to 60% transparent
        videoLength.setBackground(new Color(0, 0, 0, 155));
        videoLength.setHorizontalAlignment(LEFT);
        videoLength.setVerticalAlignment(BOTTOM);
        videoLength.setFont(new Font("Arial", Font.BOLD, 12));
        videoLength.setForeground(Color.WHITE);

        JPanel videoLengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        videoLengthPanel.setOpaque(false);
        videoLengthPanel.add(videoLength);

        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        setFont(new Font("Arial", Font.PLAIN, 16));

        setLayout(new BorderLayout());
        add(videoLengthPanel, BorderLayout.SOUTH);
        add(checkBox, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends YoutubeResult> list, YoutubeResult value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        String videoInfo = switch (value.getItem().type()) {
            case VIDEO -> value.getItem().asVideo().viewCountText();
            case PLAYLIST -> value.getItem().asPlaylist().videoCount() + " Videos";
            default -> "";
        };

        videoLength.setText(videoInfo);

        setIcon(value.getIcon());
        //Add the tex to the center of the label
        setText(value.getTitle());

        boolean show = isSelected || cellHasFocus;
        setBackground(show ? list.getSelectionBackground() : list.getBackground());
        setForeground(show ? list.getSelectionForeground() : list.getForeground());
        checkBox.setSelected(show);

        return this;
    }
}
