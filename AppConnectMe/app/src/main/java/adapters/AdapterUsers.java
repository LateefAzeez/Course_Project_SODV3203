package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sodv3203.connectmeapp.ChatActivity;
import com.sodv3203.connectmeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import models.ModelUser;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUser> userList;

    //Constructor


    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String receiverUID = userList.get(position).getUid();
        String userImageHolder = userList.get(position).getImage();
        String userNameHolder = userList.get(position).getName();
        final String userEmailHolder = userList.get(position).getEmail();

        //set data
        holder.userName.setText(userNameHolder);
        holder.userEmail.setText(userEmailHolder);

        try {
            Picasso.get().load(userImageHolder).placeholder(R.drawable.ic_default_user_img).into(holder.userImage);
        }
        catch (Exception e) {

        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Click user from userlist to start chatting/messaging
                * starty activity by putting UID of receiver
                * we will use that UID to identify the user we are gonna chat*/

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiverUid", receiverUID );
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName, userEmail;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //set reference
            userImage = itemView.findViewById(R.id.user_user_image);
            userName = itemView.findViewById(R.id.user_user_name);
            userEmail = itemView.findViewById(R.id.user_user_email);
        }
    }
}
