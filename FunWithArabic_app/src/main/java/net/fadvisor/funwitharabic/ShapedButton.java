package net.fadvisor.funwitharabic;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ShapedButton extends Button implements View.OnTouchListener {

    private int buttonColor;
    private int pressedShift = 20;  // The amount of shift between the normal and pressed state of the button, used to push the text down with the button
    private Drawable pressedButton;
    private Drawable normalButton;
    private Bitmap bitmap;

    public ShapedButton(Context context) {
        super(context);
        setDefaults();
        this.setOnTouchListener(this);
    }

    public ShapedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaults();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    public ShapedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaults();
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Set the button background and color
        updateButton(normalButton);
//        this.setPadding(0, 0, 0, 0);
    }

    private void setDefaults() {
        //Init default values
        Resources resources = getResources();
        if (resources == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            buttonColor = resources.getColor(R.color.blue, null);
        } else {
            //noinspection deprecation
            buttonColor = resources.getColor(R.color.blue);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pressedButton = getResources().getDrawable(R.drawable.hex_button_white_pressed, null);
            normalButton = getResources().getDrawable(R.drawable.hex_button_white_normal, null);
        } else {
            //noinspection deprecation
            pressedButton = getResources().getDrawable(R.drawable.hex_button_white_pressed);
            //noinspection deprecation
            normalButton = getResources().getDrawable(R.drawable.hex_button_white_normal);
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hex_button_white_normal);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        //Load from custom attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapedButton);
        if (typedArray == null) return;
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.ShapedButton_color) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    buttonColor = typedArray.getColor(attr, getResources().getColor(R.color.blue, null));
                } else {
                    //noinspection deprecation
                    buttonColor = typedArray.getColor(attr, getResources().getColor(R.color.blue));
                }
            } else if (attr == R.styleable.ShapedButton_pressedShift) {
                pressedShift = typedArray.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.ShapedButton_normalBackground) {
                Drawable drawable = typedArray.getDrawable(attr);
                if (drawable != null) {
                    normalButton = drawable;

                    // Try to find away to get this from the xml, so it will be based on whatever the user set
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hex_button_white_normal);

                }
            } else if (attr == R.styleable.ShapedButton_pressedBackground) {
                Drawable drawable = typedArray.getDrawable(attr);
                if (drawable != null) {
                    pressedButton = drawable;
                }
            }
        }
        typedArray.recycle();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        // Get the dimensions used in the view
        int realW = this.getWidth();
        int realH = this.getHeight();

        // Get the dimensions of the actual image
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();

        // Scale the coordinates from the view to match the actual image
        float scaledX = x * bitmapW / realW;
        float scaledY = y * bitmapH / realH;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (scaledX >= 0 & scaledY >= 0 & scaledX < bitmapW & scaledY < bitmapH) {
                    if (bitmap.getPixel((int) scaledX, (int) scaledY) != 0) {
                        updateButton(pressedButton);
                        this.setPadding(0, pressedShift, 0, 0);
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Rect rect = new Rect();
                view.getLocalVisibleRect(rect);
                if (!rect.contains((int) x, (int) y)) {
                    updateButton(normalButton);
                    this.setPadding(0, 0, 0, 0);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                updateButton(normalButton);
                this.setPadding(0, 0, 0, 0);
                if (scaledX >= 0 & scaledY >= 0 & scaledX < bitmap.getWidth() & scaledY < bitmap.getHeight()) {
                    if (bitmap.getPixel((int) scaledX, (int) scaledY) != 0) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private void updateButton(Drawable background) {
        if (background == null) return;
        //Set button background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(background);
        } else {
            //noinspection deprecation
            this.setBackgroundDrawable(background);
        }
        this.getBackground().setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
//        this.setBackgroundColor(buttonColor);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (this.isEnabled()) {
            this.getBackground().setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
        } else {
            int disabledColor;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                disabledColor = getResources().getColor(R.color.gray_light, null);
            } else {
                //noinspection deprecation
                disabledColor = getResources().getColor(R.color.gray_light);
            }
            this.getBackground().setColorFilter(disabledColor, PorterDuff.Mode.MULTIPLY);
        }
    }


}
