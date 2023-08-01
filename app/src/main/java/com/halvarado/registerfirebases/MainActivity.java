package com.halvarado.registerfirebases;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Region;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText txtid, txtNombre,txtApellido,txtGenero,txtFechaNacimiento;
    private Button btnbus, btnmod, btnreg, btneli;
    private ListView lvDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtid   = (EditText) findViewById(R.id.txtid);
        txtNombre  = (EditText) findViewById(R.id.txtNombre);
        txtApellido  = (EditText) findViewById(R.id.txtApellido);
        txtGenero  = (EditText) findViewById(R.id.txtGenero);
        txtFechaNacimiento  = (EditText) findViewById(R.id.txtFechaNacimiento);
        btnbus  = (Button)   findViewById(R.id.btnbus);
        btnmod  = (Button)   findViewById(R.id.btnmod);
        btnreg  = (Button)   findViewById(R.id.btnreg);
        btneli  = (Button)   findViewById(R.id.btneli);
        lvDatos = (ListView) findViewById(R.id.lvDatos);


        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarRegistros();
    }
    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()
                        || txtNombre.getText().toString().trim().isEmpty()
                        || txtApellido.getText().toString().trim().isEmpty()
                        || txtGenero.getText().toString().trim().isEmpty()
                        || txtFechaNacimiento.getText().toString().trim().isEmpty() ){
                    ocultarTeclado();
                    Toast.makeText(getApplicationContext(),"Complete los datos faltantes", Toast.LENGTH_LONG).show();
                }else {
                    String id = txtid.getText().toString();
                    String nombre = txtNombre.getText().toString();
                    String apellido = txtApellido.getText().toString();
                    String genero = txtGenero.getText().toString();
                    String fechaNacimiento = txtFechaNacimiento.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(registros.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean respuesta = false;
                            for (DataSnapshot dto : snapshot.getChildren()){
                                if (dto.child("foro").getValue().toString().equalsIgnoreCase(id)){
                                    respuesta = true;
                                    ocultarTeclado();
                                    Toast.makeText(getApplicationContext(),"Error!!! El Foro ("+id+")ya existe",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            if (respuesta==false){
                                registros registro = new registros(id,nombre,apellido,genero,fechaNacimiento);
                                dbref.push().setValue(registro);
                                ocultarTeclado();
                                Toast.makeText(getApplicationContext(),"El registro se ha completado",Toast.LENGTH_LONG).show();
                                LimpiarCampos();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });//Cierra el evento AddListenerForSingleValueEvent

                }//Cierra el If-Else
            }
        });
    }//Cierra metodo Registrar

    private void listarRegistros(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbred = db.getReference(registros.class.getSimpleName());
                ArrayList<registros> mData = new ArrayList<registros>();
                ArrayAdapter<registros> mAdapter = new ArrayAdapter<registros>(getApplicationContext(), android.R.layout.simple_list_item_1,mData);
                lvDatos.setAdapter(mAdapter);

                dbred.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        registros reg = snapshot.getValue(registros.class);
                        mData.add(reg);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        registros regi = mData.get(position);
                        AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                        a.setCancelable(true);
                        a.setTitle("Registro Seleccionado");
                String msg = "Foro : " + regi.getForo() + "\n\n";
                msg += "Titular Registro :  " + regi.getNombres() + " " + regi.getApellidos()+ "\n\n";
                msg += "Genero : " + regi.getGenero()+ "\n\n";
                msg += "Fecha Nacimiento : " + regi.getFechaNacimiento();

                a.setMessage(msg);
                a.show();
            }
        });

    }//Cierra el metood ListarRegistros




    private void botonBuscar(){
        btnbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Digite el codigo del foro a buscar",Toast.LENGTH_LONG).show();
                }
                else {
                    String id = txtid.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(registros.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean respuesta = false;
                            for (DataSnapshot dta : snapshot.getChildren()){
                                if (id.equalsIgnoreCase(dta.child("foro").getValue().toString())){
                                    respuesta = true;
                                    ocultarTeclado();
                                    txtNombre.setText(dta.child("nombres").getValue().toString());
                                    txtApellido.setText(dta.child("apellidos").getValue().toString());
                                    txtGenero.setText(dta.child("genero").getValue().toString());
                                    txtFechaNacimiento.setText(dta.child("fechaNacimiento").getValue().toString());
                                    break;
                                }
                            }
                            if (respuesta==false){
                                ocultarTeclado();
                                Toast.makeText(getApplicationContext(),"Foro : " + id + " No fue encontrado",Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }//cierra el If-Else Inicial
            }
        });

    }//Cierra el metodo botonBuscar

    private void botonModificar(){
        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()
                        || txtNombre.getText().toString().trim().isEmpty()
                        || txtApellido.getText().toString().trim().isEmpty()
                        || txtGenero.getText().toString().trim().isEmpty()
                        || txtFechaNacimiento.getText().toString().trim().isEmpty() ){
                    ocultarTeclado();
                    Toast.makeText(getApplicationContext(),"Complete los datos faltantes para actualizar", Toast.LENGTH_LONG).show();
                }else {
                    String id = txtid.getText().toString();
                    String nombre = txtNombre.getText().toString();
                    String apellido = txtApellido.getText().toString();
                    String genero = txtGenero.getText().toString();
                    String fechaNacimiento = txtFechaNacimiento.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(registros.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean respuesta = false;
                            for (DataSnapshot dto : snapshot.getChildren()){
                                if (dto.child("foro").getValue().toString().equalsIgnoreCase(id)){
                                    respuesta = true;
                                    ocultarTeclado();
                                    dto.getRef().child("nombres").setValue(nombre);
                                    dto.getRef().child("apellidos").setValue(apellido);
                                    dto.getRef().child("genero").setValue(genero);
                                    dto.getRef().child("fechaNacimiento").setValue(fechaNacimiento);
                                    listarRegistros();
                                    LimpiarCampos();
                                    break;
                                }
                            }
                            if (respuesta==false){
                                ocultarTeclado();
                                Toast.makeText(getApplicationContext(),"El Foro " + id + " No se puede modificar porque no existe",Toast.LENGTH_LONG).show();
                                LimpiarCampos();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });//Cierra el evento AddListenerForSingleValueEvent

                }//Cierra el If-Else
            }
        });
    }//Cierre el metodo botonModificar

    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtid.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Digite el codigo del foro a Eliminanr",Toast.LENGTH_LONG).show();
                }
                else {
                    String id = txtid.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(registros.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final boolean[] respuesta = {false};
                            for (DataSnapshot dta : snapshot.getChildren()){
                                if (id.equalsIgnoreCase(dta.child("foro").getValue().toString())){

                                    AlertDialog.Builder alert  = new AlertDialog.Builder(MainActivity.this);
                                    alert.setCancelable(false);
                                    alert.setTitle("Acciones");
                                    alert.setMessage("Esta seguro de Elimianr el registro "+ id +"?");
                                    alert.setNegativeButton("Cancelar. ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    alert.setPositiveButton("Aceptar. ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            respuesta[0] = true;
                                            ocultarTeclado();
                                            dta.getRef().removeValue();
                                            listarRegistros();
                                            Toast.makeText(getApplicationContext(),"Foro Eliminado",Toast.LENGTH_LONG).show();
                                            LimpiarCampos();
                                        }
                                    });
                                    alert.show();
                                    break;
                                }
                            }
                            if (respuesta[0] ==false){
                                ocultarTeclado();
                                Toast.makeText(getApplicationContext(),"Foro : " + id + " No fue encontrado",Toast.LENGTH_LONG).show();
                            }
                        }//Cierre del evento onDataChange(@NonNull DataSnapshot snapshot)
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }//cierra el If-Else Inicial
            }
        });
    }//Cierra el metodo botonEliminar

    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.

    private void LimpiarCampos(){
        txtid.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtGenero.setText("");
        txtFechaNacimiento.setText("");
    }
}