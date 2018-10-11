package com.sandlot.dedwards.stackviews;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;


public class ConstantsFragment extends ListFragment implements DialogInterface.OnClickListener {
    private DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(getActivity(),
                        R.layout.row,
                        current, new String[]{
                        DatabaseHelper.TITLE,
                        DatabaseHelper.VALUE },
                        new int[]{ R.id.title, R.id.value },
                        0);
        setListAdapter(adapter);

        if (current == null){
            db = new DatabaseHelper(getActivity());
            task = new LoadCursorTask().execute();
        }
    }

    @Override
    public void onDestroy(){
        if (task!=null){
            task.cancel(false);
        }
        ((CursorAdapter)getListAdapter()).getCursor().close();
        db.close();

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.actions, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.add){
            add();
            return(true);
        }
        return (super.onOptionsItemSelected(item));
    }

    private void add(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View addView = inflater.inflate(R.layout.add_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.add_title).setView(addView)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel,null).show();
    }

    @Override
    public void onClick(DialogInterface di, int whichButton) {
        ContentValues values = new ContentValues(2);
        AlertDialog dlg = (AlertDialog) di;
        EditText title = dlg.findViewById(R.id.title);
        EditText value = dlg.findViewById(R.id.value);

        values.put(DatabaseHelper.TITLE, title.getText().toString());
        values.put(DatabaseHelper.VALUE, value.getText().toString());

        task = new InsertTask().execute(values);
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter)getListAdapter()).changeCursor(result);
            current = result;
            task = null;
        }

        Cursor doQuery() {
            Cursor result =
                    db.getReadableDatabase().query(DatabaseHelper.TABLE,
                            new String[] {"ROWID AS _id",
                                    DatabaseHelper.TITLE,
                                    DatabaseHelper.VALUE},
                            null, null, null, null, DatabaseHelper.TITLE);

            result.getCount();

            return(result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        @Override
        protected Cursor doInBackground(Void... params){
            return(doQuery());
        }
    }

    private class InsertTask extends BaseTask<ContentValues>{
        @Override
        protected Cursor doInBackground(ContentValues... values){
            db.getWritableDatabase().insert(DatabaseHelper.TABLE,
                    DatabaseHelper.TITLE, values[0]);
            return(doQuery());
        }
    }
}
