package me.alex.youtubedownloader;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.model.search.field.TypeField;
import me.alex.youtubedownloader.constants.Constants;
import me.alex.youtubedownloader.listener.*;
import me.alex.youtubedownloader.models.YoutubeResult;
import me.alex.youtubedownloader.renderer.ModernScrollPane;
import me.alex.youtubedownloader.renderer.ResultListCellRenderer;
import me.alex.youtubedownloader.utils.Utils;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.nio.file.Path;

import static me.alex.youtubedownloader.constants.Constants.*;

public class YoutubeUI extends JFrame {
    private final JTextField searchField;
    private final JButton searchButton;
    private final DefaultListModel<YoutubeResult> listModel;
    private final JList<YoutubeResult> resultList;
    private final JComboBox<TypeField> typeField;
    private final JComboBox<String> formatField;
    private final JLabel statusLabel;
    private final JButton downloadButton;
    private final JButton locationButton;
    private final JFileChooser locationChooser;
    private final YoutubeDownloader yt;
    private final Utils utils;
    private final JScrollPane scrollPane;
    private final JPanel statusPanel;

    public YoutubeUI() {
        setTitle("Youtube Search Client");

        this.yt = new YoutubeDownloader();
        this.yt.getConfig().setMaxRetries(2);

        this.utils = new Utils();

        design();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // Suche oben hinzufügen
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createEmptyBorder());
        searchPanel.setBackground(highlightedBackground);
        TypeField[] typeFields = {TypeField.VIDEO, TypeField.PLAYLIST};

        searchField = new JTextField(32);
        searchField.setBackground(textFieldBackground);
        searchField.setForeground(foreground);
        //a border with a white underline
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 6, 11, 6),
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)
        ));

        searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchActionListener(this));

        typeField = new JComboBox<>(typeFields);
        typeField.setBorder(BorderFactory.createEmptyBorder());

        // Location-Chooser hinzufügen
        locationChooser = new JFileChooser("Choose Download Location");
        locationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        locationChooser.setAcceptAllFileFilterUsed(false);

        searchPanel.add(typeField);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Ergebnisliste hinzufügen
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultList.setSelectionBackground(new Color(32, 33, 35).darker());
        resultList.setSelectionForeground(foreground);
        resultList.setForeground(foreground);
        resultList.setBackground(background);
        resultList.setCellRenderer(new ResultListCellRenderer());
        resultList.addListSelectionListener(new ResultListSelectionListener(this));
        scrollPane = new ModernScrollPane(resultList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        //smooth scrolling
        scrollPane.getVerticalScrollBar().setUnitIncrement(4);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(4);

        add(scrollPane, BorderLayout.CENTER);

        //Bottom Panel
        statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        // Clear-Button unten hinzufügen
        JButton clearButton = new JButton("Clear Results");
        clearButton.setOpaque(false);
        clearButton.addActionListener(new ClearResultsActionListener(this));
        // Search-Button-Action hinzufügen

        // Status-Label hinzufügen das über alles drüber liegt und durchsichtig ist
        statusLabel = new JLabel();
        statusLabel.setForeground(foreground);
        statusLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusLabel.setOpaque(false);

        // Download-Button hinzufügen
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new DownloadActionListener(this));

        // Location-Button hinzufügen
        locationButton = new JButton("Choose Location");
        locationButton.addActionListener(new LocationActionListener(this));

        formatField = new JComboBox<>(new String[]{"mp4", "mp3"});

        // Status-Label overlay
        statusPanel.setLayout(new GridLayout());
        statusPanel.add(formatField);
        statusPanel.add(downloadButton);
        statusPanel.add(clearButton); // Clear-Button auch unten hinzufügen (siehe oben
        statusPanel.add(locationButton);
        statusPanel.add(statusLabel);

        add(statusPanel, BorderLayout.SOUTH);

        mechanics();

        EventQueue.invokeLater(() -> {
            searchField.requestFocus();
            searchField.selectAll();

            setVisible(true);
        });
    }

    private void mechanics() {
        getRootPane().setDefaultButton(searchButton);
    }

    private void design() {
        //make the ui look better

        //Set default color for all components
        UIManager.put("nimbusBase", background);
        UIManager.put("nimbusBlueGrey", buttonColor);
        UIManager.put("control", background);


        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            System.out.println("UIManager set to NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addResult(YoutubeResult result) {
        listModel.addElement(result);
    }

    public void clearResults() {
        listModel.clear();
    }

    public JList<YoutubeResult> getResultList() {
        return resultList;
    }

    public JComboBox<TypeField> getTypeField() {
        return typeField;
    }

    public JComboBox<String> getFormatField() {
        return formatField;
    }

    public Utils getUtils() {
        return utils;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public DefaultListModel<YoutubeResult> getListModel() {
        return listModel;
    }

    public JButton getDownloadButton() {
        return downloadButton;
    }

    public JButton getLocationButton() {
        return locationButton;
    }

    public JFileChooser getLocationChooser() {
        return locationChooser;
    }

    public YoutubeDownloader getYt() {
        return yt;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JPanel getStatusPanel() {
        return statusPanel;
    }

    public Path getDownloadPath() {
        return this.getLocationChooser().getSelectedFile().toPath();
    }

}
