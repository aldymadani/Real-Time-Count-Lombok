package com.example.realtimecountlombok.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.adapter.ListSuaraAdapter;
import com.example.realtimecountlombok.model.SuaraKecamatan;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListSuaraFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference suaraKecamatanRef = db.collection("SuaraKecamatan");

    private ListSuaraAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list_suara, container, false);
        ComponentsInitialization();
        final Query query;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        query = suaraKecamatanRef.whereEqualTo("createdBy", user.getEmail());
        SetUpRecyclerView(query);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SetUpRecyclerView(query);
                mAdapter.refresh();
                swipeLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    private void SetUpRecyclerView(Query query) {
        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(3) // Remember that, the size you will pass to setPageSize() a method will load x3 items of that size at first load. Total initial is 9 https://medium.com/firebase-developers/firestore-pagination-in-android-using-firebaseui-library-1d7fe1a75704
                .build();

        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<SuaraKecamatan>()
                .setLifecycleOwner(this)
                .setQuery(query, config, SuaraKecamatan.class)
                .build();

        mAdapter = new ListSuaraAdapter(options, requireActivity(), swipeLayout);

//        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireActivity()));
//        recyclerView.setLayoutManager(new GridLayoutManager(this.requireActivity(), gridColumnCount));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.stopListening();
    }

    private void ComponentsInitialization() {
        recyclerView = rootView.findViewById(R.id.listSuaraRecyclerView);
        swipeLayout = rootView.findViewById(R.id.listSuaraSwipeLayout);
    }
}