package com.alistairholmes.devjournal.adapters;

import android.content.ClipData;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alistairholmes.devjournal.R;
import com.alistairholmes.devjournal.database.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder>{

    // Constant for date format
    private static final String DATE_FORMAT_DAY = "dd";
    private static final String DATE_FORMAT_MONTH = "MMM";


    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds journal data and the Context
    private List<JournalEntry> mJournalEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dayDateFormat = new SimpleDateFormat(DATE_FORMAT_DAY, Locale.getDefault());
    private SimpleDateFormat monthDateFormat = new SimpleDateFormat(DATE_FORMAT_MONTH, Locale.getDefault());

    /**
     * Constructor for the JournalAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public JournalAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new JournalViewHolder that holds the view for each entry
     */
    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the journal_entry_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.journal_entry_layout, parent, false);

        return new JournalViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        // Determine the values of the wanted data
        JournalEntry journalEntry = mJournalEntries.get(position);
        String description = journalEntry.getDescription();
        String title = journalEntry.getTitle();
        String updatedAt = dayDateFormat.format(journalEntry.getDate());
        String monthUpdate = monthDateFormat.format(journalEntry.getDate());

        //Set values
        holder.title_tv.setText(title);
        holder.description_tv.setText(description);
        holder.updatedAt_tv.setText(updatedAt);
        holder.month_tv.setText(monthUpdate);

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mJournalEntries == null) {
            return 0;
        }
        return mJournalEntries.size();
    }

    public List<JournalEntry> getEntries() {
        return mJournalEntries;
    }

    /**
     * When data changes, this method updates the list of JournalEntries
     * and notifies the adapter to use the new values on it
     */
    public void setEntries(List<JournalEntry> taskEntries) {
        mJournalEntries = taskEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the entry description and title TextViews
        TextView description_tv;
        TextView updatedAt_tv;
        TextView title_tv;
        TextView month_tv;

        /**
         * Constructor for the JournalViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public JournalViewHolder(View itemView) {
            super(itemView);

            description_tv = itemView.findViewById(R.id.entry_description);
            updatedAt_tv = itemView.findViewById(R.id.entry_day);
            title_tv = itemView.findViewById(R.id.entry_title);
            month_tv = itemView.findViewById(R.id.entry_month);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mJournalEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }

    }
}
