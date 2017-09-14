package jp.robust.dragview;

import android.app.Activity;
import android.content.ClipData;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private View layoutPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        layoutPopup = (View) findViewById(R.id.layoutPopup);

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
                if (layoutPopup.getVisibility() == View.VISIBLE) {
                    layoutPopup.setVisibility(View.GONE);

                } else {
                    layoutPopup.setVisibility(View.VISIBLE);
                }
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
                        layoutPopup.setVisibility(View.VISIBLE);
                    }
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    break;

                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    if (v instanceof LinearLayout) {
                        ViewGroup owner = (ViewGroup) view.getParent();
                        owner.removeView(view);

                        LinearLayout container = (LinearLayout) v;
                        container.addView(view);
                    }
                    view.setVisibility(View.VISIBLE);
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (v instanceof RelativeLayout) {
                        layoutPopup.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }

            return true;
        }
    }
}
