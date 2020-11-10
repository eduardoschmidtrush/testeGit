package com.topgit;

import android.os.Bundle;

import com.topgit.adapter.PaginationItem;
import com.topgit.adapter.PaginationListAdapter;
import com.topgit.models.GitHubRepo;
import com.topgit.services.GitHubService;
import com.topgit.utils.GitHubPagelinksUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    private PaginationListAdapter adapter;
    private List<PaginationItem> values = new ArrayList<>();
    private GitHubService service;

    private Callback<List<GitHubRepo>> callback =
            new Callback<List<GitHubRepo>>() {

                public void onResponse(Call<List<GitHubRepo>> call,
                                       Response<List<GitHubRepo>> response) {
                    if (response.isSuccessful()) {
                        for (GitHubRepo repo : response.body()) {
                            values.add(new PaginationItem(repo.getId(), repo.getName()));
                        }

                        adapter.notifyDataSetChanged();
                        fetchReposNextPage(response);
                    } else {
                        Log.e("Request failed: ", "Cannot request GitHub repositories");
                    }
                }

                public void onFailure(Call<List<GitHubRepo>> call,
                                      Throwable t) {
                    Log.e("Error fetching repos", t.getMessage());
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_pagination);

        Log.d("Header", "a1");

        ListView listView = (ListView) findViewById(R.id.pagination_list);
        adapter = new PaginationListAdapter(ScrollingActivity.this, values);
        listView.setAdapter(adapter);

        // Change base url to GitHub API
         //ServiceGenerator.changeApiBaseUrl("https://api.github.com/");

        // Create a simple REST adapter which points to GitHub’s API
       // service = ServiceGenerator.createService(GitHubService.class);

        // Fetch and print a list of repositories for user “fs-opensource”
       // Call<List<GitHubRepo>> call = service.reposForUser("delaroy");
       // call.enqueue(callback);
    }

    private void fetchReposNextPage(Response<List<GitHubRepo>> response) {
        GitHubPagelinksUtils pagelinksUtils =
                new GitHubPagelinksUtils(response.headers());
        String next = pagelinksUtils.getNext();

        Log.d("Header", response.headers().get("Link"));

        if (TextUtils.isEmpty(next)) {
            return; // nothing to do
        }

        Call<List<GitHubRepo>> call = service.reposForUserPaginate(next);
        call.enqueue(callback);
    }
}
