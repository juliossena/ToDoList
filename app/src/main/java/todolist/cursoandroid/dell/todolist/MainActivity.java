package todolist.cursoandroid.dell.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button inserir;
    private EditText editText;
    private ListView listaTarefas;
    private ArrayList<String> tarefas;
    private ArrayList<Integer> tarefasId;

    private SQLiteDatabase bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inserir = (Button) findViewById(R.id.btn_inserir_id);
        editText = (EditText) findViewById(R.id.edit_id);
        listaTarefas = (ListView) findViewById(R.id.list_view_id);

        try {
            bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR(200))");
            recuperarTarefas();
            inserir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textoDigitado = editText.getText().toString();

                    salvarTarefa(textoDigitado);
                }
            });

        } catch (Exception e) {

        }

        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeTarefas(tarefasId.get(i), listaTarefas.getItemAtPosition(i).toString());
                recuperarTarefas();
            }
        });

    }

    public void salvarTarefa(String texto) {
        try {
            if (!texto.equals("")) {
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "')");
                Toast.makeText(getApplicationContext(), "Inserido Com sucesso", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                editText.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "Digite uma tarefa", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {

        }
    }

    public void recuperarTarefas() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);
            int indiceId = cursor.getColumnIndex("id");
            int indiceTarefa = cursor.getColumnIndex("tarefa");

            tarefas = new ArrayList<String>();
            tarefasId = new ArrayList<Integer>();
            cursor.moveToFirst();
            while (cursor != null) {
                tarefas.add(cursor.getString(indiceTarefa));
                tarefasId.add(Integer.parseInt(cursor.getString(indiceId)));
                atualizarLista();
                cursor.moveToNext();
            }
        } catch (Exception e) {

        }
    }

    public void atualizarLista() {
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                tarefas
        );

        listaTarefas.setAdapter(adaptador);
    }

    public void removeTarefas(Integer id, String texto) {
        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id LIKE '" + id + "'");
            Toast.makeText(getApplicationContext(), texto + " removido com sucesso!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }
}
