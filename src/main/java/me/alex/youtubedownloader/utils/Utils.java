package me.alex.youtubedownloader.utils;

import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo;
import com.github.kiulian.downloader.downloader.request.RequestSearchResult;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.playlist.PlaylistInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistVideoDetails;
import com.github.kiulian.downloader.model.search.SearchResultItem;
import com.github.kiulian.downloader.model.search.field.TypeField;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import me.alex.youtubedownloader.models.YoutubeResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static me.alex.youtubedownloader.constants.Constants.globalExecutor;

public class Utils {

    private final YoutubeDownloader yt;

    public Utils() {
        Config config = new Config.Builder()
                .maxRetries(2)
                .executorService(globalExecutor)
                .build();

        yt = new YoutubeDownloader(config);
    }


    public ImageIcon getLargeThumbnail(SearchResultItem searchResultItem) {
        String thumbnailUrl = switch (searchResultItem.type()) {
            case VIDEO -> searchResultItem.asVideo().thumbnails().get(0);
            case PLAYLIST -> searchResultItem.asPlaylist().thumbnails().get(0);
            default -> throw new IllegalStateException("Unexpected value: " + searchResultItem.type());
        };

        Image image;

        try {
            URL url = URI.create(thumbnailUrl.split("\\?")[0].replace("maxresdefault", "hqdefault"))
                    .toURL();

            BufferedImage bi = ImageIO.read(url);

            double aspectRatio = (double) bi.getWidth() / (double) bi.getHeight();
            int width = 175;
            int height = (int) (width / aspectRatio);

            image = bi.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ImageIcon(image);
    }

    public String formatMS(Long ms) {
        //Format the milliseconds to seconds and 3 decimal places (e.g. 1.234 seconds)
        return String.format("%.3f seconds", (double) ms / 1000);
    }

    public CompletableFuture<Void> downloadAll(List<YoutubeResult> urls, String format, Path location, TriConsumer<Integer, Integer, Integer> loading) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        return getVideoIds(urls).thenComposeAsync(strings -> {
            final AtomicInteger percent = new AtomicInteger(0);
            final AtomicInteger done = new AtomicInteger(0);
            final int size = strings.size();

            for (String string : strings) {
                CompletableFuture<File> ft = download(
                        string,
                        format,
                        location,
                        integer -> loading.accept(size, done.get(), percent.getAndIncrement() / size)
                );

                ft.thenRun(done::getAndIncrement);

                futures.add(ft);
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }, globalExecutor);
    }

    public CompletableFuture<List<String>> getVideoIds(List<YoutubeResult> list) {
        List<String> videos = new ArrayList<>();
        List<YoutubeResult> playlists = new ArrayList<>();

        for (YoutubeResult result : list) {
            switch (result.getItem().type()) {
                case VIDEO -> videos.add(result.getItem().asVideo().videoId());
                case PLAYLIST -> playlists.add(result);
            }
        }

        if (!videos.isEmpty() && playlists.isEmpty()) return CompletableFuture.completedFuture(videos);

        List<CompletableFuture<?>> playlist = new ArrayList<>();

        for (YoutubeResult result : playlists) {
            CompletableFuture<PlaylistInfo> ft = getPlayListInfo(result.getItem().asPlaylist().playlistId());
            ft.thenAccept(playlistInfo -> {
                for (PlaylistVideoDetails video : playlistInfo.videos()) {
                    videos.add(video.videoId());
                }
            });
            playlist.add(ft);
        }

        return CompletableFuture.allOf(playlist.toArray(CompletableFuture[]::new)).thenApplyAsync(v -> videos, globalExecutor);
    }

    public CompletableFuture<File> download(String videoID, String format, Path location, Consumer<Integer> loading) {
        CompletableFuture<VideoInfo> info = getVideoInfo(videoID);
        CompletableFuture<File> downloadFuture = new CompletableFuture<>();

        info.thenAcceptAsync(videoInfo -> {

            Format sourceFormat = switch (format) {
                case "mp3" -> videoInfo.bestAudioFormat();
                case "mp4" -> videoInfo.bestVideoWithAudioFormat();
                default -> throw new IllegalStateException("Unexpected value: " + format);
            };

            RequestVideoFileDownload download = new RequestVideoFileDownload(sourceFormat);
            download.overwriteIfExists(true);
            download.saveTo(location.toFile());
            download.renameTo(videoInfo.details().title() + "." + format);
            download.callback(new YoutubeProgressCallback<>() {
                @Override
                public void onDownloading(int progress) {
                    loading.accept(progress);
                }

                @Override
                public void onFinished(File data) {
                    downloadFuture.complete(data);
                }

                @Override
                public void onError(Throwable throwable) {
                }
            });

            Response<File> res = yt.downloadVideoFile(download);

            switch (res.status()) {
                case downloading -> System.out.println("Downloading " + videoInfo.details().title() + "." + format);
                case error -> {
                    downloadFuture.completeExceptionally(res.error());
                    System.out.println("Failed to download " + videoInfo.details().title() + "." + format);
                    System.out.println(res.error().getMessage());
                }
                case completed -> {
                    System.out.println("Downloaded " + videoInfo.details().title() + "." + format);
                }
                case canceled -> {
                    downloadFuture.completeExceptionally(new CancellationException("Download canceled"));
                    System.out.println("Canceled download of " + videoInfo.details().title() + "." + format);
                }
            }
        }, globalExecutor);

        return downloadFuture;
    }

    public CompletableFuture<List<CompletableFuture<YoutubeResult>>> lookUp(CompletableFuture<List<SearchResultItem>> future) {
        return future.thenApplyAsync(searchResultItems -> {
            List<CompletableFuture<YoutubeResult>> results = new ArrayList<>();

            for (SearchResultItem searchResultItem : searchResultItems) {
                CompletableFuture<YoutubeResult> rs = CompletableFuture
                        .supplyAsync(() -> this.getLargeThumbnail(searchResultItem), globalExecutor)
                        .thenApply(imageIcon -> new YoutubeResult(searchResultItem, imageIcon));
                results.add(rs);
            }

            return results;
        }, globalExecutor);
    }

    public CompletableFuture<List<SearchResultItem>> searchFor(String query, TypeField typeField) {
        return CompletableFuture.supplyAsync(() -> {
            RequestSearchResult searchResult = new RequestSearchResult(query);

            searchResult.type(typeField);

            return yt.search(searchResult).data().items();
        }, globalExecutor);
    }

    public CompletableFuture<VideoInfo> getVideoInfo(String videoID) {
        CompletableFuture<VideoInfo> future;
        RequestVideoInfo request = new RequestVideoInfo(videoID);

        future = CompletableFuture.supplyAsync(() -> {
            Response<VideoInfo> vid = yt.getVideoInfo(request);
            if (!vid.ok()) throw new RuntimeException(vid.error());

            return vid.data();
        }, globalExecutor);

        return future;
    }

    public CompletableFuture<PlaylistInfo> getPlayListInfo(String playlistID) {
        RequestPlaylistInfo request = new RequestPlaylistInfo(playlistID);

        return CompletableFuture.supplyAsync(() -> yt.getPlaylistInfo(request).data(), globalExecutor);
    }
}
