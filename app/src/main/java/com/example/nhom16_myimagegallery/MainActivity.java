package com.example.nhom16_myimagegallery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trang_chu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // Nếu đã có quyền, thực hiện hành động liên quan đến bộ nhớ ngoài
            accessExternalStorage();
            loadAnh();
        }

    }
    public void loadAnh() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Hiển thị dạng lưới với 3 cột

        // Lấy danh sách đường dẫn ảnh
        ArrayList<String> imagePaths = getAllImages();

        // Thiết lập adapter
        ImageAdapter adapter = new ImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<String> getAllImages() {
        ArrayList<String> images = new ArrayList<>();
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(columnIndex);
                images.add(imagePath);
            }
            cursor.close();
        }
        Log.d(null, "getAllImages: " + images.size());
        return images;
    }
    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng cấp quyền
                accessExternalStorage();
            } else {
                // Người dùng từ chối quyền
                Toast.makeText(this, "Cần cấp quyền truy cập bộ nhớ ngoài", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void accessExternalStorage() {
        // Lógica để truy cập bộ nhớ ngoài
        Toast.makeText(this, "Có quyền truy cập bộ nhớ ngoài!", Toast.LENGTH_SHORT).show();
    }

    // Hàm mở cài đặt ứng dụng
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}