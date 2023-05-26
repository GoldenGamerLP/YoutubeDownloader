package me.alex.youtubedownloader.listener;

import me.alex.youtubedownloader.YoutubeUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClearResultsActionListener implements ActionListener {
    private final YoutubeUI youtubeUI;

    public ClearResultsActionListener(YoutubeUI youtubeUI) {
        this.youtubeUI = youtubeUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        youtubeUI.clearResults();
    }
}
