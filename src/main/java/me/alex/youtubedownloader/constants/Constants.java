package me.alex.youtubedownloader.constants;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class Constants {

    public static final ExecutorService globalExecutor = ForkJoinPool.commonPool();

    public static final Color
            background = new Color(70, 73, 76),
            foreground = new Color(220, 220, 221),
            buttonColor = new Color(147, 129, 255),
            textFieldBackground = new Color(64, 65, 79),
            highlightedBackground = new Color(53, 55, 64);

    public static final Integer iconSize = 38;
    public static final CompletableFuture<Image>
            mainIcon = fromResource("/icons/main-icon.png", 512),
            downloadIcon = fromResource("/icons/download-icon.png", iconSize),
            folderIcon = fromResource("/icons/folder-icon.png", iconSize),
            searchIcon = fromResource("/icons/search-icon.png", iconSize),
            trashIcon = fromResource("/icons/trash-icon.png", iconSize);


    private static CompletableFuture<Image> fromResource(String path, int iconSize) {
        return CompletableFuture.supplyAsync(() -> Toolkit.getDefaultToolkit()
                        .getImage(Constants.class.getResource(path))
                        .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING),
                globalExecutor
        );
    }
}
