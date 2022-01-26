package com.example.a220126_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {

    private MyDBHelper mDBHelper;
    private EditText mEditName;
    private EditText mEditAge;
    private TextView mTextResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new MyDBHelper(this);
        mEditName = (EditText) findViewById(R.id.editName);
        mEditAge = (EditText) findViewById(R.id.editAge);
        mTextResult = (TextView) findViewById(R.id.textResult);
    }

    public void mOnClick (View v) {
        SQLiteDatabase db;
        ContentValues values; //ContentValues는 하나의 레코드를 정의하는 객체 -> 레코드 삽입과 갱신에 사용
        String[] projection = {"_id", "name", "age"}; // 필드값 잡아주기 -> 검색에 사용
        Cursor cur; // 데이터베이스 검색 결과는 보통 여러 개의 레코드임, Cursor를 이용하면
                    // 개별 레코드에 하나씩 접근하여 필드값을 꺼낼 수 있음

        switch(v.getId()) {
            case R.id.btnInsert: //삽입
                if(mEditName.getText().length() > 0 && mEditAge.getText().length() > 0) {
                    //EditText에 입력한 값이 두 개 모두 길이가 0보다 이상이라면
                    //이름과 나이를 입력받아 삽입 연산을 처리, ContentValues 객체에 필드명과 데이터를 차례로 넣은 뒤, insert() 호출
                    db = mDBHelper.getWritableDatabase();
                    values = new ContentValues();
                    values.put("name", mEditName.getText().toString());
                    values.put("age", mEditAge.getText().toString());
                    mDBHelper.close();
                } break;
            case R.id.btnSelectAll: //전체검색
                //조건없이 전체를 검색함. query()에 테이블 이름 "people"과 필드 이름을 담은 배열을 전달하고
                //나머지 인자에 기본값인 null을 전달하면 된다. 리턴값은 Cursor 타입의 객체
                //showResult()에 전달하면 화면에 결과를 출력
                db = mDBHelper.getReadableDatabase();
                cur = db.query("people", projection, null,
                        null, null,null, null);
                if (cur != null) {
                    showResult(cur);
                    cur.close(); //Cursor 사용이 끝나면 반드시 close 선언을 해줘야 된다.
                }
                mDBHelper.close(); //DB는 사용했으면 항상 닫아줘야 된다.
                break;
            case R.id.btnSelectName: //이름검색
                //특정 이름을 가진 레코드를 검색. 전체 검색하고 코드는 비슷하지만 query()의 세 번째와 네 번째 인자에
                //조건을 전달하면 된다. 세 번째 인자의 물음표 자리에 네 번째 인자로 전달한 문자열이 차례로 채워져서 조건이 완성된다.
                if (mEditName.getText().length() > 0) {
                    db = mDBHelper.getReadableDatabase();
                    String name = mEditName.getText().toString();
                    cur = db.query("peoople", projection, "name=?",
                            new String[]{name}, null, null, null);
                    if (cur != null) {
                        showResult(cur);
                        cur.close();
                    } mDBHelper.close();
                } break;
            case R.id.btnUpdateAge: // 나이갱신
                if (mEditName.getText().length() > 0 && mEditAge.getText().length() > 0) {
                    db = mDBHelper.getWritableDatabase();
                    String name = mEditName.getText().toString();
                    values = new ContentValues();
                    values.put("age", mEditAge.getText().toString());
                    db.update("people", values, "name=?", new String[] {name });
                    mDBHelper.close();
                } break;
            case R.id.btnDeleteName: // 이름 삭제
                //delete() 첫 번째 인자는 테이블 이름, 두 번째와 세 번째는 조건을 의미
                if (mEditName.getText().length() > 0) {
                    db = mDBHelper.getWritableDatabase();
                    String name = mEditName.getText().toString();
                    db.delete("people", "name=?", new String[]{name});
                    mDBHelper.close();
                } break;
            case R.id.btnDeleteAll: //전체 삭제
                //delete() 두 번째와 세 번째에는 인자에 기본값인 null을 전달하면 된다.
                db = mDBHelper.getWritableDatabase();
                db.delete("people", null, null);
                mDBHelper.close();
                break;
        }
    }

    private void showResult(Cursor cur) {
        //검색 결과인 Cursor 객체를 인자로 받아서 Cursor가 가리키는 모든 레코드를 출력하는 메소드
        //Cursor는 첫 번째 레코드가 아닌 그 이전 위치를 가지고 있음 -> moveToNext를 호출하면서 다음 레코드로 이동
        mTextResult.setText("");
        int name_col = cur.getColumnIndex("name"); //열 번호 주고 받기
        int age_col = cur.getColumnIndex("age"); // 열 번호 주고 받기
        while (cur.moveToNext()) {
            String name = cur.getString(name_col); // 값을 꺼내기
            String age = cur.getString(age_col); // 값을 꺼내기
            mTextResult.append(name + "," + age + "\n");
        }
    }
}

class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context) {
        super(context, "mytest.db", null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE people(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "name TEXT, age TEXT);");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS people");
        onCreate(db);
    }
}
