package in.techtatva.techtatva17.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.techtatva.techtatva17.R;
import in.techtatva.techtatva17.adapters.CategoriesAdapter;
import in.techtatva.techtatva17.application.TechTatva;
import in.techtatva.techtatva17.models.categories.CategoriesListModel;
import in.techtatva.techtatva17.models.categories.CategoryModel;
import in.techtatva.techtatva17.network.APIClient;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoriesFragment extends Fragment {

    private List<CategoryModel> categoriesList = new ArrayList<>();
    private Realm mDatabase;
    private CategoriesAdapter adapter;
    private ProgressDialog dialog;
    private static final int LOAD_CATEGORIES = 0;
    private static final int UPDATE_CATEGORIES = 1;
    private MenuItem searchItem;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.categories);
        mDatabase = Realm.getDefaultInstance();

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                getActivity().findViewById(R.id.app_bar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView categoriesRecyclerView = (RecyclerView)view.findViewById(R.id.categories_recycler_view);
        adapter = new CategoriesAdapter(categoriesList, getActivity());
        categoriesRecyclerView.setAdapter(adapter);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesRecyclerView.getContext(),getResources().getConfiguration().orientation);
        categoriesRecyclerView.addItemDecoration(dividerItemDecoration);

        if (mDatabase.where(CategoryModel.class).findAll().size() != 0){
            displayData();
            prepareData();
        }
        else{
                prepareData();
            }
        return view;
    }

    private void prepareData(){
        Call<CategoriesListModel> categoriesCall= APIClient.getAPIInterface().getCategoriesList();

        categoriesCall.enqueue(new Callback<CategoriesListModel>() {
            @Override
            public void onResponse(Call<CategoriesListModel> call, Response<CategoriesListModel> response) {
                if (response.body() != null && mDatabase != null){
                    mDatabase.beginTransaction();
                    mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getCategoriesList());
                    mDatabase.where(CategoryModel.class).equalTo("categoryName", "minimilitia").or().equalTo("categoryName", "Mini Militia").or().equalTo("categoryName", "Minimilitia").or().equalTo("categoryName", "MiniMilitia").or().equalTo("categoryName", "MINIMILITIA").or().equalTo("categoryName", "MINI MILITIA").findAll().deleteAllFromRealm();
                    mDatabase.commitTransaction();
                }
                displayData();
            }

            @Override
            public void onFailure(Call<CategoriesListModel> call, Throwable t) {

            }
        });
    }
    private void displayData(){

        if (mDatabase != null){
            categoriesList.clear();
            RealmResults<CategoryModel> categoryResults = mDatabase.where(CategoryModel.class).findAllSorted("categoryName");

            if (!categoryResults.isEmpty()){
                categoriesList.clear();
                categoriesList.addAll(categoryResults);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void displaySearchData(String text){

        if (mDatabase != null){
            categoriesList.clear();
            RealmResults<CategoryModel> categoryResults = mDatabase.where(CategoryModel.class).contains("categoryName",text).findAllSorted("categoryName");

            if (!categoryResults.isEmpty()){
                categoriesList.clear();
                categoriesList.addAll(categoryResults);
                adapter.notifyDataSetChanged();
            }
            else{

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDatabase = null;
        if (dialog != null && dialog.isShowing())
            dialog.hide();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hardware, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));


        searchView.setSubmitButtonEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                displaySearchData(text);
                TechTatva.searchOpen = 1;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                displaySearchData(text);
                TechTatva.searchOpen = 1;
                return false;
            }
        });
        searchView.setQueryHint("Search Categotries");




        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                displayData();
                searchView.clearFocus();

                TechTatva.searchOpen = 1;
                return false;
            }


        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setHasOptionsMenu(false);
        setMenuVisibility(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }



}
