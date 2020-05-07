package ch.epfl.balelecbud.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import ch.epfl.balelecbud.R;

public class GalleryAdapter extends ArrayAdapter {
    ArrayList<Integer> imagesList;

    public GalleryAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        imagesList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_gallery, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        imageView.setImageResource(imagesList.get(position));
        return v;
    }
}