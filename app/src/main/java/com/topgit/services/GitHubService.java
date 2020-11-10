package com.topgit.services;

import com.topgit.models.GitHubRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GitHubService {

    @GET("users/{user}/repos")
    Call<List<GitHubRepo>> reposForUser(@Path("user") String user);

    @GET
    Call<List<GitHubRepo>> reposForUserPaginate(@Url String url);


}

//https://api.github.com/search/repositories?q=language:kotlin&sort=stars