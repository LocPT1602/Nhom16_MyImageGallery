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
    private float initialX = 0;
    private float initialY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.viewPager);

        // Lấy dữ liệu từ Intent
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        // Hiển thị ảnh hiện tại
        loadImage(currentPosition);

        // Tạo ScaleGestureDetector để nhận diện phóng to/thu nhỏ
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageView.setOnTouchListener((v, event) -> {
            // Chỉ xử lý phóng to/thu nhỏ với 2 ngón tay và vuốt 3 ngón tay
            if (event.getPointerCount() == 2) {
                // Xử lý phóng to/thu nhỏ với 2 ngón tay
                scaleGestureDetector.onTouchEvent(event);
            } else if (event.getPointerCount() == 3) {
                // Xử lý vuốt với 3 ngón tay (chuyển ảnh)
                handleThreeFingerSwipe(event);
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

    private void animateImageChange(int position, boolean isSwipeRight) {
        // Hiệu ứng fade out
        imageView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    // Sau khi fade out, thay đổi ảnh
                    loadImage(position);

                    // Hiệu ứng fade in
                    imageView.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();
                })
                .start();
    }

    private void handleThreeFingerSwipe(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // Lưu vị trí ban đầu của các ngón tay khi bắt đầu vuốt
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                // Lấy sự thay đổi theo trục X để xác định vuốt trái hay phải
                float diffX = event.getX() - initialX;

                if (Math.abs(diffX) > 100) {  // Giới hạn độ dài vuốt
                    if (diffX > 0) {
                        // Vuốt phải, chuyển về ảnh trước
                        if (currentPosition > 0) {
                            currentPosition--;
                            animateImageChange(currentPosition, true);
                        }
                    } else {
                        // Vuốt trái, chuyển đến ảnh sau
                        if (currentPosition < imagePaths.size() - 1) {
                            currentPosition++;
                            animateImageChange(currentPosition, false);
                        }
                    }
                    initialX = event.getX(); // Cập nhật vị trí để không bị trùng lặp khi vuốt liên tục
                }
                break;
        }
    }

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
