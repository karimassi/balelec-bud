package ch.epfl.balelecbud.view.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Picture;
import ch.epfl.balelecbud.utility.DateFormatter;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;
import ch.epfl.balelecbud.view.ConnectivityFragment;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;
import static ch.epfl.balelecbud.utility.storage.Storage.USER_PICTURES;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public final class GalleryFragment extends ConnectivityFragment {

    public final static String TAG = GalleryFragment.class.getSimpleName();
    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private RefreshableRecyclerViewAdapter<Picture, PictureHolder> adapter;
    private String currentPhotoPath;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView rvGallery = getView().findViewById(R.id.gallery_recycler_view);

        PictureData data = new PictureData();
        adapter = new RefreshableRecyclerViewAdapter<>(PictureHolder::new, rvGallery, data, R.layout.item_gallery);

        rvGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvGallery.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_gallery);
        adapter.setOnRefreshListener(refreshLayout);

        FloatingActionButton cameraButton = getActivity().findViewById(R.id.fab_upload_picture);
        cameraButton.setOnClickListener(v -> askCameraPermissions());

    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getActivity(), R.string.camera_permission_refused_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File f = new File(currentPhotoPath);
            Log.d(TAG, "Absolute Url of Image is " + Uri.fromFile(f));
            try {
                getAppStorage().putFile(USER_PICTURES, f.getName(), f);
                Snackbar.make(getView(), R.string.pic_uploaded_message, LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.w(TAG, "Error while putting the image in the storage", e);
            }
            adapter.reloadData();
        }

    }

    private File createImageFile() throws IOException {
        File image = File.createTempFile(
                "JPEG_" + DateFormatter.FILE_TIMESTAMP.format(new Date()) + "_",  /* prefix */
                ".jpg",                                                           /* suffix */
                getActivity().getFilesDir()                                              /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "ch.epfl.balelecbud.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }
}
