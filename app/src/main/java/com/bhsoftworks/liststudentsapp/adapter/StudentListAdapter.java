package com.bhsoftworks.liststudentsapp.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.bhsoftworks.liststudentsapp.R;
import com.bhsoftworks.liststudentsapp.model.Student;
import com.bhsoftworks.liststudentsapp.model.StudentReturn;
import com.bhsoftworks.liststudentsapp.service.StudentAPI;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by leandrodlm on 04/08/17.
 */

public class StudentListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Student> students;

    ProgressDialog pDialog;
    static final String TAG = "Retrofit";


    public StudentListAdapter(Context context, List<Student> students) {

        this.context = context;
        this.students = students;
    }

    @Override
    public int getCount() {

        return students != null ? students.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Infla a view da linha/item
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_aluno, parent, false);

        // findViewById das views que precisa atualizar
        ImageView foto = (ImageView) view.findViewById(R.id.imgUrl);
        TextView nome = (TextView) view.findViewById(R.id.txtNome);
        TextView idade = (TextView) view.findViewById(R.id.txtIdade);
        TextView endereco = (TextView) view.findViewById(R.id.txtEndereco);
        ImageView delete = (ImageView) view.findViewById(R.id.deleteStudent);

        // Atualiza os valores das views
        final Student student = students.get(position);
        Picasso.with(context).load(student.getFotoUrl()).into(foto);
        nome.setText(student.getNome());
        idade.setText(String.valueOf(String.valueOf(student.getIdade())));
        endereco.setText(student.getEndereco());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder excludeConfirm = new AlertDialog.Builder(context);

                excludeConfirm.setMessage("Deseja excluir " + student.getNome() + " ?")
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteStudent(student, position);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = excludeConfirm.create();
                alert.setTitle(R.string.atencao);
                alert.show();
            }
        });

        return view;
    }


    private void deleteStudent(Student a, final int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StudentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        StudentAPI service = retrofit.create(StudentAPI.class);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Deletando aluno(a)...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<StudentReturn> call = service.deleteAluno(a.getObjectId());
        call.enqueue(new Callback<StudentReturn>() {
            @Override
            public void onResponse(Call<StudentReturn> call, Response<StudentReturn> response) {
                if (response.isSuccessful()) {
                    students.remove(position);
                } else {
                    Log.e(TAG, response.message());
                }

                if(pDialog!=null) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StudentReturn> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                if(pDialog!=null) {
                    pDialog.dismiss();
                }
            }
        });
        notifyDataSetChanged();
    }
}
