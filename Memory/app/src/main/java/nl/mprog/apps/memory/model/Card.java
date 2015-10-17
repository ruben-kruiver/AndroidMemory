package nl.mprog.apps.memory.model;

import nl.mprog.apps.memory.view.CardView;

/**
 * This class contains the state of the current card
 * as well as an index to image in the applied theme
 */
public class Card {
    protected boolean isVisible; // Flag if the frontside should be displayed

    protected Integer imageIndex; // The index that references to a theme index

    protected boolean disabled;

    protected CardView cardView;

    public Card() {
        this.isVisible = false;
        this.disabled = false;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public Integer getImageIndex() {
        return this.imageIndex;
    }

    public void setImageIndex(Integer index) {
        this.imageIndex = index;
    }

    public void setVisible(Boolean visible) {
        if (!this.disabled) {
            this.isVisible = visible;
        }

        if (this.cardView != null) {
            this.cardView.renderCard();
        }
    }

    public Boolean isVisible() {
        return this.isVisible;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return this.disabled;
    }
}
