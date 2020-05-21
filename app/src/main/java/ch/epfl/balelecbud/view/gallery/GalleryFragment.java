package ch.epfl.balelecbud.view.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Picture;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

public final class GalleryFragment extends Fragment {

    public final static String TAG = GalleryFragment.class.getSimpleName();

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        RecyclerView rvGallery = getView().findViewById(R.id.galleryRecyclerView);
        View freshnessView = getView().findViewById(R.id.freshness_info_layout);


        PictureData data = new PictureData();
        RefreshableRecyclerViewAdapter<Picture, PictureHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                PictureHolder::new, freshnessView, data, R.layout.item_gallery);

        rvGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvGallery.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_gallery);
        adapter.setOnRefreshListener(refreshLayout);
    }
}