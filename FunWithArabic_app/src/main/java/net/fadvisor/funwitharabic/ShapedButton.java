package net.fadvisor.funwitharabic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
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
        this.setOnTouchListener(this);
    }

    public ShapedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    public ShapedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Set the button background and color
        updateButton(normalButton);
        // Set the padding so the Text will show up correctly on the initial load
        this.setPadding(0, 0, 0, 0);
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

                if (normalButton != null) {
                    normalButton = normalButton.mutate();
                    normalButton.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
                }
                if (pressedButton != null) {
                    pressedButton = pressedButton.mutate();
                    pressedButton.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
                }
            } else if (attr == R.styleable.ShapedButton_pressedShift) {
                pressedShift = typedArray.getDimensionPixelSize(attr, 20);
            } else if (attr == R.styleable.ShapedButton_normalBackground) {
                Drawable drawable = typedArray.getDrawable(attr);
                if (drawable != null) {
                    normalButton = drawable.mutate();
                    normalButton.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);

                    if(normalButton instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable)normalButton).getBitmap();
                    } else{
                        bitmap = Bitmap.createBitmap(normalButton.getIntrinsicWidth(),normalButton.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        normalButton.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        normalButton.draw(canvas);
                    }

                }
            } else if (attr == R.styleable.ShapedButton_pressedBackground) {
                Drawable drawable = typedArray.getDrawable(attr);
                if (drawable != null) {
                    pressedButton = drawable.mutate();
                    pressedButton.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
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

        float scaledX;
        float scaledY;

        // Get the dimensions of the actual image
        int bitmapW;
        int bitmapH;
        int pixelColor;
        if (bitmap != null) {
            bitmapW = bitmap.getWidth();
            bitmapH = bitmap.getHeight();

            // Scale the coordinates from the view to match the actual image
            scaledX = x * bitmapW / realW;
            scaledY = y * bitmapH / realH;
            if (scaledX >= 0 & scaledY >= 0 & scaledX < bitmapW & scaledY < bitmapH) {
                pixelColor = bitmap.getPixel((int) scaledX, (int) scaledY);
            } else {
                pixelColor = 0;
            }
        } else {
            // In case we didn't set a special drawable and we use the default button
            bitmapW = realW;
            bitmapH = realH;

            scaledX = x;
            scaledY = y;
            if (scaledX >= 0 & scaledY >= 0 & scaledX < bitmapW & scaledY < bitmapH) {
                pixelColor = 1;
            } else {
                pixelColor = 0;
            }
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (pixelColor != 0) {
                    updateButton(pressedButton);
                    this.setPadding(0, pressedShift, 0, 0);
                    return false;
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
                if (pixelColor != 0) {
                    return false;
                }
                break;
            default: //do nothing
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
