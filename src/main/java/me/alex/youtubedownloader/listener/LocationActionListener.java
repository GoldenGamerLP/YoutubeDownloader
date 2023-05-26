package me.alex.youtubedownloader.listener;

import me.alex.youtubedownloader.YoutubeUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocationActionListener implements ActionListener {
    private final YoutubeUI youtubeUI;

    public LocationActionListener(YoutubeUI youtubeUI) {
        this.youtubeUI = youtubeUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Open the location chooser for only folders
        int returnVal = youtubeUI.getLocationChooser().showOpenDialog(youtubeUI);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            youtubeUI.getLocationButton().setText(youtubeUI.getLocationChooser().getSelectedFile().getAbsolutePath());
        }
    }
}
