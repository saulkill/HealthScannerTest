package com.cookandroid.healthscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cookandroid.healthscanner.ui.food.adapter.FoodAdapter;
import com.cookandroid.healthscanner.ui.food.adapter.FoodDeleteListAdapter;
import com.cookandroid.healthscanner.ui.food.datatable.DeleteFood;
import com.cookandroid.healthscanner.ui.food.datatable.Food;
import com.cookandroid.healthscanner.ui.food.foodgraph.GraphData;
import com.cookandroid.healthscanner.ui.food.foodgraph.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity {
//음식 화면 리스트
    RecyclerView recyclerView;
    ArrayList<Food> foodArrayList;
    ArrayList<DeleteFood> deletfoodList;
    FoodAdapter foodAdapter;
    FirebaseUser user;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    public static Dialog dialog;
    Button breakfaskBt;
    Button dinnerBt;
    Button lunchBt;
    BarChart barChart;
    private String uid;
    String chage ="";
    List<GraphData> graphData = new ArrayList<>();
    ArrayList<String> xLabel = new ArrayList<>();
    EditText searchEd;
    Button searchBt;
    String searchSt ="";
    float kAverage; //나중에 정리
    float pAverage;
    float cAverage;
    float fAverage;
    BarData data;

    DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        breakfaskBt = findViewById(R.id.delete_bf);
        lunchBt = findViewById(R.id.delete_lc);
        dinnerBt = findViewById(R.id.delete_dn);

        searchEd = findViewById(R.id.search_Et);
        searchBt = findViewById(R.id.search_bt);
        barChart = (BarChart)findViewById(R.id.chart1);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user !=null ? user.getUid():null;
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching : Data...");
        progressDialog.show();
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        recyclerView = findViewById(R.id.foodrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodArrayList = new ArrayList<Food>();
        deletfoodList =new ArrayList<DeleteFood>();
        foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
        recyclerView.setAdapter(foodAdapter);
        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layoutfood);
        btMenu = findViewById(R.id.bt_menu);
        recyclerView1 = findViewById(R.id.recycler_view);

        //Set layout manager
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        //set profile
        recyclerView1.setAdapter(new MainAdapter(this, MainActivity.arrayList));
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open drawer
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        EventChangeListener();
        RadioButtonTextEventChange();
        search();
        ButtonDelete();



    }
    void search(){
        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSt = (String)searchEd.getText().toString().trim();
                Log.d("seachSt",searchSt);
                EventChangeListener();
                RadioButtonTextEventChange();
            }
        });
    }
    public void ButtonDelete(){

        breakfaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dlchage = "breakfask";
                showDialog(FoodActivity.this,dlchage);

            }
        });
        dinnerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dlchage = "dinner";
                showDialog(FoodActivity.this,dlchage);
            }
        });
        lunchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dlchage = "lunch";
                showDialog(FoodActivity.this,dlchage);
            }
        });
    }
    public void showDialog(Activity activity,String chage){

        dialog = new Dialog(activity);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                RadioButtonTextEventChange();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.delete_recycler);
        FoodDeleteListAdapter foodDeleteListAdapter = new FoodDeleteListAdapter(FoodActivity.this,deletfoodList);
        recyclerView.setAdapter(foodDeleteListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        db.collection("User").document(uid).collection("Food").whereEqualTo("type",chage)
                .orderBy("foodName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        deletfoodList.clear();
                        if (e !=null){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore errer", e.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
//
                                deletfoodList.add(dc.getDocument().toObject(DeleteFood.class));
                            }
                            foodDeleteListAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
        dialog.show();

    }
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.radioButton){
                chage ="breakfask";
                RadioButtonTextEventChange();
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);

                }
            else if(i == R.id.radioButton2){ chage ="lunch";
                RadioButtonTextEventChange();
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);
            }
            else if(i == R.id.radioButton3){ chage ="dinner";
                RadioButtonTextEventChange();
                foodAdapter = new FoodAdapter(FoodActivity.this,foodArrayList,chage);
                recyclerView.setAdapter(foodAdapter);
            }
        }
    };
    void RadioButtonTextEventChange() {
        String [] foodtypelist = {"breakfask","lunch","dinner"};
        String foodtype = "";
        for (int i =0 ; i<3; i++){
            foodtype = foodtypelist[i];
            CollectionReference collectionReference = db.collection("User").document(uid).collection("Food");
            String finalFoodtype = foodtype;
            collectionReference.whereEqualTo("type",finalFoodtype).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }
                    int count =0;
                    String str = "";
//                Log.d("TAG","outqueryDocumentSnapshots.size : "+ queryDocumentSnapshots.size());
                    if (queryDocumentSnapshots.size() ==0){
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            Log.d("TAG","zerodc.size : "+ dc.getDocument().getData().size());
                            if (dc.getDocument().getData().get("foodName")!= null) {
                                str += (String) dc.getDocument().getData().get("foodName");
                                if (count<queryDocumentSnapshots.size() -1){
                                    str+="+";
                                    count++;
                                }
                            }
                        }
                    }
                    else {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            if (ds.get("foodName")!= null) {
                                str += (String) ds.get("foodName");
                                if (count<queryDocumentSnapshots.size() -1){
                                    str+="+";
                                    count++;
                                }

                            }
                        }
                    }
                    if (finalFoodtype =="breakfask"){
                        radioButton1.setText("아침 : " + str);
                    }
                    if (finalFoodtype =="lunch"){
                        radioButton2.setText("점심 : " + str);
                    }
                    if (finalFoodtype =="dinner"){
                        radioButton3.setText("저녁 : " + str);
                    }

                    GraphViewData();
                }
            });
        }

    }

    private void EventChangeListener(){
        if (searchSt == null || searchSt == "" || searchSt == " " ||searchSt.length()==0){
            db.collection("Food")
                    .orderBy("foodName", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                            foodArrayList.clear();
                            if (e !=null){
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Log.e("Firestore errer", e.getMessage());
                                return;
                            }
                            for (DocumentChange dc : value.getDocumentChanges()){
                                if(dc.getType() == DocumentChange.Type.ADDED){
//
                                    foodArrayList.add(dc.getDocument().toObject(Food.class));
                                }
                                foodAdapter.notifyDataSetChanged();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }
                    });
        }
        else{
            db.collection("Food").whereEqualTo("foodName",searchSt)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    foodArrayList.clear();
                    for (DocumentSnapshot doc : task.getResult()){
                        foodArrayList.add(doc.toObject(Food.class));
                        foodAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

    }

    private void setFoodData(List<GraphData> GraphData){
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int green = Color.rgb(110,190,102);
        int red = Color.rgb(211,87,44);
        for(int i=0; i<GraphData.size(); i++){
            GraphData graphData = GraphData.get(i);
            BarEntry entry = new BarEntry(graphData.xValue,graphData.yValue );
            values.add(entry);
            if (graphData.yValue<=0){
                colors.add(red);
            }
            else{
                colors.add(green);
            }
        }
        if (barChart.getData() != null){
            barChart.clearValues();
            barChart.clear();
            data.clearValues();
            graphData.clear();
            Log.d("TAG","그래프 초기화 완료");
        }

        BarDataSet set;

        set = new BarDataSet(values,"영양소");
        set.setColors(colors);
        set.setValueTextColors(colors);


        data = new BarData(set);

        data.setValueTextSize(8f);
        data.setValueFormatter(new ValueFormatter());
        data.setBarWidth(0.3f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yLeft = barChart.getAxisLeft();
        yLeft.setAxisMinimum(-3000f);
        yLeft.setAxisMaximum(3000f);
        yLeft.setDrawLabels(false);

        YAxis yRight = barChart.getAxisRight();
        yRight.setAxisMinimum(-3000f);
        yRight.setAxisMaximum(3000f);
        yRight.setDrawLabels(false);


        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.getLegend().setEnabled(false);
        barChart.fitScreen();
        if (barChart.getData() != null)
            barChart.getData().clearValues();

        barChart.invalidate();
        barChart.setData(data);

    }
    private void GraphViewData(){
        DocumentReference documentReference = db.collection("User").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Double height = (Double) documentSnapshot.get("height");
                Long sex = (Long) documentSnapshot.get("sex");
                Long activityRate = (Long) documentSnapshot.get("activityRate");
                Log.d("GraphViwData","height"+height);
                Log.d("GraphViwData","sex"+sex);
                Log.d("GraphViwData","activityRate"+activityRate);
                if (height != null && sex != null && activityRate != null){
                    Faveragelist(height,sex,activityRate);
                }
                else{
                   // Toast.makeText(getApplicationContext(),"개인 정보를 입력해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Faveragelist(Double height,Long sex, Long activityRate){


        int Weightsex=0;
        int i = 0;

        if (sex == 1){
            Weightsex = 22; //남자일때
        }
        else{
            Weightsex = 21; //여자일때
        }
        if (activityRate == 1) {//심한 육체활동을 하는 경우 & 다이어트 강도
            kAverage = (float) (((height/100)*(height/100)*Weightsex)  * 35 - 40);
            pAverage=kAverage%45;
            cAverage=kAverage%35;
            fAverage=kAverage%20;
        }
        else if (activityRate == 2) {//보통의 활동을 하는 경우& 다이어트 강도
            kAverage = (float) (((height/100)*(height/100)*Weightsex) * 30 - 35);
            pAverage=kAverage%55;
            cAverage=kAverage%20;
            fAverage=kAverage%25;
        }
        else {//육체활동이 거의 없는 경우& 다이어트 강도
            kAverage = (float) (((height/10)*(height/10)*Weightsex) * 25 - 30);
            pAverage=kAverage%60;
            cAverage=kAverage%10;
            fAverage=kAverage%30;
        }

        CollectionReference collectionReference = db.collection("User").document(uid).collection("Food");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                float kGraph = 0;
                float pGraph = 0;
                float cGraph = 0;
                float fGraph = 0;
                float userkAverage = 0;
                float userpAverage = 0;
                float usercAverage = 0;
                float userfAverage = 0;
                float kGraphResult = 0;
                float pGraphResult = 0;
                float cGraphResult = 0;
                float fGraphResult = 0;

                for (DocumentSnapshot document : task.getResult()) {

                    kGraph = ((Double)document.getData().get("kcal")).floatValue();
                    pGraph = ((Double)document.getData().get("protein")).floatValue();
                    cGraph = ((Double)document.getData().get("carb")).floatValue();
                    fGraph = ((Double)document.getData().get("fat")).floatValue();
                    userkAverage = userkAverage+kGraph;
                    userpAverage = userpAverage+pGraph;
                    usercAverage = usercAverage+cGraph;
                    userfAverage = userfAverage+fGraph;
                }

                kGraphResult = usercAverage - kAverage;
                pGraphResult = userpAverage - pAverage;
                cGraphResult = usercAverage - cAverage;
                fGraphResult = userfAverage - fAverage;

                graphData.add(new GraphData(0f,kGraphResult));
                graphData.add(new GraphData(1f,pGraphResult));
                graphData.add(new GraphData(2f,cGraphResult));
                graphData.add(new GraphData(3f,fGraphResult));

                xLabel.add("Kcal");
                xLabel.add("탄수화물");
                xLabel.add("지방");
                xLabel.add("단백질");
                setFoodData(graphData);

            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        //Close drawer
        MainActivity.closeDrawer(drawerLayout);


    }
}