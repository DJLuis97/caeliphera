package com.djluis.caeliphera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListRecopilador extends AppCompatActivity {
	private Button       btn_home_list_recopilador;
	private RecyclerView list_recopiladores;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_recopilador);
		setupView();
	}

	private void setupView () {
		btn_home_list_recopilador = (Button) findViewById(R.id.btn_home_list_recopilador);
		list_recopiladores = (RecyclerView) findViewById(R.id.list_recopiladores);
		list_recopiladores.setLayoutManager(new LinearLayoutManager(ListRecopilador.this));
		// Listeners
		btn_home_list_recopilador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				startActivity(new Intent(ListRecopilador.this, MainActivity.class));
			}
		});
	}
}