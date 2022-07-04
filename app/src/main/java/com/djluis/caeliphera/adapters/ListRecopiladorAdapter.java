package com.djluis.caeliphera.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.djluis.caeliphera.R;
import com.djluis.caeliphera.entities.PersonRecopilador;

import java.util.ArrayList;
public class ListRecopiladorAdapter extends RecyclerView.Adapter<ListRecopiladorAdapter.RecopiladorViewHolder> {
	ArrayList<PersonRecopilador> recopiladores;

	public ListRecopiladorAdapter (ArrayList<PersonRecopilador> recopiladores) {
		this.recopiladores = recopiladores;
	}

	@NonNull
	@Override
	public RecopiladorViewHolder onCreateViewHolder (
		@NonNull ViewGroup parent, int viewType
	) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recopilador, null, false);
		return new RecopiladorViewHolder(view);
	}

	@Override
	public void onBindViewHolder (
		@NonNull RecopiladorViewHolder holder, int position
	) {
		holder.txt_view_fullname_recopilador.setText(recopiladores.get(position).getFullName());
	}

	/**
	 * Returns the total number of items in the data set held by the adapter.
	 *
	 * @return The total number of items in this adapter.
	 */
	@Override
	public int getItemCount () {
		return recopiladores.size();
	}

	public class RecopiladorViewHolder extends RecyclerView.ViewHolder {
		private TextView txt_view_fullname_recopilador, txt_view_email_recopilador, txt_view_parroquia_recopilador;

		public RecopiladorViewHolder (@NonNull View itemView) {
			super(itemView);
			txt_view_fullname_recopilador = (TextView) itemView.findViewById(R.id.txt_view_fullname_recopilador);
			txt_view_email_recopilador = (TextView) itemView.findViewById(R.id.txt_view_email_recopilador);
			txt_view_parroquia_recopilador = (TextView) itemView.findViewById(R.id.txt_view_parroquia_recopilador);
		}
	}
}
