package cn.demomaster.qdlogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.demomaster.qdlogger_library.QDLogger;

public class MainActivity extends AppCompatActivity {

    Button btn_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testError();
            }
        });

    }

    private void testError() {
        int a = 1;
        int b = 0;
        try {
            int c = a/b;
        }catch (Exception e){
            QDLogger.e(e);
        }
    }

    private void initLogger() {
        QDLogger.init(this,"/qdlogger/");
        QDLogger.i("123 喊口号,小朋友全立定。");
    }

    int REQUEST_CODE = 10306;

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            //此处做动态权限申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            //低于23 不需要特殊处理
            initLogger();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            //当然权限多了，建议使用Switch，不必纠结于此
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
                initLogger();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "权限申请失败，用户拒绝权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}