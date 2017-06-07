package application.listeners;

import java.awt.image.BufferedImage;

import application.MainController;
import de.yadrone.base.video.ImageListener;

public class VideoListener implements ImageListener
{
    private final MainController controller;

    public VideoListener(MainController controller) {
        this.controller = controller;
    }
    
    @Override
    public void imageUpdated(BufferedImage bi) {
        controller.updateNewFrame(bi);
    }
}