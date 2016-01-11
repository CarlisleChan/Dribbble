package com.carlisle.provider;

import com.carlisle.model.CheckFollowResult;
import com.carlisle.model.CommentRequest;
import com.carlisle.model.DribleBucket;
import com.carlisle.model.DribleComment;
import com.carlisle.model.DribleShot;
import com.carlisle.model.DribleUser;
import com.carlisle.model.FollowResult;
import com.carlisle.model.LikesResult;
import com.carlisle.model.MarkResult;
import com.carlisle.model.PushCommentResult;
import com.carlisle.model.e.Team;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by chengxin on 1/6/16.
 */
public interface DribleApi {
    @GET("/user")
    Observable<DribleUser> fetchUserInfo();

    @GET("/user/following/{userId}")
    void checkIfMeFollow(@Path("userId") long userId, Callback<CheckFollowResult> callback);

    @GET("/users/{userId}")
    Observable<DribleUser> getUserInfo(@Path("userId") long userId);

    @PUT("/users/{userId}/follow")
    void requestFollow(@Path("userId") long userId, Callback<FollowResult> followResultCallback);

    @DELETE("/users/{userId}/follow")
    void cancelFollow(@Path("userId") long userId, Callback<FollowResult> followResultCallback);

    @GET("/users/{userId}/buckets")
    Observable<List<DribleBucket>> fetchBuckets(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/followers")
    Observable<List<FollowResult>> fetchFollowers(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/following")
    Observable<List<FollowResult>> fetchFollowing(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/likes")
    Observable<List<LikesResult>> fetchShotsFromLike(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/projects")
    Observable<List<DribleShot>> fetchProjects(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/shots")
    Observable<List<DribleShot>> fetchUserShots(@Path("userId") long userId, @Query("page") int page);

    @GET("/users/{userId}/teams")
    Observable<List<Team>> fetchTeams(@Path("userId") long userId, @Query("page") int page);

    @GET("/shots/")
    Observable<List<DribleShot>> fetchOneShots(@Query("page") int page, @Query("sort") String sort, @Query("list") String list);

    @GET("/shots/{shotId}")
    Observable<DribleShot> fetchShotDetail(@Path("shotId") long shotId);

    @GET("/shots/{shotId}/like")
    Observable<MarkResult> checkIfLike(@Path("shotId") long shotId);

    @POST("/shots/{shotId}/like")
    Observable<MarkResult> requestLike(@Path("shotId") long shotId);

    @DELETE("/shots/{shotId}/like")
    Observable<MarkResult> requestUnLike(@Path("shotId") long shotId);

    @GET("/shots/{shotId}/comments")
    Observable<List<DribleComment>> fetchComments(@Path("shotId") long shotId);

    @POST("/shots/{shotId}/comments")
    void pushComment(@Path("shotId") long shotId, @Body CommentRequest commentRequest, Callback<PushCommentResult> callback);

    @GET("/buckets/{bucketId}/shots")
    Observable<List<DribleShot>> fetchShotsFromBuckets(@Path("bucketId") long bucketId, @Query("page") int page);
}
