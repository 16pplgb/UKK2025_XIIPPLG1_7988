package com.example.ukk_2025;


import static com.example.ukk_2025.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ukk_2025.R.id;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.KategoriViewHolder> {

    private List<CategoriesModel> catergoriesList;
    private Context context;

    private static final String URL_DELETE = "http://172.16.0.197/UKK2025/delcategories.php";
    private static final String URL_EDIT = "http://172.16.0.197/UKK2025/editcategories.php";

    public CategoriesAdapter(Context context, List<CategoriesModel>  catergoriesList) {
        this.context = context;
        this. catergoriesList =  catergoriesList;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout.item_categories, parent, false);
        return new KategoriViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        CategoriesModel  categories=  catergoriesList.get(position);
        holder.textNomor.setText(String.valueOf(position + 1));
        holder.textKategori.setText(categories.getCategories());

        // DELETE ACTION
        holder.deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(categories, position));

        // EDIT ACTION
        holder.editIcon.setOnClickListener(v -> showEditDialog(categories, position));
    }

    @Override
    public int getItemCount() {
        return  catergoriesList.size();
    }

    public static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView textNomor, textKategori;
        ImageView deleteIcon, editIcon;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomor = itemView.findViewById(id.textNomor);
            textKategori = itemView.findViewById(id.textNama);
            deleteIcon = itemView.findViewById(id.deleteIcon);
            editIcon = itemView.findViewById(id.editIcon);
        }
    }

    private void showDeleteConfirmationDialog(CategoriesModel kategori, int position) {
        new AlertDialog.Builder(context)
                .setMessage("Apakah Anda yakin ingin menghapus Kategori ini?")
                .setCancelable(false)
                .setPositiveButton("Yakin", (dialog, id) -> {
                    dialog.dismiss();
                    delcategories(kategori, position); // Panggil fungsi hapus di sini
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showEditDialog(CategoriesModel  categories, int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout.dialog_edit_categories, null);
        builder.setView(view);

        TextInputEditText editNama = view.findViewById(R.id.editName);

        editNama.setText(categories.getCategories());

        builder.setTitle("Edit Kategori")
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newName = editNama.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        editKategori(categories, newName, position); // Panggil function editKategori
                    } else {
                        Toast.makeText(context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Fungsi untuk menghapus kategori
    private void delcategories(CategoriesModel  categories, int position) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    if (response.trim().equals("Category deleted successfully")) {
                        Toast.makeText(context, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show();
                        catergoriesList.remove(position); // Hapus item dari daftar
                        notifyItemRemoved(position); // Perbarui adapter
                    } else {
                        Toast.makeText(context, "Gagal menghapus kategori: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(context, "Gagal menghapus kategori. Kode: " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", String.valueOf(categories.getId()));
                return params;
            }
        };

        // Tambahkan request ke antrian Volley
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void editKategori(CategoriesModel kategori, String newName, int position) {

        // Validasi input sebelum dikirim ke server
        if (newName.isEmpty()) {
            Toast.makeText(context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                response -> {
                    // Cek apakah respons mengandung pesan sukses
                    if (response.trim().equalsIgnoreCase("Category updated successfully")) {
                        Toast.makeText(context, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        kategori.setCategories(newName); // Update data di model
                        notifyItemChanged(position); // Refresh item di RecyclerView
                    } else {
                        Toast.makeText(context, "Gagal memperbarui kategori: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Terjadi kesalahan, coba lagi.";
                    if (error.networkResponse != null) {
                        errorMessage = "Error " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", String.valueOf(kategori.getId())); // Pastikan ID kategori benar
                params.put("new_name", newName);
                return params;
            }
        };

        // Menambahkan request ke antrian Volley
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}