package com.error41.firebase_practice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText name1, phone2, email3;
    private Button btn, btn1,btn2,btn3;
    private TextView textView;

    private FirebaseFirestore db;
    private DocumentReference documentReference123;
       private CollectionReference collectionReference;
    // Keys
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        collectionReference=db.collection("Users");

        name1 = findViewById(R.id.editext1name);
        phone2 = findViewById(R.id.editext2phone);
        email3 = findViewById(R.id.editext3email);
        btn = findViewById(R.id.button);
        btn1 = findViewById(R.id.button_show);
        btn2 = findViewById(R.id.button_UpdateData);
        btn3 = findViewById(R.id.button_deleteData);
        textView = findViewById(R.id.textid);

        documentReference123 = db.collection("Users")
                .document("Friends");

        btn3.setOnClickListener(view -> {

            DeleteData();

        });

        btn2.setOnClickListener(view -> {
            UpdateData();
        });

        btn1.setOnClickListener(view -> {
            // read Single user
            //ReadData();
            // Get all document

               GetallDocument();
        });

        btn.setOnClickListener(view -> {
            //Single data Read data
            //saveData();
            // double user added this method
            saveDataToNewDocument();
        });
    }

    private void GetallDocument() {
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data="";
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                           /*
                            Log.v("TAGAY",snapshots.getString(KEY_PHONE));
                            Log.v("TAGAY",snapshots.getString(KEY_NAME));
                            Log.v("TAGAY",snapshots.getString(KEY_EMAIL));

                            */
                            // Transforming datasnapshots into objects
                            // Each document is now an objects od type friend
                            Friend friend= snapshots.toObject(Friend.class);
                            data +="Name: "+friend.getName()+" Phone : "+
                                    friend.getPhone()+"Email :"+friend.getEmail()+"\n";

                        }
                        // setting the textview to the retrived data
                        textView.setText(data);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void saveDataToNewDocument() {
        String name = name1.getText().toString().trim();
        String  phone = phone2.getText().toString().trim();
        String email = email3.getText().toString().trim();

        Friend f1= new Friend(name,phone,email);

        collectionReference.add(f1);
    }

    private void DeleteData() {
        documentReference123.update(KEY_NAME, FieldValue.delete());
    }

    private void UpdateData() {
        String name = name1.getText().toString().trim();
        String phone = phone2.getText().toString().trim();
        String email = email3.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_NAME, name);
        data.put(KEY_PHONE, phone);
        data.put(KEY_EMAIL, email);

        documentReference123.update(data)
                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(),"Update Successfully",Toast.LENGTH_SHORT)
                        .show()).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                });

    }

    private void ReadData() {
        documentReference123.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String f_name = documentSnapshot.getString(KEY_NAME);
                            String f_phone = documentSnapshot.getString(KEY_PHONE);
                            String f_email = documentSnapshot.getString(KEY_EMAIL);
                            textView.setText("Username: " + f_name + "\nUser Phone: " + f_phone +
                                    "\nUser Email: " + f_email);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to read data"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveData() {
       String name = name1.getText().toString().trim();
        String  phone = phone2.getText().toString().trim();
       String email = email3.getText().toString().trim();

           Friend f1= new Friend();
           f1.setName(name);
           f1.setPhone(phone);
           f1.setEmail(email);
      /*

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_NAME, name);
        data.put(KEY_PHONE, phone);
        data.put(KEY_EMAIL, email);


       */
        db.collection("Users")
                .document("Friends")
                .set(f1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Successfully Created", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetallDocument();
   /*
        documentReference123.addSnapshotListener(this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error !=null){

                            Toast.makeText(getApplicationContext(),"Error Found",Toast.LENGTH_SHORT).show();
                        }
                        if (value!=null && value.exists()){

                            Friend friend= value.toObject(Friend.class);

                     //       String f_name = value.getString(KEY_NAME);
                       //     String f_phone = value.getString(KEY_PHONE);
                       //     String f_email = value.getString(KEY_EMAIL);

                            textView.setText("Username: " + friend.getName() + "\nUser Phone: "
                                    + friend.getPhone() +
                                    "\nUser Email: " + friend.getEmail());

                        }
                    }
                });
        */
    }
}
