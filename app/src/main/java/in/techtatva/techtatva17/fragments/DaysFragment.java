package in.techtatva.techtatva17.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.techtatva.techtatva17.R;
import in.techtatva.techtatva17.adapters.EventsAdapter;
import in.techtatva.techtatva17.application.TechTatva;
import in.techtatva.techtatva17.models.events.EventDetailsModel;
import in.techtatva.techtatva17.models.events.EventsListModel;
import io.realm.Realm;


public class DaysFragment extends Fragment {
    private static final String ARG_PARAM1 = "day";
    private static final String ARG_PARAM2 = "search";
    private int day;
    private String searchTerm;

    private EventsListModel currentDayEvents  = new EventsListModel();
    RecyclerView daysRecyclerView;
    public static EventsAdapter adapter;
    List<EventDetailsModel> events;
    Realm realm = Realm.getDefaultInstance();

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public DaysFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param day  This is the day which is selected by the user.
     * @return A new instance of fragment DaysFragment.
     */
    public static DaysFragment newInstance(int day, String searchTerm) {
        DaysFragment fragment = new DaysFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, day);
        args.putString(ARG_PARAM2, searchTerm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getInt(ARG_PARAM1);
            searchTerm =  getArguments().getString(ARG_PARAM2,"");
        }

        getSearchDataFromRealm(searchTerm);
        Log.i("REALM", String.valueOf(day) );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(container==null)
            Log.i("onCreateView","NULL Container");
        View view = inflater.inflate(R.layout.fragment_days, container, false);
        daysRecyclerView =(RecyclerView) view.findViewById(R.id.days_recycler_view);
        EventsAdapter.FavouriteClickListener favouriteClickListener = new EventsAdapter.FavouriteClickListener() {
            @Override
            public void onItemClick(EventDetailsModel event) {
                //Favourite Clicked
                //TODO : Add the favourite Event to the DB and make the Favourite Icon red
            }
        };
        EventsAdapter.EventClickListener eventClickListener = new EventsAdapter.EventClickListener() {
            @Override
            public void onItemClick(EventDetailsModel event) {
                //Event Item Clicked
                //TODO : Launch Event Activity
            }
        };

        adapter = new EventsAdapter(currentDayEvents, eventClickListener, favouriteClickListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        daysRecyclerView.setLayoutManager(layoutManager);
        daysRecyclerView.setItemAnimator(new DefaultItemAnimator());
        daysRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(daysRecyclerView.getContext(),getResources().getConfiguration().orientation);
        daysRecyclerView.addItemDecoration(dividerItemDecoration);
        return view;
    }

    public void getDataFromRealm(){
        events = realm.where(EventDetailsModel.class).equalTo("day",(day+1)+"").findAllSorted("eventName");
        currentDayEvents.setEvents(events);


    }

    public void getSearchDataFromRealm(String text){
        events = realm.where(EventDetailsModel.class).equalTo("day",(day+1)+"").contains("eventName",text).findAllSorted("eventName");
        currentDayEvents.setEvents(events);
        //adapter.notifyDataSetChanged();

    }





}
