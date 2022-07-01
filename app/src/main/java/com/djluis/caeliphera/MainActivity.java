package com.djluis.caeliphera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
  private TextView welcome_main;

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    welcome_main = (TextView) findViewById(R.id.welcome_main);
    try {
      InputStreamReader auth_file = new InputStreamReader(openFileInput("auth.txt"));
      BufferedReader    br        = new BufferedReader(auth_file);
      String            token     = br.readLine();
      br.close();
      auth_file.close();
      if (token == null) {
        startActivity(new Intent(this, LoginActivity.class));
      } else {
        Log.e("token", token);
        try {
          String full_name = (new JSONObject(token)).getJSONObject("user").getJSONObject("person").getString("full_name");
          Log.e("full_name", full_name);
          welcome_main.setText("Bienvenido " + full_name);
        } catch (JSONException e) {

        }
      }
    } catch (IOException e) {
      Log.e("MainActivity", "(╯°□°）╯︵ ┻━┻ |> " + e.getMessage());
      startActivity(new Intent(this, LoginActivity.class));
    }
  }
}