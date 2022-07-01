package com.djluis.caeliphera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StoreRecopiladorActivity extends AppCompatActivity {

  private Button btn_back_store_recopilador;

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_store_recopilador);
    btn_back_store_recopilador = (Button) findViewById(R.id.btn_back_store_recopilador);
    btn_back_store_recopilador.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick (View view) {
        startActivity(new Intent(StoreRecopiladorActivity.this, MainActivity.class));
      }
    });
  }
}