package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sodv3203.connectmeapp.GroupChatActivity;
import com.sodv3203.connectmeapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.ModelGroupChatList;

public class AdapterGroupChatList extends RecyclerView.Adapter<AdapterGroupChatList.HolderGroupChatList> {

    private Context context;
    public ArrayList<ModelGroupChatList> groupChatLists;

    public AdapterGroupChatList(Context context, ArrayList<ModelGroupChatList> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout;
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchats_list, parent, false);
        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
        //get data
        ModelGroupChatList model = groupChatLists.get(position);
        final String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        //set data
        holder.groupTitle.setText(groupTitle);
        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_icon).into(holder.groupIcon);
        }
        catch (Exception e) {
            holder.groupIcon.setImageResource(R.drawable.ic_group_icon);
        }

        //handle group click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open group chat
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    //View holder class
    class HolderGroupChatList extends RecyclerView.ViewHolder{

        //ui views
        private ImageView groupIcon;
        private TextView groupTitle, groupName, message, messageTime;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);
            groupIcon = itemView.findViewById(R.id.group_chat_icon);
            groupTitle = itemView.findViewById(R.id.group_title);
            groupName = itemView.findViewById(R.id.sender_name);
            message = itemView.findViewById(R.id.message);
            messageTime = itemView.findViewById(R.id.message_time);


        }
    }
}
