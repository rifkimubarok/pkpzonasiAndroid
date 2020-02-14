package id.go.kemdikbud.pkpberbasiszonasi.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.go.kemdikbud.pkpberbasiszonasi.GridSpacingItemDecoration;
import id.go.kemdikbud.pkpberbasiszonasi.Master.Courses;
import id.go.kemdikbud.pkpberbasiszonasi.R;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private List<Courses> itemsList;
    private CoursesFragment.CoursesAdapter mAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CoursesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CoursesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoursesFragment newInstance(String param1, String param2) {
        CoursesFragment fragment = new CoursesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_course, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        itemsList = new ArrayList<>();
        mAdapter = new CoursesFragment.CoursesAdapter(getActivity(), itemsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fetchStoreItems("111");

        return view;
    }

    private void fetchStoreItems(String uri) {
        /*JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("product");
                            JSONArray array = object.getJSONArray("data");
//                            List<Product> list = new ArrayList<Product>();
                            itemsList.clear();
                            for (int i=0;i<array.length();i++){
                                JSONObject product = array.getJSONObject(i);
                                Product p = new Product();
                                p.setCAT_CODE(product.getString("CAT_CODE"));
                                p.setPRO_SELLPRICE("Rp. "+product.getString("PRO_SELLPRICE"));
                                p.setPRO_SNAME(product.getString("PRO_SNAME"));
                                p.setPRO_INDEX(product.getInt("PRO_INDEX"));
                                p.setDEP_CODE(product.getString("DEP_CODE"));
                                p.setCAT_CODE(product.getString("CAT_CODE"));
                                p.setPRO_CODE(product.getString("PRO_CODE"));
                                p.setPRO_LNAME(product.getString("PRO_LNAME"));
                                p.setPRO_STATUS(product.getString("PRO_STATUS"));
                                p.setWIL_CODE(product.getString("WIL_CODE"));

                                HashMap<String,String> image = new HashMap<>();
                                if (product.getString("images")!="null"){
                                    JSONObject img = product.getJSONObject("images");
                                    image.put("PRO_INDEX",img.getString("PRO_INDEX"));
                                    image.put("PRO_CODE",img.getString("PRO_CODE"));
                                    image.put("PRO_IMAGE",img.getString("PRO_IMAGE"));
                                    image.put("PRO_DEFAULT",img.getString("PRO_DEFAULT"));
                                }else{
                                    image.put("PRO_INDEX","");
                                    image.put("PRO_CODE","");
                                    image.put("PRO_IMAGE","");
                                    image.put("PRO_DEFAULT","");
                                }
                                p.setImages(image);
//                                list.add(p);
                                itemsList.add(p);

                            }
//                            List<Product> items = list;
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestAdapter.getInstance().addToRequestQueue(request);*/
        /*for (int i = 0 ; i<10;i++){
            Courses courses = new Courses();
            courses.setId(""+(i+1));
            courses.setTitle("Contoh Kursus Ke-"+(i+1));
            courses.setImage("https://training.p4tkipa.net/theme/moove/pix/default_course.jpg");
            itemsList.add(courses);
        }*/
        mAdapter.notifyDataSetChanged();
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class CoursesAdapter extends RecyclerView.Adapter<CoursesFragment.CoursesAdapter.MyViewHolder> {
        private Context context;
        private List<Courses> coursesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail;
            public CardView productItem;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.title);
                thumbnail = view.findViewById(R.id.thumbnail);
                productItem = view.findViewById(R.id.card_view);
            }
        }


        public CoursesAdapter(Context context, List<Courses> coursesList) {
            this.context = context;
            this.coursesList = coursesList;
        }

        @Override
        public CoursesFragment.CoursesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_row, parent, false);

            return new CoursesFragment.CoursesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CoursesFragment.CoursesAdapter.MyViewHolder holder, final int position) {
            final Courses courses = coursesList.get(position);
           /* holder.name.setText(courses.getTitle());
            String urlgambar = courses.getImage();
            Glide.with(context)
                    .load(urlgambar)
                    .into(holder.thumbnail);*/

//            holder.productItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent detail = new Intent(getContext(),PoductDetail.class);
//                    startActivity(detail);
//                }
//            });
//
//            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent detail = new Intent(getContext(),PoductDetail.class);
//                    startActivity(detail);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return coursesList.size();
        }
    }
}
