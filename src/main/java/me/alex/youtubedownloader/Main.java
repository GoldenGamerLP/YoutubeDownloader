package me.alex.youtubedownloader;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        System.setProperty("sun.java2d.uiScale", "1.8");
        SwingUtilities.invokeAndWait(YoutubeUI::new);
    }
}