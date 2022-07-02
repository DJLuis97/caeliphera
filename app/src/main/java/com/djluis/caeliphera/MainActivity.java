package com.djluis.caeliphera;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  private TextView welcome_main;
  private Button   btn_logout, btn_go_store_recopilador;

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    welcome_main = (TextView) findViewById(R.id.welcome_main);
    btn_logout = (Button) findViewById(R.id.btn_logout);
    btn_go_store_recopilador = (Button) findViewById(R.id.btn_go_store_recopilador);
    btn_go_store_recopilador.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick (View view) {
        startActivity(new Intent(MainActivity.this, StoreRecopiladorActivity.class));
      }
    });
    btn_logout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick (View view) {
        lgout();
      }
    });

    SQLite         sqLite      = new SQLite(this, "caeliphera", null, 1);
    SQLiteDatabase writable_db = sqLite.getWritableDatabase();

    Cursor row = writable_db.rawQuery("SELECT full_name FROM users LIMIT 1;", null);
    if (row.moveToFirst()) {
      welcome_main.setText("Bienvenido " + row.getString(0));
      writable_db.close();
    } else {
      writable_db.close();
      Toast.makeText(this, "Token no encontrado", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(this, LoginActivity.class));
    }
  }


  private void lgout () {
    String         url         = "https://caeliphera-api.herokuapp.com/api/auth/logout";
    SQLite         sqLite      = new SQLite(MainActivity.this, "caeliphera", null, 1);
    SQLiteDatabase writable_db = sqLite.getWritableDatabase();
    Cursor row = writable_db.rawQuery("SELECT id,token FROM users LIMIT 1;", null);
    if (row.moveToFirst()) {
      StringRequest sr = new StringRequest(
          Request.Method.GET, url, new Response.Listener<String>() {
        @Override public void onResponse (String response) {
          int rows_affected = writable_db.delete(
              "users", "id=" + row.getString(0), null);
          writable_db.close();
          if (rows_affected == 1) {
            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
          } else {
            Toast.makeText(MainActivity.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
          }
        }
      }, new Response.ErrorListener() {
        @Override public void onErrorResponse (VolleyError error) {
          writable_db.close();
          Log.e("no-response", "(╯°□°）╯︵ ┻━┻ |> " + error.toString());
        }
      })
      {
        public Map<String, String> getHeaders () {
          Map<String, String> headers = new HashMap<String, String>();
          headers.put("Accept", "application/json");
          headers.put("Content-Type", "application/json");
          headers.put("Authorization", "Bearer " + row.getString(1));

          return headers;
        }
      };
      Volley.newRequestQueue(MainActivity.this).add(sr);
    } else {
      writable_db.close();
      Toast.makeText(MainActivity.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
    }
  }
}