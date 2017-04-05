package application;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.opencv.videoio.VideoCapture;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
	
	@FXML
	private Button connectWB;
	
	@FXML
	TextArea droneData;
	
	//Drone related variables
	private boolean droneActive;
	private boolean objectTracked;
	private ARDrone drone;
	
	
	//Other variables
	BufferedImage newFrame;
	Mat wcFrame;
	private VideoCapture capture = new VideoCapture();
	private static int cameraId = 0;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


	//Timers
	private ScheduledExecutorService origFrameTimer;
	private ScheduledExecutorService maskedFrameTimer;
	private ScheduledExecutorService morphFrameTimer;
	private ScheduledExecutorService trackStatusTimer;
	
	//PID variables
	private int center = 0;
	
	public UIController() {
		
        Runnable trackStatus = () -> {
            if(objectTracked) logWrite("Currently tracking object!");
        };
        
		this.trackStatusTimer = Executors.newSingleThreadScheduledExecutor();
		this.trackStatusTimer.scheduleAtFixedRate(trackStatus, 0, 1000, TimeUnit.MILLISECONDS);
        
	}
	
	//Method linked to onClick button "Connect to drone"
	@FXML
	private void connectDrone() {
		
		//Init PID contrøller
		center = (int) originalFrame.getFitWidth() / 2;
		
		logWrite("Value of center: " + center);
		
	try {
		drone = new ARDrone();
		System.out.println("Initialized drone");
		logWrite("Initialized drone");
		drone.setHorizontalCamera();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);	
		drone.getVideoManager().addImageListener((BufferedImage image) -> {newFrame = image;});
		logWrite("Sleeping thread for 5 sec");
		//System.out.println("Før sleep");
		Thread.sleep(5000);
		//System.out.println("Efter sleep");
		logWrite("Started proces to grab frames for main picture");
        Runnable origFrameGrabber = () -> {
            BufferedImage imageToShow = processImage("orig");
            updateImageView(originalFrame, SwingFXUtils.toFXImage(imageToShow, null));
        };
		
		this.origFrameTimer = Executors.newSingleThreadScheduledExecutor();
		this.origFrameTimer.scheduleAtFixedRate(origFrameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		
		logWrite("Started proces to grab frames for masked stream");
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
		
		droneActive = true;
		
	}catch(Exception e) {
		 e.printStackTrace();

         if (drone != null) {
             drone.stop();
         }
         System.exit(-1);
	}	
		
	}
	
	
	//Method linked to onClick button "Connect to Webcam"
	@FXML
	private void connectWB() {
		
		this.capture.open(cameraId);
		
		logWrite("Connected to webcam!");
		
		logWrite("Started proces to grab frames for main picture");
		
        Runnable origFrameGrabber = () -> {
        	grabFrame();
        	
            BufferedImage mainFrame = processImage("orig");
            BufferedImage morphedFrame = processImage("morph");
            BufferedImage maskFrame = processImage("mask");
            
            updateImageView(originalFrame, SwingFXUtils.toFXImage(mainFrame, null));
            updateImageView(maskedFrame, SwingFXUtils.toFXImage(maskFrame, null));
            updateImageView(morphFrame, SwingFXUtils.toFXImage(morphedFrame, null));
        };
		
		this.origFrameTimer = Executors.newSingleThreadScheduledExecutor();
		this.origFrameTimer.scheduleAtFixedRate(origFrameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		
		
	}
	
	private void grabFrame()
	{
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		newFrame = Utilities.matToBufferedImage(frame);
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
			//20,50
			Scalar minValues = new Scalar(160, 60, 50);
			Scalar maxValues = new Scalar(180, 200, 255);
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
			
			Scalar minValues = new Scalar(160, 113, 89);
			Scalar maxValues = new Scalar(180, 255, 255);
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
			
			Scalar minValues = new Scalar(160, 113, 89);
			Scalar maxValues = new Scalar(180, 255, 255);
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
			objectTracked = true;
			for(int i = 0; i >= 0; i = (int) hierachy.get(0, i) [0])
			{
				Imgproc.drawContours(frame, contours, i, new Scalar(250, 0 ,0));
			}
		} else {
			objectTracked = false;
			return frame;
		}
		return frame;
	}
	
	private void updateImageView(ImageView view, Image image)
	{
		Utilities.onFXThread(view.imageProperty(), image);
	}
	
	private void stopAcquisition() {
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
		
		try
		{
			// stop the timers
			this.origFrameTimer.shutdown();
			this.origFrameTimer.awaitTermination(33, TimeUnit.MILLISECONDS);

		}
		catch (InterruptedException e)
		{
			// log any exception
			System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
		}
		
		System.exit(1);
		
	}

	
	protected void setClosed()
	{
		this.stopAcquisition();
	}	
	
	public void logWrite(String message) 
	{
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		droneData.appendText("\n" + sdf.format(ts) + ": " + message);
	}
	
}
