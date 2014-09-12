package com.android.tutorial.ch4todolistfragment;

import com.android.tutorial.ch4todolistfragment.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewItemFragment extends Fragment {

    /** The listener for adding new to-do item */
    private OnNewItemAddedListener onNewItemAddedListener;

    /**
     * Each Fragment should encapsulate the functionality that it provides.<br/>
     * In this case, that accepts the new to-do item added to the list.
     * 
     * @author minhducngo
     *
     */
    public interface OnNewItemAddedListener {
        public void onNewItemAdded(String newItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.new_item_fragment, container, false);
        View view = inflater.inflate(R.layout.new_item_fragment, container, false);

        final EditText myEditText = (EditText) view.findViewById(R.id.myEditText);

        myEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_ENTER) {
                        String newItem = myEditText.getText().toString();
                        onNewItemAddedListener.onNewItemAdded(newItem);
                        myEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    /**
     * to get the reference as soon as the parent activity has been bound to the Fragment.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onNewItemAddedListener = (OnNewItemAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNewItemAddedListener");
        }
    }
}
