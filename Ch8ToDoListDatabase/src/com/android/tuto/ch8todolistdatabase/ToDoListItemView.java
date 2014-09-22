package com.android.tuto.ch8todolistdatabase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * to customize the text view
 * 
 * @author minhducngo
 *
 */
public class ToDoListItemView extends TextView {

    private Paint marginPaint;
    private Paint linePaint;
    private int paperColor;
    private float margin;

    public ToDoListItemView(Context context, AttributeSet ats, int ds) {
        super(context, ats, ds);
        init();
    }

    public ToDoListItemView(Context context) {
        super(context);
        init();
    }

    public ToDoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // gets reference to our resource table
        Resources res = getResources();

        // create paint brushes we will use in the onDraw method
        marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        marginPaint.setColor(res.getColor(R.color.notepad_margin));
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(res.getColor(R.color.notepad_lines));

        // Get the paper background color and the margin width.
        paperColor = res.getColor(R.color.notepad_paper);
        margin = res.getDimension(R.dimen.notepad_margin);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // color as paper
        canvas.drawColor(paperColor);

        // draw rules line
        canvas.drawLine(0, 0, 0, getMeasuredHeight(), linePaint);
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);

        // Draw margin
        canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);

        // Move the text across from the margin
        canvas.save();
        canvas.translate(margin, 0);

        // Use the base TextView to render the text.
        super.onDraw(canvas);
        canvas.restore();
    }
}
