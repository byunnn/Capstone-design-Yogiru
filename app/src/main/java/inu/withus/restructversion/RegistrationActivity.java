package inu.withus.restructversion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import inu.withus.restructversion.DetectorActivity;
import inu.withus.restructversion.customview.OverlayView;
import inu.withus.restructversion.env.ImageUtils;
import inu.withus.restructversion.env.Logger;
import inu.withus.restructversion.env.Utils;
import inu.withus.restructversion.tflite.Classifier;
import inu.withus.restructversion.tflite.YoloV4Classifier;
import inu.withus.restructversion.tflite.YoloV4Classifier;
import inu.withus.restructversion.tracking.MultiBoxTracker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ANOTHER = 1001;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;

    public static String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_food);

        cameraButton = findViewById(R.id.mButton);
        inputText = findViewById(R.id.InputfoodName);
        registerButton = findViewById(R.id.register);


        cameraButton.setOnClickListener(v-> {
                    Intent myIntent = new Intent(RegistrationActivity.this, DetectorActivity.class);
                    startActivityForResult(myIntent, REQUEST_CODE_ANOTHER);
                }
        );

        registerButton.setOnClickListener(v-> {
                    Intent myIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                    myIntent.putExtra("name", name);
                    startActivityForResult(myIntent, REQUEST_CODE_ANOTHER);
                }
        );




        this.sourceBitmap = Utils.getBitmapFromAsset(RegistrationActivity.this, "kite.jpg");
        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);
        initBox();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // 또 다른 액티비티에서 보내온 응답인지 요청코드로 확인
        if (requestCode == REQUEST_CODE_ANOTHER) {
            // 토스트로 메시지 보내기
//            Toast toast = Toast.makeText(getBaseContext(), "onActivityResult() 메소드가 호출됨. 요청코드 : " + requestCode + ", 결과코드 : " + resultCode, Toast.LENGTH_LONG);
//            toast.show();


            if (resultCode == RESULT_OK) {
                name = intent.getExtras().getString("title");
                inputText.setText(name);
                Toast toast = Toast.makeText(getBaseContext(), "인식된 객체 : " + name, Toast.LENGTH_LONG);
                toast.show();


            }
        }

    }

    private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 416;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    private static final String TF_OD_API_MODEL_FILE = "yolov4-416-fp32.tflite";
//    private static final String TF_OD_API_MODEL_FILE = "yolov4-416.tflite";
//    private static final String TF_OD_API_MODEL_FILE = "yolov4-tiny-416.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";
//    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/obj.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = false;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private ImageButton cameraButton, detectButton;
    //    private ImageView imageView;
    private TextView inputText;
    private TextView textView;
    private Button registerButton;

    private void initBox() {
        previewHeight = TF_OD_API_INPUT_SIZE;
        previewWidth = TF_OD_API_INPUT_SIZE;
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker = new MultiBoxTracker(this);
        trackingOverlay = findViewById(R.id.tracking_overlay3);
        trackingOverlay.addCallback(
                canvas -> tracker.draw(canvas));

        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation);

        try {
            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    private void handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint);

            }
        }

    }
}


