package pnj.exam.mycontact

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cursoradapter.widget.CursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ListView

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var cursorAdapter: CursorAdapter

    companion object {
        private const val AUTHORITY = "pnj.exam.mycontact"
        private const val BASE_PATH = "contacts"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BASE_PATH")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cursorAdapter = ContactsCursorAdapter(this, null, 0)
        val list = findViewById<ListView>(R.id.list)
        list.adapter = cursorAdapter

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        LoaderManager.getInstance(this).initLoader(0, null, this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            inputKontak()
        }
    }

    private fun restartLoader() {
        LoaderManager.getInstance(this).restartLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, CONTENT_URI, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        cursorAdapter.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter.swapCursor(null)
    }

    private fun inputKontak() {
        val li = LayoutInflater.from(this)
        val view = li.inflate(R.layout.dialog_add, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)

        val nameInput = view.findViewById<EditText>(R.id.edtNama)
        val numberInput = view.findViewById<EditText>(R.id.edtPhone)

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val contentValues = ContentValues().apply {
                    put(DBOpenHelper.CONTACT_NAME, nameInput.text.toString())
                    put(DBOpenHelper.CONTACT_PHONE, numberInput.text.toString())
                }

                contentResolver.insert(MyContentProvider.CONTENT_URI, contentValues)
                restartLoader()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}
