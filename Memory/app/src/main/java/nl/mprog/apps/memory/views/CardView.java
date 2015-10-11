package nl.mprog.apps.memory.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.mprog.apps.memory.models.Card;
import nl.mprog.apps.memory.models.Memory;
import nl.mprog.apps.memory.models.Theme;

public class CardView extends ImageView {
    protected Card card;

    protected Theme theme;

    protected Uri backSide;
    protected Uri frontSide;

    protected Integer positionIndex;

    public CardView(Context context) {
        super(context);
        this.setDefaultCardSettings();
    }

    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setDefaultCardSettings();
    }

    public void setCard(Card card, Integer positionIndex) {
        this.card = card;
        this.positionIndex = positionIndex;
        this.loadImage();
    }

    public Integer getPositionIndex() {
        return this.positionIndex;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        this.loadImage();
    }

    public void flipCard() {
        if (this.card == null || this.card.isDisabled()) { return; }
        this.card.setVisible(!this.card.isVisible());
        this.renderCard();
    }

    public void renderCard() {
        this.setVisibleImage();
    }

    protected void loadImage() {
        if (this.card == null || this.theme == null) {
            return;
        }

        this.backSide = Uri.fromFile(this.theme.getBackSideImage());
        this.frontSide = Uri.fromFile(this.theme.getFrontSideFor(this.card.getImageIndex()));

        this.setVisibleImage();
    }

    protected void setDefaultCardSettings() {
        Integer cardMargin = this.convertDPToInteger(Memory.DEFAULT_CARD_MARGIN);
        Integer cardWidth = this.convertDPToInteger(Memory.DEFAULT_IMAGE_WIDTH);
        Integer cardHeight = this.convertDPToInteger(Memory.DEFAULT_IMAGE_HEIGHT);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cardWidth, cardHeight);
        layoutParams.setMargins(cardMargin, cardMargin, cardMargin, cardMargin);

        this.setLayoutParams(layoutParams);

        this.setBackgroundColor(Memory.DEFAULT_CARD_COLOR);

        this.requestLayout();
    }

    protected void setVisibleImage() {
        if (this.card.isVisible()) {
            this.setImageURI(this.frontSide);
        } else {
            this.setImageURI(null);
            this.setImageURI(this.backSide);
        }

        this.setBackgroundColor(Color.LTGRAY);
        this.setAdjustViewBounds(true);
        this.setScaleType(ScaleType.FIT_XY);

        this.invalidate();
        this.requestLayout();
    }

    protected Integer convertDPToInteger(Integer dpValue) {
        Resources resources = getResources();
        Integer unit = TypedValue.COMPLEX_UNIT_DIP;
        DisplayMetrics metrics = resources.getDisplayMetrics();

        return Math.round(TypedValue.applyDimension(unit, dpValue, metrics));
    }
}
