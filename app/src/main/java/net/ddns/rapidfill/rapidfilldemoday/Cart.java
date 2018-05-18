package net.ddns.rapidfill.rapidfilldemoday;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Cart extends AppCompatActivity {

    DatabaseReference db;
    DatabaseReference dbOrders;
    FirebaseAuth user;
    Context context;
    ArrayList<Product> products;
    ListView resultList;
    ProductCartArrayAdapter productAdapter;

    TextView textView_total;
    float total = 0;

    Button btn_send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        user = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart");
        dbOrders = FirebaseDatabase.getInstance().getReference().child("Orders").push();
        context = this;

        products = new ArrayList<>();
        resultList = findViewById(R.id.result_list);
        productAdapter = new ProductCartArrayAdapter();

        textView_total = findViewById(R.id.total);
        btn_send = findViewById(R.id.btn_send_order);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(products.isEmpty()) {
                    Toast.makeText(Cart.this, "Cosul tau este gol!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date now = Calendar.getInstance().getTime();
                Order order = new Order(now, products);
                dbOrders.setValue(order);
                db.removeValue();
            }
        });


        showCart();

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                showCart();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                showCart();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                showCart();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                showCart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showCart();
            }
        });
    }

    public void showCart() {

        db.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                total = 0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product item = postSnapshot.getValue(Product.class);
                    products.add(item);
                    total += Float.valueOf(item.getPrice());
                }
                productAdapter.setParameters(Cart.this, products, true, db);
                resultList.setAdapter(productAdapter);
                textView_total.setText(total + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
