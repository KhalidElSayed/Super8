package com.github.super8.fragments;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.github.super8.R;

public class ContentFragment extends RoboFragment {

  private static final int CONTENT_NO_LIKES = 0;
  private static final int CONTENT_LIKE_MODE = 1;
  private static final int CONTENT_SUGGESTION_MODE = 2;

  private ViewFlipper flipper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View layout = inflater.inflate(R.layout.content_fragment, container);

    flipper = (ViewFlipper) layout.findViewById(R.id.view_flipper);
    flipper.addView(inflater.inflate(R.layout.flipper_content_no_likes, flipper, false));
    flipper.addView(inflater.inflate(R.layout.flipper_content_like_mode, flipper, false));
    flipper.addView(inflater.inflate(R.layout.flipper_content_mood_icons, flipper, false));

    return layout;
  }

  public void showWelcomeView() {
    flipper.setDisplayedChild(CONTENT_NO_LIKES);
  }

  public void showLikeModeView() {
    flipper.setDisplayedChild(CONTENT_LIKE_MODE);
  }

  public void showMoodView() {
    flipper.setDisplayedChild(CONTENT_SUGGESTION_MODE);
  }
}
