package com.example.nhom16_myimagegallery;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private List<String> imagePaths;
    private int currentPosition;
    private GestureDetector gestureDetector;

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

        imageView.setOnTouchListener((v, event) -> {
            // Kiểm tra có đúng 2 ngón tay không
            if (event.getPointerCount() == 2) {
                Log.d("DetailActivity", "Vuốt với 2 ngón tay: " + event.getActionMasked());
                // Chỉ xử lý vuốt khi có 2 ngón tay
                gestureDetector.onTouchEvent(event);
            } else {
                Log.d("DetailActivity", "Không phải vuốt với 2 ngón tay, pointer count: " + event.getPointerCount());
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
            // Kiểm tra chỉ xử lý vuốt khi có 2 ngón tay
            if (e1.getPointerCount() == 2 && e2.getPointerCount() == 2) {
                Log.d("DetailActivity", "Vuốt qua lại với 2 ngón tay: " + (e2.getX() - e1.getX()));
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
                } else {
                    Log.d("DetailActivity", "Không đủ độ dài để vuốt");
                }
            } else {
                Log.d("DetailActivity", "Không phải 2 ngón tay vuốt, pointer count: " + e1.getPointerCount());
            }
            return false;
        }
    }
}
