package com.myapps.sdr.storagefirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.ViewHolder> {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Context context;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    public void update(String filename, String url)
    {
        items.add(filename);
        urls.add(url);
        notifyDataSetChanged();
    }
    public DocAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public DocAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.imgitem,parent,false);
        return new DocAdapter.ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final DocAdapter.ViewHolder holder, final int position) {
        String old = items.get(position);
        old = old.replace("--",".");
        holder.nameoffile.setText(old);
        String name = holder.nameoffile.getText().toString();

        if(name.contains("pdf")) {
            Glide.with(context).asBitmap().load(R.drawable.pdf).into(holder.thumbnail);
        }
        else if(name.contains("pptx"))
        {
            Glide.with(context).asBitmap().load(R.drawable.ppt).into(holder.thumbnail);
        }
        else if(name.contains("docx"))
        {
            Glide.with(context).asBitmap().load(R.drawable.word).into(holder.thumbnail);
        }
        else if(name.contains("xslx"))
        {
            Glide.with(context).asBitmap().load(R.drawable.excel).into(holder.thumbnail);
        }
        else if(name.contains("txt"))
        {
            Glide.with(context).asBitmap().load(R.drawable.text).into(holder.thumbnail);
        }

        holder.dwnld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urls.get(position)));
                context.startActivity(intent);
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(context);
                progressDialog.setMessage("Deleting...");
                progressDialog.show();
                String name = holder.nameoffile.getText().toString();
                final String name1 = name.replace(".","--");
                final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child(user).child("My Documents").child(name).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(user).child("My Documents").child(name1).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                items.remove(holder.getAdapterPosition());
                                urls.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(),items.size());
                                Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm=context.getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = items.get(position);
                    text = text.replace("--",".");
                    String durl = urls.get(position);
                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, text+"\n"+durl);
                        context.startActivity(Intent.createChooser(waIntent, "Share with"));
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameoffile;
        ImageButton dwnld, share, del;
        ImageView thumbnail;

        public ViewHolder(View view){
            super(view);
            this.nameoffile = view.findViewById(R.id.nameoffile);
            this.dwnld = view.findViewById(R.id.dwnld);
            this.share = view.findViewById(R.id.share);
            this.del = view.findViewById(R.id.delete);
            this.thumbnail = view.findViewById(R.id.imageView);
        }

    }
}
