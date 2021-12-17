package com.koreatech.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivMenu;//메뉴버튼
    private DrawerLayout drawerLayout;// 드로어되는 창
    private Toolbar toolbar;// 액션바(툴바)
    private NavigationView navigationView;

    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //firebase data object
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;

    Button btnRevoke, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_member);

    btnLogout = (Button)findViewById(R.id.btn_logout);
    btnRevoke = (Button)findViewById(R.id.btn_revoke);

    mAuth = FirebaseAuth.getInstance();

    btnLogout.setOnClickListener(this);
    btnRevoke.setOnClickListener(this);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    private void revokeAccess(){
        mAuth.getCurrentUser().delete();
    }

    @Override
    public void onClick(View view) {

        int ViewId = view.getId();
        if (ViewId == R.id.btn_logout) { // 로그아웃 버튼 누르면
            Toast.makeText(getApplicationContext(), "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else if (ViewId == R.id.btn_revoke) { // 탈퇴 버튼 누르면
            Toast.makeText(getApplicationContext(), "정상적으로 탈퇴되었습니다", Toast.LENGTH_SHORT).show();
            revokeAccess();
            finishAffinity();
        }
        /*switch (view.getId()){
            case R.id.btn_logout:
                Toast.makeText(this,"정상적으로 로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
                signOut();
                finishAffinity();
                break;
            case R.id.btn_revoke:
                revokeAccess();
                finishAffinity();
                break;
        }*/
    }
}
