package com.yellowbeanlab.lovetester;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by jirawuts on 5/3/15 AD.
 */
public class MainMenu extends Fragment {

    private boolean pendingAnnounce;
    private static final String PERMISSION = "publish_actions";
    private MainActivity activity;

    private ProfilePictureView profilePictureView;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            tokenUpdated(currentAccessToken);
            profilePictureView.setProfileId(currentAccessToken.getUserId());
        } else {
            profilePictureView.setProfileId(null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu, container, false);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);


        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    activity.showSettingsFragment();
                } else {
                    activity.showSplashFragment();
                }
            }
        });

        updateWithToken(AccessToken.getCurrentAccessToken());

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Notifies that the token has been updated.
     */
    private void tokenUpdated(AccessToken currentAccessToken) {
        if (pendingAnnounce) {
            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
            if (currentAccessToken == null
                    || !currentAccessToken.getPermissions().contains(PERMISSION)) {
                pendingAnnounce = false;
                showRejectedPermissionError();
                return;
            }
            handleAnnounce();
        }
    }

    private void showRejectedPermissionError() {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, null)
                .setTitle(R.string.error_dialog_title)
                .setMessage(R.string.rejected_publish_permission)
                .show();
    }

    private void handleAnnounce() {
        Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (!permissions.contains(PERMISSION)) {
            pendingAnnounce = true;
            requestPublishPermissions();
            return;
        } else {
            pendingAnnounce = false;
        }

//        ShareApi.share(createOpenGraphContent(), shareCallback);
    }

    private void requestPublishPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .logInWithPublishPermissions(this, Arrays.asList(PERMISSION));
    }



}
