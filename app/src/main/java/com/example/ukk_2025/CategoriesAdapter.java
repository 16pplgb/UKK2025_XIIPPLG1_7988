package com.example.ukk_2025;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context context;
    private List<CategoriesModel> kategoriList;
    private OnKategoriClickListener listener;

    public CategoriesAdapter() {

    }

    public interface OnKategoriClickListener {
        void onEditClick(CategoriesModel kategori);
        void onDeleteClick(String id);
    }

    public CategoriesAdapter(Context context, List<CategoriesModel> kategoriList, OnKategoriClickListener listener) {
        this.context = context;
        this.kategoriList = kategoriList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       CategoriesModel Categories = kategoriList.get(position);
        holder.textNomor.setText(String.valueOf(position + 1));
        holder.textKategori.setText(Categories.getKategori());

        holder.editIcon.setOnClickListener(v -> listener.onEditClick(Categories));
        holder.deleteIcon.setOnClickListener(v -> showDeleteConfirmation(Categories.getId()));
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    private void showDeleteConfirmation(String id) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Kategori")
                .setMessage("Apakah Anda yakin ingin menghapus kategori ini?")
                .setPositiveButton("Ya", (dialog, which) -> listener.onDeleteClick(id))
                .setNegativeButton("Batal", null)
                .show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomor, textKategori;
        ImageView editIcon, deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomor = itemView.findViewById(R.id.textNomor);
            textKategori = itemView.findViewById(R.id.textNomor);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}