package ch.epfl.balelecbud.schedule;

public interface ScheduleAdapterFacade {

    void notifyItemInserted(int position);

    void notifyItemChanged(int position);

    void notifyItemRemoved(int position);
}
