package example.luojing.vitamiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Thread(){
//            @Override
//            public void run() {
//                Vitamio.initialize(getApplicationContext());
//            }
//        }.start();
    }
    public void next(View v){
        Intent intent = new Intent(this,VideoActivity.class);
        startActivity(intent);
    }
}
