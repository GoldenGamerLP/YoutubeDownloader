package me.alex.youtubedownloader.listener;

import com.github.kiulian.downloader.model.search.SearchResultItem;
import com.github.kiulian.downloader.model.search.field.TypeField;
import me.alex.youtubedownloader.YoutubeUI;
import me.alex.youtubedownloader.models.YoutubeResult;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchActionListener implements ActionListener {
    private final YoutubeUI youtubeUI;

    public SearchActionListener(YoutubeUI youtubeUI) {
        this.youtubeUI = youtubeUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String query = youtubeUI.getSearchField().getText();

        long ms = System.currentTimeMillis();

        youtubeUI.clearResults();
        //lock the search button and the search field
        youtubeUI.getSearchButton().setEnabled(false);
        youtubeUI.getSearchField().setEnabled(false);

        CompletableFuture<List<SearchResultItem>> future = youtubeUI.getUtils().searchFor(
                query,
                (TypeField) youtubeUI.getTypeField().getSelectedItem()
        );

        CompletableFuture<List<CompletableFuture<YoutubeResult>>> results = youtubeUI.getUtils().lookUp(future);

        results.thenAccept(list -> {
            for (CompletableFuture<YoutubeResult> result : list) {
                result.thenAccept(youtubeUI::addResult);
            }

            //unlock the search button and the search field
            youtubeUI.getSearchButton().setEnabled(true);
            youtubeUI.getSearchField().setEnabled(true);

            //Set the status label
            youtubeUI.getStatusLabel().setText("Search took" + youtubeUI.getUtils().formatMS(System.currentTimeMillis() - ms));
        });
    }
}
