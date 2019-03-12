package com.example.amr.newsservice;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY="c594ee24630f46d9a2e79c4f1a75262b";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> listOfArticles=new ArrayList<>();
    private NewsAdapter adapter;
    private String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        LoadJson("");

    }
    public void LoadJson(final String keyword ){
        ApiInterface apiInterface=ApiClient.getApiClient().create(ApiInterface.class);
        String country=Utils.getCountry();

        Call<News> call;
        if (keyword.length()>0){
            call=apiInterface.getNewsSearch(keyword,"publishedAt",API_KEY);
        }else {
            call=apiInterface.getNews(country,API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){

                    if (!listOfArticles.isEmpty()){
                        listOfArticles.clear();
                    }
                    listOfArticles=response.body().getArticles();
                    adapter=new NewsAdapter(listOfArticles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(MainActivity.this,"no result",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView=(SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem=menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>2){
                    LoadJson(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadJson(newText);
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false,false);
        return true;
    }
}
