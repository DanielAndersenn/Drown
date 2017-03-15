package application;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIController {
	//GUI Related variables
	@FXML
	private ImageView originalFrame;
	
	@FXML
	private ImageView maskedFrame;
	
	@FXML
	private ImageView morphFrame;
	
	@FXML
	private Button droneConnect;
	
	//Drone related variables
	private boolean droneActive;
	private ARDrone drone;
	
	//Other variables
	BufferedImage newFrame;

	//Timers
	private ScheduledExecutorService origFrameTimer;
	private ScheduledExecutorService maskedFrameTimer;
	private ScheduledExecutorService morphFrameTimer;
	
	//Method linked to onClick button "Connect to drone"
	@FXML
	private void connectDrone() {
	try {
		drone = new ARDrone();
		System.out.println("Initialized drone");
		drone.setHorizontalCamera();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);	
		drone.getVideoManager().addImageListener((BufferedImage image) -> {newFrame = image;});
		//System.out.println("Før sleep");
		Thread.sleep(5000);
		//System.out.println("Efter sleep");
		
        Runnable origFrameGrabber = () -> {
            BufferedImage imageToShow = processImage("orig");
            updateImageView(originalFrame, SwingFXUtils.toFXImage(imageToShow, null));
        };
		
		this.origFrameTimer = Executors.newSingleThreadScheduledExecutor();
		this.origFrameTimer.scheduleAtFixedRate(origFrameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		
        Runnable maskedFrameGrabber = () -> {
            BufferedImage imageToShow = processImage("mask");
            updateImageView(maskedFrame, SwingFXUtils.toFXImage(imageToShow, null));
        };
		
		this.maskedFrameTimer = Executors.newSingleThreadScheduledExecutor();
		this.maskedFrameTimer.scheduleAtFixedRate(maskedFrameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		
        Runnable morphFrameGrabber = () -> {
            BufferedImage imageToShow = processImage("morph");
            updateImageView(morphFrame, SwingFXUtils.toFXImage(imageToShow, null));
        };
		
		this.morphFrameTimer = Executors.newSingleThreadScheduledExecutor();
		this.morphFrameTimer.scheduleAtFixedRate(morphFrameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		
		
		
	}catch(Exception e) {
		 e.printStackTrace();

         if (drone != null) {
             drone.stop();
         }
         System.exit(-1);
	}
		
		
		
	}
	//REWRITE
	private BufferedImage processImage(String type) {
		
		BufferedImage imageToShow = null;

		
		if(newFrame != null) {
			
			if(type.equals("mask")) {
			
			Mat frame = new Mat();
			frame = Utilities.bufferedImage2Mat(newFrame);
			Mat blurredImage = new Mat();
			Mat hsvImage = new Mat();
			Mat mask = new Mat();
			
			Imgproc.blur(frame, blurredImage, new Size(7,7));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
			
			Scalar minValues = new Scalar(20, 60, 50);
			Scalar maxValues = new Scalar(50, 200, 255);
			Core.inRange(hsvImage, minValues, maxValues, mask);
			return Utilities.matToBufferedImage(mask);
			
			}
			
			imageToShow = newFrame;
		}
		
		if(type.equals("morph")) {
			
			Mat frame = new Mat();
			frame = Utilities.bufferedImage2Mat(newFrame);
			Mat blurredImage = new Mat();
			Mat hsvImage = new Mat();
			Mat mask = new Mat();
			Mat morphOutput = new Mat();
			
			Imgproc.blur(frame, blurredImage, new Size(7,7));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
			
			Scalar minValues = new Scalar(24, 113, 89);
			Scalar maxValues = new Scalar(36, 255, 255);
			Core.inRange(hsvImage, minValues, maxValues, mask);
			
			Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
			Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
			
			Imgproc.erode(mask, morphOutput, erodeElement);
			Imgproc.erode(morphOutput, morphOutput, erodeElement);
			
			Imgproc.dilate(morphOutput, morphOutput, dilateElement);
			Imgproc.dilate(morphOutput, morphOutput, dilateElement);
			
			return Utilities.matToBufferedImage(morphOutput);
		}
			if(type.equals("orig")) {
				
			Mat frame = new Mat();
			frame = Utilities.bufferedImage2Mat(newFrame);
			Mat blurredImage = new Mat();
			Mat hsvImage = new Mat();
			Mat mask = new Mat();
			Mat morphOutput = new Mat();
			
			Imgproc.blur(frame, blurredImage, new Size(7,7));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
			
			Scalar minValues = new Scalar(24, 113, 89);
			Scalar maxValues = new Scalar(36, 255, 255);
			Core.inRange(hsvImage, minValues, maxValues, mask);
			
			Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
			Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
			
			Imgproc.erode(mask, morphOutput, erodeElement);
			Imgproc.erode(morphOutput, morphOutput, erodeElement);
			
			Imgproc.dilate(morphOutput, morphOutput, dilateElement);
			Imgproc.dilate(morphOutput, morphOutput, dilateElement);
			
			frame = findAndDrawBalls(morphOutput, frame);
				
			return Utilities.matToBufferedImage(frame);
			}
			
		
		
		
		
		return imageToShow;
		
	}
	
	
	private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {
		
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierachy = new Mat();
		
		Imgproc.findContours(maskedImage, contours, hierachy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		
		if(hierachy.size().height > 0 && hierachy.size().width > 0) {
			for(int i = 0; i >= 0; i = (int) hierachy.get(0, i) [0])
			{
				Imgproc.drawContours(frame, contours, i, new Scalar(250, 0 ,0));
			}
		}
		return frame;
	}
	
	private void updateImageView(ImageView view, Image image)
	{
		Utilities.onFXThread(view.imageProperty(), image);
	}
	
	private void stopAcquisition() {
		System.out.println("Hej");
	}

	
	protected void setClosed()
	{
		this.stopAcquisition();
	}	
	
}
