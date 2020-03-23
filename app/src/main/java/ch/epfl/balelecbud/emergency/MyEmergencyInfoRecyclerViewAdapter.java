package ch.epfl.balelecbud.emergency;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;
import ch.epfl.balelecbud.emergency.EmergencyInfoListFragment;
import ch.epfl.balelecbud.util.database.DatabaseListener;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.facades.RecyclerViewAdapterFacade;

public class MyEmergencyInfoRecyclerViewAdapter extends RecyclerView.Adapter<MyEmergencyInfoRecyclerViewAdapter.ViewHolder> {

    //default implementation is Firebase
    static private DatabaseWrapper database = FirestoreDatabaseWrapper.getInstance();

    //Used to insert mocks
    @VisibleForTesting
    public static void setDatabaseImplementation(DatabaseWrapper databaseWrapper){
        database = databaseWrapper;
    }

    private final List<EmergencyInfo> mValues;
    private final EmergencyInfoListFragment.OnListFragmentInteractionListener mListener;

    public MyEmergencyInfoRecyclerViewAdapter(EmergencyInfoListFragment.OnListFragmentInteractionListener fragmentInteractionListenerl) {
        mValues = new LinkedList<>();
        RecyclerViewAdapterFacade facade = new RecyclerViewAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                MyEmergencyInfoRecyclerViewAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                MyEmergencyInfoRecyclerViewAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                MyEmergencyInfoRecyclerViewAdapter.this.notifyItemRemoved(position);
            }
        };
        DatabaseListener<EmergencyInfo> listener = new DatabaseListener(facade, mValues, EmergencyInfo.class);
        database.listen(DatabaseWrapper.EMERGENCY_INFO_PATH, listener);
        mListener = fragmentInteractionListenerl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergencyinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nameView.setText(mValues.get(position).getInstruction());
        holder.instructionView.setText(mValues.get(position).getName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView nameView;
        public final TextView instructionView;
        public EmergencyInfo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nameView = (TextView) view.findViewById(R.id.emergencyInfoName);
            instructionView = (TextView) view.findViewById(R.id.emergencyInfoInstruction);
        }

    }
}
