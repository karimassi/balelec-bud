package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.FestivalInformation.FestivalInformation;
import ch.epfl.balelecbud.FestivalInformation.FestivalInformationAdapter;

public class FestivalInformationActivity extends BasicActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter festivalInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_info);

        recyclerView = findViewById(R.id.festivalInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        festivalInfoAdapter = new FestivalInformationAdapter();
        recyclerView.setAdapter(festivalInfoAdapter);
    }
}