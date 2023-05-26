package me.alex.youtubedownloader.listener;

import me.alex.youtubedownloader.YoutubeUI;
import me.alex.youtubedownloader.models.YoutubeResult;
import me.alex.youtubedownloader.utils.TriConsumer;
import me.alex.youtubedownloader.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DownloadActionListener implements ActionListener {

    private final YoutubeUI ui;
    private final Utils utils;

    public DownloadActionListener(YoutubeUI ui) {
        this.ui = ui;
        this.utils = ui.getUtils();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<YoutubeResult> selectedValuesList = ui.getResultList().getSelectedValuesList();

        if (selectedValuesList.size() == 0) {
            JOptionPane.showMessageDialog(ui, "No items selected");
            return;
        }

        if (ui.getDownloadPath() == null) {
            JOptionPane.showMessageDialog(ui, "No location selected");
            return;
        }

        if (!ui.getDownloadPath().toFile().exists()) {
            JOptionPane.showMessageDialog(ui, "Location does not exist");
            return;
        }

        if (ui.getFormatField().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(ui, "No format selected");
            return;
        }

        long ms = System.currentTimeMillis();
        JLabel statusLabel = ui.getStatusLabel();
        TriConsumer<Integer, Integer, Integer> progressListener = (max, crr, progress) ->
                statusLabel.setText("(" + crr + "/" + max + ") Downloaded " + progress + "%");
        String format = (String) ui.getFormatField().getSelectedItem();

        utils.downloadAll(selectedValuesList, format, ui.getDownloadPath(), progressListener).whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                JOptionPane.showMessageDialog(ui, throwable.getLocalizedMessage());
            }
            statusLabel.setText("Download took " + utils.formatMS(System.currentTimeMillis() - ms));
        });
    }
}
