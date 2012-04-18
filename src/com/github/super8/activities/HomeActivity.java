package com.github.super8.activities;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.ToggleButton;

import com.github.super8.R;
import com.github.super8.apis.tmdb.v3.DefaultTmdbApiHandler;
import com.github.super8.behavior.ActsAsHomeScreen;
import com.github.super8.behavior.HomeScreenPresenter;
import com.github.super8.fragments.InfoBoxFragment;
import com.github.super8.fragments.MovieDetailsFragment;
import com.github.super8.fragments.PersonFinderFragment;
import com.github.super8.model.Movie;
import com.google.inject.Inject;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class HomeActivity extends RoboFragmentActivity implements ActsAsHomeScreen {

  @Inject private HomeScreenPresenter presenter;
  @Inject private PersonFinderFragment personFinderFragment;
  @Inject private MovieDetailsFragment movieDetailsFragment;
  @InjectView(R.id.drawer) private SlidingDrawer drawer;
  @InjectFragment(R.id.infobox_fragment) private InfoBoxFragment infoboxFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.home);

    presenter.bind(this);

    drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

      @Override
      public void onDrawerOpened() {

      }
    });
  }

  @Override
  public HomeScreenPresenter getPresenter() {
    return presenter;
  }

  @Override
  public void showSlidingDrawer() {
    animate(drawer).alpha(1.0f).setDuration(250).start();
    drawer.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideSlidingDrawer() {
    animate(drawer).alpha(0).setDuration(250).start();
    drawer.setVisibility(View.GONE);
  }

  @Override
  public void openSlidingDrawer() {
    drawer.animateOpen();
  }

  @Override
  public void closeSlidingDrawer() {
    drawer.animateClose();
  }

  public void setDrawerContentFragment(Fragment fragment) {
    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
    tx.add(R.id.drawer_content, fragment);
    tx.commit();
  }

  @Override
  public void showWelcomeView(boolean animate) {
    if (animate) {
      infoboxFragment.animateContentView(InfoBoxFragment.CONTENT_WELCOME);
    } else {
      infoboxFragment.setContentView(InfoBoxFragment.CONTENT_WELCOME);
    }
  }

  @Override
  public void showRecordView() {
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_RECORD);
    setDrawerContentFragment(personFinderFragment);
  }

  @Override
  public void showPlayView() {
    hideSlidingDrawer();
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_PLAY);
    setDrawerContentFragment(movieDetailsFragment);
    movieDetailsFragment.loadNextSuggestion(new SuggestionLoadedHandler(this));
  }

  @Override
  public void showWatchlistView() {
    // TODO Auto-generated method stub

  }

  public void onLikeModeButtonClicked(View view) {
    ToggleButton button = (ToggleButton) view;
    AnimatorSet anim = new AnimatorSet();
    anim.playTogether(ObjectAnimator.ofFloat(button, "scaleX", 1, 1.3f, 1),
        ObjectAnimator.ofFloat(button, "scaleY", 1, 1.2f, 1));
    anim.setDuration(500);
    anim.start();
    if (button.isChecked()) {
      presenter.enterRecordingMode();
    } else {
      presenter.start(true);
    }
  }

  @Override
  public void onBackPressed() {
    if (drawer.isOpened() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
      closeSlidingDrawer();
    } else {
      super.onBackPressed();
    }
  }
  
  public static class SuggestionLoadedHandler extends DefaultTmdbApiHandler<Movie> {

    public SuggestionLoadedHandler(Context context) {
      super(context);
    }

    @Override
    public boolean onTaskStarted(Context context) {
      HomeActivity activity = (HomeActivity) context;
      activity.hideSlidingDrawer();
      return super.onTaskStarted(context);
    }
    
    @Override
    public boolean onTaskSuccess(Context context, Movie result) {
      HomeActivity activity = (HomeActivity) context;
      activity.showSlidingDrawer();
      return super.onTaskSuccess(context, result);
    }
  }
}
