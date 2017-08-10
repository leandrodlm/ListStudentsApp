package com.bhsoftworks.liststudentsapp;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import com.bhsoftworks.liststudentsapp.adapter.StudentListAdapter;
import com.bhsoftworks.liststudentsapp.model.*;
import com.bhsoftworks.liststudentsapp.service.StudentAPI;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by leandrodlm on 24/07/17.
 */

public class MainActivity extends AppCompatActivity {

    static final String TAG = "Retrofit";
    ListView listviewAlunos;
    StudentAPI service;
    ProgressDialog pDialog;
    FloatingActionButton fab;
    ImageView foto;
    View view, viewList;
    public final static String EXTRA_ENDERECO = "ENDERECO";
    public final static String EXTRA_NOME = "NOME";
    MenuItem sobre;
    Toolbar toolbar;

    private EditText name;
    private EditText idade;
    private EditText telefone;
    private EditText endereco;
    private EditText fotoUrl;

    private EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        viewList = inflater.inflate(R.layout.activity_main, null);
        setContentView(viewList);
        view = inflater.inflate(R.layout.dialog_add, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("StudentsList");

        url = (EditText) view.findViewById(R.id.url);
        foto = (ImageView) view.findViewById(R.id.fotoAluno);

        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSmallImage(url != null ? url.getText().toString() : "default");
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAluno();
            }
        });

        //ProgressDialog para loading enquanto baixa os dados
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Buscando dados...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        listviewAlunos = (ListView) findViewById(R.id.lista);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StudentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(StudentAPI.class);

        callGetAlunos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        sobre = menu.findItem(R.id.action_settings);
        sobre.setVisible(true);
        return true;
    }

    private void updateSmallImage(String url) {
        Picasso.with(this)
                .load(url)
                .into(foto);
    }

    private void addAluno() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        name = (EditText) view.findViewById(R.id.name);
        idade = (EditText) view.findViewById(R.id.idade);
        telefone = (EditText) view.findViewById(R.id.tel);
        endereco = (EditText) view.findViewById(R.id.endereco);
        fotoUrl = (EditText) view.findViewById(R.id.url);
        pDialog.setMessage("Adicionando aluno(a)...");

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Student novo = new Student();

                        novo.setNome(name.getText().toString());
                        int idadeInt = 0;
                        if (idade.getText().length() != 0) {
                            idadeInt = Integer.parseInt(idade.getText().toString());
                        }
                        novo.setIdade(idadeInt);
                        novo.setTelefone(telefone.getText().toString());
                        novo.setEndereco(endereco.getText().toString());

                        String textFotoUrl = "";
                        if (null == fotoUrl.getText()) {
                            textFotoUrl = "https://robohash.org/fotobase";
                        } else {
                            textFotoUrl = fotoUrl.getText().toString();
                        }
                        novo.setFotoUrl(textFotoUrl);
                        callAddAluno(novo);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Cancelado!", Toast.LENGTH_LONG).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callAddAluno(Student student) {
        pDialog.show();
        Call<StudentReturn> call = service.addAluno(student);
        call.enqueue(new Callback<StudentReturn>() {
            @Override
            public void onResponse(Call<StudentReturn> call, Response<StudentReturn> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Aluno(a) adicionado(a) com sucesso!", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    pDialog.setMessage("Buscando dados...");
                    pDialog.setIndeterminate(true);
                    pDialog.setCancelable(false);

                    //Limpa os campos para inserir outro novo aluno
                    name.setText("");
                    idade.setText("");
                    telefone.setText("");
                    endereco.setText("");
                    //fotoUrl.setText(""); se descomentar vai dar pau

                    callGetAlunos();
                } else {
                    Log.e(TAG, response.message());
                }
                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<StudentReturn> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                pDialog.dismiss();
            }
        });

    }

    private void callGetAlunos() {
        pDialog.show();
        Call<StudentReturn> call = service.listAlunos();
        call.enqueue(new Callback<StudentReturn>() {
            @Override
            public void onResponse(Call<StudentReturn> call, Response<StudentReturn> response) {
                if (response.isSuccessful()) {
                    StudentReturn retorno = response.body();
                    makeAdapterListViewCustomizado(retorno.getResults());
                } else {
                    Log.e(TAG, response.message());
                }
                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<StudentReturn> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                pDialog.dismiss();
            }
        });

    }

    private void makeAdapterListViewCustomizado(List<Student> students) {
        final StudentListAdapter adapterCustomizado = new StudentListAdapter(this, students);
        listviewAlunos.setAdapter(adapterCustomizado);
        listviewAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) adapterCustomizado.getItem(position);
                showMaps(student);
            }
        });
    }

    private void showMaps(Student student) {
        pDialog.setMessage("Buscando endereço...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_ENDERECO, student.getEndereco());
        intent.putExtra(EXTRA_NOME, student.getNome());
        pDialog.dismiss();
        startActivity(intent);
    }
}
