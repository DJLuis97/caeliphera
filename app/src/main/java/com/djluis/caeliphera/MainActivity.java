package com.djluis.caeliphera;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
  private final OkHttpClient client = new OkHttpClient();

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //startActivity(new Intent(this, LoginActivity.class));
    Request request = new Request.Builder().url("http://publicobject.com/helloworld.txt").build();
    client.newCall(request).enqueue(new Callback() {
      @Override public void onFailure (@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse (@NonNull Call call, @NonNull Response response) throws IOException {
        Toast.makeText(MainActivity.this, "Si se conectó", Toast.LENGTH_SHORT).show();
      }
    });

  }
}