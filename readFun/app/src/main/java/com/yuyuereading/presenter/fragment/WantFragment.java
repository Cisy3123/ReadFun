package com.yuyuereading.presenter.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyuereading.model.bean.BookInfo;
import com.yuyuereading.presenter.activity.AddBookActivity;
import com.yuyuereading.presenter.adapter.BookListAdapter;
import com.yuyuereading.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WantFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WantFragment extends Fragment {

    private SwipeRefreshLayout refresh;

    private RecyclerView recyclerView;

    private FloatingActionButton addBook;

    private BookInfo[] bookInfos = {new BookInfo("https://img3.doubanio.com/lpic/s29418322.jpg","芳华","2017-4-1","8.1","严歌苓","人民文学出版社","jianjie"),
            new BookInfo("https://img3.doubanio.com/lpic/s29418322.jpg","芳华","2017-4-1","8.1","严歌苓","人民文学出版社","jianjie"),
            new BookInfo("https://img3.doubanio.com/lpic/s29418322.jpg","芳华","2017-4-1","8.1","严歌苓","人民文学出版社","jianjie"),
            new BookInfo("https://img3.doubanio.com/lpic/s29418322.jpg","芳华","2017-4-1","8.1","严歌苓","人民文学出版社","jianjie")};

    private List<BookInfo> bookInfoList = new ArrayList<>();

    private BookListAdapter adapter;

    LinearLayoutManager mLayoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WantFragment newInstance(String param1, String param2) {
        WantFragment fragment = new WantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //获取图书信息
    private void initList() {
        bookInfoList.clear();
        for (int i = 0; i < 10;i++) {
            Random random = new Random();
            int index = random.nextInt(bookInfos.length);
            bookInfoList.add(bookInfos[index]);
        }
        /*_User bmobUser = BmobUser.getCurrentUser(_User.class);
        String state = "0";
        bookInfoList = OperationReadInfo.queryBookInfoByState(bmobUser,state);*/
    }

    //adapter中添加数据
    private void addDate() {
        adapter = new BookListAdapter(bookInfoList,"want");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_want, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        addBook=view.findViewById(R.id.add_book);
        refresh = view.findViewById(R.id.refresh_want);
        //刷新控件颜色设置
        refresh.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        //刷新列表
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        initList();
        addDate();
        return view;
    }

    //刷新事件
    private void refreshList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initList();
                        addDate();
                        adapter.notifyDataSetChanged();
                        refresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });
    }
}
