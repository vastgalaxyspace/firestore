package com.example.firestore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText entertitle;
    private EditText enterthought;
    private Button savebtn,showbtn,updatebtn,deletebtn;
    private TextView rectitle,recthougth;

    public static final String KEY_TITLE="title";
    public static final String KEY_THOUGHT="thoughts";
   private FirebaseFirestore db = FirebaseFirestore.getInstance();

//   private DocumentReference jonralref=db.document("journal/frist thougts");

   private DocumentReference jonralref=db.collection("journal")
           .document("frist thougts");

   private CollectionReference collectionReference=db.collection("journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        entertitle=findViewById(R.id.edit_text_title);
        enterthought=findViewById(R.id.edit_text_title1);
        rectitle=findViewById(R.id.rec_title);
        recthougth=findViewById(R.id.rec_thougts);
        savebtn=findViewById(R.id.Save_button);
        showbtn=findViewById(R.id.rec_button);
        updatebtn=findViewById(R.id.update_button);
        deletebtn=findViewById(R.id.delete_button);

        updatebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);

        showbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jonralref.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
//                                    String title=documentSnapshot.getString(KEY_TITLE);
//                                    String thougth=documentSnapshot.getString(KEY_THOUGHT);
                                    journal journal=documentSnapshot.toObject(journal.class);


                                    if (journal != null) {
                                        rectitle.setText(journal.getTitle());
                                        recthougth.setText(journal.getThought());
                                    }



                                }else {
                                    Toast.makeText(MainActivity.this,"No data exist",Toast.LENGTH_LONG).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("main","onfailure : "+e.toString());

                            }
                        });
            }
        });

        savebtn.setOnClickListener(view -> {
            addthought();
//            String title = entertitle.getText().toString().trim();
//            String thoughts=enterthought.getText().toString().trim();
//
//            journal journal=new journal();
//            journal.setTitle(title);
//            journal.setThought(thoughts);

//            Map<String,Object> data=new HashMap<>();
//            data.put(KEY_TITLE,title);
//            data.put(KEY_THOUGHT,thoughts);

//                    jonralref.set(journal)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(MainActivity.this,"sucess",Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("main","inFailuer : "+e.toString());
//                        }
//                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        jonralref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(error!=null){

                    Toast.makeText(MainActivity.this,"something goes wrong",Toast.LENGTH_LONG).show();
                }
                if(documentSnapshot!=null && documentSnapshot.exists()){
                    journal journal=documentSnapshot.toObject(journal.class);


                    if (journal != null) {
                        rectitle.setText(journal.getTitle());
                        recthougth.setText(journal.getThought());
                    }
                }else {
                    rectitle.setText("");
                    recthougth.setText("");
                }

            }
        });

    }
    private void addthought(){
        String title = entertitle.getText().toString().trim();
        String thoughts=enterthought.getText().toString().trim();

        journal journal=new journal(title,thoughts);
//        journal.setTitle(title);
//        journal.setThought(thoughts);

        collectionReference.add(journal);

    }


//    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.update_button) {
            updatemytitle();
        } else if (view.getId() == R.id.delete_button) {
            deletetitle();

        }
    }

    private void deleteall() {
jonralref.delete();
    }

    private void deletetitle(){
        jonralref.update(KEY_TITLE, FieldValue.delete());
        jonralref.update(KEY_THOUGHT,FieldValue.delete());
    }

    private void updatemytitle() {
        String title= entertitle.getText().toString().trim();
        Map<String,Object> data=new HashMap<>();

        data.put(KEY_TITLE,title);
        jonralref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this,"updated",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}