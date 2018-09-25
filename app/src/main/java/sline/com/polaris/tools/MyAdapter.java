package sline.com.polaris.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import sline.com.polaris.BaseApplication;
import sline.com.polaris.R;
import sline.com.polaris.videoList;

/**
 * Created by dell on 2018/9/8.
 */

public class MyAdapter extends BaseAdapter {
    private List<VideoBean> list;
    private LayoutInflater inflater;
    private Context context;
    private int ITEM_CLICK;
    private String url,imagePath;
    private Handler handler;
    private Typeface typeface;

    public MyAdapter(Context context, List<VideoBean> list,String url,String imagePath,int ITEM_CLICK,Handler handler) {
        this.list = list;
        this.context = context;
        this.url=url;
        this.imagePath=imagePath;
        this.ITEM_CLICK=ITEM_CLICK;
        this.handler=handler;
        typeface=BaseApplication.typeface;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.videoitem, null);
            viewHolder.imgLeft = view.findViewById(R.id.ImageLift);
            viewHolder.imgRight = view.findViewById(R.id.ImageRight);
            viewHolder.tvLeft = view.findViewById(R.id.TextLeft);
            viewHolder.tvLeft.setTypeface(typeface);
            viewHolder.tvRight = view.findViewById(R.id.TextRight);
            viewHolder.tvRight.setTypeface(typeface);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        final VideoBean bean = list.get(i);
        Glide.with(context).load("http://" + url + imagePath + bean.getImageLeft()).skipMemoryCache(true).into(viewHolder.imgLeft);
        viewHolder.tvLeft.setVisibility(View.INVISIBLE);
        viewHolder.tvLeft.setText(bean.getNameLeft().substring(0, bean.getNameLeft().lastIndexOf(".")));
        if (!"NO".equals(bean.getNameRight())) {
            Glide.with(context).load("http://" + url + imagePath + bean.getImageRight()).skipMemoryCache(true).into(viewHolder.imgRight);
            viewHolder.tvRight.setVisibility(View.INVISIBLE);
            viewHolder.tvRight.setText(bean.getNameRight().substring(0, bean.getNameRight().lastIndexOf(".")));
        } else {
            viewHolder.imgRight.setImageBitmap(null);
            viewHolder.tvRight.setText("");
        }


        viewHolder.imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.tvLeft.getVisibility() == View.VISIBLE)
                    viewHolder.tvLeft.setVisibility(View.INVISIBLE);
                else {
                    viewHolder.tvLeft.setVisibility(View.VISIBLE);
                    viewHolder.tvLeft.startAnimation(AnimationUtils.loadAnimation(viewHolder.tvLeft.getContext(), R.anim.clickon));
                }
            }
        });

        viewHolder.imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.tvRight.getVisibility() == View.VISIBLE)
                    viewHolder.tvRight.setVisibility(View.INVISIBLE);
                else {
                    viewHolder.tvRight.setVisibility(View.VISIBLE);
                    viewHolder.tvRight.startAnimation(AnimationUtils.loadAnimation(viewHolder.tvRight.getContext(), R.anim.clickon));
                }
            }
        });

        viewHolder.tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.tvLeft.setVisibility(View.INVISIBLE);
                new MakeMessage(ITEM_CLICK,0,0,new EMS(bean.getNameLeft(),bean.getImageLeft()),handler).makeMessage();
            }
        });

        viewHolder.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"NO".equals(bean.getNameRight())) {
                    viewHolder.tvRight.setVisibility(View.INVISIBLE);
                    new MakeMessage(ITEM_CLICK,0,0,new EMS(bean.getNameRight(),bean.getImageRight()),handler).makeMessage();
                }
            }
        });

        return view;
    }
    static class ViewHolder {
        ImageView imgLeft;
        ImageView imgRight;
        TextView tvLeft;
        TextView tvRight;
    }
}
