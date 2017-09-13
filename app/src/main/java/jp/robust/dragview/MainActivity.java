package jp.robust.dragview;

import android.content.ClipData;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow;

    private Animation animationScaleUp;
    private Animation animationScaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initPopupWindow();

        animationScaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        animationScaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        findViewById(R.id.imgGoogle).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgFacebook).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgInstagram).setOnTouchListener(new TouchListener());
        findViewById(R.id.imgYoutube).setOnTouchListener(new TouchListener());

        findViewById(R.id.lnBackground).setOnDragListener(new DragListener());
        findViewById(R.id.rlGroup).setOnDragListener(new DragListener());

        findViewById(R.id.rlGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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
                    if (v instanceof RelativeLayout) {
                        RelativeLayout container = (RelativeLayout) v;
                        container.startAnimation(animationScaleUp);
                    }
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    if (v instanceof RelativeLayout) {
                        RelativeLayout container = (RelativeLayout) v;
                        container.startAnimation(animationScaleDown);
                    }
                    break;

                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    if (v instanceof RelativeLayout) {
                        RelativeLayout container = (RelativeLayout) v;
                        container.addView(view);
                        container.startAnimation(animationScaleDown);

                    } else if (v instanceof LinearLayout) {
                        LinearLayout container = (LinearLayout) v;
                        container.addView(view);
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

    private void initPopupWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.layout_popup, null);
        popupWindow = new PopupWindow(
                popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
