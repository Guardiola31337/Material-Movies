package com.hackvg.android.views.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.transition.Slide;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackvg.android.R;
import com.hackvg.android.mvp.presenters.MovieDetailPresenter;
import com.hackvg.android.mvp.views.DetailView;
import com.hackvg.android.utils.GUIUtils;
import com.hackvg.android.views.custom_views.ObservableScrollView;
import com.hackvg.android.views.custom_views.ScrollViewListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class MovieDetailActivity extends Activity implements DetailView,
    Palette.PaletteAsyncListener, ScrollViewListener {

    private static final int TITLE         = 0;
    private static final int DESCRIPTION   = 1;
    private static final int HOMEPAGE      = 2;
    private static final int COMPANY       = 3;
    private static final int TAGLINE       = 4;
    private static final int CONFIRMATION  = 5;

    // The time that the confirmation view will be shown (milliseconds)
    private static final int CONFIRMATION_VIEW_DELAY = 1500;

    private MovieDetailPresenter mDetailPresenter;
    private Palette.Swatch mBrightSwatch;
    private Drawable mFabDrawable;

    @InjectViews({
        R.id.activity_detail_title,
        R.id.activity_detail_content,
        R.id.activity_detail_homepage,
        R.id.activity_detail_company,
        R.id.activity_detail_tagline,
        R.id.activity_detail_confirmation_text,
    })
    List<TextView> movieInfoTextViews;

    @InjectViews({
//        R.id.activity_detail_header_tagline,
        R.id.activity_detail_header_description
    }) List<TextView> headers;

    @InjectView(R.id.activity_detail_book_info)
    View mMovieDescriptionContainer;
    @InjectView(R.id.activity_detail_fab)
    ImageView mFabButton;
    @InjectView(R.id.activity_detail_cover)                     ImageView mCoverImageView;
    @InjectView(R.id.activity_detail_confirmation_image)        ImageView mConfirmationView;
    @InjectView(R.id.activity_detail_confirmation_container)
    FrameLayout mConfirmationContainer;
    @InjectView(R.id.activity_movie_detail_scroll)
    ObservableScrollView mObservableScrollView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        // Completes the SharedElement transition on Lollipop and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

//            getWindow().getSharedElementEnterTransition().addListener(transitionListener);
            int moviePosition = getIntent().getIntExtra("movie_position", 0);
            mCoverImageView.setTransitionName("cover" + moviePosition);
            GUIUtils.makeTheStatusbarTranslucent(this);
        }

        String movieID = getIntent().getStringExtra("movie_id");

//        mFabDrawable = getResources().getDrawable(R.drawable.fab);
//        mFabButton.setBackground(mFabDrawable);

//        mObservableScrollView.setScrollViewListener(this);

        mDetailPresenter = new MovieDetailPresenter(this, movieID);
    }

    @Override
    protected void onStop() {

        super.onStop();
        mDetailPresenter.stop();
    }

    @Override
    protected void onStart() {

        super.onStart();
        mDetailPresenter.start();
    }

    @Override
    public void setImage(String url) {

        Bitmap bookCoverBitmap = MoviesActivity.sPhotoCache.get(0);
        mCoverImageView.setBackground(new BitmapDrawable(getResources(), bookCoverBitmap));

        Palette.generateAsync(bookCoverBitmap, this);
    }

    @Override
    public void setName(String title) {

        movieInfoTextViews.get(TITLE).setText(title);
    }

    @Override
    public void setDescription(String description) {

        movieInfoTextViews.get(DESCRIPTION).setText(description);
    }

    @Override
    public Context getContext() {

        return this;
    }

    @Override
    public void finish(String cause) {

        Toast.makeText(this, cause, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void showConfirmationView() {

//        GUIUtils.showViewByRevealEffect(mConfirmationContainer,
//            mFabButton, GUIUtils.getWindowWidth(this));

        animateConfirmationView();
        startClosingConfirmationView();
    }

    @Override
    public void animateConfirmationView() {

//        Drawable drawable = mConfirmationView.getDrawable();

        // Animated drawables are supported on Lollipop and higher
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            if (drawable instanceof Animatable)
//                ((Animatable) drawable).start();
//
//        } else {
//
//            mConfirmationView.startAnimation(AnimationUtils.loadAnimation(this,
//                R.anim.appear_rotate));
//        }
    }

    @Override
    public void startClosingConfirmationView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setReturnTransition(new Slide());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mObservableScrollView.setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    MovieDetailActivity.this. finishAfterTransition();

                else
                    MovieDetailActivity.this. finish();
            }

        }, CONFIRMATION_VIEW_DELAY);
    }

    @Override
    public void setHomepage(String homepage) {

        movieInfoTextViews.get(HOMEPAGE).setVisibility(View.VISIBLE);
        movieInfoTextViews.get(HOMEPAGE).setText(homepage);
    }

    @Override
    public void setCompanies(String companies) {

        movieInfoTextViews.get(COMPANY).setVisibility(View.VISIBLE);
        movieInfoTextViews.get(COMPANY).setText(companies);
    }

    @Override
    public void setTagline(String tagline) {

        movieInfoTextViews.get(TAGLINE).setText(tagline);
    }

    @Override
    public void onGenerated(Palette palette) {

        if (palette != null) {

            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
            Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
            Palette.Swatch lightSwatch = palette.getLightVibrantSwatch();

            if (lightSwatch != null) {

                mMovieDescriptionContainer.setBackgroundColor(lightSwatch.getRgb());

                ButterKnife.apply(movieInfoTextViews, GUIUtils.setter, lightSwatch.getTitleTextColor());

//                mFabDrawable.setColorFilter(lightSwatch.getRgb(), PorterDuff.Mode.ADD);
                mConfirmationContainer.setBackgroundColor(lightSwatch.getRgb());

            } else {

                int primaryColor = getResources()
                    .getColor(R.color.theme_primary);

                int accentColor = getResources()
                    .getColor(R.color.theme_accent);

//                mFabDrawable.setColorFilter(accentColor, PorterDuff.Mode.ADD);
//                mConfirmationView.setBackgroundColor(primaryColor);
                mMovieDescriptionContainer.setBackgroundColor(primaryColor);
            }

            if (lightSwatch == null && vibrantSwatch != null)
                colorBrightElements(vibrantSwatch);

            if (darkVibrantSwatch != null && lightSwatch != null)
                colorBrightElements(darkVibrantSwatch);
        }
    }

    public void colorBrightElements (Palette.Swatch brightSwatch) {

//        Drawable drawable = mConfirmationView.getDrawable();
//        drawable.setColorFilter(brightSwatch.getRgb(), PorterDuff.Mode.MULTIPLY);

        movieInfoTextViews.get(CONFIRMATION).setTextColor(brightSwatch.getRgb());
        movieInfoTextViews.get(TITLE).setTextColor(brightSwatch.getTitleTextColor());
        movieInfoTextViews.get(TITLE).setBackgroundColor(brightSwatch.getRgb());

        mBrightSwatch = brightSwatch;

        if (brightSwatch != null) {

            if (movieInfoTextViews.get(HOMEPAGE).getVisibility() == View.VISIBLE)
                GUIUtils.tintAndSetCompoundDrawable(this, R.drawable.ic_domain_white_24dp,
                    brightSwatch.getRgb(), movieInfoTextViews.get(HOMEPAGE));

            if (movieInfoTextViews.get(COMPANY).getVisibility() == View.VISIBLE)
                GUIUtils.tintAndSetCompoundDrawable(this, R.drawable.ic_public_white_24dp,
                    brightSwatch.getRgb(), movieInfoTextViews.get(COMPANY));

            ButterKnife.apply(headers, GUIUtils.setter, brightSwatch.getRgb());
        }
    }

    @OnClick(R.id.activity_detail_fab)
    public void onClick() {

        showConfirmationView();
    }

    boolean isTranslucent = false;

    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {

        if (y > mCoverImageView.getHeight()) {

            movieInfoTextViews.get(TITLE).setTranslationY(y - mCoverImageView.getHeight());

            if (!isTranslucent) {

                isTranslucent = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    GUIUtils.setTheStatusbarNotTranslucent(this);
                    getWindow().setStatusBarColor(mBrightSwatch.getRgb());
                }
            }
        }

        if (y < mCoverImageView.getHeight() && isTranslucent) {

            movieInfoTextViews.get(TITLE).setTranslationY(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                GUIUtils.makeTheStatusbarTranslucent(this);
                isTranslucent = false;
            }
        }
    }

//    private final HackVGTransitionListener transitionListener = new HackVGTransitionListener() {
//
//        @Override
//        public void onTransitionEnd(Transition transition) {
//
//        super.onTransitionEnd(transition);
//            GUIUtils.showViewByScale(mFabButton);
//        }
//    };
}
