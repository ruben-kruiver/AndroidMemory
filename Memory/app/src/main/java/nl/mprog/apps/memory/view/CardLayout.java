package nl.mprog.apps.memory.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.GridLayout;

import nl.mprog.apps.memory.model.Theme;

public class CardLayout extends GridLayout {

    protected Theme theme;

    public CardLayout(Context context) {
        super(context);
    }

    public CardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;

        Drawable drawable = Drawable.createFromPath(this.theme.getBackgroundImage().getAbsolutePath());

        this.setBackground(drawable);

        this.invalidate();
        this.requestLayout();
    }
}
