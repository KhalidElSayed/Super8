package com.github.super8.activities;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.SlidingDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.github.super8.R;
import com.github.super8.behavior.ActsAsHomeScreen;
import com.github.super8.behavior.HomeScreenPresenter;
import com.github.super8.fragments.HeaderFragment;
import com.github.super8.fragments.InfoBoxFragment;
import com.github.super8.fragments.MovieSuggestionFragment;
import com.github.super8.fragments.PersonDetailsFragment;
import com.github.super8.fragments.PersonFinderFragment;
import com.github.super8.fragments.WatchlistFragment;
import com.github.super8.gestures.ShakeDetector;
import com.github.super8.gestures.ShakeDetector.OnShakeListener;
import com.github.super8.services.ImportFilmographyHandler;
import com.google.inject.Inject;

public class HomeActivity extends RoboSherlockFragmentActivity implements ActsAsHomeScreen,
    OnShakeListener {

  @Inject private HomeScreenPresenter presenter;
  @Inject private PersonFinderFragment personFinderFragment;
  @Inject private MovieSuggestionFragment movieDetailsFragment;
  @InjectView(R.id.drawer) private SlidingDrawer drawer;
  @InjectFragment(R.id.header_fragment) private HeaderFragment headerFragment;
  @InjectFragment(R.id.infobox_fragment) private InfoBoxFragment infoboxFragment;

  private ShakeDetector shakeDetector;
  private ImportFilmographyHandler importHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_PROGRESS);
    
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.hide();
    
    shakeDetector = new ShakeDetector();
    shakeDetector.setOnShakeListener(this);

    setContentView(R.layout.home);

    addDrawerContentFragments();

    presenter.bind(this);

    drawer.setOnDrawerCloseListener(presenter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    SensorManager sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    sensors.registerListener(shakeDetector, sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onPause() {
    super.onPause();
    SensorManager sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    sensors.unregisterListener(shakeDetector);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (importHandler != null) {
      importHandler.detach();
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // TODO Auto-generated method stub
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public HomeScreenPresenter getPresenter() {
    return presenter;
  }
  
  @Override
  public ImportFilmographyHandler getImportFilmographyHandler() {
    if (importHandler == null) {
      importHandler = new ImportFilmographyHandler(this);
    }
    return importHandler;
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
    Fragment inactiveFragment = fragment == personFinderFragment ? movieDetailsFragment
        : personFinderFragment;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction tx = fragmentManager.beginTransaction();
    fragmentManager.popBackStack(PersonDetailsFragment.TAG,
        FragmentManager.POP_BACK_STACK_INCLUSIVE);
    tx.detach(inactiveFragment);
    tx.attach(fragment);
    tx.commit();
  }

  private void addDrawerContentFragments() {
    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
    tx.add(R.id.drawer_content, movieDetailsFragment);
    tx.add(R.id.drawer_content, personFinderFragment);
    tx.commit();
  }

  @Override
  public void showWatchlistView() {
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_WATCHLIST);
    WatchlistFragment watchlist = (WatchlistFragment) getSupportFragmentManager().findFragmentById(
        R.id.watchlist_fragment);
    watchlist.refresh();
  }

  @Override
  public void showWatchlistEmptyView() {
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_HELP_TEXT);
    infoboxFragment.setHelpText(R.string.help_text_empty_watchlist_1,
        R.string.help_text_empty_watchlist_2);
  }

  @Override
  public void showRecordView() {
    headerFragment.showRecordView();
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_HELP_TEXT);
    infoboxFragment.setHelpText(R.string.help_text_in_like_mode_1,
        R.string.help_text_in_like_mode_2);
    setDrawerContentFragment(personFinderFragment);
  }

  @Override
  public void showPlayView() {
    headerFragment.showPlayView();
    infoboxFragment.setContentView(InfoBoxFragment.CONTENT_HELP_TEXT);
    infoboxFragment.setHelpText(R.string.help_text_suggestions_1, R.string.help_text_suggestions_2);
    setDrawerContentFragment(movieDetailsFragment);
  }

  @Override
  public void loadMovieSuggestion() {
    hideSlidingDrawer();
    movieDetailsFragment.loadNextSuggestion();
  }

  // public void onLikeModeButtonClicked(View view) {
  // ToggleButton button = (ToggleButton) view;
  // AnimatorSet anim = new AnimatorSet();
  // anim.playTogether(ObjectAnimator.ofFloat(button, "scaleX", 1, 1.3f, 1),
  // ObjectAnimator.ofFloat(button, "scaleY", 1, 1.2f, 1));
  // anim.setDuration(500);
  // anim.start();
  // if (button.isChecked()) {
  // presenter.enterRecordingMode();
  // } else {
  // presenter.powerOff();
  // }
  // }

  @Override
  public void onBackPressed() {
    if (drawer.isOpened() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
      closeSlidingDrawer();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public void onShake() {
    closeSlidingDrawer();
  }

  @Override
  public void disableControlPanel() {
    headerFragment.disableControlPanel();
  }

  @Override
  public void enableControlPanel() {
    headerFragment.enableControlPanel();
  }
}
