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
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;

import application.autonomy.CMDQueue;
import application.autonomy.CommandHandler;
import application.listeners.AttitudeListener;
import application.listeners.BatteryListener;
import application.listeners.ErrorListener;
import application.listeners.VideoListener;
import application.utilities.Utilities;
import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.navdata.NavDataManager;
import de.yadrone.base.video.ImageListener;
import de.yadrone.base.video.xuggler.XugglerDecoder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController  {
	// GUI Related variables
	// Imageviews
	@FXML
	private ImageView mainIW;
	@FXML
	private ImageView maskIW;
	@FXML
	private ImageView morphIW;

	// Buttons
	@FXML
	private Button droneConnect;
	@FXML
	private Button connectWB;
	@FXML
	private Button updateHSVb;
	@FXML
	private Button landDroneField;
	@FXML
	private Button startAI;

	// Text Fields
	@FXML
	private TextField minH;
	@FXML
	private TextField minS;
	@FXML
	private TextField minV;
	@FXML
	private TextField maxH;
	@FXML
	private TextField maxS;
	@FXML
	private TextField maxV;

	@FXML
	TextArea droneData;
	
	//TextField
	@FXML
	private Label navLabel;
	@FXML
	private Label batteryLabel;

	// Drone related variables
	private boolean droneActive;
	private boolean objectTracked;
	public IARDrone drone;
	DroneController dc;
	public CMDQueue cmdQueue;

	// Other variables
	BufferedImage newFrame;
	private VideoCapture capture = new VideoCapture();
	private static int cameraId = 0;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	// Scalar values for webcam v real ring
	// Scalar minValues = new Scalar(1, 0, 0);
	// Scalar maxValues = new Scalar(6, 150, 160);

	// Scalar values for webcam v paper ring
	//Scalar minValues = new Scalar(1, 70, 70);
	//Scalar maxValues = new Scalar(5, 255, 255);
	
	//Scalar values for dronecam v paper ring
	Scalar minValues = new Scalar(1, 50, 125);
	Scalar maxValues = new Scalar(4, 150, 150);
	
	
	// Timers
	private ScheduledExecutorService frameGrabTimer;
	private ScheduledExecutorService houghTimer;
	private ScheduledExecutorService trackStatusTimer;
	
	//

	public MainController() {
		
		
		
		//Runnable to grab a frame every 33 ms 
		Runnable frameGrabber = () -> {
			processImage();
		};

		this.frameGrabTimer = Executors.newSingleThreadScheduledExecutor();
		this.frameGrabTimer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

		//Runnable to grab a frame and render circles every 5 seconds
		Runnable houghGrabber = () -> {
			findAndDrawCircle();
		};

		this.houghTimer = Executors.newSingleThreadScheduledExecutor();
		this.houghTimer.scheduleAtFixedRate(houghGrabber, 20, 5, TimeUnit.SECONDS);

	}
	
	// Method linked to onClick button "Start AI"
	@FXML
	private void startAI() {
		cmdQueue = new CMDQueue(this, new CommandHandler(this));
		cmdQueue.start(500);
		
		dc = new DroneController(drone,this,cmdQueue);

		//QR reader
		QRController qc = new QRController();
		qc.addListener(dc);
		drone.getVideoManager().addImageListener(qc);
		
		dc.start();
		
		/*
		System.out.println("Boolean from .add: " + cmdQueue.add(CMDQueue.CommandType.TAKEOFF, 0, 0));
		cmdQueue.add(CMDQueue.CommandType.HOVER, 0, 10000);
		cmdQueue.add(CMDQueue.CommandType.MOVELEFT, 10, 1000);
		cmdQueue.add(CMDQueue.CommandType.MOVERIGHT, 10, 1000);
		cmdQueue.add(CMDQueue.CommandType.LAND, 0, 0);
		cmdQueue.printQueuedCmds();
		*/
	}

	// Method linked to onClick button "Connect to drone"
	@FXML
	private void connectDrone() {
		
		refreshHSVUI();		
		
		/*   
		 *  Drone logic
		 */
		try {
			drone = new ARDrone("192.168.1.1", new XugglerDecoder());
			System.out.println("Initialized drone");
//			logWrite("Initialized drone");
			drone.start();
			//Add ErrorListener
			drone.addExceptionListener(new ErrorListener(this));


			//Configure drone
			drone.getCommandManager().setOutdoor(false, true);
			drone.setMaxAltitude(2800);
			drone.setMinAltitude(1500);

			drone.getVideoManager().addImageListener(new VideoListener(this));

			
			NavDataManager navData =  drone.getNavDataManager();
			navData.addAttitudeListener(new AttitudeListener(this));
			navData.addBatteryListener(new BatteryListener(this));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	// Method linked to onClick button "Connect to Webcam"
	@FXML
	private void connectWB() {
		
		batteryLabel.setText("Cancer");
		
		refreshHSVUI();
		this.capture.open(cameraId);

		logWrite("Connected to webcam!");

		logWrite("Started proces to grab frames");

		Runnable frameGrabber = () -> {
			grabFrame();
			processImage();
		};

		this.frameGrabTimer = Executors.newSingleThreadScheduledExecutor();
		this.frameGrabTimer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
		Runnable houghGrabber = () -> {
			findAndDrawCircle();
		};

		this.houghTimer = Executors.newSingleThreadScheduledExecutor();
		this.houghTimer.scheduleAtFixedRate(houghGrabber, 5, 5, TimeUnit.SECONDS);

	}
	
	@FXML
	private void landDrone() {
		drone.stop();
	
	}

	// Method linked to onClick button "Update HSV values"
	@FXML
	private void updateHSV() {

		minValues = new Scalar(Double.valueOf(minH.getText()), Double.valueOf(minS.getText()),
				Double.valueOf(minV.getText()));
		maxValues = new Scalar(Double.valueOf(maxH.getText()), Double.valueOf(maxS.getText()),
				Double.valueOf(maxV.getText()));

		refreshHSVUI();

	}

	private void grabFrame() {
		// init everything
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		newFrame = Utilities.matToBufferedImage(frame);
	}

	private void processImage() {

		BufferedImage mainFrame;
		BufferedImage morphedFrame;
		BufferedImage maskFrame;

		if (newFrame != null) {

			Mat frame = new Mat();
			frame = Utilities.bufferedImage2Mat(newFrame);
			Mat blurredImage = new Mat();
			Mat hsvImage = new Mat();
			Mat mask = new Mat();
			Mat morphOutput = new Mat();

			//Core.flip(frame, frame, 1);

			Imgproc.blur(frame, blurredImage, new Size(7, 7));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
			// 20,50

			Core.inRange(hsvImage, minValues, maxValues, mask);

			maskFrame = Utilities.matToBufferedImage(mask);
			updateImageView(maskIW, SwingFXUtils.toFXImage(maskFrame, null));

			mainFrame = Utilities.matToBufferedImage(frame);
			updateImageView(morphIW, SwingFXUtils.toFXImage(mainFrame, null));

		}

	}

	private void findAndDrawCircle() {
		System.out.println("Entered findAndDrawCircle");
		BufferedImage houghFrame;
		Mat frame = new Mat();
		Mat blurredImage = new Mat();
		Mat hsvImage = new Mat();
		Mat mask = new Mat();
		Mat hough = new Mat();
		frame = Utilities.bufferedImage2Mat(newFrame);
		if (newFrame != null) {

			Core.flip(frame, frame, 1);

			Imgproc.blur(frame, blurredImage, new Size(3, 3));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

			Core.inRange(hsvImage, minValues, maxValues, mask);
			// Imgproc.cvtColor(mask, hough, Imgproc.COLOR_BGR2GRAY);

			Mat circles = new Mat();
			Imgproc.HoughCircles(mask, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 200, 20, 30, 200);
			System.out.println(circles);

			System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
			double x = 0.0;
			double y = 0.0;
			int r = 0;

			for (int i = 0; i < circles.rows(); i++) {
				System.out.println("Er vi i loop?");
				double[] data = circles.get(i, 0);
				for (int j = 0; j < data.length; j++) {
					System.out.println("Er vi i loop2?");
					x = data[0];
					y = data[1];
					r = (int) data[2];
					System.out.println("Value of x: " + x + " y: " + y + " r: " + r);
				}
			}

			Point center = new Point(x, y);

			Imgproc.circle(frame, center, r, new Scalar(255, 255, 255), 15);
			Imgproc.circle(frame, center, 3, new Scalar(0, 0, 0), -1);

			houghFrame = Utilities.matToBufferedImage(frame);
			updateImageView(mainIW, SwingFXUtils.toFXImage(houghFrame, null));
		}
	}

	private void updateImageView(ImageView view, Image image) {
		Utilities.onFXThread(view.imageProperty(), image);
	}

	private void stopAcquisition() {
		
		dc.stopController();
		
		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
		}

		try {
			// stop the timers
			this.frameGrabTimer.shutdown();
			this.frameGrabTimer.awaitTermination(33, TimeUnit.MILLISECONDS);
			this.trackStatusTimer.shutdown();
			this.trackStatusTimer.awaitTermination(33, TimeUnit.MILLISECONDS);

		} catch (InterruptedException e) {
			// log any exception
			System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
		}

		System.exit(1);

	}

	public void refreshHSVUI() {

		minH.setText(String.valueOf((int) minValues.val[0]));
		minS.setText(String.valueOf((int) minValues.val[1]));
		minV.setText(String.valueOf((int) minValues.val[2]));
		maxH.setText(String.valueOf((int) maxValues.val[0]));
		maxS.setText(String.valueOf((int) maxValues.val[1]));
		maxV.setText(String.valueOf((int) maxValues.val[2]));

		logWrite("H between: " + String.valueOf((int) minValues.val[0]) + " - "
				+ String.valueOf((int) maxValues.val[0]));
		logWrite("S between: " + String.valueOf((int) minValues.val[1]) + " - "
				+ String.valueOf((int) maxValues.val[1]));
		logWrite("V between: " + String.valueOf((int) minValues.val[2]) + " - "
				+ String.valueOf((int) maxValues.val[2]));
	}

	protected void setClosed() {
		this.stopAcquisition();
	}
	
	public IARDrone getDrone(){
		return drone;
	}

	public void logWrite(String message) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		droneData.appendText("\n" + sdf.format(ts) + ": " + message);
	}

	public void updateNavigationInfo(float pitch, float roll, float yaw) {
		StringProperty value = new SimpleStringProperty("Pitch: " + pitch + " Roll: " + roll + " Yaw:" + yaw);
		navLabel.textProperty().bind(value);
		
	}
	
	public void updateBatteryLabel(int i) {
		
		StringProperty value = new SimpleStringProperty("Battery: " + i + "%");
		batteryLabel.textProperty().bind(value);
		
	}

	public void updateDroneError(ARDroneException e) {
		logWrite("Drone threw error: " + e);
		
	}

	public void updateNewFrame(BufferedImage bi) {
		newFrame = bi;
		
	}

}
