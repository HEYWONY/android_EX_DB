# android_EX_DB
people 테이블 정의하고 삽입/검색/갱신/삭제 기능 수행


### SQLite 사용
```java
class MyDBHElper extends SQLiteOpenHelper { 
	public MyDBHelper(Context context) {
		구문
	}
	public void onCreate(SQLiteDatabase db) {
		구문
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 구문
	}
} 
```
