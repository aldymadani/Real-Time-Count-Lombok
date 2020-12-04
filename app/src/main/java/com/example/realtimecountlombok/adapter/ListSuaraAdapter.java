package com.example.realtimecountlombok.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.realtimecountlombok.admin.EditSuaraActivity;
import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.model.SuaraKecamatan;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

public class ListSuaraAdapter extends FirestorePagingAdapter<SuaraKecamatan, ListSuaraAdapter.SuaraKecamatanViewHolder> {
    private static final String TAG = "ListSuaraAdapter";

    private Context context;
    private SwipeRefreshLayout swipeLayout;

    public ListSuaraAdapter(@NonNull FirestorePagingOptions<SuaraKecamatan> options, Context context, SwipeRefreshLayout swipeRefresh) {
        super(options);
        this.context = context;
        this.swipeLayout = swipeRefresh;
    }

    @NonNull
    @Override
    public SuaraKecamatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suara, parent, false);
        return new SuaraKecamatanViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull SuaraKecamatanViewHolder holder, int position, @NonNull SuaraKecamatan model) {
        // Bind to ViewHolder
        holder.bind(model);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
        Log.e(TAG, e.getMessage());
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
                Log.d(TAG, "Initially loaded" + getItemCount());
            case LOADING_MORE:
                swipeLayout.setRefreshing(true);
                Log.d(TAG, "Loading more" + getItemCount());
                break;

            case LOADED:
                swipeLayout.setRefreshing(false);
                break;

            case ERROR:
                Toast.makeText(
                        context,
                        "Error Occurred!",
                        Toast.LENGTH_SHORT
                ).show();

                swipeLayout.setRefreshing(false);
                retry();
                break;

            case FINISHED:
                Log.d(TAG, "Total item Loaded: " + getItemCount());
                swipeLayout.setRefreshing(false);
                break;
        }
    }

    static class SuaraKecamatanViewHolder extends RecyclerView.ViewHolder {
        TextView namaKecamatan;
        TextView nomorTPS;
        TextView totalSuara;
        CardView parentLayout;

        SuaraKecamatanViewHolder(@NonNull View itemView) {
            super(itemView);
            namaKecamatan = itemView.findViewById(R.id.namaKecamatanItemSuara);
            nomorTPS = itemView.findViewById(R.id.nomorTPSItemSuara);
            totalSuara = itemView.findViewById(R.id.totalSuaraItemSuara);
            parentLayout = itemView.findViewById(R.id.itemSuara);
        }

        void bind(final SuaraKecamatan suaraKecamatan) {
            namaKecamatan.setText(suaraKecamatan.getNamaKecamatan());
            nomorTPS.setText("TPS " + suaraKecamatan.getNomorTPS());
            totalSuara.setText(suaraKecamatan.getPerolehanSuaraCalonPertama() + suaraKecamatan.getPerolehanSuaraCalonKedua() +
                    suaraKecamatan.getPerolehanSuaraCalonKetiga() + suaraKecamatan.getPerolehanSuaraCalonKeempat() +
                    suaraKecamatan.getPerolehanSuaraCalonKelima() + suaraKecamatan.getTotalDPTTidakHadir() + suaraKecamatan.getTotalSuaraTidakSah() + " Suara");
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), EditSuaraActivity.class);
                    intent.putExtra("suaraKecamatan", suaraKecamatan);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
