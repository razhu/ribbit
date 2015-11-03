package com.casasmap.ribbit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by ramiro on 10/31/15.
 */
public class FriendsFragment extends ListFragment {
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }
    //adding onResume for friends
    @Override
    public void onResume() {
        super.onResume();
        //getting the current user
        mCurrentUser = ParseUser.getCurrentUser();
        //now the relation
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //setting up the progress bar indicator
//        //deprecated
//        getActivity().setProgressBarIndeterminateVisibility(true);

        //not sure
        ParseQuery<ParseUser> query = mFriendRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                //deprecated
//                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    //it worked. create list and adapter
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);

                } else {
                    //something didnt work well. show alert message.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setTitle(R.string.title_error);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


            }
        });


    }
}
