package app.misdiscos_sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener, ListView.OnItemLongClickListener {

    EditText etGrupo, etTitulo;
    ListView lvDiscos;
    Button btnAdd, btnDel;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGrupo = (EditText) findViewById(R.id.etGrupo);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        lvDiscos = (ListView) findViewById(R.id.lvDiscos);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDel = (Button) findViewById(R.id.btnDel);

        btnAdd.setOnClickListener(this);
        btnDel.setOnClickListener(this);

        lvDiscos.setOnItemLongClickListener(this);

        db = openOrCreateDatabase("MisDiscos", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS misDiscos(Grupo VARCHAR,Disco VARCHAR);");

        listar();
    }



    /**************************** AUX METHODS **********************/
    /**
     * Muestra listado de discos
     */
    public void listar() {
        ArrayAdapter<String> adaptador;
        List<String> lista = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM MisDiscos", null);

        if (c.getCount() == 0) {
            lista.add("No hay registros");
        } else {
            while (c.moveToNext())
                lista.add(c.getString(0) + "-" + c.getString(1));
        }

        adaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.lista_fila, lista);
        lvDiscos.setAdapter(adaptador);
    }

    /**
     * Limpia campos EditText Grupo y Disco
     */
    public void clearEt() {
        etGrupo.setText("");
        etTitulo.setText("");
    }

    /**
     * Verifica si un registro ya existe
     * @param xquery query de busqueda
     * @return
     */
    public boolean checkIfRecordExists(String xquery){
        Cursor cur = db.rawQuery(xquery,null);
        Boolean res=false;

        if(cur.getCount()<=0){
            res = true;
        }
        cur.close();
        return res;
    }

    /**************************** BUTTON EVENTS **********************/
    @Override
    public void onClick(View view) {
        String grupo = etGrupo.getText().toString();
        String disco = etTitulo.getText().toString();


        if (grupo.isEmpty() || disco.isEmpty()) {
            Toast toast = Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            if (view == findViewById(R.id.btnAdd)) {
                addOnClick(grupo, disco);
            } else if (view == findViewById(R.id.btnDel)) {
                deleteOnClick(grupo, disco);
            }
        }


    }

    public void addOnClick(String grupo, String disco) {

            if(checkIfRecordExists("SELECT * FROM MisDiscos WHERE Grupo = '" + grupo + "' AND Disco='" +
                    disco + "'")) {
                try {
                    db.execSQL("INSERT INTO MisDiscos VALUES ('" + grupo + "','" +
                            disco + "')");

                    Toast toast = Toast.makeText(this, "Se añadio el disco " + disco, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception e) {
                    //e.printStackTrace();
                    Toast toast = Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                clearEt();
                listar();
            }else{
                Toast toast = Toast.makeText(this, "Este disco ya está registrado.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


    }

    public void deleteOnClick(String grupo, String disco) {

        if(!checkIfRecordExists("SELECT * FROM MisDiscos WHERE Grupo = '" + grupo + "' AND Disco='" +
                disco + "'")) {


            try {
                db.execSQL("DELETE FROM MisDiscos WHERE Grupo = '" + grupo + "' AND Disco='" +
                        disco + "'");

                Toast toast = Toast.makeText(this, "Se borro el disco " + disco, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception e) {
                //e.printStackTrace();
                Toast toast = Toast.makeText(this, "Ha ocurrido un error.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            clearEt();
            listar();
        }else{
            Toast toast = Toast.makeText(this, "El disco seleccionado no está registrado.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**************************** LISTVIEW EVENTS **********************/
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView t = (TextView) view;
        String tselected = t.getText().toString();
        int im = tselected.indexOf("-");

        if (im != -1) {
            etGrupo.setText(tselected.substring(0, im));
            etTitulo.setText(tselected.substring(im + 1, tselected.length()));
        }
        return false;
    }


}
