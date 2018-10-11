package com.sandlot.dedwards.stackviews;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.widget.CursorAdapter;
import android.widget.EditText;

public class ConstantsFragment extends ListFragment implements DialogInterface.OnClickListener {
    private DataBaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;

    @Override
    public void onCreate(Bundle ssavedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }
    
    @Override
    public void onClick(DialogInterface di, int whichButton) {
        ContentValues values = new ContentValues(2);
        AlertDialog dlg = (AlertDialog) di;
        EditText title = (EditText)dlg.findViewById(R.id.title);
        EditText value = (EditText)dlg.findViewById(R.id.value);

        values.put(DatabaseHelper.TITLE, title.getText().toString());
        values.put(DatabaseHelper.VALUE, value.getText().toString());

        task = new InsertTask().execute(values);
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter)getListAdapter()).changeCursor(result);
        }
    }
}
