package com.iranstore.store.details.addcomment;

import com.google.gson.JsonObject;
import com.iranstore.store.model.Comment;
import com.iranstore.store.model.api.ApiService;
import com.iranstore.store.providers.ApiServiceProvider;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;


public class AddCommentViewModel {
    private ApiService apiService = ApiServiceProvider.provideApiService();
    private BehaviorSubject<Boolean> progressBarVisibilitySubject = BehaviorSubject.create();

    public Single<Comment> addComment(int productId, String title, String content) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("product_id", productId);
        return apiService.addComment(jsonObject);
    }

    public BehaviorSubject<Boolean> progressBarVisibility() {
        return progressBarVisibilitySubject;
    }
}
