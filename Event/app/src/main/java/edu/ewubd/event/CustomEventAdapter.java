package edu.ewubd.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class CustomEventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;

    public CustomEventAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, -1, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.event_row, parent, false);

        TextView eventName = rowView.findViewById(R.id.tvEventName);
        TextView EventDate = rowView.findViewById(R.id.EventDate);
        TextView tvEventPlace = rowView.findViewById(R.id.tvEventPlace);

        eventName.setText(events.get(position).getName());
        EventDate.setText(events.get(position).getDateAndTime());
        tvEventPlace.setText(events.get(position).getPlace());

        return rowView;
    }
}
