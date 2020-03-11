package ch.epfl.balelecbud.Transport;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.Transport.Object.Transport;
import ch.epfl.balelecbud.Transport.TransportListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTransportRecyclerViewAdapter extends RecyclerView.Adapter<MyTransportRecyclerViewAdapter.ViewHolder> {

    private final List<Transport> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyTransportRecyclerViewAdapter(List<Transport> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transport_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.typeView.setText(mValues.get(position).getTypeString());
        holder.lineView.setText(mValues.get(position).getLineString());
        holder.directionView.setText(mValues.get(position).getDirection());
        holder.timeView.setText(mValues.get(position).getTimeString());
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
        public final TextView typeView;
        public final TextView lineView;
        public final TextView directionView;
        public final TextView timeView;
        public Transport mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeView = (TextView) view.findViewById(R.id.type);
            lineView = (TextView) view.findViewById(R.id.line);
            directionView = (TextView) view.findViewById(R.id.direction);
            timeView = (TextView) view.findViewById(R.id.time);
        }

    }
}
