package ch.epfl.balelecbud.view.welcome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;

/**
 * Class adapting a page displayed in the welcome fragment
 */
public class WelcomePagerAdapter extends PagerAdapter {

    private Context context;
    private List<HelpPage> pages;

    public WelcomePagerAdapter(Context context, List<HelpPage> pages) {
        this.context = context;
        this.pages = pages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.item_welcome, null);

        ImageView image = layout.findViewById(R.id.welcome_view_image);
        TextView title = layout.findViewById(R.id.welcome_view_title);
        TextView description = layout.findViewById(R.id.welcome_view_description);

        int resID = context
                .getResources()
                .getIdentifier(pages.get(position).getImageName(), "drawable", context.getPackageName());

        image.setImageResource(resID);
        title.setText(pages.get(position).getTitle());
        description.setText(pages.get(position).getDescription());

        container.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
