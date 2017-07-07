package devtolife.viewcolorchanger;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProdList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    DB dbPL;
    SimpleCursorAdapter scAdapter;
    String name;
    private ListView lv_list;
    private Button btnAdd;
    private EditText tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prod_layout);

        try {
            getSupportActionBar().setTitle("" + DB.getNameOfTable());
        } catch (Exception e) {
        }

        dbPL = new DB(this);
        dbPL.open();

        String[] from = new String[]{DB.KEY_NAME};
        int[] to = new int[]{R.id.tv_list_name};

        scAdapter = new SimpleCursorAdapter(this, R.layout.prod_item, null, from, to, 0);
        scAdapter.setViewBinder(new MySCA());

        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setAdapter(scAdapter);

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                TextView txt = (TextView) v;

                Cursor c = dbPL.toGetCheckedItem(id);
                c.moveToFirst();

                int checked = c.getInt(c.getColumnIndex("colored"));

                toCheckProd(id, v, txt, checked);
                c.close();
            }
        });

        registerForContextMenu(lv_list);

        getSupportLoaderManager().initLoader(0, null, this);

        tvName = (EditText) findViewById(R.id.tv_name);

        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewItem();
            }
        });

        dbPL.close();
    }

    public void toCheckProd(long id, View v, TextView tv, int check) {

        if (check == 0) {

            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setTextColor(getResources().getColor(R.color.colorCheckedText));
            v.setBackgroundColor(getResources().getColor(R.color.colorCheckedItem));
            dbPL.upDateCheck(id, 1);

        } else if (check == 1) {

            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setTextColor(getResources().getColor(R.color.colorOfText));
            v.setBackgroundColor(getResources().getColor(R.color.colorOfItem));
            dbPL.upDateCheck(id, 0);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, dbPL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private static class MyCursorLoader extends CursorLoader {
        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            db = new DB(getContext());
            db.open();
            return db.getAllData();
        }
    }

    private class MySCA implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            int listCheckInfo = cursor.getInt(cursor.getColumnIndex("colored"));

            TextView tv = (TextView) view;

            switch (listCheckInfo) {
                case 1:
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tv.setTextColor(getResources().getColor(R.color.colorCheckedText));
                    view.setBackgroundColor(getResources().getColor(R.color.colorCheckedItem));
                    return false;

                case 0:
                    tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    tv.setTextColor(getResources().getColor(R.color.colorOfText));
                    view.setBackgroundColor(getResources().getColor(R.color.colorOfItem));
                    return false;
            }
            return false;

        }
    }

    private void createNewItem() {
        name = tvName.getText().toString();
        int colored = 0;

        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Введите задание!", Toast.LENGTH_SHORT).show();
        } else {
            dbPL = new DB(this);
            dbPL.open();
            dbPL.addRec(name, colored);
            getSupportLoaderManager().getLoader(0).forceLoad();
            dbPL.close();
            tvName.setText("");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    protected void onDestroy() {
        dbPL.close();
        super.onDestroy();
    }
}


