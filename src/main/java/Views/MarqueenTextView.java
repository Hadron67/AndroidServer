package Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by cfy on 15-12-15.
 */
public class MarqueenTextView extends TextView{
    public MarqueenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueenTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
