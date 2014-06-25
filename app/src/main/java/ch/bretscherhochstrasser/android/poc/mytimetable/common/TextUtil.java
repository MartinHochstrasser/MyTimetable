package ch.bretscherhochstrasser.android.poc.mytimetable.common;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

/**
 * Created by marti_000 on 24.06.2014.
 */
public class TextUtil {

    public static Spannable createStyledText(final String string, final int style) {
        SpannableString styledText = new SpannableString(string);
        styledText.setSpan(new StyleSpan(style), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText;
    }

}
