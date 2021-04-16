package com.mu.medicalsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView prescription_list;
    ArrayList<Prescription> prescriptions = new ArrayList<>();
    Spinner dropdown;
    ArrayList<Prescription> selected_array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prescription_list = (ListView) findViewById(R.id.prescription_list);
        dropdown = findViewById(R.id.spinner1);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_array = new ArrayList<>();
                for (int k=0;k<prescriptions.size();k++){
                    if(prescriptions.get(k).prescriptionNumber.equals(prescriptions.get(i).prescriptionNumber)){
                        selected_array.add(prescriptions.get(k));
                    }
                }
                prescription_list.setAdapter(new ListAdapter());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadData();

        prescription_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, PharmacyActivity.class);
                intent.putExtra("pharm", selected_array.get(i).prescriptionNumber);
                startActivity(intent);
            }
        });
    }

    void loadData(){
        String url = Constants.root+"home.php";
        JsonObjectRequest requestForChangePass = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray history = response.getJSONArray("prescription");
                    prescriptions = new ArrayList<>();
                    for (int i=0;i<history.length();i++){
                        JSONObject obj = history.getJSONObject(i);
                        Prescription pres = new Prescription();
                        pres.prescriptionNumber = obj.getString("prescriptionNumber");
                        pres.date = obj.getString("date");
                        pres.time = obj.getString("time");
                        pres.disorder = obj.getString("disorder");
                        pres.doctor = obj.getString("doctor");
                        prescriptions.add(pres);
                    }



                    ArrayList<String> items = new ArrayList<>();
                    for (int i=0;i<prescriptions.size();i++){
                        items.add(prescriptions.get(i).prescriptionNumber);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                    dropdown.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Volley.newRequestQueue(MainActivity.this).add(requestForChangePass);
    }


    static class Prescription{
        String prescriptionNumber,date, time,patientName, disorder, doctor;
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return selected_array.size();
        }

        @Override
        public Object getItem(int i) {
            return selected_array.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.prescribed_row, null);
            ((TextView) view1.findViewById(R.id.prescription_medicine)).setText(selected_array.get(i).prescriptionNumber);
            ((TextView) view1.findViewById(R.id.date)).setText(selected_array.get(i).date);
            ((TextView) view1.findViewById(R.id.time)).setText(selected_array.get(i).time);
            return view1;
        }
    }

}