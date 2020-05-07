package ch.epfl.balelecbud.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

import ch.epfl.balelecbud.R;

public class GalleryFragment extends Fragment {
    GridView simpleList;
    ArrayList<Integer> images = new ArrayList<>(Arrays.asList(R.drawable.gallery1, R.drawable.gallery2, R.drawable.gallery3, R.drawable.gallery4, R.drawable.gallery5,
            R.drawable.gallery6, R.drawable.gallery7, R.drawable.gallery8, R.drawable.gallery9));
    public final static String TAG = GalleryFragment.class.getSimpleName();

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        simpleList = (GridView) getActivity().findViewById(R.id.gallery_grid_view);
        GalleryAdapter adapter = new GalleryAdapter(getActivity(), R.layout.item_gallery, images);
        simpleList.setAdapter(adapter);
    }
}