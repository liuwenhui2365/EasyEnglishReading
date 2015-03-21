package com.example.liu.autotanslate;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
  /**
    * TODO
    * @author cuiran
    * @version 1.0.0
    */
public class fragment_content extends Fragment {

    private TextView textView;
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_content, container, false);
        textView = (TextView) view.findViewById(R.id.display);

        String text = getArguments().getString("从这里进来了");
        textView.setText(text);

        return view;
    }
}
