package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import id.go.kemdikbud.pkpberbasiszonasi.R;

import java.util.List;

public class CustomAdapterSpinner extends BaseAdapter {
    Context context;
    int images[];
    List<String> custom_text;
    LayoutInflater inflter;

    public CustomAdapterSpinner(Context applicationContext, List<String> custom_text) {
        this.context = applicationContext;
        this.custom_text = custom_text;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return custom_text.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_spinner_custom, null);
//        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.spinner_custom_text);
//        icon.setImageResource(images[i]);
        names.setText(custom_text.get(i));
        return view;
    }
}
