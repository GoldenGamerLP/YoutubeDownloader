package me.alex.youtubedownloader.listener;

import me.alex.youtubedownloader.YoutubeUI;
import me.alex.youtubedownloader.models.YoutubeResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class ResultListSelectionListener implements ListSelectionListener {
    private final YoutubeUI youtubeUI;

    public ResultListSelectionListener(YoutubeUI youtubeUI) {
        this.youtubeUI = youtubeUI;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        List<YoutubeResult> selectedValuesList = youtubeUI.getResultList().getSelectedValuesList();
        JLabel statusLabel = youtubeUI.getStatusLabel();
        if (selectedValuesList.size() == 1) {
            YoutubeResult result = selectedValuesList.get(0);
            statusLabel.setText(result.getTitle());
        } else if (selectedValuesList.size() > 1) {
            statusLabel.setText(selectedValuesList.size() + " items selected");
        } else {
            statusLabel.setText("");
        }
    }
}
