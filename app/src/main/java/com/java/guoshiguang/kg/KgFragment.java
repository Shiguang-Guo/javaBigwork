package com.java.guoshiguang.kg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.Entity;
import com.java.guoshiguang.data.ImageLoader;
import com.java.guoshiguang.data.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;


public class KgFragment extends Fragment {
    String mkeyword;
    private View view;
    private EditText msearchbar;
    private Button msearchbutton;
    private RecyclerView mrecyclerView;
    private KgAdapter madapter;
    private int mLastClickPosition = -1;

    public static KgFragment newInstance() {
        return new KgFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        madapter = new KgAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kg, container, false);
        msearchbar = view.findViewById(R.id.search_bar);
        msearchbutton = view.findViewById(R.id.search_button);
        mrecyclerView = view.findViewById(R.id.kg_view);
        mrecyclerView.setAdapter(madapter);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        msearchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mkeyword = msearchbar.getText().toString();

                Single<List<Entity>> single = Manager.I.searchEntityData(mkeyword);

                single.subscribe(new Consumer<List<Entity>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(List<Entity> entityList) throws Exception {
                        madapter.mdata = entityList;
                        madapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return view;
    }


    class KgAdapter extends RecyclerView.Adapter<KgAdapter.ItemViewHolder> {
        private Context mContext;
        private List<Entity> mdata = new ArrayList<>();

        public KgAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public KgAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kg, parent, false);
            return new KgAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Entity e = mdata.get(position);
            ItemViewHolder item = (ItemViewHolder) holder;
            item.mLabel.setText(e.label);
            if (!e.baidu.isEmpty())
                item.minfo.setText(e.baidu);
            else if (!e.enwiki.isEmpty())
                item.minfo.setText(e.enwiki);
            else
                item.minfo.setText(e.zhwiki);
            //ImageLoader.displayImage(e.img, item.mImage);
            item.mCurrentPosition = position;
            relAdapter reladapter = new relAdapter();
            proAdapter proadapter = new proAdapter();
            proadapter.pro = new ArrayList<>(e.properties.entrySet());
            reladapter.rel = e.relations;

            ImageLoader.displayImage(e.img, item.mImage);
            item.mrelationview.setAdapter(reladapter);
            item.mrelationview.setLayoutManager(new LinearLayoutManager(this.mContext));
            item.mpropertiesview.setAdapter(proadapter);
            item.mpropertiesview.setLayoutManager(new LinearLayoutManager(this.mContext));
        }

        @Override
        public int getItemCount() {
            return mdata.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            private View mView;
            private TextView mLabel, minfo;
            private RecyclerView mpropertiesview, mrelationview;
            private int mCurrentPosition = -1;
            private ImageView mImage;


            public ItemViewHolder(View view) {
                super(view);
                mView = view;
                mLabel = view.findViewById(R.id.kg_label);
                minfo = view.findViewById(R.id.kg_info);
                mImage = view.findViewById(R.id.kg_image);
                mpropertiesview = view.findViewById(R.id.propertiesView);
                mrelationview = view.findViewById(R.id.relationview);
            }

        }

        class proAdapter extends RecyclerView.Adapter<proAdapter.proViewHolder> {
            List<Map.Entry<String, String>> pro = new ArrayList<>();

            @NonNull
            @Override
            public proAdapter.proViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pro, parent, false);
                return new proViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull proAdapter.proViewHolder holder, int position) {
                Map.Entry<String, String> m = pro.get(position);
                holder.cat.setText(m.getKey());
                holder.into.setText(m.getValue());
            }

            @Override
            public int getItemCount() {
                return pro.size();
            }

            class proViewHolder extends RecyclerView.ViewHolder {
                TextView cat, into;

                public proViewHolder(@NonNull View itemView) {
                    super(itemView);
                    cat = itemView.findViewById(R.id.protextView1);
                    into = itemView.findViewById(R.id.protextView2);
                    cat.setTextColor(Color.RED);
                }
            }
        }


        class relAdapter extends RecyclerView.Adapter<relAdapter.relViewHolder> {
            List<Entity.Relation> rel = new ArrayList<>();

            @NonNull
            @Override
            public relAdapter.relViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rel, parent, false);
                return new relViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull relAdapter.relViewHolder Holder, int position) {
                Entity.Relation r = rel.get(position);
                Holder.reltype.setText(r.relationType);
                Holder.lab.setText(r.label);
                Holder.dic.setText(r.forward ? "->" : "<-");
                Holder.lab.setTextColor(Color.BLUE);
            }

            @Override
            public int getItemCount() {
                return rel.size();
            }

            class relViewHolder extends RecyclerView.ViewHolder {
                TextView reltype, dic, lab;

                public relViewHolder(@NonNull View View) {
                    super(View);
                    reltype = View.findViewById(R.id.reltextView1);
                    dic = View.findViewById(R.id.reltextView2);
                    lab = View.findViewById(R.id.reltextView3);
                    lab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msearchbar.setText(lab.getText());
                            msearchbutton.performClick();
                        }
                    });
                }
            }
        }
    }
}

