package com.example.nhom16_myimagegallery;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private List<String> imagePaths;
    private int currentPosition;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.f; // Kích thước ảnh mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.detailImageView);

        // Lấy dữ liệu từ Intent
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        // Hiển thị ảnh hiện tại
        loadImage(currentPosition);

        // Tạo GestureDetector để nhận diện vuốt
        gestureDetector = new GestureDetector(this, new GestureListener());

        // Tạo ScaleGestureDetector để nhận diện phóng to/thu nhỏ
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageView.setOnTouchListener((v, event) -> {
            int pointerCount = event.getPointerCount();

            if (pointerCount == 2) {
                // Xử lý phóng to/thu nhỏ với 2 ngón tay
                scaleGestureDetector.onTouchEvent(event);
            } else if (pointerCount == 3) {
                System.out.println("ba ngón");
                // Xử lý vuốt chuyển ảnh với 3 ngón tay
                gestureDetector.onTouchEvent(event);
            }

            return true;
        });
    }

    private void loadImage(int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(this)
                .load(imagePath)
                .into(imageView);
    }

    // Lớp GestureListener để xử lý vuốt qua lại với 2 ngón tay
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Vuốt phải, chuyển về ảnh trước
                    if (currentPosition > 0) {
                        currentPosition--;
                        loadImage(currentPosition);
                    }
                } else {
                    // Vuốt trái, chuyển đến ảnh sau
                    if (currentPosition < imagePaths.size() - 1) {
                        currentPosition++;
                        loadImage(currentPosition);
                    }
                }
                return true;
            }
            return false;
        }
    }


    // Lớp ScaleListener để xử lý sự kiện phóng to/thu nhỏ với 2 ngón tay
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Giới hạn tỷ lệ để không quá nhỏ hoặc quá lớn
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            // Áp dụng tỷ lệ phóng to/thu nhỏ cho ImageView
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);

            return true;
        }
    }
}
