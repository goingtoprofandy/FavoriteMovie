package com.aitekteam.developer.favoritemovie.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aitekteam.developer.favoritemovie.DetailMovieActivity;
import com.aitekteam.developer.favoritemovie.Item;
import com.aitekteam.developer.favoritemovie.R;
import com.aitekteam.developer.favoritemovie.TVShowViewModel;
import com.aitekteam.developer.favoritemovie.adapters.MovieAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TVShowFragment extends Fragment {

    public static TVShowFragment getInstance() {
        return new TVShowFragment();
    }

    private MovieAdapter movieAdapter;
    private ProgressDialog loading;
    private TVShowViewModel mainViewModel;
    private int argument;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        RecyclerView listMainMovieFull = view.findViewById(R.id.list_main_movie_full);
        listMainMovieFull.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter();
        listMainMovieFull.setAdapter(movieAdapter);
        mainViewModel = ViewModelProviders.of(this).get(TVShowViewModel.class);

        if (movieAdapter.getItemCount() == 0)
            prepareData();
        int page = 1;

        if (getArguments() != null) {
            argument = getArguments().getInt("typeRequest");
            mainViewModel.getItems(getContext(), getActivity(), argument, getResources()
                    .getString(R.string.api_key), "en-US", page).observe(this, getMovies);
        }
        return view;
    }

    private Observer<ArrayList<Item>> getMovies = new Observer<ArrayList<Item>>() {
        @Override
        public void onChanged(final ArrayList<Item> items) {

            movieAdapter.setItems(items, new MovieAdapter.MovieHandler() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(getContext(), DetailMovieActivity.class);
                    intent.putExtra(DetailMovieActivity.EXTRA_OBJECT, items.get(position));
                    intent.putExtra("typeRequest", argument);
                    startActivity(intent);
                }
            });
            movieAdapter.notifyDataSetChanged();

            if (items.size() == 0) Toast.makeText(getContext(), R.string.msg_not_found, Toast.LENGTH_LONG).show();
//
            loading.dismiss();
        }
    };

    @Override
    public void onResume() {
        mainViewModel.getItems(getContext(), getActivity(), argument, getResources()
                .getString(R.string.api_key),  "en-US", 1).observe(this, getMovies);
        super.onResume();
    }

    private void prepareData() {
        loading = new ProgressDialog(getContext());
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("Harap tunggu sebentar...");
        loading.show();
    }
}
