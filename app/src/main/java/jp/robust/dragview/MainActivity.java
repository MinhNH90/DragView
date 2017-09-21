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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class MainActivity extends Activity {

    private View layoutPopup;

    private ImageView imgGoogle;
    private ImageView imgFacebook;
    private ImageView imgInstagram;
    private ImageView imgYoutube;

    private TableLayout rlGroup;

    private LinearLayout lnBackground;
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

        imgGoogle = (ImageView) findViewById(R.id.imgGoogle);
        imgFacebook = (ImageView) findViewById(R.id.imgFacebook);
        imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        imgYoutube = (ImageView) findViewById(R.id.imgYoutube);

        lnListIcon = (LinearLayout) findViewById(R.id.lnListIcon);
        lnBackground = (LinearLayout) findViewById(R.id.lnBackground);

        rlGroup = (TableLayout) findViewById(R.id.rlGroup);

        imgGoogle.setOnTouchListener(new TouchListener());
        imgFacebook.setOnTouchListener(new TouchListener());
        imgInstagram.setOnTouchListener(new TouchListener());
        imgYoutube.setOnTouchListener(new TouchListener());

        lnBackground.setOnDragListener(new DragListener());
        lnListIcon.setOnDragListener(new DragListener());
        rlGroup.setOnDragListener(new DragListener());

        rlGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildCount(v)) {
                    toggleGroup();
                }
            }
        });

        lnBackground.setOnClickListener(new View.OnClickListener() {
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
                moveChildCount();
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
                        TableLayout container = (TableLayout) v;
                        container.startAnimation(animationUp);
                    }

                    if (v.getId() != R.id.lnListIcon && layoutPopup.getVisibility() == View.VISIBLE) {
                        showDialog(false);
                    }

                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    if (v.getId() == R.id.rlGroup) {
                        TableLayout container = (TableLayout) v;
                        container.startAnimation(animationDown);
                    }
                    break;

                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    if (v.getId() == R.id.lnBackground || v.getId() == R.id.lnListIcon) {
                        LinearLayout container = (LinearLayout) v;
                        addViewToLinearLayout(container, view);

                    } else if (v.getId() == R.id.rlGroup) {
                        TableLayout container = (TableLayout) v;
                        addViewToTable(container, view);
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

    private void toggleGroup() {
        if (layoutPopup.getVisibility() == View.VISIBLE) {
            showDialog(false);
        } else {
            showDialog(true);
        }
    }

    private boolean getChildCount(View view) {
        if (view.getId() == R.id.rlGroup) {
            TableLayout container = (TableLayout) view;
            int childCount = container.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = container.getChildAt(0);
                if (v != null) {
                    container.removeView(v);
                    addViewToLinearLayout(lnListIcon, v);
                    v.setVisibility(View.VISIBLE);
                }
            }

            if (childCount > 0 || lnListIcon.getChildCount() > 0) {
                return true;
            }
        }

        return false;
    }

    private void moveChildCount() {
        TableLayout container = (TableLayout) findViewById(R.id.rlGroup);
        int childCount = lnListIcon.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = lnListIcon.getChildAt(0);
            if (v != null) {
                lnListIcon.removeView(v);
                addViewToTable(container, v);
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDialog(boolean isShow) {
        if (isShow && layoutPopup.getVisibility() == View.GONE) {
            layoutPopup.startAnimation(animationFadeIn);

        } else if (!isShow && layoutPopup.getVisibility() == View.VISIBLE) {
            layoutPopup.startAnimation(animationFadeOut);
        }
    }

    private void addViewToTable(TableLayout tableLayout, View view) {
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        tableParams.weight = 1;
        view.setEnabled(false);
        tableLayout.addView(view, tableParams);
    }

    private void addViewToLinearLayout(LinearLayout linearLayout, View view) {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setEnabled(true);
        linearLayout.addView(view, linearLayoutParams);
    }
}
