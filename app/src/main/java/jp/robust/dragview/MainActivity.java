package jp.robust.dragview;

import android.app.Activity;
import android.content.ClipData;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private View layoutPopup;
    private View viewBackground;
    private LinearLayout lnListIcon;

    private Animation animationUp;
    private Animation animationDown;

    private Animation animationFadeIn;
    private Animation animationFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        animationUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        animationDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.alpha_show);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.alpha_hidden);

        layoutPopup = (View) findViewById(R.id.layoutPopup);
        viewBackground = (View) findViewById(R.id.viewBackground);
        lnListIcon = (LinearLayout) findViewById(R.id.lnListIcon);

        findViewById(R.id.imgGoogle).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgFacebook).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgInstagram).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgYoutube).setOnTouchListener(new TouchListener());

        findViewById(R.id.lnBackground).setOnDragListener(new DragListener());
        findViewById(R.id.lnListIcon).setOnDragListener(new DragListener());
        findViewById(R.id.rlGroup).setOnDragListener(new DragListener());

        findViewById(R.id.rlGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildCount(v)) {
                    toggleGroup(v);
                }
            }
        });

        viewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(false);
            }
        });

        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutPopup.setVisibility(View.VISIBLE);
                viewBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutPopup.setVisibility(View.GONE);
                viewBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private final class TouchListener implements View.OnTouchListener {

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                } else {
                    view.startDrag(data, shadowBuilder, view, 0);
                }
                view.setVisibility(View.INVISIBLE);
                showDialog(false);

                return true;

            } else {
                return false;
            }
        }
    }

    class DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v.getId() == R.id.rlGroup) {
                        RelativeLayout container = (RelativeLayout) v;
                        showDialog(false);
                        container.startAnimation(animationUp);
                    }

                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    if (v.getId() == R.id.rlGroup) {
                        RelativeLayout container = (RelativeLayout) v;
                        container.startAnimation(animationDown);
                    }
                    break;

                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    if (v.getId() == R.id.lnBackground) {
                        LinearLayout container = (LinearLayout) v;
                        container.addView(view);

                    } else if (v.getId() == R.id.rlGroup) {
                        RelativeLayout container = (RelativeLayout) v;
                        container.addView(view);
                        view.setEnabled(false);
                        container.startAnimation(animationDown);
                    }

                    view.setVisibility(View.VISIBLE);
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    private void toggleGroup(View view) {
        if (layoutPopup.getVisibility() == View.VISIBLE) {
            showDialog(false);
        } else {
            showDialog(true);
        }
    }

    private boolean getChildCount(View view) {
        if (view.getId() == R.id.rlGroup) {
            RelativeLayout container = (RelativeLayout) view;
            int childCount = container.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = container.getChildAt(0);
                if (v != null) {
                    container.removeView(v);
                    lnListIcon.addView(v);
                    v.setEnabled(true);
                    v.setVisibility(View.VISIBLE);
                }
            }

            if (childCount > 0 || lnListIcon.getChildCount() > 0) {
                return true;
            }
        }

        return false;
    }

    private void showDialog(boolean isShow) {
        if (isShow) {
//            layoutPopup.startAnimation(animationFadeIn);

            layoutPopup.setVisibility(View.VISIBLE);
            viewBackground.setVisibility(View.VISIBLE);

        } else {
//            layoutPopup.startAnimation(animationFadeOut);

            layoutPopup.setVisibility(View.GONE);
            viewBackground.setVisibility(View.GONE);
        }
    }
}
