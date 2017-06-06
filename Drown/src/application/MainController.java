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

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.video.xuggler.XugglerDecoder;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController {
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

	// Drone related variables
	private boolean droneActive;
	private boolean objectTracked;
	public IARDrone drone;

	// Other variables
	BufferedImage newFrame;
	private VideoCapture capture = new VideoCapture();
	private static int cameraId = 0;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Scalar minValues = new Scalar(1, 70, 70);
	Scalar maxValues = new Scalar(5, 255, 255);

	// Timers
	private ScheduledExecutorService frameGrabTimer;
	private ScheduledExecutorService trackStatusTimer;

	// PID variables
	private int center = 0;

	public MainController() {
		Runnable trackStatus = () -> {
			if (objectTracked)
				logWrite("Currently tracking object!");
		};

		this.trackStatusTimer = Executors.newSingleThreadScheduledExecutor();
		this.trackStatusTimer.scheduleAtFixedRate(trackStatus, 0, 1000, TimeUnit.MILLISECONDS);
	}

	// Method linked to onClick button "Connect to drone"
	@FXML
	private void connectDrone() {

		refreshHSVUI();
		// Init PID contrøller
		// center = (int) mainIW.getFitWidth() / 2;

		logWrite("Value of center: " + center);
		
		try {
			drone = new ARDrone("192.168.1.1", new XugglerDecoder());
			System.out.println("Initialized drone");
			logWrite("Initialized drone");
			drone.setHorizontalCamera();
			drone.start();
			drone.getCommandManager().setVideoChannel(VideoChannel.HORI);

			drone.getVideoManager().addImageListener((BufferedImage image) -> {
				newFrame = image;
			});
			logWrite("Sleeping thread for 5 sec");
			// System.out.println("Før sleep");
			Thread.sleep(5000);
			// System.out.println("Efter sleep");
			logWrite("Started process to grab frames for main picture");
			Runnable frameGrabber = () -> {
				processImage();
			};

			this.frameGrabTimer = Executors.newSingleThreadScheduledExecutor();
			this.frameGrabTimer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			droneActive = true;
			DroneController dc = new DroneController(drone);
			QRController qc = new QRController();
			dc.start();
		} catch (Exception e) {
			e.printStackTrace();

			if (drone != null) {
				drone.stop();
			}
			System.exit(-1);
		}
	}

	// Method linked to onClick button "Connect to Webcam"
	@FXML
	private void connectWB() {
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

			Core.flip(frame, frame, 1);

			Imgproc.blur(frame, blurredImage, new Size(7, 7));
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
			// 20,50

			Core.inRange(hsvImage, minValues, maxValues, mask);

			maskFrame = Utilities.matToBufferedImage(mask);
			updateImageView(maskIW, SwingFXUtils.toFXImage(maskFrame, null));

			Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
			Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

			Imgproc.erode(mask, morphOutput, erodeElement);
			Imgproc.erode(morphOutput, morphOutput, erodeElement);

			Imgproc.dilate(morphOutput, morphOutput, dilateElement);
			Imgproc.dilate(morphOutput, morphOutput, dilateElement);

			morphedFrame = Utilities.matToBufferedImage(morphOutput);
			updateImageView(morphIW, SwingFXUtils.toFXImage(morphedFrame, null));

			frame = findAndDrawContours(morphOutput, frame);

			mainFrame = Utilities.matToBufferedImage(frame);
			updateImageView(mainIW, SwingFXUtils.toFXImage(mainFrame, null));

		}

	}

	private Mat findAndDrawContours(Mat maskedImage, Mat frame) {

		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierachy = new Mat();

		Imgproc.findContours(maskedImage, contours, hierachy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		if (hierachy.size().height > 0 && hierachy.size().width > 0) {
			objectTracked = true;
			for (int i = 0; i >= 0; i = (int) hierachy.get(0, i)[0]) {
				Imgproc.drawContours(frame, contours, i, new Scalar(250, 0, 0));
			}
		} else {
			objectTracked = false;
			return frame;
		}
		return frame;
	}

	private void updateImageView(ImageView view, Image image) {
		Utilities.onFXThread(view.imageProperty(), image);
	}

	private void stopAcquisition() {

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

	public void logWrite(String message) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		droneData.appendText("\n" + sdf.format(ts) + ": " + message);
	}

}
