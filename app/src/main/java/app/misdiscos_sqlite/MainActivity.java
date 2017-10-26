package app.misdiscos_sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    EditText etGrupo, etTitulo;
    ListView lvDiscos;
    Button btnAdd, btnDel;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGrupo = (EditText)findViewById(R.id.etGrupo);
        etTitulo = (EditText)findViewById(R.id.etTitulo);
        lvDiscos = (ListView)findViewById(R.id.lvDiscos);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnDel = (Button)findViewById(R.id.btnDel);

        btnAdd.setOnClickListener(this);
        btnDel.setOnClickListener(this);

        db = openOrCreateDatabase("MisDiscos", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS misDiscos(Grupo VARCHAR,Disco VARCHAR);");

        listar();
    }

    public void listar(){
        ArrayAdapter<String> adaptador;
        List<String> lista = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM MisDiscos", null);

        if(c.getCount()==0){
            lista.add("No hay registros");
        }else{
            while(c.moveToNext())
                lista.add(c.getString(0)+"-"+c.getString(1));
        }

        adaptador = new ArrayAdapter<String>(getApplicationContext(),R.layout.lista_fila,lista);
        lvDiscos.setAdapter(adaptador);
    }

    @Override
    public void onClick(View view) {
        if(view == findViewById(R.id.btnAdd)){
            addOnClick();
        }else if(view == findViewById(R.id.btnDel)){
            deleteOnClick();
        }
    }

    public void addOnClick(){
        String grupo = etGrupo.getText().toString();
        String disco = etTitulo.getText().toString();
        if(!(grupo.isEmpty() && disco.isEmpty())){
            db.execSQL("INSERT INTO MisDiscos VALUES ('"+etGrupo.getText().toString() + "','"+
                    etTitulo.getText().toString()+"')");

            Toast toast = Toast.makeText(this, "Se añadio el disco " + etTitulo.getText().toString(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            listar();
        }else{

            Toast toast = Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    public void deleteOnClick(){
        db.execSQL("DELETE FROM MisDiscos WHERE Grupo = '"+etGrupo.getText().toString() + "' AND Disco='"+
                etTitulo.getText().toString()+"'");

        Toast toast = Toast.makeText(this, "Se borro el disco " + etTitulo.getText().toString(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        listar();
    }
}
