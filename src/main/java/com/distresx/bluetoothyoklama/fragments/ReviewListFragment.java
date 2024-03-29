package com.distresx.bluetoothyoklama.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.distresx.bluetoothyoklama.R;
import com.distresx.bluetoothyoklama.adapters.ReviewAdapter;
import com.distresx.bluetoothyoklama.others.Question;

import java.util.ArrayList;


public class ReviewListFragment extends Fragment {

    ListView reviewList;
    ArrayList<Question> questions;
    int questionOrder[], answerMarked[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);
        reviewList = (ListView) view.findViewById(R.id.reviewListView);

        ReviewAdapter adapter = new ReviewAdapter(reviewList.getContext(), questions, questionOrder, answerMarked);
        reviewList.setAdapter(adapter);

        return view;
    }

    public void setQuestions(ArrayList<Question> q) {
        questions = q;
    }

    public void setAnswerMarked(int[] am) {
        answerMarked = am;
    }

    public void setQuestionOrder(int[] qo) {
        questionOrder = qo;
    }
}
